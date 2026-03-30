package com.transport.transport.api.service;

import com.transport.transport.api.entity.*;
import com.transport.transport.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TestDataService {

    private final EmployeRepository employeRepo;
    private final AdresseEmployeRepository adresseRepo;
    private final AuthentificationRepository authRepo;
    private final AffectationRepository affectationRepo;
    private final HeureTransportRepository heureTransportRepo;
    private final TypeTransportRepository typeTransportRepo;
    private final SiteRepository siteRepo;
    private final VehiculeRepository vehiculeRepo;
    private final TypeAffectationRepository typeAffectationRepo;
    private final RoleRepository roleRepo;
    private final DepartementRepository departementRepo;

    // ============================================================
    // COORDONNÉES GPS RÉELLES AUTOUR D'ANTANANARIVO
    // ============================================================
    private static final double[][] COORDS_ANTANANARIVO = {
        // Analakely
        {-18.9137, 47.5361},
        // Behoririka
        {-18.9200, 47.5320},
        // Faravohitra
        {-18.9050, 47.5280},
        // Ambohijatovo
        {-18.9180, 47.5290},
        // Antanimena
        {-18.9100, 47.5310},
        // Tsaralalana
        {-18.9120, 47.5370},
        // Ankadifotsy
        {-18.9250, 47.5350},
        // Ambatonakanga
        {-18.9090, 47.5260},
        // Isotry
        {-18.9230, 47.5290},
        // Mahamasina
        {-18.9170, 47.5340},
        // Andohalo
        {-18.9060, 47.5300},
        // Ambanidia
        {-18.9080, 47.5330},
        // Isoraka
        {-18.9150, 47.5380},
        // Ankazomanga
        {-18.9280, 47.5310},
        // Manjakaray
        {-18.9040, 47.5250},
        // Ambodivona
        {-18.9190, 47.5270},
        // Ambohibao
        {-18.8900, 47.5150},
        // Ivandry
        {-18.8850, 47.5200},
        // Ambohijanaka
        {-18.9300, 47.5280},
        // Mandroseza
        {-18.9400, 47.5200},
        // Ambohipo
        {-18.8980, 47.5420},
        // Tanjombato
        {-18.9500, 47.5300},
        // Androhibe
        {-18.8920, 47.5180},
        // Ambohitrarahaba
        {-18.8870, 47.5100},
        // Itaosy
        {-18.9450, 47.5100},
        // Amboditsiry
        {-18.9220, 47.5400},
        // Ankadindramamy
        {-18.9160, 47.5250},
        // Ambohimangakely
        {-18.8800, 47.5500},
        // Alasora
        {-18.9600, 47.5400},
        // Ambatobe
        {-18.8950, 47.5350},
    };

    private static final String[] NOMS = {
        "Rakoto", "Rabe", "Randria", "Ramaroson", "Rajaonarison",
        "Razafindrakoto", "Rasolofonirina", "Rakotondrabe", "Andriantsoa",
        "Rasoamahenina", "Rakotomalala", "Andriamahefa", "Razafindrabe",
        "Raharison", "Andrianarivo", "Rakotonjanahary", "Razafy",
        "Ratovoson", "Andriananja", "Ramarozaka"
    };

    private static final String[] PRENOMS = {
        "Jean", "Marie", "Paul", "Pierre", "Hery", "Zo", "Fara",
        "Tiana", "Mamy", "Lova", "Soa", "Aina", "Njaka", "Tojo",
        "Fanja", "Henintsoa", "Volatiana", "Mihaja", "Lalaina", "Diary"
    };

    private static final String[] QUARTIERS = {
        "Analakely", "Behoririka", "Faravohitra", "Ambohijatovo",
        "Antanimena", "Tsaralalana", "Ankadifotsy", "Ambatonakanga",
        "Isotry", "Mahamasina", "Andohalo", "Ambanidia", "Isoraka",
        "Ankazomanga", "Manjakaray", "Ambodivona", "Ambohibao",
        "Ivandry", "Ambohijanaka", "Mandroseza", "Ambohipo",
        "Tanjombato", "Androhibe", "Ambohitrarahaba", "Itaosy",
        "Amboditsiry", "Ankadindramamy", "Ambohimangakely", "Alasora", "Ambatobe"
    };

    @Transactional
    public int genererDonneesTest() {
        // Récupère les données existantes
        Departement departement = departementRepo.findById(3)
                .orElseThrow(() -> new RuntimeException("Département introuvable"));
        Site site = siteRepo.findAll().get(0);
        TypeAffectation typeAuto = typeAffectationRepo.findById(1)
                .orElseThrow(() -> new RuntimeException("Type Automatique introuvable"));

        // Récupère les heures de transport (Matin, Soir, Nuit)
        List<HeureTransport> heures = heureTransportRepo.findAllByOrderByHeure();
        if (heures.size() < 2) {
            throw new RuntimeException("Pas assez d'heures de transport configurées");
        }

        // Récupère les types de transport (Aller, Retour)
        TypeTransport typeAller = typeTransportRepo.findById(1)
                .orElseThrow(() -> new RuntimeException("Type Aller introuvable"));
        TypeTransport typeRetour = typeTransportRepo.findById(2)
                .orElseThrow(() -> new RuntimeException("Type Retour introuvable"));

        Random random = new Random(42); // seed fixe pour reproductibilité
        int nbAffectations = 0;
        LocalDate dateDemain = LocalDate.now().plusDays(1);

        // Génère 50 agents
        for (int i = 0; i < 50; i++) {
            // 1. Crée l'employé
            String nom = NOMS[i % NOMS.length];
            String prenom = PRENOMS[i % PRENOMS.length];
            String matricule = String.format("EMP-%03d", i + 100);

            Employe employe = employeRepo.save(Employe.builder()
                    .nom(nom)
                    .prenom(prenom)
                    .matricule(matricule)
                    .telephone("034" + String.format("%07d", i + 1000000))
                    .departement(departement)
                    .estBeneficiaire(true) // tous bénéficiaires pour le test
                    .actif(true)
                    .build());

            // 3. Crée l'adresse GPS
            int coordIndex = i % COORDS_ANTANANARIVO.length;
            double lat = COORDS_ANTANANARIVO[coordIndex][0] + (random.nextDouble() - 0.5) * 0.01;
            double lon = COORDS_ANTANANARIVO[coordIndex][1] + (random.nextDouble() - 0.5) * 0.01;
            String quartier = QUARTIERS[coordIndex];

            AdresseEmploye adresse = adresseRepo.save(AdresseEmploye.builder()
                    .employe(employe)
                    .adresse("Lot " + (i + 1) + " " + quartier + ", Antananarivo")
                    .latitude(new java.math.BigDecimal(lat))
                    .longitude(new java.math.BigDecimal(lon))
                    .estPrincipale(true)
                    .actif(true)
                    .build());

            // 4. Crée une demande ALLER pour demain matin
            HeureTransport heureMatin = heures.get(0);
            affectationRepo.save(Affectation.builder()
                    .dateTransport(dateDemain)
                    .employe(employe)
                    .adresse(adresse)
                    .site(site)
                    .heureTransport(heureMatin)
                    .typeTransport(typeAller)
                    .typeAffectation(typeAuto)
                    .estValidee(true) // en attente → pour tester l'optimisation
                    .estArchive(false)
                    .dateCreation(LocalDateTime.now())
                    .dateValidation(LocalDateTime.now())
                    .commentaire("Demande ALLER test #" + (i + 1))
                    .build());
            nbAffectations++;

            // 5. Crée une demande RETOUR pour demain soir
            HeureTransport heureSoir = heures.get(1);
            affectationRepo.save(Affectation.builder()
                    .dateTransport(dateDemain)
                    .employe(employe)
                    .adresse(adresse)
                    .site(site)
                    .heureTransport(heureSoir)
                    .typeTransport(typeRetour)
                    .typeAffectation(typeAuto)
                    .estValidee(true)
                    .estArchive(false)
                    .dateCreation(LocalDateTime.now())
                    .dateValidation(LocalDateTime.now())
                    .commentaire("Demande RETOUR test #" + (i + 1))
                    .build());
            nbAffectations++;
        }

        return nbAffectations; // 100 demandes au total
    }
}