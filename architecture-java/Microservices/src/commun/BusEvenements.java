package commun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * COMMUN — Bus d'événements (Event Bus)
 *
 * Le bus d'événements est le SYSTÈME NERVEUX de l'architecture microservices.
 * Il permet aux services de communiquer SANS se connaître mutuellement.
 *
 * Principe :
 * - Un service PUBLIE un message sur le bus (ex: "COMMANDE_CREEE")
 * - Les services intéressés S'ABONNENT à ce type de message
 * - Le bus DISTRIBUE le message à tous les abonnés
 *
 * C'est le pattern "Publish / Subscribe" (Pub/Sub).
 *
 * Avantage majeur : DÉCOUPLAGE
 * - Le service Commande ne sait pas que le service Produit existe
 * - Il publie juste un événement, et le bus s'occupe du reste
 *
 * En production, ce bus serait remplacé par :
 * - Apache Kafka
 * - RabbitMQ
 * - Redis Pub/Sub
 * - AWS EventBridge
 */
public class BusEvenements {

    // Table des abonnements : type de message → liste de callbacks
    // Chaque abonné fournit un Consumer<Message> qui sera appelé
    private final Map<String, List<Consumer<Message>>> abonnements = new HashMap<>();

    // Journal des messages pour traçabilité (en production : logs centralisés)
    private final List<Message> journal = new ArrayList<>();

    /**
     * S'abonner à un type de message.
     *
     * Le callback sera appelé à chaque fois qu'un message
     * de ce type est publié sur le bus.
     *
     * @param typeMessage le type de message à écouter
     * @param callback    la fonction à exécuter quand le message arrive
     */
    public void abonner(String typeMessage, Consumer<Message> callback) {
        abonnements
                .computeIfAbsent(typeMessage, k -> new ArrayList<>())
                .add(callback);
    }

    /**
     * Publier un message sur le bus.
     *
     * Tous les abonnés au type de ce message seront notifiés.
     * Le message est aussi enregistré dans le journal.
     *
     * @param message le message à diffuser
     */
    public void publier(Message message) {
        // 1. Enregistrer dans le journal (traçabilité)
        journal.add(message);
        System.out.println("    [BUS] " + message);

        // 2. Notifier tous les abonnés de ce type
        List<Consumer<Message>> abonnes = abonnements.get(message.getType());
        if (abonnes != null) {
            for (Consumer<Message> callback : abonnes) {
                callback.accept(message);
            }
        }
    }

    /**
     * Affiche l'historique complet des messages échangés.
     * Utile pour le débogage et la compréhension du flux.
     */
    public void afficherJournal() {
        System.out.println("\n====================================");
        System.out.println("  JOURNAL DU BUS D'EVENEMENTS");
        System.out.println("====================================");
        if (journal.isEmpty()) {
            System.out.println("  (aucun message)");
        } else {
            for (int i = 0; i < journal.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, journal.get(i));
            }
        }
        System.out.println("====================================\n");
    }
}
