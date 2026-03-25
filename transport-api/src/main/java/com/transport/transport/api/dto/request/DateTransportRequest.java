package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DateTransportRequest {

    @NotNull(message = "La date de transport est obligatoire.")
    @FutureOrPresent(message = "La date de transport ne peut pas être dans le passé.")
    private LocalDate dateJour;
}
