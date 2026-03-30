package com.transport.transport.api.controller;

import com.transport.transport.api.service.TestDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Test Data", description = "Endpoints de génération de données de test")
public class TestDataController {

    private final TestDataService testDataService;

    @PostMapping("/generer")
    @Operation(
        summary = "Générer 100 demandes de transport",
        description = "Génère 100 agents avec adresses GPS autour d'Antananarivo + demandes de transport Aller/Retour"
    )
    public ResponseEntity<String> generer() {
        int nbCreés = testDataService.genererDonneesTest();
        return ResponseEntity.ok(nbCreés + " demandes de transport générées avec succès !");
    }
}