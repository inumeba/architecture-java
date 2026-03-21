package com.exemple.mvcspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * POINT D'ENTRÉE — Classe principale de l'application Spring Boot
 *
 * @SpringBootApplication combine 3 annotations :
 *
 * 1. @Configuration     → Cette classe peut définir des @Bean
 * 2. @EnableAutoConfiguration → Spring Boot configure automatiquement
 *    les composants détectés (Hibernate, Tomcat, Jackson...)
 * 3. @ComponentScan     → Scanne ce package et ses sous-packages
 *    pour trouver les @Controller, @Service, @Repository...
 *
 * Au lancement, Spring Boot :
 * - Démarre un serveur Tomcat embarqué (port 8080)
 * - Configure Hibernate avec la base H2
 * - Crée automatiquement les tables à partir des @Entity
 * - Enregistre tous les composants trouvés par le scan
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // SpringApplication.run() :
        // 1. Crée le contexte Spring (conteneur IoC)
        // 2. Démarre le serveur web embarqué
        // 3. Initialise Hibernate et la base de données
        SpringApplication.run(Application.class, args);
    }
}
