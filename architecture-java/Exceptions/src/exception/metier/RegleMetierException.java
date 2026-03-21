package exception.metier;

/**
 * EXCEPTION CHECKED — Règle métier violée
 *
 * Lancée quand une action viole une règle métier.
 * Exemples :
 * - Solde insuffisant pour un virement
 * - Stock insuffisant pour une vente
 * - Remise supérieure au maximum autorisé
 *
 * C'est CHECKED car l'appelant doit informer l'utilisateur
 * de la violation et peut proposer une correction.
 */
public class RegleMetierException extends ApplicationException {

    private final String regle;

    public RegleMetierException(String regle, String message) {
        super("REGLE_METIER_VIOLEE", message);
        this.regle = regle;
    }

    /**
     * Avec chaînage — la règle a été violée à cause d'une autre erreur.
     */
    public RegleMetierException(String regle, String message, Throwable cause) {
        super("REGLE_METIER_VIOLEE", message, cause);
        this.regle = regle;
    }

    public String getRegle() {
        return regle;
    }
}
