package com.wedding.rsvpplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "families", uniqueConstraints = @UniqueConstraint(columnNames = "invite_token"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Family {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "invite_token", nullable = false, updatable = false)
    private String inviteToken;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "language_pref")
    @Builder.Default
    private String languagePref = "en";

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Guest> guests = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (inviteToken == null) {
            inviteToken = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }
    }
}
