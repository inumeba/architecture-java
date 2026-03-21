package exemples;

import exception.metier.*;

/**
 * EXEMPLES — Tous les patterns de try-catch en Java
 *
 * Ce fichier démontre CHAQUE façon de capturer et gérer les exceptions,
 * du plus basique au plus avancé.
 */
public class TryCatchExemples {

    // ======================================================================
    // CAS 1 : TRY-CATCH BASIQUE
    // ======================================================================

    /**
     * Le pattern le plus simple : try + catch un seul type.
     */
    public static void cas1_tryCatchBasique() {
        System.out.println("\n=== CAS 1 : try-catch basique ===");

        try {
            int resultat = 10 / 0; // ArithmeticException (unchecked)
            System.out.println("Resultat : " + resultat); // Jamais exécuté
        } catch (ArithmeticException e) {
            // Le bloc catch est exécuté quand l'exception correspond au type
            System.out.println("  [CATCH] Division par zero : " + e.getMessage());
        }

        // L'exécution continue normalement après le catch
        System.out.println("  Le programme continue apres le catch.");
    }

    // ======================================================================
    // CAS 2 : CATCH MULTIPLE (plusieurs blocs catch)
    // ======================================================================

    /**
     * Plusieurs blocs catch pour gérer différents types d'exceptions.
     *
     * RÈGLE IMPORTANTE : les catch doivent aller du PLUS SPÉCIFIQUE
     * au PLUS GÉNÉRAL. Sinon, le compilateur refuse.
     *
     * MAUVAIS : catch(Exception e) → catch(IOException e)  // Erreur !
     * BON     : catch(IOException e) → catch(Exception e)   // OK
     */
    public static void cas2_catchMultiple() {
        System.out.println("\n=== CAS 2 : catch multiple (du specifique au general) ===");

        String[] tableau = {"10", "abc", null};

        for (String valeur : tableau) {
            try {
                // Peut lancer : NullPointerException, NumberFormatException
                int nombre = Integer.parseInt(valeur.trim());
                System.out.println("  Valeur '" + valeur + "' → " + nombre);

            } catch (NumberFormatException e) {
                // SPÉCIFIQUE : mauvais format de nombre
                System.out.println("  [CATCH specifique] '" + valeur + "' n'est pas un nombre");

            } catch (NullPointerException e) {
                // SPÉCIFIQUE : valeur null
                System.out.println("  [CATCH specifique] La valeur est null");

            } catch (Exception e) {
                // GÉNÉRAL : tout le reste (filet de sécurité)
                System.out.println("  [CATCH general] Erreur inattendue : " + e.getClass().getSimpleName());
            }
        }
    }

    // ======================================================================
    // CAS 3 : MULTI-CATCH (Java 7+) — un seul bloc pour plusieurs types
    // ======================================================================

    /**
     * Multi-catch avec le pipe (|) pour regrouper des exceptions
     * qui ont le MÊME traitement.
     *
     * Avantage : évite la duplication de code.
     * Contrainte : les types ne doivent pas avoir de relation d'héritage.
     */
    public static void cas3_multiCatch() {
        System.out.println("\n=== CAS 3 : multi-catch (|) ===");

        Object[] valeurs = {42, "texte", null, 3.14};

        for (Object valeur : valeurs) {
            try {
                // Force le cast en String puis en int
                String texte = (String) valeur;
                int nombre = Integer.parseInt(texte);
                System.out.println("  OK : " + nombre);

                // MULTI-CATCH : un seul bloc pour deux types d'exceptions
                // ClassCastException OU NumberFormatException → même traitement
            } catch (ClassCastException | NumberFormatException e) {
                System.out.println("  [MULTI-CATCH] " + e.getClass().getSimpleName()
                        + " pour '" + valeur + "'");

            } catch (NullPointerException e) {
                System.out.println("  [CATCH] Valeur null detectee");
            }
        }
    }

    // ======================================================================
    // CAS 4 : TRY-CATCH-FINALLY
    // ======================================================================

