package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "affectation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_date")
    private DateTransport dateTransport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employe")
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_adresse")
    private AdresseEmploye adresse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_transport")
    private TypeTransport typeTransport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_site")
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehicule")
    private Vehicule vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_heure_transport")
    private HeureTransport heureTransport;

    private Boolean estValidee;

    @Column(length = 255)
    private String commentaire;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    private LocalDateTime dateValidation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type")
    private TypeAffectation typeAffectation;

    @Builder.Default
    private Boolean estArchive = false;
}
