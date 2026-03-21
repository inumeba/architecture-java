package adaptateur.sortant;

import domaine.modele.Produit;
import domaine.port.sortant.ProduitRepositoryPort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ADAPTATEUR SORTANT — Persistance en mémoire
 *
 * Un adaptateur sortant IMPLÉMENTE un PORT SORTANT du domaine.
 * Il fournit l'accès à une ressource technique externe.
 *
 * Cet adaptateur stocke les produits dans une simple ArrayList.
 * En production, on aurait :
 * - ProduitPostgresRepository (base de données relationnelle)
 * - ProduitMongoRepository (base NoSQL)
 * - ProduitRedisRepository (cache)
 *
 * Le domaine ne sait PAS quelle implémentation est utilisée.
 * On peut changer de base de données sans modifier
 * une seule ligne du domaine ou des cas d'utilisation.
 *
 * C'est la PUISSANCE de l'inversion de dépendances :
 *
 *   [Use Case] → [ProduitRepositoryPort] ← [ProduitMemoireAdaptateur]
 *                  (interface = contrat)     (implémentation = détail)
 *
 *   Le use case dépend de l'abstraction (→)
 *   L'adaptateur implémente l'abstraction (←)
 *   Le use case ne connaît PAS l'adaptateur
 *
 * En production avec Spring : @Repository implémentant JpaRepository
 */
public class ProduitMemoireAdaptateur implements ProduitRepositoryPort {

    // Simulation d'une base de données en mémoire
    private final List<Produit> baseDeDonnees = new ArrayList<>();

    @Override
    public Produit sauvegarder(Produit produit) {
        // Si le produit existe déjà, on le remplace (mise à jour)
        baseDeDonnees.removeIf(p -> p.getId() == produit.getId());
        baseDeDonnees.add(produit);
        return produit;
    }

    @Override
    public List<Produit> trouverTous() {
        return List.copyOf(baseDeDonnees);
    }

    @Override
    public Optional<Produit> trouverParId(int id) {
        return baseDeDonnees.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    @Override
    public boolean supprimer(int id) {
        return baseDeDonnees.removeIf(p -> p.getId() == id);
    }
}
