package controleur;

import modele.Produit;
import modele.ProduitDepot;
import vue.ProduitVue;

import java.util.List;

/**
 * CONTRÔLEUR — Orchestre le Modèle et la Vue
 *
 * Le contrôleur est le CHEF D'ORCHESTRE de l'application MVC.
 * Il reçoit les actions de l'utilisateur, interroge le modèle,
 * puis transmet les résultats à la vue pour affichage.
 *
 * Responsabilités du contrôleur :
 * 1. Recevoir les entrées utilisateur (ici via des appels de méthode)
 * 2. Appeler le modèle pour lire ou modifier les données
 * 3. Choisir quelle vue afficher et avec quelles données
 *
 * Ce que le contrôleur ne fait PAS :
 * - Il ne contient pas de logique métier → c'est le rôle du modèle
 * - Il ne formate pas l'affichage → c'est le rôle de la vue
 */
public class ProduitControleur {

    // Le contrôleur possède une référence vers le modèle ET la vue
    private final ProduitDepot depot;
    private final ProduitVue vue;

    /**
     * Injection de dépendances via le constructeur.
     *
     * Le contrôleur ne crée pas le dépôt ni la vue lui-même.
     * On les lui fournit de l'extérieur → cela facilite les tests
     * et permet de changer l'implémentation sans modifier le contrôleur.
     */
    public ProduitControleur(ProduitDepot depot, ProduitVue vue) {
        this.depot = depot;
        this.vue = vue;
    }

    /**
     * Action : créer un nouveau produit.
     *
     * Flux MVC :
     * Utilisateur → Contrôleur → Modèle (ajouter) → Contrôleur → Vue (confirmer)
     */
    public void creerProduit(String nom, double prix) {
        // 1. Demande au modèle de créer le produit
        Produit nouveau = depot.ajouter(nom, prix);

        // 2. Demande à la vue d'afficher la confirmation
        vue.afficherMessage("Produit cree : " + nouveau.getNom() + " (ID: " + nouveau.getId() + ")");
    }

    /**
     * Action : afficher tous les produits.
     *
     * Flux MVC :
     * Utilisateur → Contrôleur → Modèle (lire) → Contrôleur → Vue (afficher)
     */
    public void listerProduits() {
        // 1. Récupère les données depuis le modèle
        List<Produit> produits = depot.obtenirTous();

        // 2. Transmet les données à la vue pour affichage
        vue.afficherListeProduits(produits);
    }

    /**
     * Action : afficher le détail d'un produit par son ID.
     *
     * Illustre la gestion d'erreur dans le contrôleur :
     * si le produit n'existe pas, on demande à la vue d'afficher une erreur.
     */
    public void afficherProduit(int id) {
        // Utilise Optional pour gérer proprement l'absence de résultat
        depot.trouverParId(id).ifPresentOrElse(
                // Cas trouvé → afficher le détail
                vue::afficherDetailProduit,
                // Cas non trouvé → afficher l'erreur
                () -> vue.afficherErreur("Aucun produit trouve avec l'ID " + id)
        );
    }

    /**
     * Action : supprimer un produit.
     */
    public void supprimerProduit(int id) {
        boolean supprime = depot.supprimer(id);

        if (supprime) {
            vue.afficherMessage("Produit #" + id + " supprime.");
        } else {
            vue.afficherErreur("Impossible de supprimer : produit #" + id + " introuvable.");
        }
    }
}
