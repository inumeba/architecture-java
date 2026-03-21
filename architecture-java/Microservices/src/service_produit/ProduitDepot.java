package service_produit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SERVICE PRODUIT — Dépôt de données local
 *
 * Chaque microservice gère SA PROPRE base de données.
 * C'est le pattern "Database per Service" :
 * - Le service Produit a sa base (ici, une liste en mémoire)
 * - Le service Commande a sa propre base, séparée
 * - Aucun service n'accède à la base d'un autre
 *
 * En production : chaque service aurait sa propre instance
 * PostgreSQL, MongoDB, Redis, etc.
 */
public class ProduitDepot {

    private final List<Produit> produits = new ArrayList<>();
    private int prochainId = 1;

    /**
     * Ajoute un produit au catalogue avec un stock initial.
     */
    public Produit ajouter(String nom, double prix, int stock) {
        Produit produit = new Produit(prochainId++, nom, prix, stock);
        produits.add(produit);
        return produit;
    }

    /**
     * Retourne tous les produits du catalogue.
     */
    public List<Produit> obtenirTous() {
        return List.copyOf(produits);
    }

    /**
     * Recherche un produit par son identifiant.
     */
    public Optional<Produit> trouverParId(int id) {
        return produits.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }
}
