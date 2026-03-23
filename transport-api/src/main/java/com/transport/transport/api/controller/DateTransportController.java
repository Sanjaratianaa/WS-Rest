package com.transport.transport.api.controller;

import com.transport.transport.api.dto.request.DateTransportRequest;
import com.transport.transport.api.dto.response.DateTransportResponse;
import com.transport.transport.api.service.DateTransportService;
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
@RequestMapping("/api/dates-transport")
@RequiredArgsConstructor
@Tag(name = "Dates de Transport")
public class DateTransportController {

    private final DateTransportService service;

    @GetMapping
    @Operation(summary = "Lister toutes les dates de transport actives")
    public ResponseEntity<CollectionModel<DateTransportResponse>> findAll() {
        List<DateTransportResponse> list = service.findAll();
        list.forEach(r -> r.add(linkTo(methodOn(DateTransportController.class).findById(r.getId())).withSelfRel()));
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(DateTransportController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une date de transport par ID")
    public ResponseEntity<DateTransportResponse> findById(@PathVariable Integer id) {
        DateTransportResponse response = service.findById(id);
        response.add(linkTo(methodOn(DateTransportController.class).findById(id)).withSelfRel());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une date de transport")
    public ResponseEntity<DateTransportResponse> create(@Valid @RequestBody DateTransportRequest request) {
        DateTransportResponse response = service.create(request);
        response.add(linkTo(methodOn(DateTransportController.class).findById(response.getId())).withSelfRel());
        return ResponseEntity.ok(response);
    }
}
