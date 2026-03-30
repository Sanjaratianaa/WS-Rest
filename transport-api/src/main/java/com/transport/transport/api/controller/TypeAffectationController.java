package com.transport.transport.api.controller;

import com.transport.transport.api.dto.response.TypeAffectationResponse;
import com.transport.transport.api.service.TypeAffectationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/types-affectation")
@RequiredArgsConstructor
@Tag(name = "Types d'Affectation")
public class TypeAffectationController {

    private final TypeAffectationService service;

    @GetMapping
    @Operation(summary = "Lister tous les types d'affectation actifs")
    @ApiResponse(responseCode = "200", description = "Liste des types d'affectation actifs")
    public ResponseEntity<CollectionModel<TypeAffectationResponse>> findAll() {
        List<TypeAffectationResponse> list = service.findAll();
        list.forEach(r -> r.add(linkTo(methodOn(TypeAffectationController.class).findById(r.getId())).withSelfRel()));
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(TypeAffectationController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un type d'affectation par ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Type d'affectation trouvé"),
            @ApiResponse(responseCode = "404", description = "Type d'affectation non trouvé")
    })
    public ResponseEntity<TypeAffectationResponse> findById(@PathVariable Integer id) {
        TypeAffectationResponse response = service.findById(id);
        response.add(linkTo(methodOn(TypeAffectationController.class).findById(id)).withSelfRel());
        return ResponseEntity.ok(response);
    }
}
