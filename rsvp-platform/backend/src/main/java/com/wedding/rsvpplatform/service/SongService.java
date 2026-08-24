package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.dto.SongCreateRequest;
import com.wedding.rsvpplatform.dto.SongDto;
import com.wedding.rsvpplatform.exception.ConflictException;
import com.wedding.rsvpplatform.exception.NotFoundException;
import com.wedding.rsvpplatform.model.Family;
import com.wedding.rsvpplatform.model.Song;
import com.wedding.rsvpplatform.repository.SongRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final FamilyService familyService;
    private final EntityManager entityManager;

    public SongService(SongRepository songRepository, FamilyService familyService, EntityManager entityManager) {
        this.songRepository = songRepository;
        this.familyService = familyService;
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public java.util.List<SongDto> listSongs() {
        return songRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public SongDto claim(UUID songId, String familyInviteToken) {
        Family family = familyService.requireByInviteToken(familyInviteToken);

        int updated = songRepository.claimIfUnclaimed(songId, family.getId());
        if (updated == 0) {
            // Either it's already taken, or the song doesn't exist — tell them apart.
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new NotFoundException("Song not found"));
            if (song.getClaimedByFamily() != null) {
                throw new ConflictException("This song was just claimed by another family");
            }
            throw new ConflictException("Could not claim song, please try again");
        }

        entityManager.clear();
        Song claimed = songRepository.findById(songId)
                .orElseThrow(() -> new NotFoundException("Song not found"));
        return toDto(claimed);
    }

    @Transactional
    public SongDto create(SongCreateRequest request) {
        Song saved = songRepository.save(Song.builder()
                .title(request.title())
                .practiceVideoUrl(request.practiceVideoUrl())
                .build());
        return toDto(saved);
    }

    private SongDto toDto(Song song) {
        return new SongDto(
                song.getId(),
                song.getTitle(),
                song.getPracticeVideoUrl(),
                song.isLocked(),
                song.getClaimedByFamily() != null ? song.getClaimedByFamily().getDisplayName() : null
        );
    }
}
