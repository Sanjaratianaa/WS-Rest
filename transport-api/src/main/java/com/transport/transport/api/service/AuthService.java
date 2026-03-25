package com.transport.transport.api.service;

import com.transport.transport.api.dto.request.LoginRequest;
import com.transport.transport.api.dto.request.RegisterRequest;
import com.transport.transport.api.dto.response.AuthResponse;
import com.transport.transport.api.entity.Authentification;
import com.transport.transport.api.entity.Employe;
import com.transport.transport.api.entity.Role;
import com.transport.transport.api.repository.AuthentificationRepository;
import com.transport.transport.api.repository.DepartementRepository;
import com.transport.transport.api.repository.EmployeRepository;
import com.transport.transport.api.repository.RoleRepository;
import com.transport.transport.api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthentificationRepository authRepo;
    private final EmployeRepository employeRepo;
    private final RoleRepository roleRepo;
    private final DepartementRepository departementRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        Authentification auth = authRepo.findByEmailAndActifTrue(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getMotDePasse(), auth.getMotDePasse())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(
                auth.getEmail(),
                auth.getRole().getLibelle(),
                auth.getEmploye().getId()
        );

        return AuthResponse.builder()
                .token(token)
                .email(auth.getEmail())
                .role(auth.getRole().getLibelle())
                .idEmploye(auth.getEmploye().getId())
                .nomComplet(auth.getEmploye().getNom() + " " + auth.getEmploye().getPrenom())
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request, String role) {
        if (authRepo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        if (!request.getMotDePasse().equals(request.getConfirmationMotDePasse()))
            throw new RuntimeException("Les mots de passe ne correspondent pas");

        // Génération automatique du matricule
        String prefix = role.equals("ADMIN") ? "ADM" : "EMP";
        String matricule = genererMatricule(prefix);

        Employe employe = Employe.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .matricule(matricule)
                .telephone(request.getTelephone())
                .build();

        if (request.getIdDepartement() != null) {
            employe.setDepartement(departementRepo.findById(request.getIdDepartement())
                    .orElseThrow(() -> new RuntimeException("Département introuvable")));
        }

        employe = employeRepo.save(employe);

        Role roleEmploye = roleRepo.findByLibelle(role)
                .orElseThrow(() -> new RuntimeException("Rôle " + role +" introuvable"));

        Authentification auth = Authentification.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .employe(employe)
                .role(roleEmploye)
                .build();

        authRepo.save(auth);

        String token = jwtUtil.generateToken(auth.getEmail(), roleEmploye.getLibelle(), employe.getId());

        return AuthResponse.builder()
                .token(token)
                .email(auth.getEmail())
                .role(roleEmploye.getLibelle())
                .idEmploye(employe.getId())
                .nomComplet(employe.getNom() + " " + employe.getPrenom())
                .build();
    }

    private String genererMatricule(String prefix) {
        // Compte le nombre d'employés avec ce prefix
        long count = employeRepo.countByMatriculeStartingWith(prefix);
        // Génère le prochain numéro avec padding (EMP001, EMP002, ...)
        return String.format("%s%03d", prefix, count + 1);
    }
}
