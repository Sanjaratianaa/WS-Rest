package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "departement")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Departement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime dateInsertion = LocalDateTime.now();

    private LocalDateTime dateDesactivation;
}
