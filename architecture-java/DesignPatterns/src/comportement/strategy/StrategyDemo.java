package comportement.strategy;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 7 : STRATEGY — Changer l'algorithme au runtime.    ║
 * ║  Comme choisir son mode de transport : bus, vélo, taxi.     ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Plusieurs algorithmes pour la même tâche
 * → On veut changer d'algorithme à l'exécution
 * → Éviter les if/else ou switch géants
 *
 * ANALOGIE :
 * Google Maps propose : voiture, transport en commun, vélo, marche.
 * Chaque mode calcule l'itinéraire DIFFÉREMMENT.
 * L'utilisateur CHOISIT la stratégie → l'app l'utilise.
 *
 * COMMENT ÇA MARCHE ?
 * 1. INTERFACE Strategy : définit l'algorithme (calculer())
 * 2. STRATÉGIES CONCRÈTES : chaque implémentation est un algo
 * 3. CONTEXTE : utilise une stratégie (peut en changer)
 *
 * EN PRODUCTION :
 * → Comparator est une stratégie de tri
 * → Spring Security : AuthenticationStrategy
 * → Java Streams : les lambdas SONT des stratégies
 */

// ─── INTERFACE STRATÉGIE ───

/**
 * Chaque stratégie de tri DOIT implémenter cette interface.
 * Le contexte manipule CETTE interface, pas les classes concrètes.
 *
 * On utilise une interface fonctionnelle (1 seule méthode abstraite)
 * → compatible avec les lambdas Java 8+
 */
@FunctionalInterface
interface StrategieTri {
    int[] trier(int[] donnees);
}

// ─── STRATÉGIES CONCRÈTES ───

/** Tri à bulles (Bubble Sort) — Simple mais O(n²) */
class TriBulles implements StrategieTri {
    @Override
    public int[] trier(int[] donnees) {
        int[] copie = donnees.clone();
        for (int i = 0; i < copie.length - 1; i++) {
            for (int j = 0; j < copie.length - 1 - i; j++) {
                if (copie[j] > copie[j + 1]) {
                    int temp = copie[j];
                    copie[j] = copie[j + 1];
                    copie[j + 1] = temp;
                }
            }
        }
        System.out.println("    [Tri a bulles] O(n^2) - Simple mais lent");
        return copie;
    }
}

/** Tri par sélection (Selection Sort) — Peu d'échanges, O(n²) */
class TriSelection implements StrategieTri {
    @Override
    public int[] trier(int[] donnees) {
        int[] copie = donnees.clone();
        for (int i = 0; i < copie.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < copie.length; j++) {
                if (copie[j] < copie[minIdx]) minIdx = j;
            }
            int temp = copie[minIdx];
            copie[minIdx] = copie[i];
            copie[i] = temp;
        }
        System.out.println("    [Tri selection] O(n^2) - Peu d'echanges");
        return copie;
    }
}

/** Tri rapide (Quick Sort) — Efficace, O(n log n) en moyenne */
class TriRapide implements StrategieTri {
    @Override
    public int[] trier(int[] donnees) {
        int[] copie = donnees.clone();
        triRapide(copie, 0, copie.length - 1);
        System.out.println("    [Tri rapide] O(n log n) - Performant");
        return copie;
    }

    private void triRapide(int[] arr, int debut, int fin) {
        if (debut < fin) {
            int pivot = partition(arr, debut, fin);
            triRapide(arr, debut, pivot - 1);
            triRapide(arr, pivot + 1, fin);
        }
    }

    private int partition(int[] arr, int debut, int fin) {
        int pivot = arr[fin];
        int i = debut - 1;
        for (int j = debut; j < fin; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
            }
        }
        int temp = arr[i + 1]; arr[i + 1] = arr[fin]; arr[fin] = temp;
        return i + 1;
    }
}

// ─── CONTEXTE : utilise la stratégie choisie ───

/**
 * Le contexte DÉLÈGUE le tri à la stratégie choisie.
 * Il peut CHANGER de stratégie à tout moment.
 */
class Trieur {
    private StrategieTri strategie;

    Trieur(StrategieTri strategie) {
        this.strategie = strategie;
    }

    /** Changer de stratégie à l'exécution */
    void changerStrategie(StrategieTri nouvelleStrategie) {
        this.strategie = nouvelleStrategie;
    }

    int[] executer(int[] donnees) {
        return strategie.trier(donnees);
    }
}

// ─── DÉMONSTRATION ───

public class StrategyDemo {

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 7 : STRATEGY ===");
        System.out.println("  But : changer l'algorithme a l'execution\n");

        int[] donnees = {64, 25, 12, 22, 11};
        System.out.println("  Donnees : " + java.util.Arrays.toString(donnees));

        Trieur trieur = new Trieur(new TriBulles());

        // Stratégie 1 : Tri à bulles
        System.out.println("\n  Strategie 1 :");
        int[] resultat = trieur.executer(donnees);
        System.out.println("    Resultat : " + java.util.Arrays.toString(resultat));

        // Stratégie 2 : Tri par sélection (on CHANGE à l'exécution)
        trieur.changerStrategie(new TriSelection());
        System.out.println("\n  Strategie 2 :");
        resultat = trieur.executer(donnees);
        System.out.println("    Resultat : " + java.util.Arrays.toString(resultat));

        // Stratégie 3 : Tri rapide
        trieur.changerStrategie(new TriRapide());
        System.out.println("\n  Strategie 3 :");
        resultat = trieur.executer(donnees);
        System.out.println("    Resultat : " + java.util.Arrays.toString(resultat));

        // BONUS : avec une lambda (Java 8+)
        System.out.println("\n  Strategie 4 (lambda) :");
        trieur.changerStrategie(d -> {
            int[] copie = d.clone();
            java.util.Arrays.sort(copie);
            System.out.println("    [Arrays.sort] Utilise le tri natif Java");
            return copie;
        });
        resultat = trieur.executer(donnees);
        System.out.println("    Resultat : " + java.util.Arrays.toString(resultat));
    }
}
