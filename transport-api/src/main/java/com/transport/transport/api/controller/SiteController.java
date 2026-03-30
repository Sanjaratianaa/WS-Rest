package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.SiteRequest;
import com.transport.transport.api.dto.response.SiteResponse;
import com.transport.transport.api.service.SiteService;
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
@RequestMapping("/api/sites")
@RequiredArgsConstructor
@Tag(name = "Sites")
public class SiteController {

    private final SiteService service;

    @GetMapping
    @Operation(summary = "Lister tous les sites actifs")
    @ApiResponse(responseCode = "200", description = "Liste des sites actifs")
    public ResponseEntity<CollectionModel<SiteResponse>> findAll() {
        List<SiteResponse> list = service.findAll();
        list.forEach(this::addLinks);
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(SiteController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un site par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Site trouvé"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<SiteResponse> findById(@PathVariable Integer id) {
        SiteResponse response = service.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un site")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Site créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)")
    })
    public ResponseEntity<SiteResponse> create(@Valid @RequestBody SiteRequest request) {
        SiteResponse response = service.create(request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un site")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Site modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<SiteResponse> update(@PathVariable Integer id, @Valid @RequestBody SiteRequest request) {
        SiteResponse response = service.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un site", description = "Désactivation logique (soft delete).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Site désactivé"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)"),
            @ApiResponse(responseCode = "404", description = "Site non trouvé")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinks(SiteResponse r) {
        r.add(linkTo(methodOn(SiteController.class).findById(r.getId())).withSelfRel());
        r.add(linkTo(methodOn(SiteController.class).findAll()).withRel("sites"));
    }
}
