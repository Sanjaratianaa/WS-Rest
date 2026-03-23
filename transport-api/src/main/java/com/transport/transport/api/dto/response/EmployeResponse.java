package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeResponse extends RepresentationModel<EmployeResponse> {

    private Integer id;
    private String nom;
    private String prenom;
    private String matricule;
    private String telephone;
    private Integer idDepartement;
    private String nomDepartement;
    private Boolean actif;
    private LocalDateTime dateInsertion;
    private LocalDateTime dateDesactivation;
}
