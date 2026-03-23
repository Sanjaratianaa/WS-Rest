package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.DepartementRequest;
import com.transport.transport.api.dto.response.DepartementResponse;
import com.transport.transport.api.service.DepartementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
    public ResponseEntity<CollectionModel<DepartementResponse>> findAll() {
        List<DepartementResponse> list = service.findAll();
        list.forEach(this::addLinks);
        CollectionModel<DepartementResponse> model = CollectionModel.of(list,
                linkTo(methodOn(DepartementController.class).findAll()).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un département par ID")
    public ResponseEntity<DepartementResponse> findById(@PathVariable Integer id) {
        DepartementResponse response = service.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un département")
    public ResponseEntity<DepartementResponse> create(@Valid @RequestBody DepartementRequest request) {
        DepartementResponse response = service.create(request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un département")
    public ResponseEntity<DepartementResponse> update(@PathVariable Integer id, @Valid @RequestBody DepartementRequest request) {
        DepartementResponse response = service.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un département")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinks(DepartementResponse r) {
        r.add(linkTo(methodOn(DepartementController.class).findById(r.getId())).withSelfRel());
        r.add(linkTo(methodOn(DepartementController.class).findAll()).withRel("departements"));
    }
}
