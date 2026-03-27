package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmployeRequest {

    @NotBlank(message = "Le nom de l'employé est obligatoire.")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
    private String nom;

    @NotBlank(message = "Le prénom de l'employé est obligatoire.")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères.")
    private String prenom;

    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères.")
    @Pattern(regexp = "^\\+?[0-9\\s\\-()]*$", message = "Le numéro de téléphone n'est pas valide.")
    private String telephone;

    @Positive(message = "L'identifiant du département doit être un entier positif.")
    private Integer idDepartement;

    @NotNull(message = "Le statut de bénéficiaire du transport est obligatoire.")
    private Boolean estBeneficiaireTransport;
}
