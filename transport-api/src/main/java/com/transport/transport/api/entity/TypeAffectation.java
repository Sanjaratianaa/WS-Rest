package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type_affectation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeAffectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String libelle;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;
}
