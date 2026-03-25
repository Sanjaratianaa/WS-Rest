package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValidationRequest {

    @Positive(message = "L'identifiant du véhicule doit être un entier positif.")
    private Integer idVehicule;

    @Builder.Default
    private Boolean reassign = false;
}
