package service_produit;

/**
 * SERVICE PRODUIT — Entité Produit
 *
 * Chaque microservice possède SES PROPRES entités.
 * Même si le service Commande a aussi besoin d'infos sur les produits,
 * il ne partage PAS cette classe. Il a sa propre représentation.
 *
 * C'est le principe de "Bounded Context" (contexte délimité) du DDD :
 * chaque service définit ses propres modèles de données.
 */
public class Produit {

    private final int id;
    private final String nom;
    private final double prix;
    private int stock;

    public Produit(int id, String nom, double prix, int stock) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public double getPrix() {
        return prix;
    }

    public int getStock() {
        return stock;
    }

    /**
     * Diminue le stock après une vente.
     * Règle métier : on ne peut pas vendre plus que le stock disponible.
     *
     * @param quantite nombre d'unités à retirer
     * @return true si le stock était suffisant
     */
    public boolean diminuerStock(int quantite) {
        if (quantite <= 0 || quantite > stock) {
            return false;
        }
        stock -= quantite;
        return true;
    }

    @Override
    public String toString() {
        return "%s (%.2f EUR, stock: %d)".formatted(nom, prix, stock);
    }
}
