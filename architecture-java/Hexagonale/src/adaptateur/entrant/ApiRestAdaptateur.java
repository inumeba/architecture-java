package adaptateur.entrant;

import domaine.modele.Produit;
import domaine.port.entrant.GestionProduitPort;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTATEUR ENTRANT — API REST (simulée)
 *
 * Un adaptateur entrant BRANCHE un point d'entrée externe
 * sur un PORT ENTRANT du domaine.
 *
 * Cet adaptateur simule une API REST.
 * En production avec Spring, ce serait un @RestController.
 *
 * RÈGLE DE L'HEXAGONALE :
 * L'adaptateur connaît le port (interface), PAS l'implémentation.
 * Il ne sait pas si derrière le port il y a :
 * - Le vrai use case avec une base PostgreSQL
 * - Un mock pour les tests
 * - Une implémentation en cache
 *
 * L'adaptateur entrant a 2 responsabilités :
 * 1. TRADUIRE la requête externe → appel au port entrant
 * 2. TRADUIRE la réponse du port → format externe (JSON, HTML...)
 *
 *   [Client HTTP] → [Adaptateur REST] → [Port Entrant] → [Use Case]
 */
public class ApiRestAdaptateur {

    // L'adaptateur dépend du PORT (interface), pas du use case directement
    private final GestionProduitPort gestionProduit;

    /**
     * Injection du port entrant.
     *
     * En production avec Spring :
     * @Autowired
     * public ProduitController(GestionProduitPort gestionProduit) { ... }
     */
    public ApiRestAdaptateur(GestionProduitPort gestionProduit) {
        this.gestionProduit = gestionProduit;
    }

    /**
     * GET /api/produits — Liste tous les produits.
     *
     * L'adaptateur :
     * 1. Reçoit la requête HTTP (simulée ici)
     * 2. Appelle le port entrant
     * 3. Formate la réponse (en production : JSON)
     */
    public void getProduits() {
        System.out.println("\n>> GET /api/produits");
        List<Produit> produits = gestionProduit.listerProduits();

        if (produits.isEmpty()) {
            System.out.println("   Reponse 200 : []");
        } else {
            System.out.println("   Reponse 200 :");
            System.out.printf("   %-4s | %-20s | %10s | %5s%n", "ID", "Nom", "Prix", "Stock");
            System.out.println("   -----|----------------------|------------|------");
            for (Produit p : produits) {
                System.out.printf("   %-4d | %-20s | %7.2f EUR | %5d%n",
                        p.getId(), p.getNom(), p.getPrix(), p.getStock());
            }
        }
    }

    /**
     * GET /api/produits/{id} — Détail d'un produit.
     */
    public void getProduit(int id) {
        System.out.println("\n>> GET /api/produits/" + id);
        Optional<Produit> produit = gestionProduit.trouverProduit(id);

        if (produit.isPresent()) {
            System.out.println("   Reponse 200 : " + produit.get());
        } else {
            System.out.println("   Reponse 404 : Produit non trouve");
        }
    }

    /**
     * POST /api/produits — Créer un produit.
     */
    public void postProduit(String nom, double prix, int stock) {
        System.out.println("\n>> POST /api/produits { nom: \"%s\", prix: %.2f, stock: %d }"
                .formatted(nom, prix, stock));
        try {
            Produit produit = gestionProduit.creerProduit(nom, prix, stock);
            System.out.println("   Reponse 201 : " + produit);
        } catch (IllegalArgumentException e) {
            System.out.println("   Reponse 400 : " + e.getMessage());
        }
    }

    /**
     * PATCH /api/produits/{id}/remise — Appliquer une remise.
     */
    public void patchRemise(int idProduit, double pourcentage) {
        System.out.println("\n>> PATCH /api/produits/%d/remise { pourcentage: %.0f }"
                .formatted(idProduit, pourcentage));
        try {
            Produit produit = gestionProduit.appliquerRemise(idProduit, pourcentage);
            System.out.println("   Reponse 200 : " + produit);
        } catch (IllegalArgumentException e) {
            System.out.println("   Reponse 400 : " + e.getMessage());
        }
    }

    /**
     * POST /api/produits/{id}/vente — Effectuer une vente.
     */
    public void postVente(int idProduit, int quantite) {
        System.out.println("\n>> POST /api/produits/%d/vente { quantite: %d }"
                .formatted(idProduit, quantite));
        try {
            Produit produit = gestionProduit.vendre(idProduit, quantite);
            System.out.println("   Reponse 200 : " + produit);
        } catch (IllegalArgumentException e) {
            System.out.println("   Reponse 400 : " + e.getMessage());
        }
    }
}
