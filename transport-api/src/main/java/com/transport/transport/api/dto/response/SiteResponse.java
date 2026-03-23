package com.transport.transport.api.dto.response;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SiteResponse extends RepresentationModel<SiteResponse> {

    private Integer id;
    private String nom;
    private String adresse;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Boolean actif;
    private LocalDateTime dateInsertion;
}
