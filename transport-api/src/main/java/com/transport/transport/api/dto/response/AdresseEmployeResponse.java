package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdresseEmployeResponse extends RepresentationModel<AdresseEmployeResponse> {

    private Integer id;
    private Integer idEmploye;
    private String nomEmploye;
    private String adresse;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean estPrincipale;
    private Boolean actif;
    private LocalDateTime dateInsertion;
}
