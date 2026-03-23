package com.transport.transport.api.service;

import com.transport.transport.api.dto.request.HeureTransportRequest;
import com.transport.transport.api.dto.response.HeureTransportResponse;
import com.transport.transport.api.entity.HeureTransport;
import com.transport.transport.api.repository.HeureTransportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeureTransportService {

    private final HeureTransportRepository repo;

    public List<HeureTransportResponse> findAll() {
        return repo.findByActifTrue().stream().map(this::toResponse).toList();
    }

    public HeureTransportResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("HeureTransport introuvable")));
    }

    @Transactional
    public HeureTransportResponse create(HeureTransportRequest request) {
        HeureTransport entity = HeureTransport.builder()
                .heure(request.getHeure())
                .libelle(request.getLibelle())
                .build();
        return toResponse(repo.save(entity));
    }

    public HeureTransportResponse toResponse(HeureTransport h) {
        return HeureTransportResponse.builder()
                .id(h.getId())
                .heure(h.getHeure())
                .libelle(h.getLibelle())
                .actif(h.getActif())
                .dateInsertion(h.getDateInsertion())
                .build();
    }
}
