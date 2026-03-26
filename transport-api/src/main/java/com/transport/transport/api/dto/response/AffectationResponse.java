package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AffectationResponse extends RepresentationModel<AffectationResponse> {

    private Integer id;

    private LocalDate dateJour;

    private Integer idEmploye;
    private String nomEmploye;
    private String prenomEmploye;

    private Integer idAdresse;
    private String adresse;

    private Integer idTypeTransport;
    private String libelleTypeTransport;

    private Integer idSite;
    private String nomSite;

    private Integer idVehicule;
    private String matriculeVehicule;

    private Integer idHeureTransport;
    private LocalTime heure;
    private String libelleHeure;

    private Boolean estValidee;
    private String commentaire;

    private LocalDateTime dateCreation;
    private LocalDateTime dateValidation;

    private Integer idType;
    private String libelleTypeAffectation;

    private Boolean estArchive;
}
