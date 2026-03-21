package main;

import modele.Produit;
import modele.Commande;
import exemples.*;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ENTRY POINT DU PROJET STREAMS CAS DE FIGURES                ║
 * ╚══════════════════════════════════════════════════════════════╝
 * - Ce fichier regroupe et appelle tous les exemples du projet.
 * - Le modèle (Produit / Commande) est partagé ici à travers toutes
 *   les démonstrations pour de la constance.
 */
public class Application {

    public static void main(String[] args) {

        System.out.println("┌────────────────────────────────────────────────────────┐");
        System.out.println("│  DÉMONSTRATION JAVA STREAMS - ARCHITECTURE DE RÉFÉRENCE │");
        System.out.println("└────────────────────────────────────────────────────────┘");

        // 1. Initialiser le jeu de données métier commun
        List<Produit> catalogueInitial = genererCatalogue();
        List<Commande> commandesInitiales = genererCommandes(catalogueInitial);

        // 2. Appel des modules de démonstration séquentiellement
        CreationStreams.demonstrer();
        OperationsIntermediaires.demonstrer(catalogueInitial, commandesInitiales);
        OperationsTerminales.demonstrer(catalogueInitial, commandesInitiales);
        GroupementEtPartition.demonstrer(catalogueInitial, commandesInitiales);
        CasAvances.demonstrer();

        System.out.println("\n✅ FIN DU PARCOURS STREAMS.");
    }

    // ==========================================
    // MÉTHODES FACTORY : GÉNÉRATION DES DONNÉES
    // ==========================================

    private static List<Produit> genererCatalogue() {
        return List.of(
                new Produit("MacBook Pro M3", "Electronique", 2499.0),
                new Produit("Chaise Ergonomique", "Mobilier", 350.0),
                new Produit("Clavier Mécanique", "Electronique", 120.0),
                new Produit("Bureau Assis-Debout", "Mobilier", 800.0),
                new Produit("Souris Logitech", "Electronique", 85.0),
                // Cas particuliers pour tests filter/map/reduce
                new Produit("Tasse à café", "Accessoire", 12.0),
                new Produit("Stylo Plume", "Accessoire", 55.0)
        );
    }

    private static List<Commande> genererCommandes(List<Produit> catalogueBase) {
        // Commande 1 : Gros client, contient 3 produits
        Commande c1 = new Commande(
                "CMD-2024-001",
                "Alice",
                List.of(
                        catalogueBase.get(0), // MacBook
                        catalogueBase.get(2), // Clavier
                        catalogueBase.get(4)  // Souris
                ),
                true  // Livrée
        );

        // Commande 2 : Setup bureau modeste (2 produits)
        Commande c2 = new Commande(
                "CMD-2024-002",
                "Bob",
                List.of(
                        catalogueBase.get(1), // Chaise
                        catalogueBase.get(3)  // Bureau
                ),
                false // NON Livrée
        );

        // Commande 3 : Juste une tasse
        Commande c3 = new Commande(
                "CMD-2024-003",
                "Charlie",
                List.of(catalogueBase.get(5)), // Tasse
                true  // Livrée
        );

        return List.of(c1, c2, c3);
    }
}
