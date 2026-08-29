package com.wedding.rsvpplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * The root record. One row per phone number — there are no households.
 *
 * <p>Two phone columns on purpose: {@code phoneE164} is the canonical unique key, and
 * {@code phoneDigits} holds the trailing national digits so a guest who types their number
 * in a different shape than you saved it still resolves. See PhoneNumberService.
 */
@Entity
@Table(name = "guests", uniqueConstraints = @UniqueConstraint(columnNames = "phone_e164"),
       indexes = @Index(name = "idx_guest_phone_digits", columnList = "phone_digits"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guest {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "phone_e164", nullable = false)
    private String phoneE164;

    /** Trailing digits of the national number, used as a fallback match. */
    @Column(name = "phone_digits", nullable = false)
    private String phoneDigits;

    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GuestSource source = GuestSource.IMPORTED;

    /** Consent to receive WhatsApp updates. Captured at RSVP, default on. */
    @Column(name = "whatsapp_consent", nullable = false)
    @Builder.Default
    private boolean whatsappConsent = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
