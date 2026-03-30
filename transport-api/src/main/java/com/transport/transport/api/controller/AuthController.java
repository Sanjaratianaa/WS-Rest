package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.LoginRequest;
import com.transport.transport.api.dto.request.RegisterAdminRequest;
import com.transport.transport.api.dto.request.RegisterRequest;
import com.transport.transport.api.dto.response.AuthResponse;
import com.transport.transport.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur par matricule et mot de passe, retourne un token JWT.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie, token JWT retourné"),
            @ApiResponse(responseCode = "400", description = "Matricule ou mot de passe incorrect")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription employé existant", description = "Crée un compte pour un employé existant (identifié par matricule). Rôle EMPLOYE attribué automatiquement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscription réussie, token JWT retourné"),
            @ApiResponse(responseCode = "400", description = "Matricule introuvable, compte déjà existant, ou mots de passe non conformes")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/admin-register")
    @Operation(summary = "Créer un compte administrateur", description = "Crée un nouvel employé et son compte avec le rôle ADMIN. Le matricule est généré automatiquement (ADMxxx).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inscription réussie, token JWT retourné"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou mots de passe non conformes")
    })
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        return ResponseEntity.ok(authService.registerAdmin(request));
    }
}
