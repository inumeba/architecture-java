package creation.factory;

/** Notification PUSH (mobile) */
public final class NotificationPush implements Notification {
    @Override
    public void envoyer(String destinataire, String message) {
        System.out.println("    [PUSH] -> %s : %s".formatted(destinataire, message));
    }

    @Override
    public String getType() { return "PUSH"; }
}
