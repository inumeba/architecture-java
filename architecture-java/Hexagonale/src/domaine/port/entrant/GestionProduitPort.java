package domaine.port.entrant;

import domaine.modele.Produit;
import java.util.List;
import java.util.Optional;

/**
 * PORT ENTRANT — Interface de gestion des produits
 *
 * Un PORT ENTRANT définit CE QUE l'application PEUT FAIRE.
 * C'est un contrat (interface) que le domaine EXPOSE au monde extérieur.
 *
 * ANALOGIE : c'est comme une PRISE ÉLECTRIQUE sur un mur.
 * - La prise (le port) définit la forme et le voltage
 * - N'importe quel appareil compatible (adaptateur) peut s'y brancher
 * - Le mur (le domaine) ne sait pas quel appareil est branché
 *
 * QUI IMPLÉMENTE ce port ? → Le CAS D'UTILISATION (dans le domaine)
 * QUI APPELLE ce port ?    → Les ADAPTATEURS ENTRANTS (API REST, CLI, tests...)
 *
 * Pourquoi une interface et pas directement la classe ?
 * → Pour que les adaptateurs ne dépendent PAS de l'implémentation
 * → On peut changer la logique métier sans toucher aux adaptateurs
 * → On peut tester avec un mock
 *
 * En production avec Spring : c'est le @Service injectable via @Autowired
 */
public interface GestionProduitPort {

    /**
     * Crée un nouveau produit dans le catalogue.
     */
    Produit creerProduit(String nom, double prix, int stock);

    /**
     * Retourne tous les produits du catalogue.
     */
    List<Produit> listerProduits();

    /**
     * Recherche un produit par son identifiant.
     */
    Optional<Produit> trouverProduit(int id);

    /**
     * Applique une remise à un produit.
     *
     * @param idProduit   identifiant du produit
     * @param pourcentage pourcentage de remise (0-50)
     * @return le produit modifié
     */
    Produit appliquerRemise(int idProduit, double pourcentage);

    /**
     * Effectue une vente (retire du stock).
     *
     * @param idProduit identifiant du produit
     * @param quantite  nombre d'unités vendues
     * @return le produit modifié
     */
    Produit vendre(int idProduit, int quantite);
}
