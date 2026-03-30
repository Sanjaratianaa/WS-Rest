package com.transport.transport.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @Schema(description = "Matricule d'un employe existant (sans compte)", example = "EMP003")
    @NotBlank(message = "Le matricule est obligatoire")
    private String matricule;

    @Schema(description = "Mot de passe (min 8 car., 1 maj., 1 min., 1 chiffre, 1 special)", example = "Test@1234")
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&)"
    )
    private String motDePasse;

    @Schema(description = "Confirmation du mot de passe (identique)", example = "Test@1234")
    @NotBlank(message = "La confirmation du mot de passe est obligatoire")
    private String confirmationMotDePasse;

}