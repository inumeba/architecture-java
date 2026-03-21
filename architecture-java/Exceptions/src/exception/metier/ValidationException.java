package exception.metier;

/**
 * EXCEPTION CHECKED — Validation des données d'entrée
 *
 * Lancée quand les données fournies par l'utilisateur sont invalides.
 * Exemples :
 * - Email mal formaté
 * - Champ obligatoire vide
 * - Valeur hors limites
 *
 * Différence avec RegleMetierException :
 * - ValidationException → les données sont MALFORMÉES (format, nullité)
 * - RegleMetierException → les données sont valides mais INTERDITES par une règle
 *
 * Exemple :
 * - Prix = "abc"     → ValidationException (ce n'est pas un nombre)
 * - Prix = -10       → ValidationException (format invalide)
 * - Remise = 60%     → RegleMetierException (la règle dit max 50%)
 */
public class ValidationException extends ApplicationException {

    private final String champ;
    private final Object valeur;

    public ValidationException(String champ, Object valeur, String message) {
        super("VALIDATION_ECHEC", "Champ '%s' invalide : %s (valeur recue : %s)"
                .formatted(champ, message, valeur));
        this.champ = champ;
        this.valeur = valeur;
    }

    public String getChamp() {
        return champ;
    }

    public Object getValeur() {
        return valeur;
    }
}
