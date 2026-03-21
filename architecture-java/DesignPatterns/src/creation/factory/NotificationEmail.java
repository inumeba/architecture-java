package creation.factory;

/** Notification par EMAIL */
public final class NotificationEmail implements Notification {
    @Override
    public void envoyer(String destinataire, String message) {
        System.out.println("    [EMAIL] -> %s : %s".formatted(destinataire, message));
    }

    @Override
    public String getType() { return "EMAIL"; }
}
