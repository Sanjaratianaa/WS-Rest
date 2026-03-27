package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.LoginRequest;
import com.transport.transport.api.dto.request.RegisterAdminRequest;
import com.transport.transport.api.dto.request.RegisterRequest;
import com.transport.transport.api.dto.response.AuthResponse;
import com.transport.transport.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Login et Register")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription nouvel utilisateur (rôle EMPLOYE)")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/admin-register")
    @Operation(summary = "Inscription nouvel utilisateur (rôle ADMIN)")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }
}
