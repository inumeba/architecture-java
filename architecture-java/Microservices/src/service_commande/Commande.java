package service_commande;

/**
 * SERVICE COMMANDE — Entité Commande
 *
 * Cette entité appartient EXCLUSIVEMENT au service Commande.
 * Elle contient sa propre vision d'un produit (juste l'ID, le nom, le prix)
 * sans dépendre de la classe Produit du service Produit.
 *
 * C'est le concept de "Bounded Context" du DDD :
 * chaque service a sa propre modélisation du domaine.
 */
public class Commande {

    /**
     * Les différents états d'une commande.
     * La commande suit un cycle de vie :
     * EN_ATTENTE → VALIDEE (si produit dispo) ou REFUSEE (si indisponible)
     */
    public enum Statut {
        EN_ATTENTE,   // Commande créée, en attente de vérification
        VALIDEE,      // Produit vérifié et disponible
        REFUSEE       // Produit indisponible ou erreur
    }

    private final int id;
    private final int idProduit;       // Référence vers le produit (par ID seulement)
    private final int quantite;
    private Statut statut;

    // Infos produit remplies APRÈS vérification par le service Produit
    private String nomProduit;
    private double prixUnitaire;

    public Commande(int id, int idProduit, int quantite) {
        this.id = id;
        this.idProduit = idProduit;
        this.quantite = quantite;
        this.statut = Statut.EN_ATTENTE; // Toujours en attente à la création
    }

    // --- Accesseurs ---

    public int getId() {
        return id;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public int getQuantite() {
        return quantite;
    }

    public Statut getStatut() {
        return statut;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    /**
     * Calcule le montant total de la commande.
     */
    public double getMontantTotal() {
        return prixUnitaire * quantite;
    }

    // --- Actions de changement d'état ---

    /**
     * Valide la commande avec les infos du produit.
     * Appelé quand le service Produit confirme la disponibilité.
     */
    public void valider(String nomProduit, double prixUnitaire) {
        this.statut = Statut.VALIDEE;
        this.nomProduit = nomProduit;
        this.prixUnitaire = prixUnitaire;
    }

    /**
     * Refuse la commande avec une raison.
     */
    public void refuser(String raison) {
        this.statut = Statut.REFUSEE;
        this.nomProduit = raison;
    }

    @Override
    public String toString() {
        return switch (statut) {
            case VALIDEE -> "Commande #%d : %dx %s = %.2f EUR [VALIDEE]"
                    .formatted(id, quantite, nomProduit, getMontantTotal());
            case REFUSEE -> "Commande #%d : produit #%d [REFUSEE - %s]"
                    .formatted(id, idProduit, nomProduit);
            case EN_ATTENTE -> "Commande #%d : produit #%d x%d [EN ATTENTE]"
                    .formatted(id, idProduit, quantite);
        };
    }
}
