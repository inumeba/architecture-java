package exemples;

import exception.metier.*;

/**
 * GESTIONNAIRE GLOBAL — Capturer toutes les exceptions non gérées
 *
 * En production, il faut toujours avoir un FILET DE SÉCURITÉ
 * pour les exceptions qui ne sont pas catchées localement.
 *
 * Java offre deux mécanismes :
 *
 * 1. Thread.setDefaultUncaughtExceptionHandler()
 *    → Capture les exceptions non gérées dans TOUS les threads
 *    → En production : logger l'erreur, envoyer une alerte
 *
 * 2. try-catch dans main()
 *    → Capture tout dans le thread principal
 *
 * En Spring Boot : @ControllerAdvice + @ExceptionHandler
 * → Gestion centralisée des exceptions pour les API REST
 */
public class GestionnaireGlobal {

    /**
     * Configure le gestionnaire d'exceptions non capturées.
     *
     * Appelé UNE FOIS au démarrage de l'application.
     * Toute exception qui n'est pas catchée quelque part
     * finira ici au lieu de crasher avec une stack trace brute.
     */
    public static void configurer() {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            System.err.println("\n!!! EXCEPTION NON GEREE !!!");
            System.err.println("  Thread  : " + thread.getName());
            System.err.println("  Type    : " + exception.getClass().getSimpleName());
            System.err.println("  Message : " + exception.getMessage());

            if (exception.getCause() != null) {
                System.err.println("  Cause   : " + exception.getCause().getMessage());
            }

            // En production : logger + envoyer une alerte (Sentry, Datadog...)
            // logger.error("Exception non geree", exception);
        });
    }

    /**
     * Gère une exception métier de façon centralisée.
     *
     * Simule ce que ferait un @ControllerAdvice dans Spring Boot.
     * Convertit les exceptions métier en réponses HTTP structurées.
     *
     * @return un code HTTP simulé
     */
    public static int gererExceptionMetier(ApplicationException e) {
        // Pattern matching pour déterminer le code HTTP
        int codeHttp;
        String niveau;

        if (e instanceof ValidationException) {
            codeHttp = 400; // Bad Request
            niveau = "WARN";
        } else if (e instanceof RessourceNonTrouveeException) {
            codeHttp = 404; // Not Found
            niveau = "WARN";
        } else if (e instanceof RegleMetierException) {
            codeHttp = 422; // Unprocessable Entity
            niveau = "WARN";
        } else if (e instanceof ErreurTechniqueException) {
            codeHttp = 503; // Service Unavailable
            niveau = "ERROR";
        } else {
            codeHttp = 500; // Internal Server Error
            niveau = "ERROR";
        }

        System.out.printf("  [%s] HTTP %d → [%s] %s%n",
                niveau, codeHttp, e.getCodeErreur(), e.getMessage());

        return codeHttp;
    }

    /**
     * Démontre le gestionnaire global avec des exceptions métier.
     */
    public static void demonstrerGestionCentralisee() {
        System.out.println("\n=== GESTION CENTRALISEE (simule @ControllerAdvice) ===");

        // Simuler plusieurs erreurs et les traiter de façon centralisée
        ApplicationException[] erreurs = {
                new ValidationException("email", "abc", "format invalide"),
                new RessourceNonTrouveeException("Commande", 999),
                new RegleMetierException("SOLDE", "Solde insuffisant pour ce virement"),
                new ErreurTechniqueException("Service de paiement indisponible",
                        new java.net.ConnectException("timeout"))
        };

        for (ApplicationException e : erreurs) {
            gererExceptionMetier(e);
        }
    }
}
