package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String motDePasse;

    private String matricule;
    private String telephone;
    private Integer idDepartement;
}
