package com.wedding.rsvpplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * An event is configured by data, not named in code. Adding a Mehendi is a row in this
 * table plus an accent colour on the front end — not a schema change or a release.
 *
 * <p>Two behaviours are deliberately independent:
 * <ul>
 *   <li>{@code requiresInvite} — whether the event is hidden from guests who aren't invited.
 *       Only the Sangeet sets this today.</li>
 *   <li>{@code collectsRsvp} — whether the event asks for anything back. The Engagement and
 *       Haldi are information-only.</li>
 * </ul>
 */
@Entity
@Table(name = "events", uniqueConstraints = @UniqueConstraint(columnNames = "event_key"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue
    private UUID id;

    /** Stable url-safe identifier, e.g. "wedding", "sangeet", "haldi", "engagement". */
    @Column(name = "event_key", nullable = false)
    private String key;

    @Column(nullable = false)
    private String name;

    @Column(name = "event_date")
    private LocalDateTime date;

    private String venue;

    @Column(name = "dress_code")
    private String dressCode;

    /** Free text shown prominently on info-only events — the Haldi colour theme lives here. */
    @Column(name = "colour_theme")
    private String colourTheme;

    /** When true, the event is invisible to guests without an explicit invite. */
    @Column(name = "requires_invite", nullable = false)
    @Builder.Default
    private boolean requiresInvite = false;

    @Column(name = "collects_rsvp", nullable = false)
    @Builder.Default
    private boolean collectsRsvp = false;

    @Column(name = "collects_meal", nullable = false)
    @Builder.Default
    private boolean collectsMeal = false;

    @Column(name = "collects_songs", nullable = false)
    @Builder.Default
    private boolean collectsSongs = false;

    /** Front-end accent token: wed | sangeet | haldi | engagement. */
    @Column(nullable = false)
    @Builder.Default
    private String accent = "wed";

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private int displayOrder = 0;
}
