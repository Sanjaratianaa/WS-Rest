package com.transport.transport.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {

    @Schema(description = "Matricule de l'utilisateur", example = "ADM001")
    @NotBlank(message = "Le matricule est obligatoire.")
    @Size(min = 4, max = 20, message = "Le matricule doit contenir entre 4 et 20 caractères.")
    private String matricule;

    @Schema(description = "Mot de passe (min 8 car., 1 maj., 1 min., 1 chiffre, 1 spécial)", example = "Admin@1234")
    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Le mot de passe doit contenir au moins une majuscule, une minuscule, un chiffre et un caractère spécial (@$!%*?&)"
    )
    private String motDePasse;
}
