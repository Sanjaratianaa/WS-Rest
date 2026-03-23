package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehiculeResponse extends RepresentationModel<VehiculeResponse> {

    private Integer id;
    private String matricule;
    private Integer nombrePlaces;
    private Boolean actif;
    private LocalDateTime dateInsertion;
}
