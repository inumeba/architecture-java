package modele;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MODÈLE — Dépôt de produits (Repository)
 *
 * Cette classe gère la persistance des produits.
 * Ici, on simule une base de données avec une simple liste en mémoire.
 *
 * En production, cette classe serait remplacée par un accès
 * à une vraie base de données (JDBC, JPA/Hibernate, etc.).
 *
 * Le dépôt fait partie du modèle car il gère le cycle de vie des données.
 * Le contrôleur l'utilise, mais ne sait pas comment les données sont stockées.
 */
public class ProduitDepot {

    // Stockage en mémoire — simule une table en base de données
    private final List<Produit> produits = new ArrayList<>();

    // Compteur auto-incrémenté pour les identifiants
    private int prochainId = 1;

    /**
     * Ajoute un nouveau produit au dépôt.
     * L'identifiant est généré automatiquement.
     *
     * @param nom  nom du produit
     * @param prix prix du produit
     * @return le produit créé avec son identifiant
     */
    public Produit ajouter(String nom, double prix) {
        Produit produit = new Produit(prochainId++, nom, prix);
        produits.add(produit);
        return produit;
    }

    /**
     * Retourne tous les produits.
     * On renvoie une copie pour protéger la liste interne (encapsulation).
     */
    public List<Produit> obtenirTous() {
        return List.copyOf(produits);
    }

    /**
     * Recherche un produit par son identifiant.
     * Utilise Optional pour éviter les NullPointerException.
     */
    public Optional<Produit> trouverParId(int id) {
        return produits.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    /**
     * Supprime un produit par son identifiant.
     *
     * @return true si le produit existait et a été supprimé
     */
    public boolean supprimer(int id) {
        return produits.removeIf(p -> p.getId() == id);
    }
}
