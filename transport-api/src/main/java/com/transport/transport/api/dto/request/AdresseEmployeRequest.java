package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AdresseEmployeRequest {

    @Positive(message = "L'identifiant de l'employé doit être un entier positif.")
    private Integer idEmploye;

    @NotBlank(message = "L'adresse ne doit pas être vide.")
    private String adresse;

    @NotNull(message = "La latitude est obligatoire. Elle doit être associée à l'adresse.")
    private BigDecimal latitude;

    @NotNull(message = "La longitude est obligatoire. Elle doit être associée à l'adresse.")
    private BigDecimal longitude;

    @NotNull(message = "L'indicateur de principalité est obligatoire.")
    private Boolean estPrincipale;
}
