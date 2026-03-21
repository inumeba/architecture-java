package exemples;

import modele.Produit;
import modele.Commande;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  3. OPÉRATIONS TERMINALES                                  ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Ces opérations DÉCLENCHENT le traitement de tout le pipeline
 * et produisent un résultat (une valeur, une liste, une action).
 * Après une opération terminale, le Stream est FERMÉ (consommé).
 */
public class OperationsTerminales {

    public static void demonstrer(List<Produit> catalogue, List<Commande> commandes) {
        System.out.println("\n=== 3. OPÉRATIONS TERMINALES ===");

        // ─── COLLECT : Regrouper les résultats dans une Collection (List, Set...) ───
        System.out.println("\n  >> COLLECT (Mettre le résultat dans une nouvelle List) :");
        List<Produit> pasCher = catalogue.stream()
                .filter(p -> p.prix() < 50)
                .collect(Collectors.toList()); // ou simplement .toList() depuis Java 16
        System.out.println("     Produits < 50€ : " + pasCher.size());

        // ─── MATCH : Vérifier des conditions (Retourne un boolean) ───
        System.out.println("\n  >> MATCHING (anyMatch, allMatch, noneMatch) :");
        
        boolean ifYaUnMac = catalogue.stream()
                .anyMatch(p -> p.nom().contains("MacBook"));
        System.out.println("     Y a-t-il au moins 1 MacBook ? " + ifYaUnMac);

        boolean toutEstLivre = commandes.stream()
                .allMatch(Commande::estLivree);
        System.out.println("     Toutes les commandes sont-elles livrées ? " + toutEstLivre);

        // ─── FIND : Chercher un élément ───
        System.out.println("\n  >> FIND FIRST & FIND ANY (Retourne un Optional) :");
        
        Optional<Produit> livreTrouve = catalogue.stream()
                .filter(p -> p.categorie().equals("Livre"))
                .findFirst(); // Prend le premier qui matche
        
        livreTrouve.ifPresentOrElse(
            p -> System.out.println("     Premier livre trouvé : " + p),
            () -> System.out.println("     Aucun livre dans le catalogue")
        );

        // ─── REDUCE : Combiner tous les éléments en UNE SEULE valeur ───
        // Utile pour sommer, concaténer, chercher le max, etc.
        System.out.println("\n  >> REDUCE (Calculer le prix total catalogue) :");
        
        double valeurStockTheorique = catalogue.stream()
                .map(Produit::prix)
                .reduce(0.0, (somme, prix) -> somme + prix); // Accumulateur
                // ou : .reduce(0.0, Double::sum)
        
        System.out.println("     Valeur totale d'une unité de chaque produit : " + valeurStockTheorique + "€");

        // ─── MIN / MAX ───
        System.out.println("\n  >> MAX (Trouver le produit le plus cher) :");
        catalogue.stream()
                .max((p1, p2) -> Double.compare(p1.prix(), p2.prix()))
                .ifPresent(p -> System.out.println("     Plus cher : " + p));
    }
}
