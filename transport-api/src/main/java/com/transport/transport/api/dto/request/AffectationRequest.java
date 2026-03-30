package com.transport.transport.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AffectationRequest {

    @Schema(description = "Date du transport (YYYY-MM-DD)", example = "2026-04-02")
    @NotNull(message = "La date est obligatoire.")
    @FutureOrPresent(message = "La date ne peut pas être dans le passé.")
    private LocalDate date;

    @Positive(message = "L'identifiant de l'employé doit être un entier positif.")
    private Integer idEmploye;

    @Positive(message = "L'identifiant de l'adresse doit être un entier positif.")
    private Integer idAdresse;

    @Schema(description = "ID type de transport (1=Aller, 2=Retour)", example = "1")
    @NotNull(message = "Le type de transport est obligatoire.")
    @Positive(message = "L'identifiant du type de transport doit être un entier positif.")
    private Integer idTypeTransport;

    @Schema(description = "ID du site de destination/départ", example = "1")
    @NotNull(message = "Le site est obligatoire.")
    @Positive(message = "L'identifiant du site doit être un entier positif.")
    private Integer idSite;

    @Positive(message = "L'identifiant du véhicule doit être un entier positif.")
    private Integer idVehicule;

    @Schema(description = "ID de l'heure de transport", example = "1")
    @NotNull(message = "L'heure de transport est obligatoire.")
    @Positive(message = "L'identifiant de l'heure de transport doit être un entier positif.")
    private Integer idHeureTransport;

    @Schema(description = "Commentaire optionnel", example = "Demande de transport matin")
    @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères.")
    private String commentaire;
}
