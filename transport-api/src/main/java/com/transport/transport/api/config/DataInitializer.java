package com.transport.transport.api.config;

import com.transport.transport.api.entity.*;
import com.transport.transport.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final AuthentificationRepository authRepo;
    private final EmployeRepository employeRepo;
    private final DepartementRepository departementRepo;
    private final AdresseEmployeRepository adresseRepo;
    private final SiteRepository siteRepo;
    private final VehiculeRepository vehiculeRepo;
    private final TypeTransportRepository typeTransportRepo;
    private final HeureTransportRepository heureTransportRepo;
    private final DateTransportRepository dateTransportRepo;
    private final TypeAffectationRepository typeAffectationRepo;
    private final AffectationRepository affectationRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("=== Initialisation des données de test ===");

        // Roles
        Role roleAdmin = roleRepo.save(Role.builder().libelle("ADMIN").build());
        Role roleEmploye = roleRepo.save(Role.builder().libelle("EMPLOYE").build());
        log.info("Rôles créés : ADMIN, EMPLOYE");

        // Départements
        Departement depIT = departementRepo.save(Departement.builder()
                .nom("Informatique").description("Département IT").build());
        Departement depRH = departementRepo.save(Departement.builder()
                .nom("Ressources Humaines").description("Département RH").build());
        Departement depFin = departementRepo.save(Departement.builder()
                .nom("Finance").description("Département Finance").build());
        log.info("Départements créés : Informatique, RH, Finance");

        // Employés
        Employe admin = employeRepo.save(Employe.builder()
                .nom("Admin").prenom("Super").matricule("ADM001")
                .telephone("0340000001").departement(depIT).build());
        Employe emp1 = employeRepo.save(Employe.builder()
                .nom("Rakoto").prenom("Jean").matricule("EMP001")
                .telephone("0340000002").departement(depIT).build());
        Employe emp2 = employeRepo.save(Employe.builder()
                .nom("Rabe").prenom("Marie").matricule("EMP002")
                .telephone("0340000003").departement(depRH).build());
        Employe emp3 = employeRepo.save(Employe.builder()
                .nom("Randria").prenom("Paul").matricule("EMP003")
                .telephone("0340000004").departement(depFin).build());
        log.info("Employés créés : Admin, Rakoto, Rabe, Randria");

        // Authentification
        authRepo.save(Authentification.builder()
                .email("admin@transport.com")
                .motDePasse(passwordEncoder.encode("admin123"))
                .employe(admin).role(roleAdmin).build());
        authRepo.save(Authentification.builder()
                .email("jean@transport.com")
                .motDePasse(passwordEncoder.encode("emp123"))
                .employe(emp1).role(roleEmploye).build());
        authRepo.save(Authentification.builder()
                .email("marie@transport.com")
                .motDePasse(passwordEncoder.encode("emp123"))
                .employe(emp2).role(roleEmploye).build());
        authRepo.save(Authentification.builder()
                .email("paul@transport.com")
                .motDePasse(passwordEncoder.encode("emp123"))
                .employe(emp3).role(roleEmploye).build());
        log.info("Comptes créés : admin@transport.com / admin123, jean/marie/paul@transport.com / emp123");

        // Adresses
        AdresseEmploye adr1 = adresseRepo.save(AdresseEmploye.builder()
                .employe(emp1).adresse("Analakely, Antananarivo")
                .latitude(new BigDecimal("-18.910000000")).longitude(new BigDecimal("47.525000000"))
                .estPrincipale(true).build());
        AdresseEmploye adr2 = adresseRepo.save(AdresseEmploye.builder()
                .employe(emp2).adresse("Ankorondrano, Antananarivo")
                .latitude(new BigDecimal("-18.895000000")).longitude(new BigDecimal("47.520000000"))
                .estPrincipale(true).build());
        AdresseEmploye adr3 = adresseRepo.save(AdresseEmploye.builder()
                .employe(emp3).adresse("Ivandry, Antananarivo")
                .latitude(new BigDecimal("-18.888000000")).longitude(new BigDecimal("47.535000000"))
                .estPrincipale(true).build());
        log.info("Adresses créées");

        // Sites
        Site siege = siteRepo.save(Site.builder()
                .nom("Siège").adresse("Galaxy, Andraharo")
                .latitude(new BigDecimal("-18.880000000")).longitude(new BigDecimal("47.540000000")).build());
        Site usine = siteRepo.save(Site.builder()
                .nom("Usine").adresse("Zone Industrielle Antsirabe")
                .latitude(new BigDecimal("-19.870000000")).longitude(new BigDecimal("47.030000000")).build());
        log.info("Sites créés : Siège, Usine");

        // Véhicules
        Vehicule v1 = vehiculeRepo.save(Vehicule.builder()
                .matricule("TAN-1234").nombrePlaces(8).build());
        Vehicule v2 = vehiculeRepo.save(Vehicule.builder()
                .matricule("TAN-5678").nombrePlaces(15).build());
        Vehicule v3 = vehiculeRepo.save(Vehicule.builder()
                .matricule("TAN-9012").nombrePlaces(4).build());
        log.info("Véhicules créés : TAN-1234, TAN-5678, TAN-9012");

        // Types de transport
        TypeTransport aller = typeTransportRepo.save(TypeTransport.builder().libelle("Aller").build());
        TypeTransport retour = typeTransportRepo.save(TypeTransport.builder().libelle("Retour").build());
        log.info("Types transport : Aller, Retour");

        // Heures de transport
        HeureTransport matin = heureTransportRepo.save(HeureTransport.builder()
                .heure(LocalTime.of(7, 0)).libelle("Matin").build());
        HeureTransport soir = heureTransportRepo.save(HeureTransport.builder()
                .heure(LocalTime.of(17, 0)).libelle("Soir").build());
        HeureTransport nuit = heureTransportRepo.save(HeureTransport.builder()
                .heure(LocalTime.of(22, 0)).libelle("Nuit").build());
        log.info("Heures transport : Matin(7h), Soir(17h), Nuit(22h)");

        // Dates de transport
        DateTransport today = dateTransportRepo.save(DateTransport.builder()
                .dateJour(LocalDate.now()).build());
        DateTransport tomorrow = dateTransportRepo.save(DateTransport.builder()
                .dateJour(LocalDate.now().plusDays(1)).build());
        DateTransport dayAfter = dateTransportRepo.save(DateTransport.builder()
                .dateJour(LocalDate.now().plusDays(2)).build());
        log.info("Dates transport : aujourd'hui, demain, après-demain");

        // Types d'affectation
        TypeAffectation auto = typeAffectationRepo.save(TypeAffectation.builder().libelle("Automatique").build());
        TypeAffectation manuel = typeAffectationRepo.save(TypeAffectation.builder().libelle("Manuel").build());
        log.info("Types affectation : Automatique, Manuel");

        // Affectations de test
        affectationRepo.save(Affectation.builder()
                .dateTransport(today).employe(emp1).adresse(adr1)
                .typeTransport(aller).site(siege).vehicule(v1)
                .heureTransport(matin).typeAffectation(manuel)
                .estValidee(true).commentaire("Trajet matin validé").build());

        affectationRepo.save(Affectation.builder()
                .dateTransport(today).employe(emp2).adresse(adr2)
                .typeTransport(aller).site(siege).vehicule(v1)
                .heureTransport(matin).typeAffectation(manuel)
                .estValidee(true).commentaire("Trajet matin validé").build());

        affectationRepo.save(Affectation.builder()
                .dateTransport(today).employe(emp1).adresse(adr1)
                .typeTransport(retour).site(siege).vehicule(v1)
                .heureTransport(soir).typeAffectation(auto)
                .estValidee(null).commentaire("En attente de validation").build());

        affectationRepo.save(Affectation.builder()
                .dateTransport(tomorrow).employe(emp3).adresse(adr3)
                .typeTransport(aller).site(siege)
                .heureTransport(matin).typeAffectation(auto)
                .estValidee(null).commentaire("Demande sans véhicule - à valider").build());

        log.info("Affectations de test créées");
        log.info("=== Initialisation terminée ===");
        log.info("Swagger UI : http://localhost:8080/swagger-ui.html");
        log.info("H2 Console : http://localhost:8080/h2-console");
    }
}
