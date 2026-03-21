package exception.metier;

/**
 * EXCEPTION CHECKED — Erreur technique (infrastructure)
 *
 * Lancée quand un problème TECHNIQUE empêche l'opération :
 * - Base de données inaccessible
 * - Fichier introuvable ou corrompu
 * - Service externe indisponible (API, SMTP...)
 *
 * C'est CHECKED car l'appelant doit gérer la situation :
 * → Réessayer plus tard
 * → Utiliser un cache
 * → Informer l'utilisateur
 *
 * BONNE PRATIQUE : cette exception ENCAPSULE l'exception technique originale
 * sans la propager. Cela évite que le code métier dépende de
 * java.sql.SQLException ou java.io.IOException.
 */
public class ErreurTechniqueException extends ApplicationException {

    public ErreurTechniqueException(String message, Throwable cause) {
        super("ERREUR_TECHNIQUE", message, cause);
    }

    public ErreurTechniqueException(String message) {
        super("ERREUR_TECHNIQUE", message);
    }
}
