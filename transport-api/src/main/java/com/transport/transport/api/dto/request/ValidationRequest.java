package com.transport.transport.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ValidationRequest {

    @Positive(message = "L'identifiant du véhicule doit être un entier positif.")
    private Integer idVehicule;

    @Builder.Default
    private Boolean reassign = false;
}
