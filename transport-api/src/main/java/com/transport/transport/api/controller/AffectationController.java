package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.AffectationRequest;
import com.transport.transport.api.dto.response.AffectationResponse;
import com.transport.transport.api.dto.response.AffectationStatsResponse;
import com.transport.transport.api.service.AffectationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/affectations")
@RequiredArgsConstructor
@Tag(name = "Affectations")
public class AffectationController {

    private final AffectationService service;

    @GetMapping
    @Operation(summary = "Lister les affectations avec filtres optionnels")
    public ResponseEntity<CollectionModel<AffectationResponse>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer idVehicule,
            @RequestParam(required = false) Integer idEmploye,
            @RequestParam(required = false) Integer idSite,
            @RequestParam(required = false) Boolean estValidee,
            @RequestParam(required = false) Integer idDepartement,
            Authentication authentication) {

        List<AffectationResponse> list;

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            Integer currentEmployeId = (Integer) authentication.getDetails();
            list = service.findByEmploye(currentEmployeId);
        } else {
            list = service.findWithFilters(date, idVehicule, idEmploye, idSite, estValidee, idDepartement);
        }

        list.forEach(this::addLinks);
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(AffectationController.class).findAll(null, null, null, null, null, null, null)).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une affectation par ID")
    public ResponseEntity<AffectationResponse> findById(@PathVariable Integer id, Authentication authentication) {
        AffectationResponse response = service.findById(id);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Integer currentEmployeId = (Integer) authentication.getDetails();

        if (!isAdmin && !response.getIdEmploye().equals(currentEmployeId)) {
            return ResponseEntity.status(403).build();
        }

        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Créer une demande d'affectation")
    public ResponseEntity<AffectationResponse> create(@Valid @RequestBody AffectationRequest request,
                                                       Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Integer currentEmployeId = (Integer) authentication.getDetails();

        if (!isAdmin) {
            request.setIdEmploye(currentEmployeId);
        }

        AffectationResponse response = service.create(request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une affectation")
    public ResponseEntity<AffectationResponse> update(@PathVariable Integer id, @Valid @RequestBody AffectationRequest request) {
        AffectationResponse response = service.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Valider une affectation (affectation automatique de véhicule si disponible)")
    public ResponseEntity<AffectationResponse> valider(@PathVariable Integer id) {
        AffectationResponse response = service.valider(id);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques des affectations")
    public ResponseEntity<AffectationStatsResponse> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    private void addLinks(AffectationResponse r) {
        r.add(linkTo(methodOn(AffectationController.class).findById(r.getId(), null)).withSelfRel());
        r.add(linkTo(methodOn(AffectationController.class).findAll(null, null, null, null, null, null, null)).withRel("affectations"));
        if (r.getIdEmploye() != null) {
            r.add(linkTo(methodOn(EmployeController.class).findById(r.getIdEmploye())).withRel("employe"));
        }
        if (r.getIdVehicule() != null) {
            r.add(linkTo(methodOn(VehiculeController.class).findById(r.getIdVehicule())).withRel("vehicule"));
        }
        if (r.getIdSite() != null) {
            r.add(linkTo(methodOn(SiteController.class).findById(r.getIdSite())).withRel("site"));
        }
    }
}
