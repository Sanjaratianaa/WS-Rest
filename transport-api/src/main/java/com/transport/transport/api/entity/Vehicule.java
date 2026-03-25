package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicule")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String matricule;

    private Integer nombrePlaces;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime dateInsertion = LocalDateTime.now();

    private LocalDateTime dateDesactivation;
}
