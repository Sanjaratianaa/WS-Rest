package com.transport.transport.api.service;

import com.transport.transport.api.dto.request.SiteRequest;
import com.transport.transport.api.dto.response.SiteResponse;
import com.transport.transport.api.entity.Site;
import com.transport.transport.api.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository repo;

    public List<SiteResponse> findAll() {
        return repo.findByActifTrue().stream().map(this::toResponse).toList();
    }

    public SiteResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Site introuvable")));
    }

    @Transactional
    public SiteResponse create(SiteRequest request) {
        Site entity = Site.builder()
                .nom(request.getNom())
                .adresse(request.getAdresse())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        return toResponse(repo.save(entity));
    }

    @Transactional
    public SiteResponse update(Integer id, SiteRequest request) {
        Site entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Site introuvable"));
        entity.setNom(request.getNom());
        entity.setAdresse(request.getAdresse());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        return toResponse(repo.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Site entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Site introuvable"));
        entity.setActif(false);
        repo.save(entity);
    }

    public SiteResponse toResponse(Site s) {
        return SiteResponse.builder()
                .id(s.getId())
                .nom(s.getNom())
                .adresse(s.getAdresse())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .actif(s.getActif())
                .dateInsertion(s.getDateInsertion())
                .build();
    }
}
