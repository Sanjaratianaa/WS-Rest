package com.transport.transport.api.service;

import com.transport.transport.api.dto.request.VehiculeRequest;
import com.transport.transport.api.dto.response.VehiculeResponse;
import com.transport.transport.api.dto.response.VehiculeTrajetResponse;
import com.transport.transport.api.entity.Affectation;
import com.transport.transport.api.entity.Vehicule;
import com.transport.transport.api.repository.AffectationRepository;
import com.transport.transport.api.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehiculeService {

    private final VehiculeRepository repo;
    private final AffectationRepository affectationRepo;

    public List<VehiculeResponse> findAll() {
        return repo.findByActifTrue().stream().map(this::toResponse).toList();
    }

    public VehiculeResponse findById(Integer id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable")));
    }

    @Transactional
    public VehiculeResponse create(VehiculeRequest request) {
        Vehicule entity = Vehicule.builder()
                .matricule(request.getMatricule())
                .nombrePlaces(request.getNombrePlaces())
                .build();
        return toResponse(repo.save(entity));
    }

    @Transactional
    public VehiculeResponse update(Integer id, VehiculeRequest request) {
        Vehicule entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        entity.setMatricule(request.getMatricule());
        entity.setNombrePlaces(request.getNombrePlaces());
        return toResponse(repo.save(entity));
    }

    @Transactional
    public void delete(Integer id) {
        Vehicule entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));
        entity.setActif(false);
        repo.save(entity);
    }

    public VehiculeTrajetResponse getTrajets(Integer idVehicule) {
        Vehicule vehicule = repo.findById(idVehicule)
                .orElseThrow(() -> new RuntimeException("Véhicule introuvable"));

        List<Affectation> affectations = affectationRepo.findByVehiculeIdAndEstArchiveFalse(idVehicule);

        // Group by date + heure
        Map<String, List<Affectation>> grouped = affectations.stream()
                .collect(Collectors.groupingBy(a -> {
                    String dateKey = a.getDateTransport() != null ? a.getDateTransport().getDateJour().toString() : "null";
                    String heureKey = a.getHeureTransport() != null ? a.getHeureTransport().getId().toString() : "null";
                    return dateKey + "|" + heureKey;
                }));

        List<VehiculeTrajetResponse.TrajetGroupe> trajets = grouped.entrySet().stream().map(entry -> {
            List<Affectation> group = entry.getValue();
            Affectation first = group.get(0);

            List<VehiculeTrajetResponse.Passager> passagers = group.stream()
                    .filter(a -> a.getEmploye() != null)
                    .map(a -> VehiculeTrajetResponse.Passager.builder()
                            .idEmploye(a.getEmploye().getId())
                            .nom(a.getEmploye().getNom())
                            .prenom(a.getEmploye().getPrenom())
                            .adresse(a.getAdresse() != null ? a.getAdresse().getAdresse() : null)
                            .build())
                    .toList();

            return VehiculeTrajetResponse.TrajetGroupe.builder()
                    .date(first.getDateTransport() != null ? first.getDateTransport().getDateJour() : null)
                    .heure(first.getHeureTransport() != null ? first.getHeureTransport().getHeure() : null)
                    .libelleHeure(first.getHeureTransport() != null ? first.getHeureTransport().getLibelle() : null)
                    .nomSite(first.getSite() != null ? first.getSite().getNom() : null)
                    .libelleTypeTransport(first.getTypeTransport() != null ? first.getTypeTransport().getLibelle() : null)
                    .passagers(passagers)
                    .build();
        }).toList();

        return VehiculeTrajetResponse.builder()
                .idVehicule(vehicule.getId())
                .matriculeVehicule(vehicule.getMatricule())
                .nombrePlaces(vehicule.getNombrePlaces())
                .trajets(trajets)
                .build();
    }

    public VehiculeResponse toResponse(Vehicule v) {
        return VehiculeResponse.builder()
                .id(v.getId())
                .matricule(v.getMatricule())
                .nombrePlaces(v.getNombrePlaces())
                .actif(v.getActif())
                .dateInsertion(v.getDateInsertion())
                .build();
    }
}
