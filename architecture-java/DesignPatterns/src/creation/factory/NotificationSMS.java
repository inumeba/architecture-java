package creation.factory;

/** Notification par SMS */
public final class NotificationSMS implements Notification {
    @Override
    public void envoyer(String destinataire, String message) {
        System.out.println("    [SMS] -> %s : %s".formatted(destinataire, message));
    }

    @Override
    public String getType() { return "SMS"; }
}
