package exemples;

import modele.Produit;
import modele.Commande;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  4. COLLECTORS AVANCÉS : GROUPEMENT & PARTITIONEMENT       ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Comme en SQL avec le "GROUP BY", les Streams permettent de
 * regrouper facilement des données en Maps.
 */
public class GroupementEtPartition {

    public static void demonstrer(List<Produit> catalogue, List<Commande> commandes) {
        System.out.println("\n=== 4. GROUPEMENT & PARTITION (Collectors) ===");

        // ─── GROUPING BY : Regrouper selon une catégorie ───
        // Résultat : Map<Clé, List<Élément>>
        System.out.println("\n  >> GROUPING BY (Produits groupés par catégorie) :");
        
        Map<String, List<Produit>> produitsParCategorie = catalogue.stream()
                .collect(Collectors.groupingBy(Produit::categorie));
                
        produitsParCategorie.forEach((cat, listeProds) -> {
            System.out.println("     [" + cat + "] -> " + listeProds.size() + " produit(s)");
        });

        // ─── GROUPING BY AVEC OPÉRATION CASCADEE (AVANCÉ) ───
        // Ici, on groupe par catégorie, mais au lieu de garder la liste 
        // entière, on calcule juste la MOYENNE des prix de ce groupe.
        System.out.println("\n  >> GROUPING BY (Prix moyen par catégorie) :");
        
        Map<String, Double> prixMoyenParCategorie = catalogue.stream()
                .collect(Collectors.groupingBy(
                        Produit::categorie,
                        Collectors.averagingDouble(Produit::prix) // Opérateur en cascade
                ));
                
        prixMoyenParCategorie.forEach((cat, avg) -> {
            System.out.println("     [" + cat + "] -> Moyenne: " + String.format("%.2f€", avg));
        });

        // ─── PARTITIONING BY : "GROUP BY" mais avec un résultat VRAI / FAUX ───
        // Sépare en exactement 2 groupes selon un prédicat.
        // Toujours Map<Boolean, List>
        System.out.println("\n  >> PARTITIONING BY (Commandes Livrées vs En attente) :");
        
        Map<Boolean, List<Commande>> partitionLivraison = commandes.stream()
                .collect(Collectors.partitioningBy(Commande::estLivree));
                
        System.out.println("     Livrées (TRUE)  : " + partitionLivraison.get(true).size() + " commandes");
        System.out.println("     En cours (FALSE): " + partitionLivraison.get(false).size() + " commandes");

        // ─── JOINING : Concaténer des chaînes ───
        System.out.println("\n  >> JOINING (Noms de tous les produits) :");
        String noms = catalogue.stream()
                .map(Produit::nom)
                .collect(Collectors.joining(", ", "[", "]")); // Délémiteur, Préfixe, Suffixe
        System.out.println("     " + noms);
    }
}
