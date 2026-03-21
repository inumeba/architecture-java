package commun;

/**
 * COMMUN — Message de communication inter-services
 *
 * Dans une architecture microservices, les services ne s'appellent
 * PAS directement entre eux. Ils communiquent via des MESSAGES.
 *
 * Ce message est l'unité de communication :
 * - Il a un TYPE (ex: "PRODUIT_VERIFIE", "COMMANDE_CREEE")
 * - Il a un EXPEDITEUR (quel service l'a envoyé)
 * - Il a un CONTENU (les données transportées)
 *
 * En production, ces messages transiteraient via :
 * - Apache Kafka (streaming d'événements)
 * - RabbitMQ (file de messages)
 * - AWS SQS / SNS
 * - gRPC (appels synchrones entre services)
 */
public class Message {

    // Le type identifie la nature du message (comme un sujet d'email)
    private final String type;

    // Quel service a créé ce message
    private final String expediteur;

    // Le contenu du message (données sérialisées)
    // En production, ce serait du JSON ou du Protobuf
    private final String contenu;

    public Message(String type, String expediteur, String contenu) {
        this.type = type;
        this.expediteur = expediteur;
        this.contenu = contenu;
    }

    public String getType() {
        return type;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public String getContenu() {
        return contenu;
    }

    @Override
    public String toString() {
        return "[%s] de '%s' : %s".formatted(type, expediteur, contenu);
    }
}
