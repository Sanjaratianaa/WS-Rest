package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SiteRequest {

    @NotBlank(message = "Le nom du site est obligatoire.")
    private String nom;

    @NotBlank(message = "L'adresse du site est obligatoire.")
    private String adresse;

    @NotNull(message = "La latitude est obligatoire.")
    private BigDecimal latitude;

    @NotNull(message = "La longitude est obligatoire.")
    private BigDecimal longitude;
}
