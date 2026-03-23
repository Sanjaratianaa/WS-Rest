package com.transport.transport.api.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AffectationRequest {

    private Integer idDate;
    private Integer idEmploye;
    private Integer idAdresse;
    private Integer idTypeTransport;
    private Integer idSite;
    private Integer idVehicule;
    private Integer idHeureTransport;
    private String commentaire;
    private Integer idType;
}
