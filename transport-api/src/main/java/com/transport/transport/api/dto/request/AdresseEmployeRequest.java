package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AdresseEmployeRequest {

    @NotNull
    private Integer idEmploye;

    private String adresse;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean estPrincipale;
}
