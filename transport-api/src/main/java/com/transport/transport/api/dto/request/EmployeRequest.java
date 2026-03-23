package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EmployeRequest {

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    private String matricule;
    private String telephone;
    private Integer idDepartement;
}
