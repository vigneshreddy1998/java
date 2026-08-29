package com.wedding.rsvpplatform.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.wedding.rsvpplatform.config.AppProperties;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Canonicalises phone numbers. This is the single most failure-prone part of the access
 * model: a contacts export stores numbers however they were saved ("+1 704-555-0100",
 * "(704) 555-0100", "07045550100") and the guest types whatever they remember. Both sides
 * are reduced to the same E.164 string here so they meet.
 *
 * <p>Deliberately deterministic — no model involved. A wrong country-code guess would lock a
 * real guest out of their own invite.
 */
@Service
public class PhoneNumberService {

    /**
     * How many trailing digits to keep for the fallback match. Ten covers US and Indian
     * national numbers; shorter would collide far too easily.
     */
    private static final int FALLBACK_DIGITS = 10;

    private final PhoneNumberUtil util = PhoneNumberUtil.getInstance();
    private final String defaultRegion;

    public PhoneNumberService(AppProperties appProperties) {
        this.defaultRegion = appProperties.phone().defaultRegion();
    }

    /**
     * Parses user or contact-file input into E.164. Empty when the input isn't a plausible
     * number at all — callers should treat that as "no match" rather than guessing.
     */
    public Optional<String> toE164(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            Phonenumber.PhoneNumber parsed = util.parse(raw.trim(), defaultRegion);
            if (!util.isValidNumber(parsed)) {
                return Optional.empty();
            }
            return Optional.of(util.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164));
        } catch (NumberParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Trailing digits used for the fallback lookup, derived from whatever digits are present.
     * Works even on input that failed strict validation, which is the point — it's the safety
     * net for numbers saved in an odd shape.
     */
    public String toFallbackDigits(String raw) {
        if (raw == null) {
            return "";
        }
        String digits = raw.replaceAll("\\D", "");
        return digits.length() <= FALLBACK_DIGITS
                ? digits
                : digits.substring(digits.length() - FALLBACK_DIGITS);
    }

    /** Formats for display, falling back to the stored value if it can't be parsed. */
    public String forDisplay(String e164) {
        try {
            Phonenumber.PhoneNumber parsed = util.parse(e164, defaultRegion);
            return util.format(parsed, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        } catch (NumberParseException e) {
            return e164;
        }
    }
}
