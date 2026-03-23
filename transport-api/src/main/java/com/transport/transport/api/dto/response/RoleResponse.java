package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleResponse extends RepresentationModel<RoleResponse> {

    private Integer id;
    private String libelle;
}
