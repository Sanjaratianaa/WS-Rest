package com.transport.transport.api.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class VehiculeRequest {

    private String matricule;
    private Integer nombrePlaces;
}
