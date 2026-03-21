package comportement.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 8 : OBSERVER — Notifier automatiquement les        ║
 * ║  abonnés quand un événement se produit.                     ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Quand un objet change d'état et d'autres doivent réagir
 * → Notifications en temps réel (chat, bourse, alertes)
 * → Découpler l'émetteur des récepteurs
 *
 * ANALOGIE :
 * YouTube : quand une chaîne publie une vidéo,
 * tous les ABONNÉS reçoivent une notification.
 * La chaîne ne connaît pas personnellement chaque abonné.
 *
 * COMMENT ÇA MARCHE ?
 * 1. SUJET (Observable) : maintient une liste d'observateurs
 * 2. OBSERVATEUR : interface avec une méthode mettreAJour()
 * 3. OBSERVATEURS CONCRETS : réagissent à la notification
 * 4. Le sujet NOTIFIE tous les observateurs quand il change
 *
 * EN PRODUCTION :
 * → JavaFX : PropertyChangeListener, Observable
 * → Spring : ApplicationEventPublisher / @EventListener
 * → React : le state change → les components se re-rendent
 * → Kafka/RabbitMQ : publish/subscribe à grande échelle
 */

// ─── OBSERVATEUR (interface) ───

interface Observateur {
    void mettreAJour(String evenement, Object donnees);
}

// ─── SUJET (Observable) ───

/**
 * Le sujet maintient une LISTE d'observateurs.
 * Quand son état change, il NOTIFIE tout le monde.
 *
 * Les observateurs ne se connaissent pas entre eux.
 * Le sujet ne connaît pas les détails des observateurs.
 * → DÉCOUPLAGE TOTAL
 */
class ChaineYouTube {
    private final String nom;
    private final List<Observateur> abonnes = new ArrayList<>();

    ChaineYouTube(String nom) { this.nom = nom; }

    /** S'abonner = ajouter un observateur */
    void abonner(Observateur observateur) {
        abonnes.add(observateur);
        System.out.println("    [%s] Nouvel abonne ! (total: %d)".formatted(nom, abonnes.size()));
    }

    /** Se désabonner = retirer un observateur */
    void desabonner(Observateur observateur) {
        abonnes.remove(observateur);
    }

    /** Publier = notifier TOUS les abonnés */
    void publierVideo(String titreVideo) {
        System.out.println("\n    [%s] Nouvelle video : \"%s\"".formatted(nom, titreVideo));
        // Notifier chaque abonné
        for (Observateur abonne : abonnes) {
            abonne.mettreAJour("NOUVELLE_VIDEO", titreVideo);
        }
    }

    /** Lancer un live = notifier avec un événement différent */
    void lancerLive(String sujet) {
        System.out.println("\n    [%s] LIVE : \"%s\"".formatted(nom, sujet));
        for (Observateur abonne : abonnes) {
            abonne.mettreAJour("LIVE", sujet);
        }
    }
}

// ─── OBSERVATEURS CONCRETS ───

/** Abonné qui regarde sur son téléphone */
class AbonneMobile implements Observateur {
    private final String nom;

    AbonneMobile(String nom) { this.nom = nom; }

    @Override
    public void mettreAJour(String evenement, Object donnees) {
        String icone = evenement.equals("LIVE") ? "🔴" : "🔔";   // Pas d'emoji réel en console
        System.out.println("      [MOBILE] %s recoit: [%s] %s".formatted(nom, evenement, donnees));
    }
}

/** Abonné qui reçoit un email */
class AbonneEmail implements Observateur {
    private final String email;

    AbonneEmail(String email) { this.email = email; }

    @Override
    public void mettreAJour(String evenement, Object donnees) {
        System.out.println("      [EMAIL] -> %s : [%s] %s".formatted(email, evenement, donnees));
    }
}

/** Statisticien qui compte les publications (observateur interne) */
class StatistiquesChaine implements Observateur {
    private int nbVideos = 0;
    private int nbLives = 0;

    @Override
    public void mettreAJour(String evenement, Object donnees) {
        switch (evenement) {
            case "NOUVELLE_VIDEO" -> nbVideos++;
            case "LIVE" -> nbLives++;
        }
        System.out.println("      [STATS] Videos: %d, Lives: %d".formatted(nbVideos, nbLives));
    }
}

// ─── DÉMONSTRATION ───

public class ObserverDemo {

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 8 : OBSERVER ===");
        System.out.println("  But : notifier les abonnes lors d'un changement\n");

        // Créer la chaîne (le sujet)
        ChaineYouTube chaine = new ChaineYouTube("Code En Francais");

        // Créer les observateurs
        AbonneMobile alice = new AbonneMobile("Alice");
        AbonneEmail bob = new AbonneEmail("bob@mail.com");
        StatistiquesChaine stats = new StatistiquesChaine();

        // Les abonner
        chaine.abonner(alice);
        chaine.abonner(bob);
        chaine.abonner(stats);

        // Publier → TOUS les abonnés sont notifiés automatiquement
        chaine.publierVideo("Design Patterns en Java");
        chaine.lancerLive("Questions/Reponses");

        // Alice se désabonne
        System.out.println("\n    Alice se desabonne...");
        chaine.desabonner(alice);

        // Seuls Bob et Stats reçoivent la notification
        chaine.publierVideo("SOLID en 10 minutes");
    }
}
