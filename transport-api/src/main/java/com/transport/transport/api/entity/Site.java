package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "site")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String nom;

    @Column(length = 255)
    private String adresse;

    @Column(precision = 18, scale = 9)
    private BigDecimal latitude;

    @Column(precision = 18, scale = 9)
    private BigDecimal longitude;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime dateInsertion = LocalDateTime.now();
}
