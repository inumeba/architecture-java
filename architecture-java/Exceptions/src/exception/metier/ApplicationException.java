package exception.metier;

/**
 * EXCEPTION MÉTIER DE BASE — Exception racine de l'application
 *
 * C'est la classe parente de TOUTES les exceptions métier de l'application.
 * Elle étend Exception (CHECKED) — le compilateur OBLIGE l'appelant à la gérer.
 *
 * HIÉRARCHIE DES EXCEPTIONS EN JAVA :
 *
 *   Throwable                     ← Ancêtre de tout ce qui peut être lancé
 *   ├── Error                     ← Erreurs JVM graves (OutOfMemoryError, StackOverflowError)
 *   │                               → Ne JAMAIS catcher (sauf cas très rares)
 *   └── Exception                 ← Exceptions « normales »
 *       ├── RuntimeException      ← UNCHECKED : pas obligé de les catcher
 *       │   ├── NullPointerException
 *       │   ├── IllegalArgumentException
 *       │   └── IndexOutOfBoundsException
 *       └── (Autres)              ← CHECKED : obligé de les catcher ou déclarer
 *           ├── IOException
 *           ├── SQLException
 *           └── ApplicationException  ← ← ← NOTRE CLASSE (ici)
 *
 * CHECKED vs UNCHECKED — QUAND UTILISER QUOI ?
 *
 *   CHECKED (extends Exception) :
 *   → L'appelant PEUT et DOIT réagir à cette erreur
 *   → Ex : fichier introuvable, réseau indisponible, règle métier violée
 *   → Le compilateur force le try/catch ou le throws
 *
 *   UNCHECKED (extends RuntimeException) :
 *   → C'est un BUG dans le code, pas une situation normale
 *   → Ex : null inattendu, index hors limites, argument invalide
 *   → Le compilateur ne force rien
 */
public class ApplicationException extends Exception {

    // Code d'erreur unique pour identifier le type d'erreur
    // Utile pour les API REST (renvoyer un code structuré au client)
    private final String codeErreur;

    /**
     * Constructeur avec message uniquement.
     */
    public ApplicationException(String codeErreur, String message) {
        super(message);
        this.codeErreur = codeErreur;
    }

    /**
     * Constructeur avec CHAINAGE D'EXCEPTION (exception cause).
     *
     * Le chaînage permet de conserver la CAUSE ORIGINALE de l'erreur.
     * C'est essentiel pour le débogage :
     * → "L'utilisateur n'a pas pu être créé" (notre exception)
     * → "car la connexion à la base de données a échoué" (cause originale)
     *
     * BONNE PRATIQUE : toujours passer la cause quand on encapsule une exception.
     */
    public ApplicationException(String codeErreur, String message, Throwable cause) {
        super(message, cause);
        this.codeErreur = codeErreur;
    }

    public String getCodeErreur() {
        return codeErreur;
    }

    @Override
    public String toString() {
        String base = "[%s] %s".formatted(codeErreur, getMessage());
        if (getCause() != null) {
            base += " (cause: " + getCause().getMessage() + ")";
        }
        return base;
    }
}
