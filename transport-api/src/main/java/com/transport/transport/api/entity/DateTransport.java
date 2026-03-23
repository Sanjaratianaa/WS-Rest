package com.transport.transport.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "date_transport")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DateTransport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate dateJour;

    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;
}
