package service_commande;

import commun.BusEvenements;
import commun.Message;

import java.util.List;

/**
 * SERVICE COMMANDE — Microservice autonome
 *
 * Ce service gère le cycle de vie des commandes.
 * Il est TOTALEMENT INDÉPENDANT du service Produit :
 * - Il ne connaît PAS la classe Produit
 * - Il ne sait PAS comment les produits sont stockés
 * - Il communique UNIQUEMENT via le bus d'événements
 *
 * Flux de commande (communication asynchrone) :
 * 1. Le client passe commande → ServiceCommande crée une commande EN_ATTENTE
 * 2. ServiceCommande publie "VERIFIER_PRODUIT" sur le bus
 * 3. ServiceProduit reçoit le message et vérifie la disponibilité
 * 4. ServiceProduit publie "PRODUIT_VERIFIE" ou "PRODUIT_INDISPONIBLE"
 * 5. ServiceCommande reçoit la réponse et valide ou refuse la commande
 * 6. Si validée, publie "COMMANDE_VALIDEE" pour que le stock soit diminué
 *
 * En production : application Spring Boot sur son propre serveur/port.
 */
public class ServiceCommande {

    private static final String NOM = "ServiceCommande";

    // Base de données locale (séparée de celle du service Produit)
    private final CommandeDepot depot = new CommandeDepot();

    // Référence vers le bus pour communiquer
    private final BusEvenements bus;

    /**
     * Constructeur — initialise le service et configure les abonnements.
     */
    public ServiceCommande(BusEvenements bus) {
        this.bus = bus;

        // Écouter les réponses de vérification du service Produit
        bus.abonner("PRODUIT_VERIFIE", this::traiterProduitVerifie);
        bus.abonner("PRODUIT_INDISPONIBLE", this::traiterProduitIndisponible);

        System.out.println("[" + NOM + "] Service demarre et abonne aux evenements.");
    }

    // === API PUBLIQUE DU SERVICE ===

    /**
     * Passer une commande pour un produit.
     * En production : POST /api/commandes { idProduit: 1, quantite: 2 }
     *
     * La commande est créée en état EN_ATTENTE.
     * On demande ensuite au service Produit de vérifier la disponibilité.
     */
    public Commande passerCommande(int idProduit, int quantite) {
        // 1. Créer la commande localement (EN_ATTENTE)
        Commande commande = depot.creer(idProduit, quantite);
        System.out.println("    [" + NOM + "] Commande #" + commande.getId()
                + " creee (produit #" + idProduit + " x" + quantite + ")");

        // 2. Demander au service Produit de vérifier la disponibilité
        //    On ne l'appelle pas directement ! On publie un message.
        bus.publier(new Message(
                "VERIFIER_PRODUIT",
                NOM,
                String.valueOf(idProduit)
        ));

        return commande;
    }

    /**
     * Retourne toutes les commandes.
     * En production : GET /api/commandes
     */
    public List<Commande> listerCommandes() {
        return depot.obtenirToutes();
    }

    // === GESTIONNAIRES D'ÉVÉNEMENTS (privés) ===

    /**
     * Réception : le produit est vérifié et disponible.
     *
     * Le service Produit a confirmé que le produit existe et est en stock.
     * On valide la commande et on publie un événement de confirmation.
     */
    private void traiterProduitVerifie(Message message) {
        // Format : "id|nom|prix"
        String[] parties = message.getContenu().split("\\|");
        int idProduit = Integer.parseInt(parties[0]);
        String nomProduit = parties[1];
        double prix = Double.parseDouble(parties[2]);

        // Retrouver la commande en attente pour ce produit
        Commande commande = depot.trouverDerniereEnAttente(idProduit);
        if (commande != null) {
            // Valider la commande avec les infos du produit
            commande.valider(nomProduit, prix);
            System.out.println("    [" + NOM + "] " + commande);

            // Publier un événement pour notifier les autres services
            // (le service Produit écoutera pour diminuer le stock)
            bus.publier(new Message(
                    "COMMANDE_VALIDEE",
                    NOM,
                    idProduit + "|" + commande.getQuantite()
            ));
        }
    }

    /**
     * Réception : le produit est indisponible.
     *
     * Le service Produit a répondu que le produit n'existe pas
     * ou qu'il est en rupture de stock. On refuse la commande.
     */
    private void traiterProduitIndisponible(Message message) {
        // Format : "id|raison"
        String[] parties = message.getContenu().split("\\|");
        int idProduit = Integer.parseInt(parties[0]);
        String raison = parties[1];

        Commande commande = depot.trouverDerniereEnAttente(idProduit);
        if (commande != null) {
            commande.refuser(raison);
            System.out.println("    [" + NOM + "] " + commande);
        }
    }
}
