package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HeureTransportRequest {

    @NotNull(message = "L'heure de transport est obligatoire.")
    private LocalTime heure;

    @NotBlank(message = "Le libellé est obligatoire.")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères.")
    private String libelle;
}
