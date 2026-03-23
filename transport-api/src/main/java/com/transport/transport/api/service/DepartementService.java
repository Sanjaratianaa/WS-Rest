package com.transport.transport.api.service;

import com.transport.transport.api.dto.request.DepartementRequest;
import com.transport.transport.api.dto.response.DepartementResponse;
import com.transport.transport.api.entity.Departement;
import com.transport.transport.api.repository.DepartementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartementService {

    private final DepartementRepository repo;

    public List<DepartementResponse> findAll() {
        return repo.findByActifTrue().stream().map(this::toResponse).toList();
    }

    public DepartementResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Département introuvable")));
    }

    @Transactional
    public DepartementResponse create(DepartementRequest request) {
        Departement entity = Departement.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .build();
        return toResponse(repo.save(entity));
    }

    @Transactional
    public DepartementResponse update(Integer id, DepartementRequest request) {
        Departement entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Département introuvable"));
        entity.setNom(request.getNom());
        entity.setDescription(request.getDescription());
        return toResponse(repo.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Departement entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Département introuvable"));
        entity.setActif(false);
        entity.setDateDesactivation(LocalDateTime.now());
        repo.save(entity);
    }

    public DepartementResponse toResponse(Departement d) {
        return DepartementResponse.builder()
                .id(d.getId())
                .nom(d.getNom())
                .description(d.getDescription())
                .actif(d.getActif())
                .dateInsertion(d.getDateInsertion())
                .dateDesactivation(d.getDateDesactivation())
                .build();
    }
}
