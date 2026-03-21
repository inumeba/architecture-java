package domaine.port.sortant;

/**
 * PORT SORTANT — Interface de notification
 *
 * Un deuxième port sortant pour illustrer que le domaine
 * peut avoir PLUSIEURS dépendances externes, chacune
 * représentée par un port différent.
 *
 * Ce port permet au domaine de notifier le monde extérieur
 * quand un événement métier se produit (stock bas, vente, etc.)
 * SANS savoir comment la notification est envoyée.
 *
 * L'adaptateur pourrait envoyer :
 * - Un email
 * - Un SMS
 * - Un message Slack
 * - Un log dans la console (c'est ce qu'on fait ici)
 */
public interface NotificationPort {

    /**
     * Envoie une alerte quand le stock d'un produit est bas.
     */
    void alerterStockBas(String nomProduit, int stockRestant);

    /**
     * Notifie qu'une vente a été effectuée.
     */
    void notifierVente(String nomProduit, int quantite, double montant);
}
