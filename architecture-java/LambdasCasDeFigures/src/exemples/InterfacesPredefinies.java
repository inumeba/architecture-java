package exemples;

import modele.Utilisateur;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  1. INTERFACES PREDEFINIES DU JDK (java.util.function)       ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Depuis Java 8, il n'est plus nécessaire de créer ses propres
 * interfaces pour les cas standards (Predicate, Function, etc.).
 */
public class InterfacesPredefinies {

    public static void demonstrer(List<Utilisateur> utilisateurs) {
        System.out.println("\n=== 1. INTERFACES PRÉDÉFINIES ===");

        // ─── PREDICATE (Tester une condition : T -> boolean) ───
        // Utile pour les filtres.
        Predicate<Utilisateur> estMajeur = u -> u.age() >= 18;
        Predicate<Utilisateur> estActif = Utilisateur::estActif; // (Méthode de référence)

        System.out.println("\n  >> PREDICATE (Combine 2 conditions avec .and()) :");
        utilisateurs.stream()
                .filter(estMajeur.and(estActif))
                .forEach(u -> System.out.println("     Majeur & Actif : " + u.nom()));


        // ─── FUNCTION (Transformer des données : T -> R) ───
        // Prend une entrée de type T, et retourne un type R.
        Function<Utilisateur, String> obtenirEtiquette = 
                u -> String.format("%s (Solde: %.2f)", u.nom().toUpperCase(), u.solde());

        System.out.println("\n  >> FUNCTION (Transformer Utilisateur en String) :");
        utilisateurs.stream()
                .map(obtenirEtiquette)
                .limit(2) // On en affiche juste 2 pour l'exemple
                .forEach(label -> System.out.println("     " + label));


        // ─── CONSUMER (Consommer une donnée sans retour : T -> void) ───
        // Idéal pour l'affichage, les logs, les sauvegardes en BDD.
        Consumer<Utilisateur> feliciter = 
                u -> System.out.println("     Bravo " + u.nom() + ", vous êtes un bon client !");

        System.out.println("\n  >> CONSUMER (Action sur un élément) :");
        utilisateurs.stream()
                .filter(u -> u.solde() > 1000)
                .forEach(feliciter);


        // ─── SUPPLIER (Générer une donnée : () -> T) ───
        // Ne prend rien, retourne quelque chose. Parfait pour la création différée (lazy-loading)
        // et les valeurs par défaut.
        Supplier<Utilisateur> utilisateurParDefaut = 
                () -> new Utilisateur(0L, "Anonyme", 0, false, 0.0);

        System.out.println("\n  >> SUPPLIER (Fournir l'utilisateur ou une alternative) :");
        Utilisateur found = utilisateurs.stream()
                .filter(u -> u.nom().equals("Introuvable"))
                .findFirst()
                .orElseGet(utilisateurParDefaut); // .orElseGet prend un Supplier !
        
        System.out.println("     Utilisateur récupéré : " + found.nom());
    }
}
