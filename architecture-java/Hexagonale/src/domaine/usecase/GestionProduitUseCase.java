package domaine.usecase;

import domaine.modele.Produit;
import domaine.port.entrant.GestionProduitPort;
import domaine.port.sortant.NotificationPort;
import domaine.port.sortant.ProduitRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * CAS D'UTILISATION — Gestion des produits
 *
 * Le cas d'utilisation (Use Case) est l'ORCHESTRATEUR du domaine.
 * Il implémente le PORT ENTRANT et coordonne :
 * - Les ENTITÉS (logique métier pure)
 * - Les PORTS SORTANTS (accès aux ressources externes)
 *
 * C'est ici que se trouve la LOGIQUE APPLICATIVE :
 * "quand l'utilisateur fait X, le système fait Y puis Z"
 *
 * ATTENTION à ne pas confondre :
 * - LOGIQUE MÉTIER   → dans l'entité (ex: "un prix ne peut pas être négatif")
 * - LOGIQUE APPLICATIVE → dans le use case (ex: "après une vente, vérifier le stock et notifier")
 *
 * Le use case ne connaît QUE des interfaces (ports), jamais des implémentations.
 * C'est l'INVERSION DE DÉPENDANCES du principe SOLID (le D).
 *
 *   [Adaptateur REST] → [Port Entrant] → [Use Case] → [Port Sortant] → [Adaptateur BDD]
 *          ↑                                                                    ↑
 *     Dépend du port                                                    Implémente le port
 *
 * En production avec Spring : @Service avec injection via constructeur
 */
public class GestionProduitUseCase implements GestionProduitPort {

    // Seuil en dessous duquel le stock est considéré comme bas
    private static final int SEUIL_STOCK_BAS = 3;

    // Ports sortants — le use case dépend d'INTERFACES, pas d'implémentations
    private final ProduitRepositoryPort repository;
    private final NotificationPort notification;

    /**
     * Injection de dépendances via le constructeur.
     *
     * On injecte des PORTS (interfaces), pas des classes concrètes.
     * Cela signifie qu'on peut :
     * - Utiliser une vraie base de données en production
     * - Utiliser une liste en mémoire pour les tests
     * - Changer d'implémentation sans toucher au use case
     */
    public GestionProduitUseCase(ProduitRepositoryPort repository,
                                  NotificationPort notification) {
        this.repository = repository;
        this.notification = notification;
    }

    /**
     * Crée un produit et le persiste.
     *
     * Logique applicative :
     * 1. Créer l'entité (la validation métier est dans le constructeur de Produit)
     * 2. Sauvegarder via le port sortant
     */
    @Override
    public Produit creerProduit(String nom, double prix, int stock) {
        Produit produit = new Produit(
                genererIdUnique(),
                nom,
                prix,
                stock
        );
        return repository.sauvegarder(produit);
    }

    @Override
    public List<Produit> listerProduits() {
        return repository.trouverTous();
    }

    @Override
    public Optional<Produit> trouverProduit(int id) {
        return repository.trouverParId(id);
    }

    /**
     * Applique une remise à un produit.
     *
     * Logique applicative :
     * 1. Chercher le produit (port sortant → repository)
     * 2. Appliquer la remise (logique métier → entité)
     * 3. Sauvegarder le résultat (port sortant → repository)
     */
    @Override
    public Produit appliquerRemise(int idProduit, double pourcentage) {
        Produit produit = repository.trouverParId(idProduit)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produit introuvable : ID " + idProduit));

        // La règle "max 50%" est dans l'entité, PAS ici
        produit.appliquerRemise(pourcentage);

        return repository.sauvegarder(produit);
    }

    /**
     * Effectue une vente : retire du stock et notifie si nécessaire.
     *
     * C'est un bon exemple de LOGIQUE APPLICATIVE :
     * - L'entité sait retirer du stock (règle métier)
     * - Le use case orchestre : retirer, sauvegarder, notifier, alerter
     *
     * Logique applicative :
     * 1. Chercher le produit
     * 2. Retirer du stock (logique métier dans l'entité)
     * 3. Sauvegarder
     * 4. Notifier la vente (port sortant → notification)
     * 5. Si stock bas → alerter (port sortant → notification)
     */
    @Override
    public Produit vendre(int idProduit, int quantite) {
        // 1. Chercher le produit
        Produit produit = repository.trouverParId(idProduit)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produit introuvable : ID " + idProduit));

        // 2. Retirer du stock (la validation est dans l'entité)
        double montant = produit.getPrix() * quantite;
        produit.retirerDuStock(quantite);

        // 3. Sauvegarder
        repository.sauvegarder(produit);

        // 4. Notifier la vente via le port sortant
        notification.notifierVente(produit.getNom(), quantite, montant);

        // 5. Si le stock est bas, envoyer une alerte
        if (produit.getStock() <= SEUIL_STOCK_BAS) {
            notification.alerterStockBas(produit.getNom(), produit.getStock());
        }

        return produit;
    }

    /**
     * Génère un ID unique simple.
     * En production : UUID ou séquence de la base de données.
     */
    private int genererIdUnique() {
        return (int) (System.nanoTime() % 100_000);
    }
}
