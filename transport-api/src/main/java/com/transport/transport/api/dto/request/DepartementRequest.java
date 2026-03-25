package com.transport.transport.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DepartementRequest {

    @NotBlank(message = "Le nom du département est obligatoire.")
    @Size(max = 100, message = "Le nom du département ne doit pas dépasser 100 caractères.")
    private String nom;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères.")
    private String description;
}
