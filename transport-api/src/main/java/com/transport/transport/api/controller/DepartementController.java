package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.DepartementRequest;
import com.transport.transport.api.dto.response.DepartementResponse;
import com.transport.transport.api.service.DepartementService;
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
@RequestMapping("/api/departements")
@RequiredArgsConstructor
@Tag(name = "Départements")
public class DepartementController {

    private final DepartementService service;

    @GetMapping
    @Operation(summary = "Lister tous les départements actifs")
    @ApiResponse(responseCode = "200", description = "Liste des départements actifs")
    public ResponseEntity<CollectionModel<DepartementResponse>> findAll() {
        List<DepartementResponse> list = service.findAll();
        list.forEach(this::addLinks);
        CollectionModel<DepartementResponse> model = CollectionModel.of(list,
                linkTo(methodOn(DepartementController.class).findAll()).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un département par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Département trouvé"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé")
    })
    public ResponseEntity<DepartementResponse> findById(@PathVariable Integer id) {
        DepartementResponse response = service.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un département")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Département créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)")
    })
    public ResponseEntity<DepartementResponse> create(@Valid @RequestBody DepartementRequest request) {
        DepartementResponse response = service.create(request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un département")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Département modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé")
    })
    public ResponseEntity<DepartementResponse> update(@PathVariable Integer id, @Valid @RequestBody DepartementRequest request) {
        DepartementResponse response = service.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un département", description = "Désactivation logique (soft delete).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Département désactivé"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)"),
            @ApiResponse(responseCode = "404", description = "Département non trouvé")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinks(DepartementResponse r) {
        r.add(linkTo(methodOn(DepartementController.class).findById(r.getId())).withSelfRel());
        r.add(linkTo(methodOn(DepartementController.class).findAll()).withRel("departements"));
    }
}
