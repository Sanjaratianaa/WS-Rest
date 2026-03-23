package com.transport.transport.api.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AffectationStatsResponse {

    private long totalAffectations;
    private long totalValidees;
    private long totalNonValidees;
    private double tauxValidation;
    private List<DepartementStat> parDepartement;
    private List<VehiculeStat> parVehicule;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class DepartementStat {
        private String nomDepartement;
        private long nbAffectations;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VehiculeStat {
        private String matriculeVehicule;
        private long nbAffectations;
    }
}
