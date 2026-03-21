package domaine.port.sortant;

import domaine.modele.Produit;
import java.util.List;
import java.util.Optional;

/**
 * PORT SORTANT — Interface de persistance des produits
 *
 * Un PORT SORTANT définit CE DONT le domaine A BESOIN du monde extérieur.
 * C'est un contrat que le domaine EXIGE, sans savoir qui le remplit.
 *
 * ANALOGIE : c'est comme un CÂBLE D'ALIMENTATION.
 * - Le domaine dit "j'ai besoin d'électricité" (le port)
 * - Peu importe si ça vient d'une prise murale, d'un générateur ou d'une batterie
 * - L'adaptateur sortant fournit l'implémentation concrète
 *
 * QUI DÉFINIT ce port ?    → Le DOMAINE (il dit ce dont il a besoin)
 * QUI IMPLÉMENTE ce port ? → Les ADAPTATEURS SORTANTS (base de données, API externe, fichier...)
 *
 * C'est l'INVERSION DE DÉPENDANCES en action :
 * - Le domaine définit l'interface (abstraction)
 * - L'adaptateur implémente l'interface (détail)
 * - Le domaine ne connaît PAS l'adaptateur
 * - L'adaptateur connaît le domaine (via l'interface)
 *
 * En production avec Spring : @Repository avec JPA / JDBC / MongoDB
 */
public interface ProduitRepositoryPort {

    /**
     * Sauvegarde un produit (création ou mise à jour).
     */
    Produit sauvegarder(Produit produit);

    /**
     * Retourne tous les produits.
     */
    List<Produit> trouverTous();

    /**
     * Recherche un produit par son identifiant.
     */
    Optional<Produit> trouverParId(int id);

    /**
     * Supprime un produit par son identifiant.
     *
     * @return true si le produit existait
     */
    boolean supprimer(int id);
}
