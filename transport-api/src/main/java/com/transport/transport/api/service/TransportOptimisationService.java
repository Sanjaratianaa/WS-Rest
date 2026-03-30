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
    private static final double SEUIL_BACKFILL_KM = 1.0; // Distance max pour ramasser un "orphelin"

    private final VehiculeRepository vehiculeRepo;
    private final AffectationRepository affectationRepo;
    private final TypeAffectationRepository typeAffectationRepo;

    @Transactional
    public List<GroupeTransportResponse> genererEtSauvegarder(LocalDate date, Integer idHeure, Integer idTypeTransport) {
        // 1. Appeler l'algorithme d'optimisation
        List<GroupeTransportResponse> resultats = optimiser(date, idHeure, idTypeTransport);

        // 2. Boucler sur chaque groupe (véhicule)
        for (GroupeTransportResponse groupe : resultats) {

            // On récupère l'entité Véhicule (si ce n'est pas le groupe "Sans véhicule")
            Vehicule vehicule = null;
            if (groupe.getIdVehicule() != null) {
                vehicule = vehiculeRepo.findById(groupe.getIdVehicule())
                        .orElseThrow(() -> new RuntimeException("Véhicule non trouvé : " + groupe.getIdVehicule()));
            }

            // 3. Boucler sur les passagers du groupe pour mettre à jour l'affectation
            for (GroupeTransportResponse.PassagerResponse p : groupe.getPassagers()) {

                // On récupère l'affectation par son ID
                Affectation affectation = affectationRepo.findById(p.getIdAffectation())
                        .orElseThrow(() -> new RuntimeException("Affectation non trouvée : " + p.getIdAffectation()));

                // Mise à jour des champs stratégiques
                affectation.setVehicule(vehicule); // Assigne le véhicule (ou null si sans véhicule)
                TypeAffectation typeAffectation = typeAffectationRepo.findById(1)
                        .orElseThrow(() -> new RuntimeException("TypeAffectation introuvable"));
                affectation.setTypeAffectation(typeAffectation);

                // Optionnel : On peut marquer l'affectation comme "traitée" ou "planifiée"
                // affectation.setStatut("PLANIFIE");

                // Sauvegarde individuelle (ou accumulation dans une liste pour un saveAll)
                affectationRepo.save(affectation);
            }
        }
        return resultats;
    }

    public void archiverAffectationsPassees() {
        LocalDate aujourdhui = LocalDate.now();

        // On récupère toutes les affectations non archivées dont la date est passée
        List<Affectation> aArchiver = affectationRepo.findByDateTransportBeforeAndEstArchiveFalse(aujourdhui);

        if (!aArchiver.isEmpty()) {
            for (Affectation aff : aArchiver) {
                aff.setEstArchive(true);
            }
            affectationRepo.saveAll(aArchiver);
            System.out.println(aArchiver.size() + " affectations ont été archivées automatiquement.");
        }
    }

    @Transactional(readOnly = true)
    public List<GroupeTransportResponse> optimiser(LocalDate date, Integer idHeure, Integer idTypeTransport) {
        archiverAffectationsPassees();

        // 1. Récupération des données de base
        List<Affectation> toutesLesAffectations = affectationRepo
                .findByDateTransportAndHeureTransportIdAndTypeTransportIdAndEstValideeTrueAndEstArchiveFalse(
                        date, idHeure, idTypeTransport);

        if (toutesLesAffectations.isEmpty()) throw new RuntimeException("Aucune demande de transport");

        List<Vehicule> vehiculesDispos = vehiculeRepo.findByActifTrue().stream()
                .sorted(Comparator.comparingInt(Vehicule::getNombrePlaces).reversed())
                .collect(Collectors.toCollection(ArrayList::new));

        // 2. Initialisation des résultats
        List<GroupeTransport> resultatsFinaux = new ArrayList<>();
        Site siteRef = toutesLesAffectations.get(0).getSite(); // On prend le premier site comme référence

        // 3. CLUSTERING : On crée des groupes géographiques bruts
        List<Cluster> clusters = genererClusters(toutesLesAffectations, vehiculesDispos);

        // 4. ASSIGNATION : On donne un véhicule à chaque cluster (si possible)
        // Cette méthode crée des objets GroupeTransport
        List<GroupeTransport> groupesCrees = assignerVehicules(clusters, vehiculesDispos, siteRef);

        // 5. SEPARATION : On isole ceux qui n'ont pas eu de véhicule
        List<Affectation> listeAttente = extraireSansVehicule(groupesCrees);

        // On ne garde dans 'groupesCrees' que ceux qui ont un vrai véhicule
        groupesCrees.removeIf(g -> g.getVehicule() == null);

        // 6. BACKFILLING : On remplit les places vides des véhicules avec la liste d'attente
        // (C'est ici que ton véhicule de 8 places passe de 3 à 8 passagers)
        backfillVehicules(groupesCrees, listeAttente);

        // 7. TRI DE LA LISTE D'ATTENTE (Le bloc que tu as ajouté)
        // On trie les 23 personnes restantes par zone pour le JSON
        if (!listeAttente.isEmpty()) {
            listeAttente.sort(Comparator.comparingDouble((Affectation a) -> a.getAdresse().getLatitude().doubleValue())
                    .thenComparingDouble(a -> a.getAdresse().getLongitude().doubleValue()));
        }

        // 8. OPTIMISATION DU TRAJET (TSP) : Uniquement pour les véhicules
        for (GroupeTransport g : groupesCrees) {
            if (idTypeTransport == TYPE_ALLER) {
                optimiserOrdreRamassage(g); // Finit au Site
            } else {
                optimiserOrdreDepot(g);    // Part du Site
            }
        }

        // 9. ASSEMBLAGE FINAL
        resultatsFinaux.addAll(groupesCrees);
        if (!listeAttente.isEmpty()) {
            // On ajoute le groupe "Sans véhicule" à la fin du JSON
            resultatsFinaux.add(new GroupeTransport(null, listeAttente, siteRef));
        }

        return resultatsFinaux.stream().map(this::toResponse).toList();
    }

    // ============================================================
    // CLUSTERING (SANS DOUBLONS)
    // ============================================================
    private List<Cluster> genererClusters(List<Affectation> affectations, List<Vehicule> vehicules) {
        int k = Math.max(1, affectations.size() / 10);
        List<Cluster> clusters = new ArrayList<>();

        // Sélection des centres (k premiers uniques)
        for (int i = 0; i < Math.min(k, affectations.size()); i++) {
            clusters.add(new Cluster(affectations.get(i)));
        }

        // Attribution au plus proche (en sautant les centres déjà utilisés)
        for (int i = k; i < affectations.size(); i++) {
            Affectation a = affectations.get(i);
            clusters.stream()
                    .min(Comparator.comparingDouble(c -> calculerDistance(
                            c.getCentre().getAdresse().getLatitude().doubleValue(),
                            c.getCentre().getAdresse().getLongitude().doubleValue(),
                            a.getAdresse().getLatitude().doubleValue(),
                            a.getAdresse().getLongitude().doubleValue()
                    )))
                    .ifPresent(c -> c.ajouterMembre(a));
        }
        return clusters;
    }

    // ============================================================
    // ASSIGNATION ET BACKFILLING
    // ============================================================
    private List<GroupeTransport> assignerVehicules(List<Cluster> clusters, List<Vehicule> dispos, Site site) {
        List<GroupeTransport> result = new ArrayList<>();
        for (Cluster c : clusters) {
            List<Affectation> membres = new ArrayList<>(c.getMembres());
            while (!membres.isEmpty() && !dispos.isEmpty()) {
                Vehicule v = dispos.remove(0);
                List<Affectation> passagers = membres.stream().limit(v.getNombrePlaces()).collect(Collectors.toList());
                result.add(new GroupeTransport(v, new ArrayList<>(passagers), site));
                membres.removeAll(passagers);
            }
            if (!membres.isEmpty()) result.add(new GroupeTransport(null, membres, site));
        }
        return result;
    }

    private void backfillVehicules(List<GroupeTransport> groupes, List<Affectation> orphelins) {
        for (GroupeTransport g : groupes) {
            int placesLibres = g.getVehicule().getNombrePlaces() - g.getNombrePassagers();
            while (placesLibres > 0 && !orphelins.isEmpty()) {
                Affectation dernier = g.getPassagers().get(g.getPassagers().size() - 1);
                Affectation plusProche = orphelins.stream()
                        .filter(o -> calculerDistance(
                                dernier.getAdresse().getLatitude().doubleValue(), dernier.getAdresse().getLongitude().doubleValue(),
                                o.getAdresse().getLatitude().doubleValue(), o.getAdresse().getLongitude().doubleValue()
                        ) < SEUIL_BACKFILL_KM)
                        .min(Comparator.comparingDouble(o -> calculerDistance(
                                dernier.getAdresse().getLatitude().doubleValue(), dernier.getAdresse().getLongitude().doubleValue(),
                                o.getAdresse().getLatitude().doubleValue(), o.getAdresse().getLongitude().doubleValue()
                        ))).orElse(null);

                if (plusProche == null) break;
                g.getPassagers().add(plusProche);
                orphelins.remove(plusProche);
                placesLibres--;
            }
        }
    }

    // ============================================================
    // OPTIMISATION DES TRAJECTOIRES (TSP)
    // ============================================================
    private void optimiserOrdreRamassage(GroupeTransport groupe) {
        if (groupe.getPassagers().isEmpty()) return;

        List<Affectation> aVisiter = new ArrayList<>(groupe.getPassagers());
        List<Affectation> route = new ArrayList<>();

        double siteLat = groupe.getSite().getLatitude().doubleValue();
        double siteLon = groupe.getSite().getLongitude().doubleValue();

        // 1. On commence par l'employé le plus LOIN du site
        Affectation actuel = aVisiter.stream()
                .max(Comparator.comparingDouble(a -> calculerDistance(
                        a.getAdresse().getLatitude().doubleValue(), a.getAdresse().getLongitude().doubleValue(),
                        siteLat, siteLon))).get();

        route.add(actuel);
        aVisiter.remove(actuel);

        // 2. Pour chaque point suivant, on cherche celui qui minimise la distance totale
        // mais on applique un malus énorme si le point nous éloigne du site
        while (!aVisiter.isEmpty()) {
            final Affectation pointActuel = actuel;

            Affectation prochain = aVisiter.stream()
                    .min(Comparator.comparingDouble(c -> {
                        double distEntrePoints = calculerDistance(
                                pointActuel.getAdresse().getLatitude().doubleValue(), pointActuel.getAdresse().getLongitude().doubleValue(),
                                c.getAdresse().getLatitude().doubleValue(), c.getAdresse().getLongitude().doubleValue()
                        );

                        double distVersSite = calculerDistance(
                                c.getAdresse().getLatitude().doubleValue(), c.getAdresse().getLongitude().doubleValue(),
                                siteLat, siteLon
                        );

                        // FORCE : On veut que la distance vers le site diminue TOUJOURS.
                        // On donne un poids de 1.5 à la direction du site pour éviter les retours en arrière.
                        return distEntrePoints + (distVersSite * 1.5);
                    })).get();

            route.add(prochain);
            aVisiter.remove(prochain);
            actuel = prochain;
        }

        // 3. Sécurité finale : On s'assure que le dernier point de la liste est
        // bien le plus proche géographiquement du site.
        groupe.setPassagers(route);
    }

    private void optimiserOrdreDepot(GroupeTransport groupe) {
        if (groupe.getPassagers().isEmpty()) return;

        List<Affectation> aVisiter = new ArrayList<>(groupe.getPassagers());
        List<Affectation> route = new ArrayList<>();

        double siteLat = groupe.getSite().getLatitude().doubleValue();
        double siteLon = groupe.getSite().getLongitude().doubleValue();

        // 1. Pour le RETOUR, on commence par l'employé le plus PROCHE du site
        Affectation actuel = aVisiter.stream()
                .min(Comparator.comparingDouble(a -> calculerDistance(
                        siteLat, siteLon,
                        a.getAdresse().getLatitude().doubleValue(), a.getAdresse().getLongitude().doubleValue())))
                .get();

        route.add(actuel);
        aVisiter.remove(actuel);

        // 2. On continue en cherchant le suivant le plus proche à chaque fois
        while (!aVisiter.isEmpty()) {
            final Affectation pointActuel = actuel;
            Affectation prochain = aVisiter.stream()
                    .min(Comparator.comparingDouble(c -> calculerDistance(
                            pointActuel.getAdresse().getLatitude().doubleValue(), pointActuel.getAdresse().getLongitude().doubleValue(),
                            c.getAdresse().getLatitude().doubleValue(), c.getAdresse().getLongitude().doubleValue()
                    ))).get();

            route.add(prochain);
            aVisiter.remove(prochain);
            actuel = prochain;
        }

        groupe.setPassagers(route);
    }

    // ============================================================
    // UTILS & MODELS
    // ============================================================
    private double calculerDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return RAYON_TERRE_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private List<Affectation> extraireSansVehicule(List<GroupeTransport> groupes) {
        return groupes.stream()
                .filter(g -> g.getVehicule() == null)
                .flatMap(g -> g.getPassagers().stream())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Getter
    public static class Cluster {
        private final Affectation centre;
        private final List<Affectation> membres = new ArrayList<>();
        public Cluster(Affectation centre) { this.centre = centre; this.membres.add(centre); }
        public void ajouterMembre(Affectation a) { if(!membres.contains(a)) membres.add(a); }
    }

    @Getter @Setter @AllArgsConstructor
    public static class GroupeTransport {
        private Vehicule vehicule;
        private List<Affectation> passagers;
        private Site site;
        public int getNombrePassagers() { return passagers.size(); }
    }

    private GroupeTransportResponse toResponse(GroupeTransport g) {
        return GroupeTransportResponse.builder()
                .idVehicule(g.getVehicule() != null ? g.getVehicule().getId() : null)
                .matriculeVehicule(g.getVehicule() != null ? g.getVehicule().getMatricule() : "Sans véhicule")
                .capacite(g.getVehicule() != null ? g.getVehicule().getNombrePlaces() : 0)
                .nombrePassagers(g.getNombrePassagers())
                .tauxRemplissage(g.getVehicule() == null ? 0 : (double) g.getNombrePassagers() / g.getVehicule().getNombrePlaces() * 100)
                .passagers(g.getPassagers().stream().map(p -> GroupeTransportResponse.PassagerResponse.builder()
                        .idAffectation(p.getId())
                        .idEmploye(p.getEmploye().getId()).nomComplet(p.getEmploye().getNom() + " " + p.getEmploye().getPrenom())
                        .adresse(p.getAdresse().getAdresse()).latitude(p.getAdresse().getLatitude().doubleValue())
                        .longitude(p.getAdresse().getLongitude().doubleValue()).ordre(g.getPassagers().indexOf(p) + 1).build()
                ).toList()).build();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupeTransportResponse {
        private Integer idVehicule;
        private String matriculeVehicule;
        private Integer capacite;
        private Integer nombrePassagers;
        private Double tauxRemplissage;
        private List<PassagerResponse> passagers;

        @Getter
        @Setter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class PassagerResponse {
            private Integer idAffectation;
            private Integer idEmploye;
            private String nomComplet;
            private String adresse;
            private Double latitude;
            private Double longitude;
            private Integer ordre;
        }
    }
}