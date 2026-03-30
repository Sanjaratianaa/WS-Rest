package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.AffectationRequest;
import com.transport.transport.api.dto.request.ValidationRequest;
import com.transport.transport.api.dto.response.AffectationResponse;
import com.transport.transport.api.dto.response.AffectationStatsResponse;
import com.transport.transport.api.service.AffectationService;
import com.transport.transport.api.service.TransportOptimisationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Demandes de Transport")
public class AffectationController {

    private final AffectationService service;
    private final TransportOptimisationService optimisationService;

    @GetMapping
    @Operation(
        summary = "Lister les demandes de transport",
        description = "Retourne toutes les demandes de transport. Un ADMIN peut filtrer par date, véhicule, employé, site, statut et département. Un EMPLOYÉ ne voit que ses propres demandes."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des demandes retournée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
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
    @Operation(summary = "Obtenir le détail d'une demande de transport par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Détail de la demande"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (employé ne peut voir que ses propres demandes)"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
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
    @Operation(summary = "Créer une demande de transport", description = "Crée une demande. Pour un employé bénéficiaire, le véhicule est assigné automatiquement. Si la demande est créée après 15h pour le jour même, le traitement est forcé en non-bénéficiaire.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demande créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
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
    @Operation(summary = "Modifier une demande de transport", description = "Modifie une demande existante. Réservé aux ADMIN. Un historique est sauvegardé avant modification.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demande modifiée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<AffectationResponse> update(@PathVariable Integer id, @Valid @RequestBody AffectationRequest request) {
        AffectationResponse response = service.update(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Valider une demande de transport", description = "Valide une demande en attente. Options : corps vide = garder le véhicule actuel, idVehicule = changer de véhicule, reassign = true pour réassignation automatique.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Demande validée"),
            @ApiResponse(responseCode = "400", description = "Demande déjà validée ou véhicule invalide"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)"),
            @ApiResponse(responseCode = "404", description = "Demande non trouvée")
    })
    public ResponseEntity<AffectationResponse> valider(@PathVariable Integer id,
                                                        @RequestBody(required = false) @Valid ValidationRequest request) {
        AffectationResponse response = service.valider(id, request);
        addLinks(response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques globales des transports", description = "Retourne le total des demandes, le taux de validation, et la répartition par département et véhicule.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistiques retournées"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)")
    })
    public ResponseEntity<AffectationStatsResponse> getStats() {
        return ResponseEntity.ok(service.getStats());
    }

    @GetMapping("/optimiser")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Optimiser les transports",
            description = "Groupe les employés par proximité géographique (rayon 2km, Haversine) et optimise les routes par nearest-neighbor. Type 1 = Aller (adresses → site), Type 2 = Retour (site → adresses)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Groupes de transport optimisés"),
            @ApiResponse(responseCode = "403", description = "Accès refusé (ADMIN requis)")
    })
    public ResponseEntity<List<TransportOptimisationService.GroupeTransport>> optimiser(
            @Parameter(description = "Date des demandes à optimiser (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "ID de l'heure de transport", required = true) @RequestParam Integer idHeure,
            @Parameter(description = "ID du type de transport (1=Aller, 2=Retour)", required = true) @RequestParam Integer idTypeTransport
    ) {
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
