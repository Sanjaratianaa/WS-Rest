package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.HeureTransportRequest;
import com.transport.transport.api.dto.response.HeureTransportResponse;
import com.transport.transport.api.service.HeureTransportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/heures-transport")
@RequiredArgsConstructor
@Tag(name = "Heures de Transport")
public class HeureTransportController {

    private final HeureTransportService service;

    @GetMapping
    @Operation(summary = "Lister toutes les heures de transport actives")
    @ApiResponse(responseCode = "200", description = "Liste des heures de transport actives")
    public ResponseEntity<CollectionModel<HeureTransportResponse>> findAll() {
        List<HeureTransportResponse> list = service.findAll();
        list.forEach(r -> r.add(linkTo(methodOn(HeureTransportController.class).findById(r.getId())).withSelfRel()));
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(HeureTransportController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une heure de transport par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Heure de transport trouvée"),
            @ApiResponse(responseCode = "404", description = "Heure de transport non trouvée")
    })
    public ResponseEntity<HeureTransportResponse> findById(@PathVariable Integer id) {
        HeureTransportResponse response = service.findById(id);
        response.add(linkTo(methodOn(HeureTransportController.class).findById(id)).withSelfRel());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une heure de transport")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Heure de transport créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)")
    })
    public ResponseEntity<HeureTransportResponse> create(@Valid @RequestBody HeureTransportRequest request) {
        HeureTransportResponse response = service.create(request);
        response.add(linkTo(methodOn(HeureTransportController.class).findById(response.getId())).withSelfRel());
        return ResponseEntity.ok(response);
    }
}
