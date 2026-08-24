package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.dto.AdminLoginRequest;
import com.wedding.rsvpplatform.dto.AdminLoginResponse;
import com.wedding.rsvpplatform.exception.UnauthorizedException;
import com.wedding.rsvpplatform.model.AdminUser;
import com.wedding.rsvpplatform.repository.AdminUserRepository;
import com.wedding.rsvpplatform.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminAuthController(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder,
                                JwtService jwtService) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AdminLoginResponse login(@Valid @RequestBody AdminLoginRequest request) {
        AdminUser user = adminUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return new AdminLoginResponse(jwtService.generateToken(user.getUsername(), user.getRole()));
    }
}
