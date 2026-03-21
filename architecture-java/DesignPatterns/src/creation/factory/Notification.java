package creation.factory;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 2 : FACTORY METHOD — Déléguer la création          ║
 * ║  d'objets à une méthode spécialisée.                        ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Quand on ne sait pas à l'avance quel type concret créer
 * → Quand la création est complexe (beaucoup de paramètres)
 * → Quand on veut découpler le code appelant du type concret
 *
 * ANALOGIE :
 * Une pizzeria (factory) crée des pizzas.
 * Le client dit "margherita" → la pizzeria sait comment la faire.
 * Le client ne connaît PAS la recette, juste le nom.
 *
 * COMMENT ÇA MARCHE ?
 * 1. Une INTERFACE commune (Notification)
 * 2. Plusieurs IMPLÉMENTATIONS concrètes (Email, SMS, Push)
 * 3. Une FACTORY qui choisit quelle classe instancier
 *
 * EN PRODUCTION :
 * → Spring utilise des factories partout (BeanFactory, FactoryBean)
 * → JDBC : DriverManager.getConnection() est une factory
 * → Collections : List.of(), Map.of() sont des factory methods
 */

// ─── INTERFACE COMMUNE (le "produit") ───

/**
 * Toutes les notifications ont la même interface.
 * Le code appelant manipule CETTE interface,
 * sans connaître le type concret (Email, SMS...).
 *
 * C'est le PRINCIPE DE SUBSTITUTION DE LISKOV :
 * on peut remplacer n'importe quelle implémentation
 * sans changer le code appelant.
 */
public sealed interface Notification permits NotificationEmail, NotificationSMS, NotificationPush {
    void envoyer(String destinataire, String message);
    String getType();
}
