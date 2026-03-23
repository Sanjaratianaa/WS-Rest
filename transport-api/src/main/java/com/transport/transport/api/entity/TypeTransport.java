package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type_transport")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TypeTransport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String libelle;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;
}
