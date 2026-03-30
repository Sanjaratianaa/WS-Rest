package com.transport.transport.api.service;

import com.transport.transport.api.entity.*;
import com.transport.transport.api.repository.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportOptimisationService {

    private static final double RAYON_TERRE_KM = 6371.0;
    private static final int TYPE_ALLER = 1;
    private static final int MIN_REMPLISSAGE = 5;

    private static final double VITESSE_MOYENNE_KMH = 30;
    private static final int TEMPS_MAX_MIN = 90;

    private final VehiculeRepository vehiculeRepo;
    private final AffectationRepository affectationRepo;

    // ============================================================
    // 🚀 POINT D’ENTRÉE PRINCIPAL
    // ============================================================
    @Transactional(readOnly = true)
    public List<GroupeTransportResponse> optimiser(LocalDate date, Integer idHeure, Integer idTypeTransport) {

        List<Affectation> affectations = affectationRepo
                .findByDateTransportAndHeureTransportIdAndTypeTransportIdAndEstValideeTrueAndEstArchiveFalse(
                        date, idHeure, idTypeTransport);

        if (affectations.isEmpty()) {
            throw new RuntimeException("Aucune demande de transport");
        }

        List<Vehicule> vehicules = vehiculeRepo.findByActifTrue().stream()
                .sorted(Comparator.comparingInt(Vehicule::getNombrePlaces).reversed())
                .toList();

        if (vehicules.isEmpty()) {
            throw new RuntimeException("Aucun véhicule actif");
        }

        // 🔥 1. MULTI-SITE
        Map<Site, List<Affectation>> parSite = affectations.stream()
                .collect(Collectors.groupingBy(Affectation::getSite));

        List<GroupeTransport> resultats = new ArrayList<>();

        for (Map.Entry<Site, List<Affectation>> entry : parSite.entrySet()) {

            List<Affectation> affectationsSite = entry.getValue();

            // 🔥 2. CLUSTERING
            List<Cluster> clusters = clusteringSimple(affectationsSite, vehicules);

            // 🔥 3. ASSIGNATION OPTIMISÉE
            List<GroupeTransport> groupes = assignerVehicules(clusters, vehicules);

            // 🔥 4. FUSION PETITS GROUPES
            fusionnerPetitsGroupes(groupes);

            // 🔥 5. OPTIMISATION TRAJET
            optimiserTrajets(groupes, idTypeTransport);

            // 🔥 6. VALIDATION TEMPS
            groupes.removeIf(g -> !estValide(g));

            resultats.addAll(groupes);
        }

        return resultats.stream().map(this::toResponse).toList();
    }

    // ============================================================
    // 🔥 CLUSTERING SIMPLE (AMÉLIORÉ)
    // ============================================================
    private List<Cluster> clusteringSimple(List<Affectation> affectations, List<Vehicule> vehicules) {

        int capaciteMoyenne = vehicules.stream()
                .mapToInt(Vehicule::getNombrePlaces)
                .sum() / vehicules.size();

        int k = Math.max(1, affectations.size() / capaciteMoyenne);

        List<Cluster> clusters = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            clusters.add(new Cluster(affectations.get(i)));
        }

        for (Affectation a : affectations) {

            Cluster meilleur = null;
            double min = Double.MAX_VALUE;

            for (Cluster c : clusters) {

                Affectation centre = c.getMembres().get(0);

                double d = calculerDistance(
                        centre.getAdresse().getLatitude().doubleValue(),
                        centre.getAdresse().getLongitude().doubleValue(),
                        a.getAdresse().getLatitude().doubleValue(),
                        a.getAdresse().getLongitude().doubleValue()
                );

                if (d < min) {
                    min = d;
                    meilleur = c;
                }
            }

            meilleur.ajouterMembre(a);
        }

        return clusters;
    }

    // ============================================================
    // 🔥 ASSIGNATION OPTIMISÉE (GLOBAL)
    // ============================================================
    private List<GroupeTransport> assignerVehicules(List<Cluster> clusters, List<Vehicule> vehicules) {

        List<Affectation> restants = clusters.stream()
                .flatMap(c -> c.getMembres().stream())
                .collect(Collectors.toCollection(ArrayList::new));

        List<GroupeTransport> groupes = new ArrayList<>();
        List<Vehicule> disponibles = new ArrayList<>(vehicules);

        while (!restants.isEmpty()) {

            if (disponibles.isEmpty()) {
                groupes.add(new GroupeTransport(null, new ArrayList<>(restants)));
                break;
            }

            Vehicule v = disponibles.remove(0);
            List<Affectation> groupe = construireGroupeProche(restants, v.getNombrePlaces());

            restants.removeAll(groupe);

            groupes.add(new GroupeTransport(v, groupe));
        }

        return groupes;
    }

    // ============================================================
    // 🔥 GROUPE PAR PROXIMITÉ
    // ============================================================
    private List<Affectation> construireGroupeProche(List<Affectation> pool, int capacite) {

        List<Affectation> groupe = new ArrayList<>();

        Affectation depart = pool.get(0);
        groupe.add(depart);
        pool.remove(depart);

        while (groupe.size() < capacite && !pool.isEmpty()) {

            Affectation dernier = groupe.get(groupe.size() - 1);

            Affectation plusProche = null;
            double min = Double.MAX_VALUE;

            for (Affectation a : pool) {

                double d = calculerDistance(
                        dernier.getAdresse().getLatitude().doubleValue(),
                        dernier.getAdresse().getLongitude().doubleValue(),
                        a.getAdresse().getLatitude().doubleValue(),
                        a.getAdresse().getLongitude().doubleValue()
                );

                if (d < min) {
                    min = d;
                    plusProche = a;
                }
            }

            groupe.add(plusProche);
            pool.remove(plusProche);
        }

        return groupe;
    }

    // ============================================================
    // 🔥 FUSION PETITS GROUPES
    // ============================================================
    private void fusionnerPetitsGroupes(List<GroupeTransport> groupes) {

        List<GroupeTransport> petits = groupes.stream()
                .filter(g -> g.getNombrePassagers() < MIN_REMPLISSAGE)
                .toList();

        for (GroupeTransport petit : petits) {

            for (GroupeTransport autre : groupes) {

                if (autre == petit) continue;
                if (autre.getVehicule() == null) continue;

                if (autre.getNombrePassagers() + petit.getNombrePassagers()
                        <= autre.getVehicule().getNombrePlaces()) {

                    autre.getPassagers().addAll(petit.getPassagers());
                    petit.getPassagers().clear();
                    break;
                }
            }
        }

        groupes.removeIf(g -> g.getPassagers().isEmpty());
    }

    // ============================================================
    // 🔥 OPTIMISATION TRAJETS
    // ============================================================
    private void optimiserTrajets(List<GroupeTransport> groupes, Integer type) {

        for (GroupeTransport g : groupes) {

            if (type == TYPE_ALLER) {
                optimiserOrdreRamassage(g);
            } else {
                optimiserOrdreDepot(g);
            }
        }
    }

    // ============================================================
    // 🔥 VALIDATION TEMPS
    // ============================================================
    private boolean estValide(GroupeTransport groupe) {

        double distance = 0;
        List<Affectation> p = groupe.getPassagers();

        for (int i = 0; i < p.size() - 1; i++) {
            distance += calculerDistance(
                    p.get(i).getAdresse().getLatitude().doubleValue(),
                    p.get(i).getAdresse().getLongitude().doubleValue(),
                    p.get(i + 1).getAdresse().getLatitude().doubleValue(),
                    p.get(i + 1).getAdresse().getLongitude().doubleValue()
            );
        }

        double temps = (distance / VITESSE_MOYENNE_KMH) * 60;

        return temps <= TEMPS_MAX_MIN;
    }

    // ============================================================
    // 🔥 DISTANCE
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
    // DTO + CLASSES
    // ============================================================
    @Getter
    public static class Cluster {
        private final List<Affectation> membres = new ArrayList<>();
        public Cluster(Affectation centre) { membres.add(centre); }
        public void ajouterMembre(Affectation a) { membres.add(a); }
    }

    @Getter @Setter @AllArgsConstructor
    public static class GroupeTransport {
        private Vehicule vehicule;
        private List<Affectation> passagers;

        public int getNombrePassagers() { return passagers.size(); }

        public double getTauxRemplissage() {
            if (vehicule == null) return 0;
            return (double) passagers.size() / vehicule.getNombrePlaces() * 100;
        }
    }

    private GroupeTransportResponse toResponse(GroupeTransport g) {

        List<GroupeTransportResponse.PassagerResponse> passagers = new ArrayList<>();

        for (int i = 0; i < g.getPassagers().size(); i++) {

            Affectation a = g.getPassagers().get(i);

            passagers.add(GroupeTransportResponse.PassagerResponse.builder()
                    .idEmploye(a.getEmploye().getId())
                    .nomComplet(a.getEmploye().getNom() + " " + a.getEmploye().getPrenom())
                    .adresse(a.getAdresse().getAdresse())
                    .latitude(a.getAdresse().getLatitude().doubleValue())
                    .longitude(a.getAdresse().getLongitude().doubleValue())
                    .ordre(i + 1)
                    .build());
        }

        return GroupeTransportResponse.builder()
                .idVehicule(g.getVehicule() != null ? g.getVehicule().getId() : null)
                .matriculeVehicule(g.getVehicule() != null ? g.getVehicule().getMatricule() : "Sans véhicule")
                .capacite(g.getVehicule() != null ? g.getVehicule().getNombrePlaces() : 0)
                .nombrePassagers(g.getNombrePassagers())
                .tauxRemplissage(g.getTauxRemplissage())
                .passagers(passagers)
                .build();
    }

    private void optimiserOrdreRamassage(GroupeTransport groupe) {

        List<Affectation> passagers = new ArrayList<>(groupe.getPassagers());
        List<Affectation> ordreOptimal = new ArrayList<>();

        // 🔥 NOUVEAU : commencer par le plus éloigné du site
        Affectation actuel = passagers.stream()
                .max(Comparator.comparingDouble(a -> calculerDistance(
                        a.getAdresse().getLatitude().doubleValue(),
                        a.getAdresse().getLongitude().doubleValue(),
                        a.getSite().getLatitude().doubleValue(),
                        a.getSite().getLongitude().doubleValue()
                )))
                .orElse(passagers.get(0));

        passagers.remove(actuel);
        ordreOptimal.add(actuel);

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

    private void optimiserOrdreDepot(GroupeTransport groupe) {

        List<Affectation> passagers = new ArrayList<>(groupe.getPassagers());
        List<Affectation> ordreOptimal = new ArrayList<>();

        // 🔥 départ = site
        Affectation premier = passagers.get(0);

        double latActuelle = premier.getSite().getLatitude().doubleValue();
        double lonActuelle = premier.getSite().getLongitude().doubleValue();

        while (!passagers.isEmpty()) {

            Affectation plusProche = null;
            double distanceMin = Double.MAX_VALUE;

            for (Affectation candidat : passagers) {

                double distance = calculerDistance(
                        latActuelle, lonActuelle,
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

            latActuelle = plusProche.getAdresse().getLatitude().doubleValue();
            lonActuelle = plusProche.getAdresse().getLongitude().doubleValue();
        }

        groupe.setPassagers(ordreOptimal);
    }

    @Getter @Setter @Builder
    public static class GroupeTransportResponse {
        private Integer idVehicule;
        private String matriculeVehicule;
        private Integer capacite;
        private Integer nombrePassagers;
        private Double tauxRemplissage;
        private List<PassagerResponse> passagers;

        @Getter @Setter @Builder
        public static class PassagerResponse {
            private Integer idEmploye;
            private String nomComplet;
            private String adresse;
            private Double latitude;
            private Double longitude;
            private Integer ordre;
        }
    }
}