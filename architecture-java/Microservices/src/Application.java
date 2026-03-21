import commun.BusEvenements;
import passerelle.Passerelle;
import service_commande.ServiceCommande;
import service_produit.ServiceProduit;

/**
 * POINT D'ENTRÉE — Démonstration de l'architecture Microservices
 *
 * Cette classe simule le déploiement et l'interaction
 * entre plusieurs microservices autonomes.
 *
 * En production, chaque service serait :
 * - Un projet Maven/Gradle SÉPARÉ
 * - Une application Spring Boot avec son propre main()
 * - Déployé dans son propre conteneur Docker
 * - Avec sa propre base de données
 * - Communiquant via Kafka / RabbitMQ / HTTP
 *
 * Ici, tout tourne dans un seul processus pour simplifier la démonstration.
 * Le bus d'événements simule la messagerie asynchrone.
 */
public class Application {

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println("  ARCHITECTURE MICROSERVICES");
        System.out.println("  Demonstration Java");
        System.out.println("====================================\n");

        // =============================================================
        // ÉTAPE 1 : Créer l'infrastructure de communication
        // =============================================================
        // Le bus d'événements est le lien entre tous les services.
        // En production : Apache Kafka, RabbitMQ, AWS SNS/SQS

        System.out.println("--- DEMARRAGE DE L'INFRASTRUCTURE ---");
        BusEvenements bus = new BusEvenements();

        // =============================================================
        // ÉTAPE 2 : Démarrer les microservices
        // =============================================================
        // Chaque service s'initialise et s'abonne aux événements
        // qui le concernent. L'ordre de démarrage n'a pas d'importance.

        System.out.println("\n--- DEMARRAGE DES SERVICES ---");
        ServiceProduit serviceProduit = new ServiceProduit(bus);
        ServiceCommande serviceCommande = new ServiceCommande(bus);

        // Initialiser les données de démonstration
        serviceProduit.initialiserCatalogue();

        // =============================================================
        // ÉTAPE 3 : Démarrer la passerelle (API Gateway)
        // =============================================================

        System.out.println("\n--- DEMARRAGE DE LA PASSERELLE ---");
        Passerelle api = new Passerelle(serviceProduit, serviceCommande);

        // =============================================================
        // ÉTAPE 4 : Simuler des requêtes client via la passerelle
        // =============================================================

        System.out.println("\n\n========================================");
        System.out.println("  SIMULATION DES REQUETES CLIENT");
        System.out.println("========================================");

        // --- Requête 1 : Consulter le catalogue ---
        api.getProduits();

        // --- Requête 2 : Commander un produit disponible ---
        // Le flux complet se déclenche :
        // Client → Passerelle → ServiceCommande → Bus → ServiceProduit → Bus → ServiceCommande → Bus → ServiceProduit (stock)
        System.out.println("\n--- Commander 2 claviers (produit #1) ---");
        api.postCommande(1, 2);

        // --- Requête 3 : Commander un autre produit ---
        System.out.println("\n--- Commander 1 ecran (produit #3) ---");
        api.postCommande(3, 1);

        // --- Requête 4 : Commander un produit en rupture de stock ---
        System.out.println("\n--- Commander 1 casque (produit #4 - rupture !) ---");
        api.postCommande(4, 1);

        // --- Requête 5 : Commander un produit inexistant ---
        System.out.println("\n--- Commander un produit #99 (inexistant !) ---");
        api.postCommande(99, 1);

        // --- Requête 6 : Voir toutes les commandes ---
        System.out.println("\n--- Liste des commandes ---");
        api.getCommandes();

        // --- Requête 7 : Vérifier le stock mis à jour ---
        System.out.println("\n--- Catalogue apres les commandes ---");
        api.getProduits();

        // --- Requête 8 : Résumé agrégé multi-services ---
        api.getResume();

        // =============================================================
        // ÉTAPE 5 : Afficher le journal des événements
        // =============================================================
        // Permet de voir TOUS les messages échangés entre les services
        bus.afficherJournal();
    }
}
