package adaptateur.sortant;

import domaine.port.sortant.NotificationPort;

/**
 * ADAPTATEUR SORTANT — Notification par console
 *
 * Implémente le port de notification en affichant dans la console.
 *
 * En production, on pourrait avoir :
 * - NotificationEmailAdaptateur  → envoie un email via SMTP
 * - NotificationSlackAdaptateur  → envoie un message sur Slack
 * - NotificationSmsAdaptateur    → envoie un SMS via Twilio
 *
 * Le use case dit "j'ai besoin de notifier" via le port.
 * Il ne sait pas (et ne VEUT pas savoir) si c'est un email ou un SMS.
 *
 * Pour changer le canal de notification, il suffit de :
 * 1. Créer un nouvel adaptateur qui implémente NotificationPort
 * 2. L'injecter dans le use case à la place de celui-ci
 * 3. Aucun changement dans le domaine !
 */
public class NotificationConsoleAdaptateur implements NotificationPort {

    @Override
    public void alerterStockBas(String nomProduit, int stockRestant) {
        System.out.println("   ** ALERTE ** Stock bas pour '" + nomProduit
                + "' : seulement " + stockRestant + " unite(s) restante(s) !");
    }

    @Override
    public void notifierVente(String nomProduit, int quantite, double montant) {
        System.out.printf("   >> Vente enregistree : %dx %s = %.2f EUR%n",
                quantite, nomProduit, montant);
    }
}
