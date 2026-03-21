package modele;

/**
 * MODÈLE — Entité Produit
 *
 * Le modèle représente les données de l'application.
 * Il ne connaît ni la vue, ni le contrôleur.
 * C'est un objet "pur" : il ne dépend d'aucune couche externe.
 *
 * Dans MVC, le modèle est la source de vérité.
 * Toute règle métier liée aux données se trouve ici.
 */
public class Produit {

    // --- Attributs privés (encapsulation) ---
    private final int id;
    private String nom;
    private double prix;

    /**
     * Constructeur — crée un produit avec un identifiant unique.
     *
     * @param id   identifiant unique du produit
     * @param nom  nom du produit
     * @param prix prix en euros (doit être positif)
     */
    public Produit(int id, String nom, double prix) {
        this.id = id;
        this.nom = nom;
        // Règle métier : le prix ne peut pas être négatif
        this.prix = Math.max(0, prix);
    }

    // --- Accesseurs (getters) ---

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public double getPrix() {
        return prix;
    }

    // --- Mutateurs (setters) avec validation métier ---

    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Modifie le prix avec une règle métier :
     * le prix ne peut jamais descendre en dessous de zéro.
     */
    public void setPrix(double prix) {
        this.prix = Math.max(0, prix);
    }

    /**
     * Représentation textuelle — utile pour le débogage.
     */
    @Override
    public String toString() {
        return "Produit{id=%d, nom='%s', prix=%.2f€}".formatted(id, nom, prix);
    }
}
