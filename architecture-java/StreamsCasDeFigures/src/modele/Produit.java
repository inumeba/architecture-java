package modele;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MODÈLE : PRODUIT (Utilisation des Records Java 14+)       ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Les "Records" sont parfaits avec les Streams car ils sont
 * immuables. Les Streams encouragent la programmation fonctionnelle
 * sans effet de bord (side-effects).
 */
public record Produit(
    String nom,
    String categorie,
    double prix
) {
    // La méthode toString est générée automatiquement par le Record,
    // mais on peut la surcharger pour un affichage plus propre dans l'exemple.
    @Override
    public String toString() {
        return "%s (%.2f€ - %s)".formatted(nom, prix, categorie);
    }
}
