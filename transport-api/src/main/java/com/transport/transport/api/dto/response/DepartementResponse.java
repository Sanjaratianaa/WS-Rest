package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DepartementResponse extends RepresentationModel<DepartementResponse> {

    private Integer id;
    private String nom;
    private String description;
    private Boolean actif;
    private LocalDateTime dateInsertion;
    private LocalDateTime dateDesactivation;
}
