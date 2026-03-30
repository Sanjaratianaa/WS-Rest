package com.transport.transport.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transport API")
                        .version("1.0")
                        .description("API REST de gestion de transport d'entreprise.\n\n"
                                + "**Fonctionnalités principales :**\n"
                                + "- Authentification JWT (login par matricule)\n"
                                + "- Gestion des demandes de transport (CRUD + validation + optimisation)\n"
                                + "- Gestion des employés, adresses, départements, sites et véhicules\n"
                                + "- Optimisation géographique des trajets (clustering + nearest-neighbor)\n"
                                + "- Contrôle d'accès : ADMIN / EMPLOYE\n\n"
                                + "**Authentification :** Utilisez `/api/auth/login` pour obtenir un token JWT, "
                                + "puis cliquez sur le bouton **Authorize** et saisissez le token.")
                        .contact(new Contact()
                                .name("Transport API")))
                .tags(List.of(
                        new Tag().name("Authentification").description("Endpoints de login et d'inscription (publics)"),
                        new Tag().name("Demandes de Transport").description("Gestion des demandes de transport : création, consultation, validation, modification, statistiques et optimisation"),
                        new Tag().name("Employés").description("Gestion des employés (CRUD). Les créations/modifications/suppressions sont réservées aux ADMIN."),
                        new Tag().name("Adresses Employés").description("Gestion des adresses des employés. Un employé ne peut voir/créer que ses propres adresses."),
                        new Tag().name("Véhicules").description("Gestion des véhicules de transport (CRUD + agrégation trajets). Réservé aux ADMIN sauf consultation des trajets."),
                        new Tag().name("Sites").description("Gestion des sites de l'entreprise (bureaux, usines, etc.)"),
                        new Tag().name("Départements").description("Gestion des départements de l'entreprise"),
                        new Tag().name("Types de Transport").description("Référentiel des types de transport (Aller, Retour). Lecture seule."),
                        new Tag().name("Heures de Transport").description("Référentiel des créneaux horaires de transport (Matin, Soir, Nuit)"),
                        new Tag().name("Types d'Affectation").description("Référentiel des types d'affectation (Automatique, Manuel). Lecture seule.")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .scheme("bearer")
                                        .description("Saisissez votre token JWT (sans le préfixe 'Bearer')")));
    }
}
