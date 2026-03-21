package service_commande;

import java.util.ArrayList;
import java.util.List;

/**
 * SERVICE COMMANDE — Dépôt de données local
 *
 * Base de données PROPRE au service Commande.
 * Séparée de celle du service Produit (Database per Service).
 *
 * Le service Commande ne peut PAS lire directement la base
 * du service Produit. S'il a besoin d'infos sur un produit,
 * il doit DEMANDER via le bus d'événements.
 */
public class CommandeDepot {

    private final List<Commande> commandes = new ArrayList<>();
    private int prochainId = 1;

    /**
     * Crée une nouvelle commande en attente de validation.
     */
    public Commande creer(int idProduit, int quantite) {
        Commande commande = new Commande(prochainId++, idProduit, quantite);
        commandes.add(commande);
        return commande;
    }

    /**
     * Retourne toutes les commandes.
     */
    public List<Commande> obtenirToutes() {
        return List.copyOf(commandes);
    }

    /**
     * Recherche la dernière commande en attente pour un produit donné.
     * Utilisé pour mettre à jour la commande après vérification.
     */
    public Commande trouverDerniereEnAttente(int idProduit) {
        for (int i = commandes.size() - 1; i >= 0; i--) {
            Commande c = commandes.get(i);
            if (c.getIdProduit() == idProduit
                    && c.getStatut() == Commande.Statut.EN_ATTENTE) {
                return c;
            }
        }
        return null;
    }
}
