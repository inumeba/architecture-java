import exception.metier.*;
import service.*;
import exemples.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║       EXCEPTIONS EN JAVA — TOUS LES CAS DE FIGURES         ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║                                                              ║
 * ║  Ce projet couvre l'INTEGRALITE des mecanismes               ║
 * ║  d'exceptions en Java avec des exemples concrets.            ║
 * ║                                                              ║
 * ║  Throwable                                                   ║
 * ║  ├── Error (JVM, ne pas catcher)                             ║
 * ║  │   ├── OutOfMemoryError                                    ║
 * ║  │   └── StackOverflowError                                  ║
 * ║  └── Exception                                               ║
 * ║      ├── IOException (checked)                               ║
 * ║      ├── SQLException (checked)                              ║
 * ║      ├── ApplicationException (nos exceptions metier)        ║
 * ║      │   ├── ValidationException                             ║
 * ║      │   ├── RessourceNonTrouveeException                    ║
 * ║      │   ├── RegleMetierException                            ║
 * ║      │   └── ErreurTechniqueException                        ║
 * ║      └── RuntimeException (unchecked)                        ║
 * ║          ├── NullPointerException                            ║
 * ║          ├── IllegalArgumentException                        ║
 * ║          ├── IndexOutOfBoundsException                       ║
 * ║          └── ArithmeticException                             ║
 * ║                                                              ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║   EXCEPTIONS JAVA — TOUS LES CAS DE FIGURES        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        // Installer le gestionnaire d'exceptions non capturées
        GestionnaireGlobal.configurer();

        // ──────────────────────────────────────────────────────
        // PARTIE 1 : Exceptions métier (checked, hiérarchie)
        // ──────────────────────────────────────────────────────
        demonstrerExceptionsMetier();

        // ──────────────────────────────────────────────────────
        // PARTIE 2 : Try-with-resources (gestion de ressources)
        // ──────────────────────────────────────────────────────
        demonstrerTryWithResources();

        // ──────────────────────────────────────────────────────
        // PARTIE 3 : Tous les patterns try-catch (10 cas)
        // ──────────────────────────────────────────────────────
        TryCatchExemples.cas1_tryCatchBasique();
        TryCatchExemples.cas2_catchMultiple();
        TryCatchExemples.cas3_multiCatch();
        TryCatchExemples.cas4_tryCatchFinally();
        TryCatchExemples.cas5_relancerException();
        TryCatchExemples.cas6_chainageExceptions();
        TryCatchExemples.cas7_exceptionsSupprimees();
        TryCatchExemples.cas8_uncheckedExceptions();
        TryCatchExemples.cas9_patternMatchingCatch();
        TryCatchExemples.cas10_assertions();

        // ──────────────────────────────────────────────────────
        // PARTIE 4 : Gestion centralisée (simule @ControllerAdvice)
        // ──────────────────────────────────────────────────────
        GestionnaireGlobal.demonstrerGestionCentralisee();

        // ──────────────────────────────────────────────────────
        // PARTIE 5 : Anti-patterns (ce qu'il ne faut PAS faire)
        // ──────────────────────────────────────────────────────
        AntiPatterns.demonstrer();

        // ──────────────────────────────────────────────────────
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.println("  RESUME DES BONNES PRATIQUES :");
        System.out.println("  1. Catcher le type le plus SPECIFIQUE possible");
        System.out.println("  2. TOUJOURS conserver la cause (throw new X(msg, e))");
        System.out.println("  3. Utiliser try-with-resources pour les ressources");
        System.out.println("  4. NE JAMAIS avaler une exception (catch vide)");
        System.out.println("  5. NE PAS utiliser les exceptions pour le flux");
        System.out.println("  6. Creer une HIERARCHIE d'exceptions metier");
        System.out.println("  7. Centraliser la gestion (@ControllerAdvice)");
        System.out.println("══════════════════════════════════════════════════════");
    }

    /**
     * Démontre les exceptions métier personnalisées.
     */
    private static void demonstrerExceptionsMetier() {
        System.out.println("\n=== PARTIE 1 : EXCEPTIONS METIER ===");

        ProduitService service = new ProduitService();

        // Cas 1 : Créer un produit valide
        System.out.println("\n--- Cas 1 : Creation valide ---");
        try {
            service.creerProduit("Laptop Pro", 1299.99, 5);
            System.out.println("  Produit cree avec succes !");
        } catch (ApplicationException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }

        // Cas 2 : Validation échoue (nom vide)
        System.out.println("\n--- Cas 2 : Validation echouee (nom vide) ---");
        try {
            service.creerProduit("", 100.0, 1);
        } catch (ValidationException e) {
            System.out.println("  [ValidationException]");
            System.out.println("  Champ   : " + e.getChamp());
            System.out.println("  Valeur  : '" + e.getValeur() + "'");
            System.out.println("  Code    : " + e.getCodeErreur());
            System.out.println("  Message : " + e.getMessage());
        } catch (ApplicationException e) {
            System.out.println("  Erreur inattendue : " + e.getMessage());
        }

        // Cas 3 : Validation échoue (prix négatif)
        System.out.println("\n--- Cas 3 : Validation echouee (prix negatif) ---");
        try {
            service.creerProduit("Clavier", -50.0, 1);
        } catch (ValidationException e) {
            System.out.println("  [ValidationException] " + e.getCodeErreur()
                    + " -> " + e.getMessage());
        } catch (ApplicationException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }

        // Cas 4 : Ressource non trouvée
        System.out.println("\n--- Cas 4 : Ressource non trouvee ---");
        try {
            service.trouverParId(999);
        } catch (RessourceNonTrouveeException e) {
            System.out.println("  [RessourceNonTrouveeException]");
            System.out.println("  Ressource   : " + e.getTypeRessource());
            System.out.println("  Identifiant : " + e.getIdentifiant());
            System.out.println("  Code        : " + e.getCodeErreur());
        } catch (ApplicationException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }

        // Cas 5 : Règle métier violée (stock insuffisant)
        System.out.println("\n--- Cas 5 : Regle metier violee ---");
        try {
            // D'abord créer un produit, puis tenter une vente impossible
            service.creerProduit("Souris", 25.0, 2);
            service.vendre(2, 100); // stock = 2, demande = 100
        } catch (RegleMetierException e) {
            System.out.println("  [RegleMetierException]");
            System.out.println("  Regle   : " + e.getRegle());
            System.out.println("  Code    : " + e.getCodeErreur());
            System.out.println("  Message : " + e.getMessage());
        } catch (ApplicationException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }

        // Cas 6 : Erreur technique (simulation DB)
        System.out.println("\n--- Cas 6 : Erreur technique (simulation BD) ---");
        try {
            ProduitService.Produit produit = service.listerTous().get(0);
            service.sauvegarderEnBase(produit);
        } catch (ErreurTechniqueException e) {
            System.out.println("  [ErreurTechniqueException]");
            System.out.println("  Code    : " + e.getCodeErreur());
            System.out.println("  Message : " + e.getMessage());
            System.out.println("  Cause   : " + e.getCause().getMessage());
        } catch (ApplicationException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }
    }

    /**
     * Démontre try-with-resources avec le FichierService.
     */
    private static void demonstrerTryWithResources() {
        System.out.println("\n=== PARTIE 2 : TRY-WITH-RESOURCES ===");

        FichierService fichierService = new FichierService();

        // Cas 1 : Lecture d'un fichier inexistant (IOException)
        System.out.println("\n--- Try-with-resources (IOException) ---");
        try {
            fichierService.lireFichier("fichier_inexistant.txt");
        } catch (java.io.IOException e) {
            System.out.println("  IOException attrapee : " + e.getMessage());
        }

        // Cas 2 : Exceptions supprimées (close + try)
        System.out.println("\n--- Exceptions supprimees (try + close) ---");
        try {
            fichierService.demonstrerExceptionSupprimee();
        } catch (Exception e) {
            System.out.println("  Exception principale : " + e.getMessage());
            for (Throwable supprimee : e.getSuppressed()) {
                System.out.println("  Exception supprimee  : " + supprimee.getMessage());
            }
        }
    }
}
