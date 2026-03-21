package com.exemple.securite.config;

import com.exemple.securite.depot.UtilisateurDepot;
import com.exemple.securite.modele.Utilisateur;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  INITIALISATION : CRÉATION DES UTILISATEURS DE TEST        ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Se lance au démarrage de l'application et charge deux utilisateurs
 * dans la base de données H2.
 */
@Configuration
public class InitialisateurDonnees {

    @Bean
    public CommandLineRunner initialiserBase(UtilisateurDepot depot, PasswordEncoder encodeur) {
        return args -> {
            System.out.println("⏳ Insertion des utilisateurs de test dans la base de donnees...");
            
            // 1. Un utilisateur standard
            String mdpUser = encodeur.encode("user123");
            Utilisateur utilisateurNormal = new Utilisateur("francois", mdpUser, "ROLE_USER");
            
            // 2. Un administrateur
            String mdpAdmin = encodeur.encode("admin123");
            Utilisateur administrateur = new Utilisateur("alice_admin", mdpAdmin, "ROLE_ADMIN");

            // Sauvegarde en base
            depot.save(utilisateurNormal);
            depot.save(administrateur);

            System.out.println("✅ Comptes crees !");
            System.out.println("   - Simple User : francois / user123 (ROLE_USER)");
            System.out.println("   - Admin       : alice_admin / admin123 (ROLE_ADMIN)");
        };
    }
}
