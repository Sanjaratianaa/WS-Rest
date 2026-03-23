package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_affectation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoriqueAffectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_affectation")
    private Affectation affectation;

    private Integer idDate;
    private Integer idEmploye;
    private Integer idAdresse;
    private Integer idTypeTransport;
    private Integer idSite;
    private Integer idVehicule;
    private Integer idHeureTransport;

    private Boolean estValidee;

    @Column(length = 255)
    private String commentaire;

    private LocalDateTime dateCreation;
    private LocalDateTime dateValidation;
    private Integer idType;

    @Builder.Default
    private LocalDateTime dateModification = LocalDateTime.now();
}
