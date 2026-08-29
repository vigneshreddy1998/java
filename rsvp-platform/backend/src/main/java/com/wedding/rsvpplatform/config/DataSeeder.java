package com.wedding.rsvpplatform.config;

import com.wedding.rsvpplatform.model.AdminUser;
import com.wedding.rsvpplatform.model.Event;
import com.wedding.rsvpplatform.repository.AdminUserRepository;
import com.wedding.rsvpplatform.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds the four events and the bootstrap admin. Each event is created only if its key is
 * missing, so editing dates and venues in the admin panel survives a restart.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    public DataSeeder(EventRepository eventRepository,
                       AdminUserRepository adminUserRepository,
                       PasswordEncoder passwordEncoder,
                       AppProperties appProperties) {
        this.eventRepository = eventRepository;
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.appProperties = appProperties;
    }

    @Override
    public void run(String... args) {
        seed(Event.builder()
                .key("engagement")
                .name("The Engagement")
                .venue("Charlotte, NC")
                .accent("engagement")
                .displayOrder(1)
                .build());

        seed(Event.builder()
                .key("haldi")
                .name("The Haldi")
                .venue("Charlotte, NC")
                .accent("haldi")
                .displayOrder(2)
                .build());

        // The only gated event: invisible to anyone without an explicit invite.
        seed(Event.builder()
                .key("sangeet")
                .name("Sangeet Night")
                .venue("Charlotte, NC")
                .dressCode("Festive / Indian formal")
                .requiresInvite(true)
                .collectsRsvp(true)
                .collectsMeal(true)
                .collectsSongs(true)
                .accent("sangeet")
                .displayOrder(3)
                .build());

        seed(Event.builder()
                .key("wedding")
                .name("The Wedding")
                .date(LocalDateTime.of(2026, 11, 15, 10, 0))
                .venue("Charlotte, NC — venue TBA")
                .dressCode("Formal")
                .collectsRsvp(true)
                .collectsMeal(true)
                .accent("wed")
                .displayOrder(4)
                .build());

        String username = appProperties.admin().bootstrapUsername();
        adminUserRepository.findByUsername(username).orElseGet(() ->
                adminUserRepository.save(AdminUser.builder()
                        .username(username)
                        .passwordHash(passwordEncoder.encode(appProperties.admin().bootstrapPassword()))
                        .role("ADMIN")
                        .build()));
    }

    private void seed(Event event) {
        eventRepository.findByKey(event.getKey()).orElseGet(() -> eventRepository.save(event));
    }
}
