package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "heure_transport")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HeureTransport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalTime heure;

    @Column(length = 100)
    private String libelle;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime dateInsertion = LocalDateTime.now();
}
