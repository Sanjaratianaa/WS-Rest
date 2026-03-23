package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeTransportResponse extends RepresentationModel<TypeTransportResponse> {

    private Integer id;
    private String libelle;
    private Boolean actif;
}
