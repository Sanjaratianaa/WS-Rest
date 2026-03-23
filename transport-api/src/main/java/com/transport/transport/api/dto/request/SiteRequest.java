package com.transport.transport.api.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SiteRequest {

    private String nom;
    private String adresse;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
