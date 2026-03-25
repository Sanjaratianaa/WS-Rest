package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.VehiculeRequest;
import com.transport.transport.api.dto.response.VehiculeResponse;
import com.transport.transport.api.dto.response.VehiculeTrajetResponse;
import com.transport.transport.api.service.VehiculeService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules")
public class VehiculeController {

    private final VehiculeService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les véhicules actifs")
    public ResponseEntity<CollectionModel<VehiculeResponse>> findAll() {
        List<VehiculeResponse> list = service.findAll();
        list.forEach(this::addLinks);
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(VehiculeController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un véhicule par ID")
    public ResponseEntity<VehiculeResponse> findById(@PathVariable Integer id) {
        VehiculeResponse response = service.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/trajets")
    @Operation(summary = "Agrégation : véhicule + passagers groupés par date/heure")
    public ResponseEntity<VehiculeTrajetResponse> getTrajets(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getTrajets(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un véhicule")
    public ResponseEntity<VehiculeResponse> create(@Valid @RequestBody VehiculeRequest request) {
        VehiculeResponse response = service.create(request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un véhicule")
    public ResponseEntity<VehiculeResponse> update(@PathVariable Integer id, @Valid @RequestBody VehiculeRequest request) {
        VehiculeResponse response = service.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un véhicule")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinks(VehiculeResponse r) {
        r.add(linkTo(methodOn(VehiculeController.class).findById(r.getId())).withSelfRel());
        r.add(linkTo(methodOn(VehiculeController.class).getTrajets(r.getId())).withRel("trajets"));
        r.add(linkTo(methodOn(VehiculeController.class).findAll()).withRel("vehicules"));
    }
}
