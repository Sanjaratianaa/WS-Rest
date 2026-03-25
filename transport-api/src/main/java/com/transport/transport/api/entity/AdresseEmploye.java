package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "adresse_employe")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdresseEmploye {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_employe")
    private Employe employe;

    @Column(length = 255)
    private String adresse;

    @Column(precision = 18, scale = 9)
    private BigDecimal latitude;

    @Column(precision = 18, scale = 9)
    private BigDecimal longitude;

    @Builder.Default
    private Boolean estPrincipale = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime dateInsertion = LocalDateTime.now();
}
