package domaine.modele;

/**
 * DOMAINE — Entité Produit
 *
 * L'entité est au CŒUR de l'architecture hexagonale.
 * Elle représente un concept métier avec ses règles.
 *
 * RÈGLE FONDAMENTALE de l'hexagonale :
 * Le domaine ne dépend de RIEN d'extérieur.
 * Pas de framework, pas de base de données, pas d'interface graphique.
 * C'est le code le plus pur et le plus stable de l'application.
 *
 * Les dépendances pointent VERS le domaine, jamais l'inverse :
 *
 *   [Adaptateur] → [Port] → [Domaine] ← [Port] ← [Adaptateur]
 *
 * Cette entité contient les RÈGLES MÉTIER :
 * - Un prix ne peut pas être négatif
 * - Le stock ne peut pas descendre en dessous de zéro
 * - Une remise ne peut pas dépasser 50%
 */
public class Produit {

    private final int id;
    private String nom;
    private double prix;
    private int stock;

    /**
     * Constructeur — crée un produit avec validation métier.
     *
     * Les validations sont ICI, dans le domaine, pas dans le contrôleur
     * ni dans la base de données. C'est le domaine qui dit ce qui est valide.
     */
    public Produit(int id, String nom, double prix, int stock) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom du produit ne peut pas etre vide");
        }
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas etre negatif");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Le stock ne peut pas etre negatif");
        }
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.stock = stock;
    }

    // --- Accesseurs ---

    public int getId() { return id; }
    public String getNom() { return nom; }
    public double getPrix() { return prix; }
    public int getStock() { return stock; }

    // --- Logique métier (dans le domaine, pas ailleurs !) ---

    /**
     * Applique une remise au produit.
     *
     * RÈGLE MÉTIER : la remise ne peut pas dépasser 50%.
     * Cette règle est dans le DOMAINE, pas dans le contrôleur.
     * Peu importe que la requête vienne d'une API REST ou d'une CLI,
     * la même règle s'applique.
     *
     * @param pourcentage pourcentage de remise (0-50)
     * @return le nouveau prix après remise
     */
    public double appliquerRemise(double pourcentage) {
        if (pourcentage < 0 || pourcentage > 50) {
            throw new IllegalArgumentException(
                    "La remise doit etre entre 0% et 50% (recue : " + pourcentage + "%)");
        }
        this.prix = this.prix * (1 - pourcentage / 100.0);
        return this.prix;
    }

    /**
     * Retire des unités du stock après une vente.
     *
     * RÈGLE MÉTIER : on ne peut pas vendre plus que le stock disponible.
     *
     * @param quantite nombre d'unités à retirer
     */
    public void retirerDuStock(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive");
        }
        if (quantite > stock) {
            throw new IllegalArgumentException(
                    "Stock insuffisant : demande %d, disponible %d".formatted(quantite, stock));
        }
        this.stock -= quantite;
    }

    /**
     * Ajoute des unités au stock (réapprovisionnement).
     */
    public void ajouterAuStock(int quantite) {
        if (quantite <= 0) {
            throw new IllegalArgumentException("La quantite doit etre positive");
        }
        this.stock += quantite;
    }

    /**
     * Vérifie si le produit est en stock.
     */
    public boolean estDisponible() {
        return stock > 0;
    }

    @Override
    public String toString() {
        return "Produit{id=%d, nom='%s', prix=%.2f EUR, stock=%d}".formatted(id, nom, prix, stock);
    }
}
