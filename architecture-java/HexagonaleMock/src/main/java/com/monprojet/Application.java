package com.monprojet;

import com.monprojet.domaine.UtilisateurService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Classe principale Spring Boot.
 * L'injection de dépendances qui était manuelle est maintenant gérée par Spring.
 */
@SpringBootApplication
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Bean exécuté au démarrage de l'application.
     * Spring injecte automatiquement le UtilisateurService ici.
     */
    @Bean
    public CommandLineRunner executerLogiqueMetier(UtilisateurService service) {
        return (args) -> {
            System.out.println("--- Début du traitement avec Spring Boot ---");
            service.creerUtilisateur("1A", "Alice Dupont");
            service.creerUtilisateur("2B", "Bob Martin");
            System.out.println("--- Fin du traitement ---");
        };
    }
}