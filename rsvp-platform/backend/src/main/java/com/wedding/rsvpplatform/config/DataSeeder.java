package com.wedding.rsvpplatform.config;

import com.wedding.rsvpplatform.model.AdminUser;
import com.wedding.rsvpplatform.model.EventType;
import com.wedding.rsvpplatform.model.WeddingEvent;
import com.wedding.rsvpplatform.repository.AdminUserRepository;
import com.wedding.rsvpplatform.repository.WeddingEventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final WeddingEventRepository eventRepository;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    public DataSeeder(WeddingEventRepository eventRepository,
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
        eventRepository.findByType(EventType.WEDDING).orElseGet(() ->
                eventRepository.save(WeddingEvent.builder()
                        .type(EventType.WEDDING)
                        .name("The Wedding")
                        .venue("TBD")
                        .dressCode("Formal")
                        .build()));

        eventRepository.findByType(EventType.SANGEET).orElseGet(() ->
                eventRepository.save(WeddingEvent.builder()
                        .type(EventType.SANGEET)
                        .name("Sangeet Night")
                        .venue("TBD")
                        .dressCode("Festive / Indian formal")
                        .build()));

        String bootstrapUsername = appProperties.admin().bootstrapUsername();
        adminUserRepository.findByUsername(bootstrapUsername).orElseGet(() ->
                adminUserRepository.save(AdminUser.builder()
                        .username(bootstrapUsername)
                        .passwordHash(passwordEncoder.encode(appProperties.admin().bootstrapPassword()))
                        .role("ADMIN")
                        .build()));
    }
}
