package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.AdresseEmployeRequest;
import com.transport.transport.api.dto.response.AdresseEmployeResponse;
import com.transport.transport.api.service.AdresseEmployeService;
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
@RequestMapping("/api/adresses")
@RequiredArgsConstructor
@Tag(name = "Adresses Employés")
public class AdresseEmployeController {

    private final AdresseEmployeService service;

    @GetMapping("/employe/{idEmploye}")
    @Operation(summary = "Lister les adresses d'un employé")
    public ResponseEntity<CollectionModel<AdresseEmployeResponse>> findByEmploye(@PathVariable Integer idEmploye) {
        List<AdresseEmployeResponse> list = service.findByEmploye(idEmploye);
        list.forEach(this::addLinks);
        return ResponseEntity.ok(CollectionModel.of(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une adresse par ID")
    public ResponseEntity<AdresseEmployeResponse> findById(@PathVariable Integer id) {
        AdresseEmployeResponse response = service.findById(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Créer une adresse")
    public ResponseEntity<AdresseEmployeResponse> create(@Valid @RequestBody AdresseEmployeRequest request) {
        AdresseEmployeResponse response = service.create(request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une adresse")
    public ResponseEntity<AdresseEmployeResponse> update(@PathVariable Integer id, @Valid @RequestBody AdresseEmployeRequest request) {
        AdresseEmployeResponse response = service.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver une adresse")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinks(AdresseEmployeResponse r) {
        r.add(linkTo(methodOn(AdresseEmployeController.class).findById(r.getId())).withSelfRel());
        if (r.getIdEmploye() != null) {
            r.add(linkTo(methodOn(EmployeController.class).findById(r.getIdEmploye())).withRel("employe"));
        }
    }
}