    /**
     * Le bloc finally est TOUJOURS exécuté, que l'exception soit
     * lancée ou non, catchée ou non.
     *
     * Utilisations du finally :
     * - Libérer des ressources (AVANT Java 7, remplacé par try-with-resources)
     * - Logger la fin d'une opération
     * - Remettre un état à sa valeur initiale
     *
     * ATTENTION : éviter de mettre un return dans le finally !
     * Il écraserait le return du try ou du catch.
     */
    public static void cas4_tryCatchFinally() {
        System.out.println("\n=== CAS 4 : try-catch-finally ===");

        // Cas A : pas d'exception → try + finally
        System.out.println("  --- Cas sans exception ---");
        try {
            System.out.println("  [TRY] Calcul : " + (10 + 5));
        } catch (Exception e) {
            System.out.println("  [CATCH] Jamais execute ici");
        } finally {
            System.out.println("  [FINALLY] Toujours execute !");
        }

        // Cas B : avec exception → try + catch + finally
        System.out.println("  --- Cas avec exception ---");
        try {
            System.out.println("  [TRY] Avant l'erreur");
            int x = 10 / 0;
            System.out.println("  [TRY] Jamais execute"); // Sauté !
        } catch (ArithmeticException e) {
            System.out.println("  [CATCH] " + e.getMessage());
        } finally {
            System.out.println("  [FINALLY] Execute meme apres l'exception !");
        }

        // Cas C : try-finally SANS catch (l'exception se propage)
        System.out.println("  --- Cas try-finally sans catch ---");
        try {
            fonctionAvecFinallySansCatch();
        } catch (Exception e) {
            System.out.println("  [CATCH exterieur] Exception propagee : " + e.getMessage());
        }
    }

    private static void fonctionAvecFinallySansCatch() {
        try {
            throw new RuntimeException("Erreur dans la fonction");
        } finally {
            // Exécuté AVANT que l'exception se propage à l'appelant
            System.out.println("  [FINALLY] Execute avant la propagation");
        }
    }

    // ======================================================================
    // CAS 5 : RELANCER (RE-THROW) une exception
    // ======================================================================

    /**
     * Parfois on veut faire quelque chose avec l'exception
     * (logger, nettoyer) puis la RELANCER pour que l'appelant la gère.
     */
    public static void cas5_relancerException() {
        System.out.println("\n=== CAS 5 : relancer (re-throw) ===");

        try {
            traiterAvecRelancement();
        } catch (Exception e) {
            System.out.println("  [CATCH final] Exception relancee : " + e.getMessage());
        }
    }

    private static void traiterAvecRelancement() {
        try {
            int[] tab = new int[3];
            tab[10] = 42; // IndexOutOfBoundsException

        } catch (IndexOutOfBoundsException e) {
            // Logger ou nettoyer...
            System.out.println("  [LOG] Exception detectee, on la relance...");

            // Option A : relancer la même exception
            // throw e;

            // Option B : encapsuler dans une nouvelle exception (RECOMMANDÉ)
            // Conserve la cause originale pour le débogage
            throw new RuntimeException("Erreur de traitement du tableau", e);
        }
    }

    // ======================================================================
    // CAS 6 : CHAINAGE D'EXCEPTIONS (Exception Chaining)
    // ======================================================================

    /**
     * Le chaînage conserve la TRACE COMPLÈTE de l'erreur :
     * Exception finale → cause → cause de la cause → ...
     *
     * BONNE PRATIQUE : TOUJOURS passer la cause (le 'e') quand on encapsule.
     *
     * MAUVAIS : throw new MaException("message")           → perd la cause
     * BON     : throw new MaException("message", e)        → conserve la cause
     */
    public static void cas6_chainageExceptions() {
        System.out.println("\n=== CAS 6 : chainage d'exceptions ===");

        try {
            couche3_baseDeDonnees();
        } catch (Exception e) {
            // Remonter toute la chaîne de causes
            System.out.println("  Chaine d'exceptions :");
            Throwable courant = e;
            int niveau = 1;
            while (courant != null) {
                System.out.println("    " + niveau + ". [" + courant.getClass().getSimpleName()
                        + "] " + courant.getMessage());
                courant = courant.getCause();
                niveau++;
            }
        }
    }

    // Simulation de couches applicatives
    private static void couche3_baseDeDonnees() {
        try {
            throw new java.net.ConnectException("Connection refused: localhost:5432");
        } catch (Exception e) {
            // Couche 3 encapsule dans une erreur de repository
            throw new RuntimeException("Echec de la requete SQL : SELECT * FROM produits", e);
        }
    }

    // ======================================================================
    // CAS 7 : EXCEPTIONS SUPPRIMÉES (Suppressed Exceptions)
    // ======================================================================

    /**
     * Quand try ET close() lancent tous les deux une exception,
     * Java conserve les deux grâce à getSuppressed().
     */
    public static void cas7_exceptionsSupprimees() {
        System.out.println("\n=== CAS 7 : exceptions supprimees (try-with-resources) ===");

        try (service.FichierService.RessourceCapricieuse res =
                     new service.FichierService.RessourceCapricieuse()) {
            res.faireQuelqueChose();
        } catch (Exception e) {
            System.out.println("  Exception principale : " + e.getMessage());

            // Récupérer les exceptions supprimées (celles du close())
            Throwable[] supprimees = e.getSuppressed();
            for (Throwable s : supprimees) {
                System.out.println("  Exception supprimee : " + s.getMessage());
            }
        }
    }

    // ======================================================================
    // CAS 8 : EXCEPTIONS UNCHECKED (RuntimeException)
    // ======================================================================

