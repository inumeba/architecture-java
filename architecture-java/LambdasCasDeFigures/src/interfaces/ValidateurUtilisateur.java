package interfaces;

import modele.Utilisateur;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  INTERFACE FONCTIONNELLE PERSONNALISÉE                       ║
 * ╚══════════════════════════════════════════════════════════════╝
 * - L'annotation @FunctionalInterface n'est pas obligatoire mais 
 *   fortement recommandée. Elle demande au compilateur de vérifier
 *   qu'il n'y a qu'UNE SEULE méthode abstraite (SAM - Single Abstract Method).
 */
@FunctionalInterface
public interface ValidateurUtilisateur {

    /**
     * Méthode abstraite unique qui définit la signature de la lambda.
     * Expression Lambda correspondante : (Utilisateur u) -> boolean
     */
    boolean valider(Utilisateur utilisateur);

    // 💡 On peut tout de même avoir des méthodes "default" ou "static"
    // sans casser l'interface fonctionnelle.
    default ValidateurUtilisateur et(ValidateurUtilisateur autre) {
        return (Utilisateur u) -> this.valider(u) && autre.valider(u);
    }
}
