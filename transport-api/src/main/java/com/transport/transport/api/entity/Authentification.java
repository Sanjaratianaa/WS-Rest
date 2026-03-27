package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "authentification")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Authentification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_employe")
    private Employe employe;

    @Column(nullable = false, length = 255)
    private String motDePasse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role")
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();
}
