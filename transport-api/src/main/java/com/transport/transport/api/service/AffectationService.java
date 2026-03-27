package com.transport.transport.api.service;

import com.transport.transport.api.dto.request.AffectationRequest;
import com.transport.transport.api.dto.request.ValidationRequest;
import com.transport.transport.api.dto.response.AffectationResponse;
import com.transport.transport.api.dto.response.AffectationStatsResponse;
import com.transport.transport.api.entity.*;
import com.transport.transport.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AffectationService {

    private final AffectationRepository repo;
    private final EmployeRepository employeRepo;
    private final AdresseEmployeRepository adresseRepo;
    private final TypeTransportRepository typeTransportRepo;
    private final SiteRepository siteRepo;
    private final VehiculeRepository vehiculeRepo;
    private final HeureTransportRepository heureTransportRepo;
    private final TypeAffectationRepository typeAffectationRepo;
    private final HistoriqueAffectationRepository historiqueRepo;

    public List<AffectationResponse> findWithFilters(LocalDate date, Integer idVehicule,
                                                     Integer idEmploye, Integer idSite,
                                                     Boolean estValidee, Integer idDepartement) {
        return repo.findWithFilters(date, idVehicule, idEmploye, idSite, estValidee, idDepartement)
                .stream().map(this::toResponse).toList();
    }

    public List<AffectationResponse> findByEmploye(Integer idEmploye) {
        return repo.findByEmployeIdAndEstArchiveFalse(idEmploye)
                .stream().map(this::toResponse).toList();
    }

    public AffectationResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation introuvable")));
    }

    @Transactional
    public AffectationResponse create(AffectationRequest request) {
        Employe employe = employeRepo.findById(request.getIdEmploye())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));

        AdresseEmploye adresse = request.getIdAdresse() != null
                ? adresseRepo.findById(request.getIdAdresse())
                .orElseThrow(() -> new RuntimeException("Adresse introuvable"))
                : adresseRepo.findByEmployeIdAndEstPrincipaleTrueAndActifTrue(request.getIdEmploye())
                .orElseThrow(() -> new RuntimeException(
                        "Vous devez avoir une adresse principale pour faire une demande de transport"));

        HeureTransport heureTransport = heureTransportRepo.findById(request.getIdHeureTransport())
                .orElseThrow(() -> new RuntimeException("HeureTransport introuvable"));

        Vehicule vehicule = request.getIdVehicule() != null
                ? vehiculeRepo.findById(request.getIdVehicule())
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"))
                : findVehiculeDisponible(request.getDate(), heureTransport.getId());

        Affectation entity = Affectation.builder()
                .dateTransport(request.getDate())
                .employe(employe)
                .adresse(adresse)
                .typeTransport(typeTransportRepo.findById(request.getIdTypeTransport())
                        .orElseThrow(() -> new RuntimeException("TypeTransport introuvable")))
                .site(siteRepo.findById(request.getIdSite())
                        .orElseThrow(() -> new RuntimeException("Site introuvable")))
                .heureTransport(heureTransport)
                .typeAffectation(typeAffectationRepo.findById(request.getIdType())
                        .orElseThrow(() -> new RuntimeException("TypeAffectation introuvable")))
                .vehicule(vehicule)
                .commentaire(request.getCommentaire())
                .estValidee(null)
                .estArchive(false)
                .dateCreation(LocalDateTime.now())
                .build();

        return toResponse(repo.save(entity));
    }

    @Transactional
    public AffectationResponse update(Integer id, AffectationRequest request) {
        Affectation entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation introuvable"));

        saveHistorique(entity);

        if (request.getDate() != null)
            entity.setDateTransport(request.getDate());
        if (request.getIdEmploye() != null)
            entity.setEmploye(employeRepo.findById(request.getIdEmploye())
                    .orElseThrow(() -> new RuntimeException("Employé introuvable")));
        if (request.getIdAdresse() != null)
            entity.setAdresse(adresseRepo.findById(request.getIdAdresse())
                    .orElseThrow(() -> new RuntimeException("Adresse introuvable")));
        if (request.getIdTypeTransport() != null)
            entity.setTypeTransport(typeTransportRepo.findById(request.getIdTypeTransport())
                    .orElseThrow(() -> new RuntimeException("TypeTransport introuvable")));
        if (request.getIdSite() != null)
            entity.setSite(siteRepo.findById(request.getIdSite())
                    .orElseThrow(() -> new RuntimeException("Site introuvable")));
        if (request.getIdHeureTransport() != null)
            entity.setHeureTransport(heureTransportRepo.findById(request.getIdHeureTransport())
                    .orElseThrow(() -> new RuntimeException("HeureTransport introuvable")));
        if (request.getIdType() != null)
            entity.setTypeAffectation(typeAffectationRepo.findById(request.getIdType())
                    .orElseThrow(() -> new RuntimeException("TypeAffectation introuvable")));
        if (request.getIdVehicule() != null)
            entity.setVehicule(vehiculeRepo.findById(request.getIdVehicule())
                    .orElseThrow(() -> new RuntimeException("Véhicule introuvable")));
        entity.setCommentaire(request.getCommentaire());

        return toResponse(repo.save(entity));
    }

    @Transactional
    public AffectationResponse valider(Integer id, ValidationRequest request) {
        Affectation entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation introuvable"));

        saveHistorique(entity);

        if (request != null && request.getIdVehicule() != null) {
            entity.setVehicule(vehiculeRepo.findById(request.getIdVehicule())
                    .orElseThrow(() -> new RuntimeException("Véhicule introuvable")));
        } else if (request != null && Boolean.TRUE.equals(request.getReassign())) {
            Vehicule vehiculeAuto = findVehiculeDisponible(
                    entity.getDateTransport(), entity.getHeureTransport().getId());
            if (vehiculeAuto == null) {
                throw new RuntimeException("Aucun véhicule disponible pour cette date et heure");
            }
            entity.setVehicule(vehiculeAuto);
        }

        entity.setEstValidee(true);
        entity.setDateValidation(LocalDateTime.now());

        return toResponse(repo.save(entity));
    }

    private Vehicule findVehiculeDisponible(LocalDate date, Integer idHeure) {
        return vehiculeRepo.findByActifTrue().stream()
                .filter(v -> repo.countByVehiculeAndDateAndHeure(v.getId(), date, idHeure) < v.getNombrePlaces())
                .findFirst()
                .orElse(null);
    }

    public AffectationStatsResponse getStats() {
        List<Affectation> all = repo.findByEstArchiveFalse();

        long total = all.size();
        long validees = all.stream().filter(a -> Boolean.TRUE.equals(a.getEstValidee())).count();
        long nonValidees = total - validees;
        double taux = total > 0 ? (double) validees / total * 100 : 0;

        Map<String, Long> parDepartement = all.stream()
                .filter(a -> a.getEmploye() != null && a.getEmploye().getDepartement() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getEmploye().getDepartement().getNom(),
                        Collectors.counting()));

        Map<String, Long> parVehicule = all.stream()
                .filter(a -> a.getVehicule() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getVehicule().getMatricule(),
                        Collectors.counting()));

        return AffectationStatsResponse.builder()
                .totalAffectations(total)
                .totalValidees(validees)
                .totalNonValidees(nonValidees)
                .tauxValidation(taux)
                .parDepartement(parDepartement.entrySet().stream()
                        .map(e -> AffectationStatsResponse.DepartementStat.builder()
                                .nomDepartement(e.getKey())
                                .nbAffectations(e.getValue())
                                .build())
                        .toList())
                .parVehicule(parVehicule.entrySet().stream()
                        .map(e -> AffectationStatsResponse.VehiculeStat.builder()
                                .matriculeVehicule(e.getKey())
                                .nbAffectations(e.getValue())
                                .build())
                        .toList())
                .build();
    }

    private void saveHistorique(Affectation a) {
        HistoriqueAffectation h = HistoriqueAffectation.builder()
                .affectation(a)
                .date(a.getDateTransport() != null ? a.getDateTransport() : null)
                .idEmploye(a.getEmploye() != null ? a.getEmploye().getId() : null)
                .idAdresse(a.getAdresse() != null ? a.getAdresse().getId() : null)
                .idTypeTransport(a.getTypeTransport() != null ? a.getTypeTransport().getId() : null)
                .idSite(a.getSite() != null ? a.getSite().getId() : null)
                .idVehicule(a.getVehicule() != null ? a.getVehicule().getId() : null)
                .idHeureTransport(a.getHeureTransport() != null ? a.getHeureTransport().getId() : null)
                .estValidee(a.getEstValidee())
                .commentaire(a.getCommentaire())
                .dateCreation(a.getDateCreation())
                .dateValidation(a.getDateValidation())
                .idType(a.getTypeAffectation() != null ? a.getTypeAffectation().getId() : null)
                .build();
        historiqueRepo.save(h);
    }

    public AffectationResponse toResponse(Affectation a) {
        return AffectationResponse.builder()
                .id(a.getId())
                .dateJour(a.getDateTransport() != null ? a.getDateTransport() : null)
                .idEmploye(a.getEmploye() != null ? a.getEmploye().getId() : null)
                .nomEmploye(a.getEmploye() != null ? a.getEmploye().getNom() : null)
                .prenomEmploye(a.getEmploye() != null ? a.getEmploye().getPrenom() : null)
                .idAdresse(a.getAdresse() != null ? a.getAdresse().getId() : null)
                .adresse(a.getAdresse() != null ? a.getAdresse().getAdresse() : null)
                .idTypeTransport(a.getTypeTransport() != null ? a.getTypeTransport().getId() : null)
                .libelleTypeTransport(a.getTypeTransport() != null ? a.getTypeTransport().getLibelle() : null)
                .idSite(a.getSite() != null ? a.getSite().getId() : null)
                .nomSite(a.getSite() != null ? a.getSite().getNom() : null)
                .idVehicule(a.getVehicule() != null ? a.getVehicule().getId() : null)
                .matriculeVehicule(a.getVehicule() != null ? a.getVehicule().getMatricule() : null)
                .idHeureTransport(a.getHeureTransport() != null ? a.getHeureTransport().getId() : null)
                .heure(a.getHeureTransport() != null ? a.getHeureTransport().getHeure() : null)
                .libelleHeure(a.getHeureTransport() != null ? a.getHeureTransport().getLibelle() : null)
                .estValidee(a.getEstValidee())
                .commentaire(a.getCommentaire())
                .dateCreation(a.getDateCreation())
                .dateValidation(a.getDateValidation())
                .idType(a.getTypeAffectation() != null ? a.getTypeAffectation().getId() : null)
                .libelleTypeAffectation(a.getTypeAffectation() != null ? a.getTypeAffectation().getLibelle() : null)
                .estArchive(a.getEstArchive())
                .build();
    }
}