    /**
     * Les RuntimeException ne sont PAS obligatoires à catcher.
     * Elles indiquent généralement un BUG dans le code.
     *
     * Exceptions Unchecked courantes :
     * - NullPointerException       → accès à un objet null
     * - IllegalArgumentException   → paramètre invalide
     * - IllegalStateException      → objet dans un mauvais état
     * - IndexOutOfBoundsException  → index hors limites
     * - ClassCastException         → cast impossible
     * - UnsupportedOperationException → opération non supportée
     * - ArithmeticException        → erreur mathématique (division par 0)
     * - ConcurrentModificationException → modification pendant itération
     */
    public static void cas8_uncheckedExceptions() {
        System.out.println("\n=== CAS 8 : exceptions unchecked courantes ===");

        // NullPointerException
        demonstrerUnchecked("NullPointer", () -> {
            String s = null;
            s.length(); // BOOM
        });

        // ArrayIndexOutOfBoundsException
        demonstrerUnchecked("IndexOutOfBounds", () -> {
            int[] tab = {1, 2, 3};
            int x = tab[10]; // BOOM
        });

        // ClassCastException
        demonstrerUnchecked("ClassCast", () -> {
            Object o = "texte";
            Integer n = (Integer) o; // BOOM
        });

        // StackOverflowError (Error, pas Exception !)
        demonstrerUnchecked("StackOverflow", () -> {
            recursionInfinie();
        });

        // NumberFormatException
        demonstrerUnchecked("NumberFormat", () -> {
            int n = Integer.parseInt("pas_un_nombre"); // BOOM
        });
    }

    private static void recursionInfinie() {
        recursionInfinie(); // Pas de condition d'arrêt → StackOverflowError
    }

    /**
     * Utilitaire pour exécuter et afficher proprement les exceptions.
     */
    private static void demonstrerUnchecked(String nom, Runnable code) {
        try {
            code.run();
        } catch (Throwable t) { // Throwable pour catcher aussi les Error
            System.out.printf("  %-20s → %s : %s%n",
                    nom, t.getClass().getSimpleName(),
                    t.getMessage() != null ? t.getMessage() : "(pas de message)");
        }
    }

    // ======================================================================
    // CAS 9 : PATTERN MATCHING avec instanceof (Java 16+)
    // ======================================================================

    /**
     * Java 16+ permet de combiner instanceof et cast en une seule ligne.
     * Très utile pour gérer différents types d'exceptions métier.
     */
    public static void cas9_patternMatchingCatch() {
        System.out.println("\n=== CAS 9 : traitement selon le type d'exception ===");

        ApplicationException[] exceptions = {
                new ValidationException("email", "abc", "format invalide"),
                new RessourceNonTrouveeException("Utilisateur", 42),
                new RegleMetierException("STOCK", "Stock insuffisant"),
                new ErreurTechniqueException("Base de donnees inaccessible")
        };

        for (ApplicationException e : exceptions) {
            // Pattern matching avec instanceof (Java 16+)
            String gravite;
            if (e instanceof ValidationException v) {
                gravite = "FAIBLE (champ: " + v.getChamp() + ")";
            } else if (e instanceof RessourceNonTrouveeException r) {
                gravite = "MOYENNE (" + r.getTypeRessource() + " #" + r.getIdentifiant() + ")";
            } else if (e instanceof RegleMetierException rm) {
                gravite = "MOYENNE (regle: " + rm.getRegle() + ")";
            } else if (e instanceof ErreurTechniqueException) {
                gravite = "HAUTE (probleme technique)";
            } else {
                gravite = "INCONNUE";
            }

            System.out.printf("  [%s] Gravite: %s%n", e.getCodeErreur(), gravite);
        }
    }

    // ======================================================================
    // CAS 10 : ASSERTION (assert)
    // ======================================================================

    /**
     * Les assertions vérifient des CONDITIONS QUI DOIVENT TOUJOURS ÊTRE VRAIES.
     * Si la condition est fausse → AssertionError (Error, pas Exception).
     *
     * ATTENTION : les assertions sont DÉSACTIVÉES par défaut en production !
     * Pour les activer : java -ea MonProgramme
     *
     * Utilisation :
     * - Tests et débogage : OUI
     * - Validation d'entrées utilisateur : NON (utiliser des exceptions)
     * - Vérification d'invariants internes : OUI
     */
    public static void cas10_assertions() {
        System.out.println("\n=== CAS 10 : assertions (activer avec -ea) ===");

        int age = 25;
        assert age >= 0 : "L'age ne peut pas etre negatif : " + age;
        System.out.println("  Assertion OK : age = " + age);

        // Avec -ea, la ligne suivante lancerait AssertionError :
        // assert age < 0 : "Test : cette assertion echoue";

        System.out.println("  (Activer les assertions avec : java -ea Application)");
    }
}
