package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.AffectationRequest;
import com.transport.transport.api.dto.request.ValidationRequest;
import com.transport.transport.api.dto.response.AffectationResponse;
import com.transport.transport.api.dto.response.AffectationStatsResponse;
import com.transport.transport.api.service.AffectationService;
import com.transport.transport.api.service.TransportOptimisationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/demandeTransports")
@RequiredArgsConstructor
@Tag(name = "DemandeTransports")
public class AffectationController {

    private final AffectationService service;
    private final TransportOptimisationService optimisationService;

    @GetMapping
    @Operation(
        summary = "Lister les demandes de transport",
        description = "Retourne toutes les demandes de transport. Un ADMIN peut filtrer par date, véhicule, employé, site, statut et département. Un EMPLOYÉ ne voit que ses propres demandes."
    )
    public ResponseEntity<CollectionModel<AffectationResponse>> findAll(
            @Parameter(description = "Filtrer par date (YYYY-MM-DD)", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Filtrer par véhicule", required = false) @RequestParam(required = false) Integer idVehicule,
            @Parameter(description = "Filtrer par employé", required = false) @RequestParam(required = false) Integer idEmploye,
            @Parameter(description = "Filtrer par site", required = false) @RequestParam(required = false) Integer idSite,
            @Parameter(description = "Filtrer par statut de validation", required = false) @RequestParam(required = false) Boolean estValidee,
            @Parameter(description = "Filtrer par département", required = false) @RequestParam(required = false) Integer idDepartement,
            @Parameter(hidden = true) Authentication authentication) {

        List<AffectationResponse> list;

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            Integer currentEmployeId = (Integer) authentication.getDetails();
            list = service.findWithFilters(date, idVehicule, currentEmployeId, idSite, estValidee, null);
        } else {
            list = service.findWithFilters(date, idVehicule, idEmploye, idSite, estValidee, idDepartement);
        }

        list.forEach(this::addLinks);
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(AffectationController.class).findAll(null, null, null, null, null, null, null)).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un détail d'une demande de transport par ID")
    public ResponseEntity<AffectationResponse> findById(@PathVariable Integer id, @Parameter(hidden = true) Authentication authentication) {
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
    @Operation(summary = "Créer une demande de transport")
    public ResponseEntity<AffectationResponse> create(@Valid @RequestBody AffectationRequest request,
                                                       @Parameter(hidden = true) Authentication authentication) {
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
    @Operation(summary = "Modifier une demande de transport")
    public ResponseEntity<AffectationResponse> update(@PathVariable Integer id, @Valid @RequestBody AffectationRequest request) {
        AffectationResponse response = service.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Valider un demande de transport (optionnel: changer véhicule ou reassign auto)")
    public ResponseEntity<AffectationResponse> valider(@PathVariable Integer id,
                                                        @RequestBody(required = false) @Valid ValidationRequest request) {
        AffectationResponse response = service.valider(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques globals des transports")
    public ResponseEntity<AffectationStatsResponse> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    @GetMapping("/optimiser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransportOptimisationService.GroupeTransportResponse>> optimiser(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer idHeure,
            @RequestParam Integer idTypeTransport) {
        return ResponseEntity.ok(optimisationService.optimiser(date, idHeure, idTypeTransport));
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
