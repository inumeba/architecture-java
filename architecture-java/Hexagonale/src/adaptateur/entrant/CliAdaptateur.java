package adaptateur.entrant;

import domaine.modele.Produit;
import domaine.port.entrant.GestionProduitPort;

import java.util.List;

/**
 * ADAPTATEUR ENTRANT — Interface en ligne de commande (CLI)
 *
 * Un DEUXIÈME adaptateur entrant pour illustrer un point clé
 * de l'architecture hexagonale : le MÊME DOMAINE peut être
 * accédé par PLUSIEURS points d'entrée différents.
 *
 * Ici, on a :
 * - Un adaptateur REST (ApiRestAdaptateur) → pour les clients HTTP
 * - Un adaptateur CLI (celui-ci) → pour l'administration en ligne de commande
 *
 * Les deux utilisent le MÊME port entrant (GestionProduitPort)
 * et donc la MÊME logique métier. Pas de duplication !
 *
 *   [Client HTTP] → [Adaptateur REST] ──┐
 *                                        ├──→ [Port Entrant] → [Use Case]
 *   [Admin CLI]   → [Adaptateur CLI]  ──┘
 */
public class CliAdaptateur {

    private final GestionProduitPort gestionProduit;

    public CliAdaptateur(GestionProduitPort gestionProduit) {
        this.gestionProduit = gestionProduit;
    }

    /**
     * Commande CLI : lister le catalogue.
     * Affiche dans un format différent de l'API REST.
     */
    public void commandeLister() {
        System.out.println("\n$ catalogue --lister");
        List<Produit> produits = gestionProduit.listerProduits();

        System.out.println("  Catalogue (" + produits.size() + " produits) :");
        for (Produit p : produits) {
            String dispo = p.estDisponible() ? "EN STOCK" : "RUPTURE";
            System.out.printf("  [%d] %s - %.2f EUR (%d unites) [%s]%n",
                    p.getId(), p.getNom(), p.getPrix(), p.getStock(), dispo);
        }
    }

    /**
     * Commande CLI : vente rapide.
     */
    public void commandeVendre(int idProduit, int quantite) {
        System.out.println("\n$ vente --produit " + idProduit + " --quantite " + quantite);
        try {
            Produit produit = gestionProduit.vendre(idProduit, quantite);
            System.out.println("  Vente effectuee : " + produit);
        } catch (IllegalArgumentException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }
    }
}
