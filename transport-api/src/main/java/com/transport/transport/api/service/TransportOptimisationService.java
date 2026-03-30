package com.transport.transport.api.service;

import com.transport.transport.api.entity.*;
import com.transport.transport.api.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransportOptimisationService {

    private static final double RAYON_TERRE_KM = 6371.0;
    private static final double RAYON_GROUPEMENT_KM = 2.0;
    private static final int TYPE_ALLER = 1;

    private final VehiculeRepository vehiculeRepo;
    private final AffectationRepository affectationRepo;

    // ============================================================
    // POINT D'ENTRÉE PRINCIPAL
    // ============================================================
    public List<GroupeTransport> optimiser(LocalDate date, Integer idHeure, Integer idTypeTransport) {

        // 1. Récupère les affectations en attente pour cette date/heure/type
        List<Affectation> affectations = affectationRepo
                .findByDateTransportAndHeureTransportIdAndTypeTransportIdAndEstValideeNullAndEstArchiveFalse(
                        date, idHeure, idTypeTransport);

        if (affectations.isEmpty()) {
            throw new RuntimeException(
                    "Aucune demande de transport en attente pour le " + date
                            + " à l'heure #" + idHeure
                            + " (" + (idTypeTransport == TYPE_ALLER ? "Aller" : "Retour") + ")"
            );
        }

        // 2. Récupère les véhicules disponibles triés par capacité décroissante
        List<Vehicule> vehicules = vehiculeRepo.findByActifTrue().stream()
                .sorted(Comparator.comparingInt(Vehicule::getNombrePlaces).reversed())
                .toList();

        if (vehicules.isEmpty()) {
            throw new RuntimeException("Aucun véhicule actif disponible");
        }

        // 3. Phase 1 — Grouper par proximité géographique
        List<Cluster> clusters = grouperParProximite(affectations);

        // 4. Phase 2 — Assigner les véhicules aux clusters
        List<GroupeTransport> groupes = assignerVehicules(clusters, vehicules);

        // 5. Phase 3 — Optimiser l'ordre selon le type (Aller ou Retour)
        groupes.forEach(groupe -> {
            if (idTypeTransport == TYPE_ALLER) {
                // ALLER → ramassage : adresses des employés → site
                optimiserOrdreRamassage(groupe);
            } else {
                // RETOUR → dépôt : site → adresses des employés
                optimiserOrdreDepot(groupe);
            }
        });

        return groupes;
    }

    // ============================================================
    // PHASE 1 — CLUSTERING GÉOGRAPHIQUE
    // ============================================================
    private List<Cluster> grouperParProximite(List<Affectation> affectations) {
        List<Cluster> clusters = new ArrayList<>();
        List<Affectation> nonAssignes = new ArrayList<>(affectations);

        while (!nonAssignes.isEmpty()) {
            // Prend le premier non assigné comme centre du cluster
            Affectation centre = nonAssignes.remove(0);
            Cluster cluster = new Cluster(centre);

            // Cherche tous les employés dans le rayon défini
            Iterator<Affectation> it = nonAssignes.iterator();
            while (it.hasNext()) {
                Affectation autre = it.next();
                double distance = calculerDistance(
                        centre.getAdresse().getLatitude().doubleValue(),
                        centre.getAdresse().getLongitude().doubleValue(),
                        autre.getAdresse().getLatitude().doubleValue(),
                        autre.getAdresse().getLongitude().doubleValue()
                );
                if (distance <= RAYON_GROUPEMENT_KM) {
                    cluster.ajouterMembre(autre);
                    it.remove();
                }
            }
            clusters.add(cluster);
        }

        // Trie les clusters par taille décroissante (les plus grands d'abord)
        clusters.sort(Comparator.comparingInt(Cluster::taille).reversed());
        return clusters;
    }

    // ============================================================
    // PHASE 2 — ASSIGNATION DES VÉHICULES
    // ============================================================
    private List<GroupeTransport> assignerVehicules(List<Cluster> clusters, List<Vehicule> vehicules) {
        List<GroupeTransport> groupes = new ArrayList<>();
        List<Vehicule> vehiculesDisponibles = new ArrayList<>(vehicules);

        for (Cluster cluster : clusters) {
            List<Affectation> membres = new ArrayList<>(cluster.getMembres());

            while (!membres.isEmpty()) {
                if (vehiculesDisponibles.isEmpty()) {
                    // ⚠️ Plus de véhicules → groupe sans véhicule
                    groupes.add(new GroupeTransport(null, new ArrayList<>(membres)));
                    membres.clear();
                    break;
                }

                // Prend le véhicule le plus grand disponible
                Vehicule vehicule = vehiculesDisponibles.get(0);
                int capacite = vehicule.getNombrePlaces();

                // Remplit le véhicule au maximum
                List<Affectation> passagers = new ArrayList<>(
                        membres.subList(0, Math.min(capacite, membres.size()))
                );
                membres.removeAll(passagers);

                groupes.add(new GroupeTransport(vehicule, passagers));

                // Si véhicule plein → on passe au suivant
                if (passagers.size() >= capacite) {
                    vehiculesDisponibles.remove(0);
                }
            }
        }

        return groupes;
    }

    // ============================================================
    // PHASE 3A — ALLER : Optimisation ordre de ramassage
    // Adresses employés → Site
    // ============================================================
    private void optimiserOrdreRamassage(GroupeTransport groupe) {
        List<Affectation> passagers = new ArrayList<>(groupe.getPassagers());
        List<Affectation> ordreOptimal = new ArrayList<>();

        // Point de départ = premier passager (le plus éloigné du centre)
        Affectation actuel = passagers.remove(0);
        ordreOptimal.add(actuel);

        // Algorithme du plus proche voisin
        while (!passagers.isEmpty()) {
            Affectation plusProche = null;
            double distanceMin = Double.MAX_VALUE;

            for (Affectation candidat : passagers) {
                double distance = calculerDistance(
                        actuel.getAdresse().getLatitude().doubleValue(),
                        actuel.getAdresse().getLongitude().doubleValue(),
                        candidat.getAdresse().getLatitude().doubleValue(),
                        candidat.getAdresse().getLongitude().doubleValue()
                );
                if (distance < distanceMin) {
                    distanceMin = distance;
                    plusProche = candidat;
                }
            }

            ordreOptimal.add(plusProche);
            passagers.remove(plusProche);
            actuel = plusProche;
        }

        groupe.setPassagers(ordreOptimal);
    }

    // ============================================================
    // PHASE 3B — RETOUR : Optimisation ordre de dépôt
    // Site → Adresses employés
    // ============================================================
    private void optimiserOrdreDepot(GroupeTransport groupe) {
        List<Affectation> passagers = new ArrayList<>(groupe.getPassagers());
        List<Affectation> ordreOptimal = new ArrayList<>();

        // Point de départ = le site
        Affectation premiereAffectation = groupe.getPassagers().get(0);
        double latActuelle = premiereAffectation.getSite().getLatitude().doubleValue();
        double lonActuelle = premiereAffectation.getSite().getLongitude().doubleValue();

        // Algorithme du plus proche voisin depuis le site
        while (!passagers.isEmpty()) {
            Affectation plusProche = null;
            double distanceMin = Double.MAX_VALUE;

            for (Affectation candidat : passagers) {
                double distance = calculerDistance(
                        latActuelle,
                        lonActuelle,
                        candidat.getAdresse().getLatitude().doubleValue(),
                        candidat.getAdresse().getLongitude().doubleValue()
                );
                if (distance < distanceMin) {
                    distanceMin = distance;
                    plusProche = candidat;
                }
            }

            ordreOptimal.add(plusProche);
            passagers.remove(plusProche);
            // Le prochain départ = adresse du passager déposé
            latActuelle = plusProche.getAdresse().getLatitude().doubleValue();
            lonActuelle = plusProche.getAdresse().getLongitude().doubleValue();
        }

        groupe.setPassagers(ordreOptimal);
    }

    // ============================================================
    // FORMULE HAVERSINE — Distance réelle entre 2 points GPS (en km)
    // ============================================================
    private double calculerDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return RAYON_TERRE_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // ============================================================
    // CLASSES INTERNES
    // ============================================================
    @Getter
    public static class Cluster {
        private final List<Affectation> membres = new ArrayList<>();

        public Cluster(Affectation centre) {
            membres.add(centre);
        }

        public void ajouterMembre(Affectation a) {
            membres.add(a);
        }

        public int taille() {
            return membres.size();
        }
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class GroupeTransport {
        private Vehicule vehicule;
        private List<Affectation> passagers;

        public int getNombrePassagers() {
            return passagers.size();
        }

        public double getTauxRemplissage() {
            if (vehicule == null || vehicule.getNombrePlaces() == 0) return 0;
            return (double) passagers.size() / vehicule.getNombrePlaces() * 100;
        }

        public boolean hasVehicule() {
            return vehicule != null;
        }
    }
}
