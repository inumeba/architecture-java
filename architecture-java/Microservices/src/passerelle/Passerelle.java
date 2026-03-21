package passerelle;

import service_commande.Commande;
import service_commande.ServiceCommande;
import service_produit.Produit;
import service_produit.ServiceProduit;

import java.util.List;

/**
 * PASSERELLE API (API Gateway)
 *
 * La passerelle est le POINT D'ENTRÉE UNIQUE pour les clients externes.
 * Au lieu de contacter chaque microservice directement, le client
 * envoie toutes ses requêtes à la passerelle, qui les redirige.
 *
 * Responsabilités d'une API Gateway :
 * 1. ROUTAGE    — diriger les requêtes vers le bon service
 * 2. AGRÉGATION — combiner les réponses de plusieurs services
 * 3. SÉCURITÉ   — authentification, autorisation (non illustré ici)
 * 4. MONITORING — logs, métriques, rate limiting (non illustré ici)
 *
 * Sans passerelle, le client devrait connaître l'adresse de chaque service :
 *   - http://produits:8081/api/produits
 *   - http://commandes:8082/api/commandes
 *
 * Avec passerelle, une seule adresse suffit :
 *   - http://api-gateway:8080/produits
 *   - http://api-gateway:8080/commandes
 *
 * En production : Spring Cloud Gateway, Kong, Nginx, Envoy, AWS API Gateway
 */
public class Passerelle {

    private static final String NOM = "Passerelle";

    // Références vers les services (en production : découverte de services)
    private final ServiceProduit serviceProduit;
    private final ServiceCommande serviceCommande;

    /**
     * Constructeur — enregistre les services disponibles.
     *
     * En production, la passerelle ne connaît pas les services directement.
     * Elle utilise un "Service Registry" (Consul, Eureka) pour les découvrir.
     */
    public Passerelle(ServiceProduit serviceProduit, ServiceCommande serviceCommande) {
        this.serviceProduit = serviceProduit;
        this.serviceCommande = serviceCommande;
        System.out.println("[" + NOM + "] Gateway demarree. Routes disponibles :");
        System.out.println("    /produits       → ServiceProduit");
        System.out.println("    /commandes      → ServiceCommande");
    }

    // === ROUTES PRODUITS ===
    // En production : @GetMapping("/produits")

    /**
     * GET /produits — Liste tous les produits.
     * Redirige vers le service Produit.
     */
    public void getProduits() {
        System.out.println("\n>> GET /produits");
        List<Produit> produits = serviceProduit.listerProduits();

        System.out.println("  %-4s | %-20s | %10s | %5s".formatted("ID", "Nom", "Prix", "Stock"));
        System.out.println("  -----|----------------------|------------|------");
        for (Produit p : produits) {
            System.out.println("  %-4d | %-20s | %7.2f EUR | %5d".formatted(
                    p.getId(), p.getNom(), p.getPrix(), p.getStock()));
        }
    }

    // === ROUTES COMMANDES ===

    /**
     * POST /commandes — Passer une nouvelle commande.
     * Redirige vers le service Commande, qui déclenchera
     * la vérification du produit via le bus d'événements.
     */
    public void postCommande(int idProduit, int quantite) {
        System.out.println("\n>> POST /commandes { idProduit: %d, quantite: %d }".formatted(idProduit, quantite));
        serviceCommande.passerCommande(idProduit, quantite);
    }

    /**
     * GET /commandes — Liste toutes les commandes.
     * Redirige vers le service Commande.
     */
    public void getCommandes() {
        System.out.println("\n>> GET /commandes");
        List<Commande> commandes = serviceCommande.listerCommandes();

        if (commandes.isEmpty()) {
            System.out.println("  (aucune commande)");
        } else {
            for (Commande c : commandes) {
                System.out.println("  " + c);
            }
        }
    }

    /**
     * GET /resume — Agrégation de données de PLUSIEURS services.
     *
     * C'est une fonctionnalité clé de la passerelle :
     * combiner les données de différents services en une seule réponse.
     * Le client n'a pas besoin de faire 2 appels séparés.
     */
    public void getResume() {
        System.out.println("\n>> GET /resume (agregation multi-services)");
        System.out.println("  --- Produits en catalogue : " + serviceProduit.listerProduits().size());
        System.out.println("  --- Commandes passees     : " + serviceCommande.listerCommandes().size());

        long validees = serviceCommande.listerCommandes().stream()
                .filter(c -> c.getStatut() == Commande.Statut.VALIDEE)
                .count();
        long refusees = serviceCommande.listerCommandes().stream()
                .filter(c -> c.getStatut() == Commande.Statut.REFUSEE)
                .count();

        System.out.println("  --- Commandes validees    : " + validees);
        System.out.println("  --- Commandes refusees    : " + refusees);
    }
}
