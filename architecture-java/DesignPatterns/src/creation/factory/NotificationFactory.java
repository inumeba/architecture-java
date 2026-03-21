package creation.factory;

/**
 * LA FACTORY — Crée la bonne notification selon le type demandé.
 *
 * Le code appelant ne fait JAMAIS "new NotificationEmail()".
 * Il demande à la factory : "donne-moi une notification de type EMAIL".
 *
 * AVANTAGE : si on ajoute un nouveau type (WhatsApp, Slack...),
 * on modifie UNIQUEMENT la factory, pas les 50 endroits du code
 * qui créent des notifications.
 */
public class NotificationFactory {

    /**
     * Méthode factory — crée la bonne implémentation.
     *
     * Le switch expression (Java 17+) est parfait ici :
     * exhaustif avec les sealed interfaces.
     */
    public static Notification creer(String type) {
        return switch (type.toUpperCase()) {
            case "EMAIL" -> new NotificationEmail();
            case "SMS"   -> new NotificationSMS();
            case "PUSH"  -> new NotificationPush();
            default -> throw new IllegalArgumentException(
                    "Type de notification inconnu : " + type);
        };
    }

    // ─── DÉMONSTRATION ───

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 2 : FACTORY METHOD ===");
        System.out.println("  But : deleguer la creation d'objets\n");

        // Le code appelant ne connaît PAS les classes concrètes
        // Il utilise UNIQUEMENT l'interface Notification
        String[] types = {"EMAIL", "SMS", "PUSH"};

        for (String type : types) {
            // La factory choisit la bonne classe
            Notification notif = NotificationFactory.creer(type);
            notif.envoyer("utilisateur@test.com", "Bienvenue !");
        }

        // AVANTAGE : pour ajouter WhatsApp, on modifie UNIQUEMENT
        // la factory (un seul endroit), pas le code appelant
    }
}
