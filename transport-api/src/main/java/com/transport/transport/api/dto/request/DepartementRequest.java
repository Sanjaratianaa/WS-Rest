package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DepartementRequest {

    @NotBlank
    private String nom;

    private String description;
}
