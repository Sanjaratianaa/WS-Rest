package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VehiculeRequest {

    @NotBlank(message = "Le matricule est obligatoire")
    private String matricule;

    @NotNull(message = "Le nombre de places est obligatoire")
    @Min(value = 2, message = "Le nombre de places doit être supérieur à 1")
    private Integer nombrePlaces;
}
