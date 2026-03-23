package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoriqueAffectationResponse extends RepresentationModel<HistoriqueAffectationResponse> {

    private Integer idHistorique;
    private Integer idAffectation;
    private Integer idDate;
    private Integer idEmploye;
    private Integer idAdresse;
    private Integer idTypeTransport;
    private Integer idSite;
    private Integer idVehicule;
    private Integer idHeureTransport;
    private Boolean estValidee;
    private String commentaire;
    private LocalDateTime dateCreation;
    private LocalDateTime dateValidation;
    private Integer idType;
    private LocalDateTime dateModification;
}
