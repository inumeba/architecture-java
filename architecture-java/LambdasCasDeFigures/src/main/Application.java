package main;

import modele.Utilisateur;
import exemples.InterfacesPredefinies;
import exemples.LambdasPersonnaliseesEtScope;
import exemples.ReferencesDeMethodesEtBiFonctions;

import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ENTRY POINT DU PROJET: CAS DE FIGURES DES LAMBDAS JAVA      ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("┌────────────────────────────────────────────────────────┐");
        System.out.println("│  DÉMONSTRATION JAVA LAMBDAS - ARCHITECTURE DE RÉFÉRENCE │");
        System.out.println("└────────────────────────────────────────────────────────┘");

        // Jeu de données pour manipuler les lambdas
        List<Utilisateur> commun = genererUtilisateurs();

        // 1. Les 4 grandes Familles (java.util.function)
        InterfacesPredefinies.demonstrer(commun);

        // 2. Création d'interface (@FunctionalInterface) et "Effectively Final" variables
        LambdasPersonnaliseesEtScope.demonstrer(commun);

        // 3. Références de Méthodes (::) et Bi-Interfaces
        ReferencesDeMethodesEtBiFonctions.demonstrer(commun);

        System.out.println("\n✅ FIN DE LA DÉMONSTRATION DES LAMBDAS.");
    }

    private static List<Utilisateur> genererUtilisateurs() {
        return List.of(
            new Utilisateur(1L, "Alice Dubois", 28, true, 1205.50),
            new Utilisateur(2L, "Bob Martin", 17, false, 45.00),
            new Utilisateur(3L, "Charlie Dupont", 34, true, 3450.75),
            new Utilisateur(4L, "Diane Lemaire", 42, true, 530.00),
            new Utilisateur(5L, "Emile Zola", 65, false, 8000.00)
        );
    }
}
