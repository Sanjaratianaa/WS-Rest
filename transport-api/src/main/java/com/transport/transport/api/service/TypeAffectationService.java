package com.transport.transport.api.service;

import com.transport.transport.api.dto.response.TypeAffectationResponse;
import com.transport.transport.api.entity.TypeAffectation;
import com.transport.transport.api.repository.TypeAffectationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeAffectationService {

    private final TypeAffectationRepository repo;

    public List<TypeAffectationResponse> findAll() {
        return repo.findByActifTrue().stream().map(this::toResponse).toList();
    }

    public TypeAffectationResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("TypeAffectation introuvable")));
    }

    public TypeAffectationResponse toResponse(TypeAffectation t) {
        return TypeAffectationResponse.builder()
                .id(t.getId())
                .libelle(t.getLibelle())
                .actif(t.getActif())
                .build();
    }
}
