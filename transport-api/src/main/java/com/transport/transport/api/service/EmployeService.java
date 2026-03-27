package com.transport.transport.api.service;

import com.transport.transport.api.dto.request.EmployeRequest;
import com.transport.transport.api.dto.response.EmployeResponse;
import com.transport.transport.api.entity.Employe;
import com.transport.transport.api.repository.DepartementRepository;
import com.transport.transport.api.repository.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final EmployeRepository repo;
    private final DepartementRepository departementRepo;

    public List<EmployeResponse> findAll() {
        return repo.findByActifTrue().stream().map(this::toResponse).toList();
    }

    public EmployeResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé introuvable")));
    }

    public List<EmployeResponse> findByDepartement(Integer idDepartement) {
        return repo.findByDepartementIdAndActifTrue(idDepartement).stream().map(this::toResponse).toList();
    }

    @Transactional
    public EmployeResponse create(EmployeRequest request) {
        String matricule = genererMatricule("EMP");
        Employe entity = Employe.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .matricule(matricule)
                .telephone(request.getTelephone())
                .estBeneficiaire(request.getEstBeneficiaireTransport())
                .build();

        if (request.getIdDepartement() != null) {
            entity.setDepartement(departementRepo.findById(request.getIdDepartement())
                    .orElseThrow(() -> new RuntimeException("Département introuvable")));
        }

        return toResponse(repo.save(entity));
    }

    @Transactional
    public EmployeResponse update(Integer id, EmployeRequest request) {
        Employe entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        entity.setNom(request.getNom());
        entity.setPrenom(request.getPrenom());
        entity.setTelephone(request.getTelephone());
        entity.setEstBeneficiaire(request.getEstBeneficiaireTransport());
        
        if (request.getIdDepartement() != null) {
            entity.setDepartement(departementRepo.findById(request.getIdDepartement())
                    .orElseThrow(() -> new RuntimeException("Département introuvable")));
        }

        return toResponse(repo.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Employe entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        entity.setActif(false);
        entity.setDateDesactivation(LocalDateTime.now());
        repo.save(entity);
    }

    public EmployeResponse toResponse(Employe e) {
        return EmployeResponse.builder()
                .id(e.getId())
                .nom(e.getNom())
                .prenom(e.getPrenom())
                .matricule(e.getMatricule())
                .telephone(e.getTelephone())
                .idDepartement(e.getDepartement() != null ? e.getDepartement().getId() : null)
                .nomDepartement(e.getDepartement() != null ? e.getDepartement().getNom() : null)
                .actif(e.getActif())
                .estBeneficiareTransport(e.getEstBeneficiaire())
                .dateInsertion(e.getDateInsertion())
                .dateDesactivation(e.getDateDesactivation())
                .build();
    }

    private String genererMatricule(String prefix) {
        // Compte le nombre d'employés avec ce prefix
        long count = repo.countByMatriculeStartingWith(prefix);
        // Génère le prochain numéro avec padding (EMP001, EMP002, ...)
        return String.format("%s%03d", prefix, count + 1);
    }
}
