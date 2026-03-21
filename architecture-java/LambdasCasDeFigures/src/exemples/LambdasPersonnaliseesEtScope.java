package exemples;

import interfaces.ValidateurUtilisateur;
import modele.Utilisateur;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  2. UTILISATION DES INTERFACES PERSONNALISÉES                ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Comment utiliser l'interface personnalisée @FunctionalInterface
 * que nous avons créée, et comment le "Scope" (Contexte) se 
 * comporte à l'intérieur d'une Lambda.
 */
public class LambdasPersonnaliseesEtScope {

    public static void demonstrer(List<Utilisateur> utilisateurs) {
        System.out.println("\n=== 2. INTERFACE SUR-MESURE & SCOPE ===");

        // ─── IMPLEMENTATION D'INTERFACE PERSONNALISEE (Custom) ───
        // Au lieu de faire un "new ValidateurUtilisateur() { ... }",
        // on implémente directement la signature : boolean valider(Utilisateur u).
        ValidateurUtilisateur validateurNouveauClient = u -> u.age() > 20 && u.solde() < 100;
        
        System.out.println("\n  >> UTILISATION INTERFACE SUR-MESURE :");
        for (Utilisateur u : utilisateurs) {
            if (validateurNouveauClient.valider(u)) {
                System.out.println("     Nouveau client correspondant : " + u.nom());
            }
        }

        // ─── NOTION DE CAPTURE DE VARIABLE (Closures / Scope) ───
        // Les lambdas peuvent "capturer" (lire) des variables définies hors d'elles.
        // MAIS pour les variables locales, elles DOIVENT être "effectively final".
        // Cela signifie qu'une fois la variable assignée, elle ne doit plus jamais
        // être réassignée plus bas, sinon le compilateur lève une erreur.
        
        int seuilSoldeMinimum = 500; 
        // Si on fait `seuilSoldeMinimum = 600;` ici, la lambda ci-dessous ne compilerait pas.

        ValidateurUtilisateur richeValidateur = u -> u.solde() > seuilSoldeMinimum;

        System.out.println("\n  >> CAPTURE DE VARIABLE EXTERNE (Effectively Final) :");
        boolean test = richeValidateur.valider(utilisateurs.get(0));
        System.out.println("     L'utilisateur " + utilisateurs.get(0).nom() + " a-t-il plus de " + seuilSoldeMinimum + "€ ? " + test);
    }
}
