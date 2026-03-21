package service_produit;

import commun.BusEvenements;
import commun.Message;

import java.util.List;
import java.util.Optional;

/**
 * SERVICE PRODUIT — Microservice autonome
 *
 * Ce service est INDÉPENDANT et AUTONOME :
 * - Il a ses propres données (ProduitDepot)
 * - Il a sa propre logique métier
 * - Il communique avec les autres services UNIQUEMENT via le bus d'événements
 * - Il peut être déployé, mis à jour et redémarré INDÉPENDAMMENT
 *
 * Responsabilités :
 * - Gérer le catalogue de produits (CRUD)
 * - Vérifier la disponibilité des produits
 * - Diminuer le stock après une commande validée
 *
 * En production, ce serait une application Spring Boot séparée
 * avec son propre serveur, son propre port, sa propre base de données.
 */
public class ServiceProduit {

    // Nom du service (pour identifier les messages)
    private static final String NOM = "ServiceProduit";

    // Base de données locale du service
    private final ProduitDepot depot = new ProduitDepot();

    // Référence vers le bus d'événements pour communiquer
    private final BusEvenements bus;

    /**
     * Constructeur — initialise le service et configure les abonnements.
     *
     * Le service s'abonne aux événements qui le concernent.
     * Il ne connaît PAS les autres services, seulement les types de messages.
     */
    public ServiceProduit(BusEvenements bus) {
        this.bus = bus;

        // S'abonner aux demandes de vérification de produit
        // Quand le service Commande veut vérifier un produit,
        // il publie "VERIFIER_PRODUIT" et ce service répond.
        bus.abonner("VERIFIER_PRODUIT", this::traiterVerification);

        // S'abonner aux notifications de commande validée
        // pour diminuer le stock automatiquement
        bus.abonner("COMMANDE_VALIDEE", this::traiterCommandeValidee);

        System.out.println("[" + NOM + "] Service demarre et abonne aux evenements.");
    }

    /**
     * Initialise le catalogue avec des produits de démonstration.
     */
    public void initialiserCatalogue() {
        depot.ajouter("Clavier mecanique", 89.99, 15);
        depot.ajouter("Souris sans fil", 39.99, 30);
        depot.ajouter("Ecran 27 pouces", 349.00, 5);
        depot.ajouter("Casque audio", 129.99, 0); // Rupture de stock !
        System.out.println("[" + NOM + "] Catalogue initialise avec " + depot.obtenirTous().size() + " produits.");
    }

    // === API PUBLIQUE DU SERVICE ===
    // Ces méthodes seraient exposées via une API REST en production
    // (ex: GET /api/produits, GET /api/produits/{id})

    /**
     * Retourne tous les produits du catalogue.
     * En production : GET /api/produits
     */
    public List<Produit> listerProduits() {
        return depot.obtenirTous();
    }

    /**
     * Retourne un produit par son ID.
     * En production : GET /api/produits/{id}
     */
    public Optional<Produit> obtenirProduit(int id) {
        return depot.trouverParId(id);
    }

    // === GESTIONNAIRES D'ÉVÉNEMENTS (privés) ===
    // Ces méthodes sont appelées automatiquement par le bus

    /**
     * Traite une demande de vérification de produit.
     *
     * Quand un autre service veut savoir si un produit est disponible,
     * il publie "VERIFIER_PRODUIT" avec l'ID du produit.
     * Ce service vérifie et répond avec "PRODUIT_VERIFIE" ou "PRODUIT_INDISPONIBLE".
     */
    private void traiterVerification(Message message) {
        int idProduit = Integer.parseInt(message.getContenu());

        Optional<Produit> produit = depot.trouverParId(idProduit);

        if (produit.isPresent() && produit.get().getStock() > 0) {
            // Produit trouvé et en stock → répondre positivement
            // On utilise un format avec point décimal (pas de virgule)
            // pour que le parsing fonctionne quelle que soit la locale
            String reponse = produit.get().getId()
                    + "|" + produit.get().getNom()
                    + "|" + produit.get().getPrix();
            bus.publier(new Message("PRODUIT_VERIFIE", NOM, reponse));
        } else {
            // Produit introuvable ou en rupture de stock
            String raison = produit.isEmpty() ? "produit introuvable" : "rupture de stock";
            bus.publier(new Message("PRODUIT_INDISPONIBLE", NOM, idProduit + "|" + raison));
        }
    }

    /**
     * Traite la confirmation d'une commande validée.
     *
     * Quand une commande est validée, le stock doit être diminué.
     * Le contenu du message contient l'ID du produit et la quantité.
     */
    private void traiterCommandeValidee(Message message) {
        // Format du message : "idProduit|quantite"
        String[] parties = message.getContenu().split("\\|");
        int idProduit = Integer.parseInt(parties[0]);
        int quantite = Integer.parseInt(parties[1]);

        depot.trouverParId(idProduit).ifPresent(produit -> {
            produit.diminuerStock(quantite);
            System.out.println("    [" + NOM + "] Stock mis a jour : " + produit);
        });
    }
}
