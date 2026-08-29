package com.wedding.rsvpplatform.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.ThinkingConfigAdaptive;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedding.rsvpplatform.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tidies contact names pulled out of a phone's address book — "Ravi Anna",
 * "Sharma Uncle — Office", "Priya (Sonalika's cousin)" — into names you'd put on a place card.
 *
 * <p>Scope is deliberately narrow: <b>names only</b>. Phone numbers are normalised by
 * {@link PhoneNumberService} using libphonenumber, because a wrong country-code guess would
 * lock a real guest out of their own invite, and that is not a risk worth taking for tidier
 * output.
 *
 * <p>Every suggestion is reviewed by a human on the import screen before anything is saved,
 * so a bad rename costs a glance rather than a wrong place card. If no API key is configured
 * or the call fails, import continues with the original names untouched.
 */
@Service
public class ContactCleanupService {

    private static final Logger log = LoggerFactory.getLogger(ContactCleanupService.class);

    private static final String SYSTEM_PROMPT = """
            You clean up contact names exported from a personal phone address book, for a \
            wedding guest list.

            For each contact you are given an index and the raw name as stored. Return a \
            cleaned display name suitable for a wedding guest list and a place card.

            Rules:
            - Strip relationship suffixes and honorifics used as name parts (Anna, Akka, Uncle, \
              Aunty, Bhai, Ji, Garu) unless removing them leaves nothing.
            - Strip context in brackets or after a dash: "(office)", "- work", "(Sonalika's cousin)".
            - Strip emoji, stray punctuation, and duplicated whitespace.
            - Fix obvious casing problems: "RAVI SHARMA" becomes "Ravi Sharma", "ravi" becomes "Ravi".
            - Keep genuine multi-part names intact. Never invent a surname that isn't there.
            - If the raw name is only a company, a nickname with no real name, or empty, return \
              an empty string for that contact.
            - Never alter or return phone numbers.

            Reply with JSON only, no prose, in exactly this shape:
            {"names": [{"i": 0, "name": "Ravi Sharma"}, {"i": 1, "name": ""}]}
            """;

    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public ContactCleanupService(AppProperties appProperties, ObjectMapper objectMapper) {
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    public boolean isEnabled() {
        String key = appProperties.llm().apiKey();
        return key != null && !key.isBlank();
    }

    /**
     * Returns cleaned names keyed by the index of the input list. Missing entries mean "keep
     * what you had" — a partial result is still useful, so a malformed row never fails the
     * whole import.
     */
    public Map<Integer, String> suggestNames(List<String> rawNames) {
        if (!isEnabled() || rawNames.isEmpty()) {
            return Map.of();
        }

        try {
            AnthropicClient client = AnthropicOkHttpClient.builder()
                    .apiKey(appProperties.llm().apiKey())
                    .build();

            StringBuilder input = new StringBuilder("Contacts:\n");
            for (int i = 0; i < rawNames.size(); i++) {
                input.append(i).append(": ").append(rawNames.get(i) == null ? "" : rawNames.get(i)).append('\n');
            }

            MessageCreateParams params = MessageCreateParams.builder()
                    .model(appProperties.llm().model())
                    .maxTokens(16000L)
                    .thinking(ThinkingConfigAdaptive.builder().build())
                    .system(SYSTEM_PROMPT)
                    .addUserMessage(input.toString())
                    .build();

            Message response = client.messages().create(params);

            StringBuilder text = new StringBuilder();
            for (ContentBlock block : response.content()) {
                block.text().ifPresent(t -> text.append(t.text()));
            }
            return parseNames(text.toString());
        } catch (Exception e) {
            // Cleanup is a convenience, never a gate. Import proceeds with raw names.
            log.warn("Contact name cleanup failed; falling back to original names", e);
            return Map.of();
        }
    }

    private Map<Integer, String> parseNames(String raw) throws Exception {
        String json = extractJsonObject(raw);
        if (json == null) {
            return Map.of();
        }
        JsonNode root = objectMapper.readTree(json);
        JsonNode names = root.path("names");
        Map<Integer, String> result = new LinkedHashMap<>();
        for (JsonNode entry : names) {
            if (entry.hasNonNull("i") && entry.hasNonNull("name")) {
                result.put(entry.get("i").asInt(), entry.get("name").asText().trim());
            }
        }
        return result;
    }

    /** Tolerates the model wrapping its JSON in prose or a code fence. */
    private String extractJsonObject(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        return (start >= 0 && end > start) ? raw.substring(start, end + 1) : null;
    }
}
