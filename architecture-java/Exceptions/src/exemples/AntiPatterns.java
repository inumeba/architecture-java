package exemples;

/**
 * ANTI-PATTERNS — Les MAUVAISES pratiques à éviter
 *
 * Ce fichier montre les erreurs les plus courantes
 * avec les exceptions et explique POURQUOI c'est mauvais.
 */
public class AntiPatterns {

    public static void demonstrer() {
        System.out.println("\n=== ANTI-PATTERNS (a ne PAS faire) ===\n");

        antiPattern1_catchGeneral();
        antiPattern2_avalerException();
        antiPattern3_exceptionPourFlux();
        antiPattern4_perdreLaCause();
        antiPattern5_catchThrowable();
        antiPattern6_exceptionDansFinally();

        System.out.println("\n  (Voir le code source pour les explications detaillees)");
    }

    /**
     * ANTI-PATTERN 1 : Catcher Exception ou Throwable (trop large)
     *
     * MAUVAIS : catch(Exception e) attrape TOUTES les exceptions,
     * y compris celles qu'on ne devrait pas gérer (bugs, erreurs JVM).
     *
     * BON : catcher le type SPÉCIFIQUE attendu.
     */
    private static void antiPattern1_catchGeneral() {
        System.out.println("  1. [MAUVAIS] catch(Exception e) → trop large");
        System.out.println("     [BON]     catch(NumberFormatException e) → cible");

        // MAUVAIS :
        try {
            String s = null;
            int n = Integer.parseInt(s); // NullPointer, pas NumberFormat !
        } catch (Exception e) {
            // On attrape TOUT, même les bugs qu'on ne devrait pas masquer
            // On ne sait même pas si c'est un NumberFormatException ou autre chose
        }
    }

    /**
     * ANTI-PATTERN 2 : Avaler l'exception (catch vide)
     *
     * Le pire anti-pattern : l'exception est silencieusement ignorée.
     * Le programme continue avec des données corrompues.
     * Le bug est INVISIBLE et très difficile à diagnostiquer.
     */
    private static void antiPattern2_avalerException() {
        System.out.println("  2. [MAUVAIS] catch(E e) { } → exception avalee (silencieuse)");
        System.out.println("     [BON]     au minimum logger l'erreur");

        // MAUVAIS :
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            // VIDE ! L'erreur est perdue à jamais.
            // Le développeur qui debuggera n'aura AUCUNE trace.
        }

        // BON (au minimum) :
        try {
            int result = 10 / 0;
        } catch (ArithmeticException e) {
            System.err.println("Erreur ignoree (non critique) : " + e.getMessage());
            // Ou : logger.warn("Division par zero ignoree", e);
        }
    }

    /**
     * ANTI-PATTERN 3 : Utiliser les exceptions pour le flux de contrôle
     *
     * Les exceptions ne sont PAS des if/else.
     * Elles sont COÛTEUSES (création de la stack trace) et rendent
     * le code difficile à lire.
     */
    private static void antiPattern3_exceptionPourFlux() {
        System.out.println("  3. [MAUVAIS] try { parseInt } catch → pour verifier si c'est un nombre");
        System.out.println("     [BON]     verifier AVANT avec une condition");

        String valeur = "abc";

        // MAUVAIS : utiliser l'exception comme un if/else
        boolean estNombre = false;
        try {
            Integer.parseInt(valeur);
            estNombre = true;
        } catch (NumberFormatException e) {
            estNombre = false; // Exception comme branchement → COUTEUX
        }

        // BON : vérifier avant
        estNombre = valeur.chars().allMatch(Character::isDigit);
    }

    /**
     * ANTI-PATTERN 4 : Perdre la cause originale
     *
     * Quand on encapsule une exception, il faut TOUJOURS
     * passer la cause pour conserver la stack trace complète.
     */
    private static void antiPattern4_perdreLaCause() {
        System.out.println("  4. [MAUVAIS] throw new X(msg) → perd la cause");
        System.out.println("     [BON]     throw new X(msg, e) → conserve la cause");

        try {
            try {
                throw new java.io.IOException("Fichier corrompu");
            } catch (java.io.IOException e) {
                // MAUVAIS : on perd la stack trace originale !
                // throw new RuntimeException("Erreur fichier");

                // BON : on conserve la cause
                throw new RuntimeException("Erreur fichier", e);
            }
        } catch (RuntimeException e) {
            // Avec la cause, on peut remonter jusqu'à l'erreur d'origine
        }
    }

    /**
     * ANTI-PATTERN 5 : Catcher Throwable (attrape même les Error)
     *
     * Throwable inclut les Error (OutOfMemoryError, StackOverflowError).
     * Ces erreurs sont FATALES : la JVM est dans un état instable.
     * Les catcher empêche le programme de s'arrêter proprement.
     */
    private static void antiPattern5_catchThrowable() {
        System.out.println("  5. [MAUVAIS] catch(Throwable t) → attrape Error aussi");
        System.out.println("     [BON]     catch(Exception e) → maximum acceptable");

        // MAUVAIS : catch Throwable
        // try { ... } catch (Throwable t) { ... }  // OutOfMemoryError serait catchee !

        // BON : laisser les Error se propager (la JVM gère)
    }

    /**
     * ANTI-PATTERN 6 : Lancer une exception dans finally
     *
     * Une exception dans finally ÉCRASE l'exception du try.
     * L'erreur originale est perdue.
     */
    private static void antiPattern6_exceptionDansFinally() {
        System.out.println("  6. [MAUVAIS] throw dans finally → ecrase l'exception du try");
        System.out.println("     [BON]     utiliser try-with-resources");

        // MAUVAIS :
        try {
            try {
                throw new RuntimeException("Erreur originale IMPORTANTE");
            } finally {
                // Cette exception ÉCRASE "Erreur originale IMPORTANTE"
                // throw new RuntimeException("Erreur de nettoyage");
                // → Le débuggeur ne verra que "Erreur de nettoyage"
            }
        } catch (RuntimeException e) {
            // On ne voit que "Erreur originale IMPORTANTE"
            // Avec try-with-resources, les deux seraient conservées
        }
    }
}
