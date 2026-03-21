package com.exemple.mvcspringboot.config;

import com.exemple.mvcspringboot.depot.ProduitRepository;
import com.exemple.mvcspringboot.modele.Produit;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * CONFIGURATION — Données initiales pour le développement
 *
 * @Configuration : marque cette classe comme source de configuration Spring.
 * Elle peut définir des @Bean qui seront gérés par le conteneur IoC.
 *
 * @Bean : la méthode retourne un objet que Spring enregistre dans son contexte.
 * Ici, le CommandLineRunner est exécuté au DÉMARRAGE de l'application.
 *
 * EN PRODUCTION :
 * → Utiliser Flyway ou Liquibase pour gérer les migrations de schéma
 * → Les données initiales viendraient de scripts SQL versionnés
 * → Cette classe ne serait active qu'en profil "dev" (@Profile("dev"))
 */
@Configuration
public class DonneesInitiales {

    /**
     * CommandLineRunner : interface fonctionnelle exécutée au démarrage.
     *
     * Spring injecte le ProduitRepository automatiquement (car c'est un @Bean).
     * On insère quelques produits de test pour pouvoir tester l'API
     * immédiatement sans avoir à faire des POST manuels.
     */
    @Bean
    CommandLineRunner initialiserDonnees(ProduitRepository produitRepository) {
        return args -> {
            // Vérifier que la base est vide avant d'insérer
            if (produitRepository.count() == 0) {
                produitRepository.save(new Produit(
                        "MacBook Pro 16\"",
                        "Ordinateur portable Apple avec puce M3 Pro, 18 Go RAM",
                        new BigDecimal("2799.99"),
                        15
                ));

                produitRepository.save(new Produit(
                        "Clavier Mécanique",
                        "Clavier gaming RGB avec switches Cherry MX Red",
                        new BigDecimal("129.90"),
                        42
                ));

                produitRepository.save(new Produit(
                        "Souris Ergonomique",
                        "Souris verticale sans fil, rechargeable USB-C",
                        new BigDecimal("49.95"),
                        0  // En rupture de stock !
                ));

                produitRepository.save(new Produit(
                        "Écran 4K 27\"",
                        "Moniteur IPS 4K UHD, 60Hz, USB-C avec charge 90W",
                        new BigDecimal("549.00"),
                        8
                ));

                System.out.println("=== 4 produits de test insérés en base ===");
            }
        };
    }
}
