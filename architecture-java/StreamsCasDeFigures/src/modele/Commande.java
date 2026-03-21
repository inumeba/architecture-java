package modele;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MODÈLE : COMMANDE                                         ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Représente une commande contenant une liste de produits.
 * Très utile pour démontrer le flatMap() des Streams.
 */
public record Commande(
    String id,
    String nomClient,
    List<Produit> produits,
    boolean estLivree
) {
    /** 
     * Calcule le total de la commande. 
     * C'est déjà un exemple de Stream ! 
     */
    public double getTotal() {
        return produits.stream()
                .mapToDouble(Produit::prix)
                .sum();
    }
    
    @Override
    public String toString() {
        return "Commande[%s, %s, Livrée:%b, Total:%.2f€, Articles:%d]".formatted(
                id, nomClient, estLivree, getTotal(), produits.size());
    }
}
