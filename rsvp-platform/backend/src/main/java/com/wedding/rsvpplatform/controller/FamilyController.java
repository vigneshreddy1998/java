package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.dto.FamilyDto;
import com.wedding.rsvpplatform.service.FamilyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/families")
public class FamilyController {

    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @GetMapping("/{inviteToken}")
    public FamilyDto getFamily(@PathVariable String inviteToken) {
        return familyService.getByInviteToken(inviteToken);
    }
}
