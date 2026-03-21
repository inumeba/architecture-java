package vue;

import modele.Produit;

import java.util.List;

/**
 * VUE — Affichage des produits en console
 *
 * La vue est UNIQUEMENT responsable de la présentation.
 * Elle ne contient aucune logique métier.
 * Elle ne modifie jamais le modèle directement.
 *
 * Dans MVC :
 * - La vue REÇOIT les données du contrôleur
 * - La vue AFFICHE ces données à l'utilisateur
 * - La vue NE DÉCIDE PAS quoi afficher (c'est le contrôleur qui choisit)
 *
 * En production, cette vue console serait remplacée par :
 * - une page HTML (Spring MVC / Thymeleaf)
 * - une interface Swing/JavaFX
 * - une API JSON (REST)
 */
public class ProduitVue {

    /**
     * Affiche la liste de tous les produits sous forme de tableau.
     * Si la liste est vide, affiche un message approprié.
     */
    public void afficherListeProduits(List<Produit> produits) {
        System.out.println("\n══════════════════════════════════════");
        System.out.println("         LISTE DES PRODUITS          ");
        System.out.println("══════════════════════════════════════");

        if (produits.isEmpty()) {
            System.out.println("  Aucun produit en stock.");
        } else {
            // En-tête du tableau
            System.out.printf("  %-4s | %-15s | %8s%n", "ID", "Nom", "Prix");
            System.out.println("  -----|-----------------|----------");

            // Chaque ligne = un produit
            for (Produit p : produits) {
                System.out.printf("  %-4d | %-15s | %7.2f EUR%n",
                        p.getId(), p.getNom(), p.getPrix());
            }
        }

        System.out.println("══════════════════════════════════════\n");
    }

    /**
     * Affiche les détails d'un seul produit.
     */
    public void afficherDetailProduit(Produit produit) {
        System.out.println("\n-- Detail du produit --");
        System.out.println("  ID   : " + produit.getId());
        System.out.println("  Nom  : " + produit.getNom());
        System.out.printf("  Prix : %.2f EUR%n", produit.getPrix());
    }

    /**
     * Affiche un message de confirmation après une action.
     */
    public void afficherMessage(String message) {
        System.out.println("[OK] " + message);
    }

    /**
     * Affiche un message d'erreur.
     */
    public void afficherErreur(String message) {
        System.out.println("[ERREUR] " + message);
    }
}
