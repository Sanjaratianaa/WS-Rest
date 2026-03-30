package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AffectationRequest {

    @NotNull(message = "La date est obligatoire.")
    @FutureOrPresent(message = "La date ne peut pas être dans le passé.")
    private LocalDate date;

    @Positive(message = "L'identifiant de l'employé doit être un entier positif.")
    private Integer idEmploye;

    @Positive(message = "L'identifiant de l'adresse doit être un entier positif.")
    private Integer idAdresse;

    @NotNull(message = "Le type de transport est obligatoire.")
    @Positive(message = "L'identifiant du type de transport doit être un entier positif.")
    private Integer idTypeTransport;

    @NotNull(message = "Le site est obligatoire.")
    @Positive(message = "L'identifiant du site doit être un entier positif.")
    private Integer idSite;

    @Positive(message = "L'identifiant du véhicule doit être un entier positif.")
    private Integer idVehicule;

    @NotNull(message = "L'heure de transport est obligatoire.")
    @Positive(message = "L'identifiant de l'heure de transport doit être un entier positif.")
    private Integer idHeureTransport;

    @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères.")
    private String commentaire;
}
