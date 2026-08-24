package com.wedding.rsvpplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "practice_video_url")
    private String practiceVideoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by_family_id")
    private Family claimedByFamily;

    @Column(nullable = false)
    @Builder.Default
    private boolean locked = false;

    /**
     * Optimistic-lock guard: combined with the conditional UPDATE ... WHERE claimed_by_family_id
     * IS NULL in SongRepository, this makes claiming a song safe under concurrent requests.
     */
    @Version
    private Long version;
}
