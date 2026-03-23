package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DateTransportRequest {

    @NotNull
    private LocalDate dateJour;
}
