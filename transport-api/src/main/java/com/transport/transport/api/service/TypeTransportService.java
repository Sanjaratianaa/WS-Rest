package com.transport.transport.api.service;

import com.transport.transport.api.dto.response.TypeTransportResponse;
import com.transport.transport.api.entity.TypeTransport;
import com.transport.transport.api.repository.TypeTransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeTransportService {

    private final TypeTransportRepository repo;

    public List<TypeTransportResponse> findAll() {
        return repo.findByActifTrue().stream().map(this::toResponse).toList();
    }

    public TypeTransportResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("TypeTransport introuvable")));
    }

    public TypeTransportResponse toResponse(TypeTransport t) {
        return TypeTransportResponse.builder()
                .id(t.getId())
                .libelle(t.getLibelle())
                .actif(t.getActif())
                .build();
    }
}
