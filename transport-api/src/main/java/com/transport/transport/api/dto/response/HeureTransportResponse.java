package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HeureTransportResponse extends RepresentationModel<HeureTransportResponse> {

    private Integer id;
    private LocalTime heure;
    private String libelle;
    private Boolean actif;
    private LocalDateTime dateInsertion;
}
