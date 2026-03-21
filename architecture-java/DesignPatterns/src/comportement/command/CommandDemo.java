package comportement.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 10 : COMMAND — Encapsuler une action comme objet.  ║
 * ║  Permet : annuler, refaire, file d'attente, historique.     ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Undo/Redo (éditeur de texte, Photoshop)
 * → File d'attente de tâches (job queue)
 * → Historique des actions (audit log)
 * → Macros (enregistrer et rejouer des séquences)
 *
 * ANALOGIE :
 * Au restaurant :
 * - Vous donnez votre COMMANDE (l'objet Command) au serveur
 * - Le serveur la transmet au cuisinier (le récepteur)
 * - La commande est un OBJET : on peut l'annuler, la modifier, la garder en historique
 *
 * COMMENT ÇA MARCHE ?
 * 1. COMMAND : interface avec executer() et annuler()
 * 2. COMMANDES CONCRÈTES : encapsulent une action + son inverse
 * 3. RÉCEPTEUR : l'objet qui subit l'action (un document, un compte...)
 * 4. INVOCATEUR : gère l'historique et lance les commandes
 *
 * EN PRODUCTION :
 * → Java Swing : Action interface
 * → Spring Batch : Step (chaque étape est une commande)
 * → CQRS : Command Query Responsibility Segregation
 * → Git : chaque commit est une commande réversible
 */

// ─── INTERFACE COMMAND ───

interface Commande {
    void executer();
    void annuler();
    String getDescription();
}

// ─── RÉCEPTEUR : un éditeur de texte simple ───

class EditeurTexte {
    private StringBuilder contenu = new StringBuilder();

    void insererTexte(String texte, int position) {
        contenu.insert(Math.min(position, contenu.length()), texte);
    }

    void supprimerTexte(int position, int longueur) {
        int debut = Math.min(position, contenu.length());
        int fin = Math.min(debut + longueur, contenu.length());
        contenu.delete(debut, fin);
    }

    String getContenu() { return contenu.toString(); }

    void afficher() {
        System.out.println("      Contenu : \"%s\"".formatted(contenu));
    }
}

// ─── COMMANDES CONCRÈTES ───

/** Commande pour insérer du texte */
class InsererTexteCommande implements Commande {
    private final EditeurTexte editeur;
    private final String texte;
    private final int position;

    InsererTexteCommande(EditeurTexte editeur, String texte, int position) {
        this.editeur = editeur;
        this.texte = texte;
        this.position = position;
    }

    @Override
    public void executer() {
        editeur.insererTexte(texte, position);
    }

    @Override
    public void annuler() {
        // L'inverse d'insérer = supprimer
        editeur.supprimerTexte(position, texte.length());
    }

    @Override
    public String getDescription() {
        return "Inserer \"%s\" a la position %d".formatted(texte, position);
    }
}

/** Commande pour supprimer du texte */
class SupprimerTexteCommande implements Commande {
    private final EditeurTexte editeur;
    private final int position;
    private final int longueur;
    private String texteSupprime; // sauvegardé pour l'annulation

    SupprimerTexteCommande(EditeurTexte editeur, int position, int longueur) {
        this.editeur = editeur;
        this.position = position;
        this.longueur = longueur;
    }

    @Override
    public void executer() {
        // Sauvegarder le texte AVANT de le supprimer (pour annuler)
        String contenu = editeur.getContenu();
        int debut = Math.min(position, contenu.length());
        int fin = Math.min(debut + longueur, contenu.length());
        texteSupprime = contenu.substring(debut, fin);
        editeur.supprimerTexte(position, longueur);
    }

    @Override
    public void annuler() {
        // L'inverse de supprimer = réinsérer le texte sauvegardé
        editeur.insererTexte(texteSupprime, position);
    }

    @Override
    public String getDescription() {
        return "Supprimer %d caracteres a la position %d".formatted(longueur, position);
    }
}

// ─── INVOCATEUR : gère l'historique ───

/**
 * L'invocateur NE CONNAÎT PAS les détails des commandes.
 * Il sait juste les exécuter, les annuler, et garder l'historique.
 */
class HistoriqueCommandes {
    private final Deque<Commande> historique = new ArrayDeque<>();
    private final Deque<Commande> refaire = new ArrayDeque<>();

    void executer(Commande commande) {
        commande.executer();
        historique.push(commande);
        refaire.clear(); // On ne peut plus "refaire" après une nouvelle action
        System.out.println("    > %s".formatted(commande.getDescription()));
    }

    void annuler() {
        if (historique.isEmpty()) {
            System.out.println("    > Rien a annuler");
            return;
        }
        Commande derniere = historique.pop();
        derniere.annuler();
        refaire.push(derniere);
        System.out.println("    > ANNULER : %s".formatted(derniere.getDescription()));
    }

    void refaire() {
        if (refaire.isEmpty()) {
            System.out.println("    > Rien a refaire");
            return;
        }
        Commande commande = refaire.pop();
        commande.executer();
        historique.push(commande);
        System.out.println("    > REFAIRE : %s".formatted(commande.getDescription()));
    }
}

// ─── DÉMONSTRATION ───

public class CommandDemo {

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 10 : COMMAND ===");
        System.out.println("  But : encapsuler les actions (undo/redo)\n");

        EditeurTexte editeur = new EditeurTexte();
        HistoriqueCommandes historique = new HistoriqueCommandes();

        // Exécuter des commandes
        historique.executer(new InsererTexteCommande(editeur, "Bonjour", 0));
        editeur.afficher();

        historique.executer(new InsererTexteCommande(editeur, " le monde", 7));
        editeur.afficher();

        historique.executer(new InsererTexteCommande(editeur, " cruel", 7));
        editeur.afficher();

        // ANNULER (Ctrl+Z)
        System.out.println("\n  --- Ctrl+Z (Annuler) ---");
        historique.annuler(); // Enlève " cruel"
        editeur.afficher();

        historique.annuler(); // Enlève " le monde"
        editeur.afficher();

        // REFAIRE (Ctrl+Y)
        System.out.println("\n  --- Ctrl+Y (Refaire) ---");
        historique.refaire(); // Remet " le monde"
        editeur.afficher();

        // Supprimer du texte
        System.out.println("\n  --- Suppression ---");
        historique.executer(new SupprimerTexteCommande(editeur, 0, 3)); // Supprime "Bon"
        editeur.afficher();

        // Annuler la suppression
        historique.annuler();
        editeur.afficher();
    }
}
