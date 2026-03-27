package com.transport.transport.api.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {

    private String token;
    private String matricule;
    private String role;
    private Integer idEmploye;
    private String nomComplet;
}
