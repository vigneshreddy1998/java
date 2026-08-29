package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {
}
