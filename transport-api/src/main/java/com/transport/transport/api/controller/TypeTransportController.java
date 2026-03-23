package com.transport.transport.api.controller;

import com.transport.transport.api.dto.response.TypeTransportResponse;
import com.transport.transport.api.service.TypeTransportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/types-transport")
@RequiredArgsConstructor
@Tag(name = "Types de Transport")
public class TypeTransportController {

    private final TypeTransportService service;

    @GetMapping
    @Operation(summary = "Lister tous les types de transport actifs")
    public ResponseEntity<CollectionModel<TypeTransportResponse>> findAll() {
        List<TypeTransportResponse> list = service.findAll();
        list.forEach(r -> r.add(linkTo(methodOn(TypeTransportController.class).findById(r.getId())).withSelfRel()));
        return ResponseEntity.ok(CollectionModel.of(list,
                linkTo(methodOn(TypeTransportController.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un type de transport par ID")
    public ResponseEntity<TypeTransportResponse> findById(@PathVariable Integer id) {
        TypeTransportResponse response = service.findById(id);
        response.add(linkTo(methodOn(TypeTransportController.class).findById(id)).withSelfRel());
        return ResponseEntity.ok(response);
    }
}
