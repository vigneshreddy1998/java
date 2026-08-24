package com.wedding.rsvpplatform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wedding.rsvpplatform.config.AppProperties;
import com.wedding.rsvpplatform.dto.ChatMessage;
import com.wedding.rsvpplatform.dto.ChatResponse;
import com.wedding.rsvpplatform.dto.FlightDetailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Service
public class FlightExtractionService {

    private static final Logger log = LoggerFactory.getLogger(FlightExtractionService.class);
    private static final String FALLBACK_REPLY =
            "I'm having trouble connecting right now — no worries, you can fill in your flight details with the form below instead.";

    private final AppProperties appProperties;
    private final FlightDetailService flightDetailService;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public FlightExtractionService(AppProperties appProperties, FlightDetailService flightDetailService,
                                    ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.flightDetailService = flightDetailService;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.create();
    }

    public ChatResponse converse(UUID guestId, List<ChatMessage> messages) {
        String apiKey = appProperties.llm().apiKey();
        if (apiKey == null || apiKey.isBlank()) {
            return new ChatResponse(false, FALLBACK_REPLY, null);
        }

        try {
            ObjectNode requestBody = buildRequest(messages);

            String rawResponse = restClient.post()
                    .uri(appProperties.llm().baseUrl())
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseResponse(guestId, messages, rawResponse);
        } catch (Exception ex) {
            log.warn("Flight-detail extraction call failed", ex);
            return new ChatResponse(false, FALLBACK_REPLY, null);
        }
    }

    private ObjectNode buildRequest(List<ChatMessage> messages) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", appProperties.llm().model());
        root.put("max_tokens", 512);
        root.put("system", """
                You are a friendly wedding-logistics assistant collecting a guest's flight details \
                so the couple can arrange airport pickup. Ask short, natural follow-up questions one \
                at a time for whatever is still missing: flight number, arrival date, arrival time, \
                arrival airport, and whether they need a pickup. If the guest doesn't know or doesn't \
                have a field (e.g. driving instead of flying, or no flight number yet), accept that and \
                move on rather than insisting. Once you have enough information (at minimum: whether \
                they need pickup, and either a flight number or an arrival date/time/airport), call the \
                record_flight_details tool with whatever fields you gathered — leave a field out if the \
                guest never provided it. Keep replies to one or two sentences.
                """);

        ArrayNode messagesNode = root.putArray("messages");
        for (ChatMessage m : messages) {
            ObjectNode msg = messagesNode.addObject();
            msg.put("role", m.role());
            msg.put("content", m.content());
        }

        ArrayNode tools = root.putArray("tools");
        ObjectNode tool = tools.addObject();
        tool.put("name", "record_flight_details");
        tool.put("description", "Record the guest's flight/arrival details once enough information has been gathered.");
        ObjectNode schema = tool.putObject("input_schema");
        schema.put("type", "object");
        ObjectNode props = schema.putObject("properties");
        props.putObject("flightNumber").put("type", "string");
        props.putObject("arrivalDate").put("type", "string").put("description", "ISO date YYYY-MM-DD");
        props.putObject("arrivalTime").put("type", "string").put("description", "24h time HH:MM");
        props.putObject("airport").put("type", "string");
        props.putObject("pickupNeeded").put("type", "boolean");

        ObjectNode toolChoice = root.putObject("tool_choice");
        toolChoice.put("type", "auto");

        return root;
    }

    private ChatResponse parseResponse(UUID guestId, List<ChatMessage> messages, String rawResponse) throws Exception {
        JsonNode response = objectMapper.readTree(rawResponse);
        JsonNode content = response.path("content");

        StringBuilder textReply = new StringBuilder();
        JsonNode toolUse = null;

        for (JsonNode block : content) {
            String type = block.path("type").asText();
            if ("text".equals(type)) {
                if (!textReply.isEmpty()) textReply.append(" ");
                textReply.append(block.path("text").asText(""));
            } else if ("tool_use".equals(type) && "record_flight_details".equals(block.path("name").asText())) {
                toolUse = block.path("input");
            }
        }

        if (toolUse != null) {
            String flightNumber = textOrNull(toolUse, "flightNumber");
            String airport = textOrNull(toolUse, "airport");
            Boolean pickupNeeded = toolUse.has("pickupNeeded") && !toolUse.get("pickupNeeded").isNull()
                    ? toolUse.get("pickupNeeded").asBoolean() : null;
            LocalDateTime arrivalDatetime = parseArrival(textOrNull(toolUse, "arrivalDate"), textOrNull(toolUse, "arrivalTime"));

            String rawLog = objectMapper.writeValueAsString(messages);
            FlightDetailDto saved = flightDetailService.save(guestId, flightNumber, arrivalDatetime, airport,
                    pickupNeeded, rawLog);

            String confirmation = textReply.isEmpty()
                    ? "Got it, thanks! Your flight details are saved."
                    : textReply.toString();
            return new ChatResponse(true, confirmation, saved);
        }

        String reply = textReply.isEmpty()
                ? "Could you tell me a bit more about your flight?"
                : textReply.toString();
        return new ChatResponse(false, reply, null);
    }

    private String textOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v == null || v.isNull()) ? null : v.asText();
    }

    private LocalDateTime parseArrival(String date, String time) {
        if (date == null) return null;
        try {
            LocalDate d = LocalDate.parse(date);
            LocalTime t = time != null ? LocalTime.parse(time) : LocalTime.MIDNIGHT;
            return LocalDateTime.of(d, t);
        } catch (DateTimeParseException e) {
            log.debug("Could not parse arrival date/time '{}' '{}'", date, time);
            return null;
        }
    }
}
