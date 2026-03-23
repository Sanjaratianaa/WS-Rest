package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.EmployeRequest;
import com.transport.transport.api.dto.response.EmployeResponse;
import com.transport.transport.api.service.EmployeService;
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
@RequestMapping("/api/employes")
@RequiredArgsConstructor
@Tag(name = "Employés")
public class EmployeController {

    private final EmployeService service;

    @GetMapping
    @Operation(summary = "Lister tous les employés actifs")
    public ResponseEntity<CollectionModel<EmployeResponse>> findAll() {
        List<EmployeResponse> list = service.findAll();
        list.forEach(this::addLinks);
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(EmployeController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un employé par ID")
    public ResponseEntity<EmployeResponse> findById(@PathVariable Integer id) {
        EmployeResponse response = service.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/departement/{idDepartement}")
    @Operation(summary = "Lister les employés d'un département")
    public ResponseEntity<CollectionModel<EmployeResponse>> findByDepartement(@PathVariable Integer idDepartement) {
        List<EmployeResponse> list = service.findByDepartement(idDepartement);
        list.forEach(this::addLinks);
        return ResponseEntity.ok(CollectionModel.of(list));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un employé")
    public ResponseEntity<EmployeResponse> create(@Valid @RequestBody EmployeRequest request) {
        EmployeResponse response = service.create(request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un employé")
    public ResponseEntity<EmployeResponse> update(@PathVariable Integer id, @Valid @RequestBody EmployeRequest request) {
        EmployeResponse response = service.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un employé")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinks(EmployeResponse r) {
        r.add(linkTo(methodOn(EmployeController.class).findById(r.getId())).withSelfRel());
        r.add(linkTo(methodOn(EmployeController.class).findAll()).withRel("employes"));
        if (r.getIdDepartement() != null) {
            r.add(linkTo(methodOn(DepartementController.class).findById(r.getIdDepartement())).withRel("departement"));
        }
    }
}
