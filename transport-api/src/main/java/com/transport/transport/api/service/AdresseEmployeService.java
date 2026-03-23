package com.transport.transport.api.service;

import com.transport.transport.api.dto.request.AdresseEmployeRequest;
import com.transport.transport.api.dto.response.AdresseEmployeResponse;
import com.transport.transport.api.entity.AdresseEmploye;
import com.transport.transport.api.repository.AdresseEmployeRepository;
import com.transport.transport.api.repository.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdresseEmployeService {

    private final AdresseEmployeRepository repo;
    private final EmployeRepository employeRepo;

    public List<AdresseEmployeResponse> findByEmploye(Integer idEmploye) {
        return repo.findByEmployeIdAndActifTrue(idEmploye).stream().map(this::toResponse).toList();
    }

    public AdresseEmployeResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Adresse introuvable")));
    }

    @Transactional
    public AdresseEmployeResponse create(AdresseEmployeRequest request) {
        AdresseEmploye entity = AdresseEmploye.builder()
                .employe(employeRepo.findById(request.getIdEmploye())
                        .orElseThrow(() -> new RuntimeException("Employé introuvable")))
                .adresse(request.getAdresse())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .estPrincipale(request.getEstPrincipale() != null ? request.getEstPrincipale() : false)
                .build();

        return toResponse(repo.save(entity));
    }

    @Transactional
    public AdresseEmployeResponse update(Integer id, AdresseEmployeRequest request) {
        AdresseEmploye entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Adresse introuvable"));
        entity.setAdresse(request.getAdresse());
        entity.setLatitude(request.getLatitude());
        entity.setLongitude(request.getLongitude());
        entity.setEstPrincipale(request.getEstPrincipale());
        return toResponse(repo.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        AdresseEmploye entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Adresse introuvable"));
        entity.setActif(false);
        repo.save(entity);
    }

    public AdresseEmployeResponse toResponse(AdresseEmploye a) {
        return AdresseEmployeResponse.builder()
                .id(a.getId())
                .idEmploye(a.getEmploye() != null ? a.getEmploye().getId() : null)
                .nomEmploye(a.getEmploye() != null ? a.getEmploye().getNom() + " " + a.getEmploye().getPrenom() : null)
                .adresse(a.getAdresse())
                .latitude(a.getLatitude())
                .longitude(a.getLongitude())
                .estPrincipale(a.getEstPrincipale())
                .actif(a.getActif())
                .dateInsertion(a.getDateInsertion())
                .build();
    }
}
