package com.wedding.rsvpplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RsvpPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(RsvpPlatformApplication.class, args);
    }
}
