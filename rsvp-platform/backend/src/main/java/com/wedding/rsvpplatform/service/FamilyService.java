package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.dto.FamilyDto;
import com.wedding.rsvpplatform.dto.GuestDto;
import com.wedding.rsvpplatform.dto.RsvpDto;
import com.wedding.rsvpplatform.exception.NotFoundException;
import com.wedding.rsvpplatform.model.Family;
import com.wedding.rsvpplatform.model.Rsvp;
import com.wedding.rsvpplatform.repository.FamilyRepository;
import com.wedding.rsvpplatform.repository.RsvpRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final RsvpRepository rsvpRepository;

    public FamilyService(FamilyRepository familyRepository, RsvpRepository rsvpRepository) {
        this.familyRepository = familyRepository;
        this.rsvpRepository = rsvpRepository;
    }

    @Transactional(readOnly = true)
    public FamilyDto getByInviteToken(String inviteToken) {
        Family family = familyRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new NotFoundException("No invite found for this link"));

        List<Rsvp> rsvps = rsvpRepository.findByFamilyId(family.getId());
        Map<UUID, List<Rsvp>> byGuest = rsvps.stream()
                .collect(Collectors.groupingBy(r -> r.getGuest().getId()));

        List<GuestDto> guestDtos = family.getGuests().stream()
                .map(g -> new GuestDto(
                        g.getId(),
                        g.getName(),
                        g.getMealPref(),
                        g.getDietaryNotes(),
                        byGuest.getOrDefault(g.getId(), List.of()).stream()
                                .map(r -> new RsvpDto(r.getEvent().getType(), r.getStatus(),
                                        r.getPlusOneName(), r.getPlusOneMealPref()))
                                .toList()
                ))
                .toList();

        return new FamilyDto(family.getId(), family.getDisplayName(), family.getLanguagePref(),
                family.getInviteToken(), guestDtos);
    }

    @Transactional(readOnly = true)
    public Family requireByInviteToken(String inviteToken) {
        return familyRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new NotFoundException("No invite found for this link"));
    }
}
