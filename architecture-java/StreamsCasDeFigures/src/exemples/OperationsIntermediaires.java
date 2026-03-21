package exemples;

import modele.Produit;
import modele.Commande;
import java.util.List;
import java.util.stream.Stream;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  2. OPÉRATIONS INTERMÉDIAIRES                              ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Ces opérations transforment le Stream et renvoient un NOUVEAU Stream.
 * Elles sont "paresseuses" (lazy) : elles ne s'exécutent PAS tant qu'une
 * opération terminale n'est pas appelée.
 */
public class OperationsIntermediaires {

    public static void demonstrer(List<Produit> catalogue, List<Commande> commandes) {
        System.out.println("\n=== 2. OPÉRATIONS INTERMÉDIAIRES ===");

        // ─── FILTER : Garder uniquement les éléments qui matchent une condition ───
        System.out.println("\n  >> FILTER (Produits > 1000€) :");
        catalogue.stream()
                .filter(p -> p.prix() > 1000)
                .forEach(p -> System.out.println("     " + p));

        // ─── MAP : Transformer chaque élément en autre chose ───
        System.out.println("\n  >> MAP (Extraire les noms des produits) :");
        catalogue.stream()
                .map(Produit::nom)
                .limit(3) // LIMIT : Ne garder que les 3 premiers
                .forEach(nom -> System.out.println("     Nom: " + nom));

        // ─── FLATMAP : "Aplatir" une collection de collections ───
        // Une commande contient UNE LISTE de produits.
        // Si on fait .map(Commande::produits) -> on a un Stream<List<Produit>> (liste de listes).
        // Le flatMap extrait le contenu des sous-listes pour faire un seul Stream<Produit>.
        System.out.println("\n  >> FLATMAP (Tous les produits de toutes les commandes) :");
        commandes.stream()
                .flatMap(commande -> commande.produits().stream())
                .map(Produit::nom)
                .distinct() // DISTINCT : Enlever les doublons
                .forEach(nomProduit -> System.out.println("     Produit commandé : " + nomProduit));

        // ─── SORTED : Trier les éléments ───
        System.out.println("\n  >> SORTED (Trier les produits par prix décroissant) :");
        catalogue.stream()
                .sorted((p1, p2) -> Double.compare(p2.prix(), p1.prix())) // Tris décroissant
                .limit(2)
                .forEach(p -> System.out.println("     " + p));

        // ─── PEEK : Regarder pendant le passage (pour débugger) sans modifier ───
        System.out.println("\n  >> PEEK (Inspecter pendant le filtrage) :");
        long count = catalogue.stream()
                .filter(p -> p.categorie().equals("Electronique"))
                .peek(p -> System.out.println("     [DEBUG] Electronique trouvé : " + p.nom()))
                .count(); // count() est terminal
        System.out.println("     Total: " + count);
    }
}
