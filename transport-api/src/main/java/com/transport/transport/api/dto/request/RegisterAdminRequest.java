package com.transport.transport.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdminRequest {

    @Schema(description = "Nom de famille", example = "Razafy")
    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Schema(description = "Prénom", example = "Luc")
    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Schema(description = "Mot de passe", example = "Admin@5678")
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&)"
    )
    private String motDePasse;

    @Schema(description = "Confirmation du mot de passe", example = "Admin@5678")
    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmationMotDePasse;

    @Schema(description = "Numéro de téléphone", example = "0341234567")
    @NotBlank(message = "Le numéro de téléphone est obligatoire.")
    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères.")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]*$", message = "Le numéro de téléphone n'est pas valide.")
    private String telephone;

    @Schema(description = "ID du département", example = "1")
    @NotNull(message = "L'id departement est obligatoire")
    @Positive(message = "L'identifiant du département doit être un entier positif.")
    private Integer idDepartement;
}