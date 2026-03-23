package com.transport.transport.api.dto.response;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehiculeTrajetResponse {

    private Integer idVehicule;
    private String matriculeVehicule;
    private Integer nombrePlaces;
    private List<TrajetGroupe> trajets;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TrajetGroupe {
        private LocalDate date;
        private LocalTime heure;
        private String libelleHeure;
        private String nomSite;
        private String libelleTypeTransport;
        private List<Passager> passagers;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Passager {
        private Integer idEmploye;
        private String nom;
        private String prenom;
        private String adresse;
    }
}
