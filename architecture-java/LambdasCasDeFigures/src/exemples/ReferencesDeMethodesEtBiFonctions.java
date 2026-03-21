package exemples;

import modele.Utilisateur;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  3. RÉFÉRENCES DE MÉTHODES (Method References ::)            ║
 * ╚══════════════════════════════════════════════════════════════╝
 * L'opérateur :: permet d'alléger l'écriture d'une expression lambda
 * lorsqu'elle ne fait QUE appeler une méthode déjà existante.
 */
public class ReferencesDeMethodesEtBiFonctions {

    public static void demonstrer(List<Utilisateur> utilisateurs) {
        System.out.println("\n=== 3. RÉFÉRENCES DE MÉTHODES (::) ET BI-FONCTIONS ===");

        // ─── RÉFÉRENCE À UNE MÉTHODE D'INSTANCE D'UN OBJET ARBITRAIRE ───
        // (ClassName::methodName) -> équivaut à  u -> u.nom()
        System.out.println("\n  >> Method Reference (Mapping Object -> String) :");
        utilisateurs.stream()
                .map(Utilisateur::nom)
                .forEach(nom -> System.out.print(nom + " | "));
        System.out.println();

        // ─── RÉFÉRENCE À UNE MÉTHODE D'INSTANCE D'UN OBJET PARTICULIER ───
        // (instance::methodName) -> équivaut à x -> System.out.println(x)
        System.out.println("\n  >> Method Reference (Utilisation de System.out) :");
        utilisateurs.forEach(System.out::println); 
        
        System.out.println("\n  >> Method Reference (Appel de la méthode afficherConsole du Record) :");
        utilisateurs.forEach(Utilisateur::afficherConsole);

        // ─── RÉFÉRENCE À UNE MÉTHODE STATIQUE ───
        // (ClassName::staticMethodName)
        // Utile avec les comparateurs par exemple
        System.out.println("\n  >> Tri avec Comparator et Method Reference :");
        // Utilisateur::solde équivaut à u -> u.solde()
        utilisateurs.stream()
                .sorted(Comparator.comparingDouble(Utilisateur::solde).reversed())
                .limit(2)
                .forEach(u -> System.out.println("     Top riche: " + u.nom()));


        // ─── BI-FONCTIONS (Fonctions à DEUX paramètres au lieu d'un) ───
        // Parfois on a besoin de combiner deux choses (ex: un Integer et un Double)
        // Le JDK fournit BiPredicate, BiFunction, BiConsumer...
        BiFunction<Utilisateur, Double, Double> calculerSoldeApresReduction = 
                (u, reduction) -> u.solde() - reduction;

        System.out.println("\n  >> BI-FUNCTION (2 arguments) :");
        Utilisateur cible = utilisateurs.get(0);
        double nvSolde = calculerSoldeApresReduction.apply(cible, 10.50);
        System.out.println("     Solde de " + cible.nom() + " en retirant 10.50 : " + String.format("%.2f", nvSolde));
    }
}
