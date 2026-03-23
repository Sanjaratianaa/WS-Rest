package com.transport.transport.api.dto.request;

import lombok.*;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HeureTransportRequest {

    private LocalTime heure;
    private String libelle;
}
