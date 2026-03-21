package comportement.template;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 9 : TEMPLATE METHOD — Définir le squelette d'un    ║
 * ║  algorithme et laisser les sous-classes remplir les étapes. ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Plusieurs classes ont le MÊME algorithme général
 *   mais des étapes DIFFÉRENTES
 * → On veut éviter la duplication de la logique commune
 *
 * ANALOGIE :
 * Préparer une boisson chaude :
 * 1. Faire bouillir l'eau     (commun)
 * 2. Infuser le produit       (DIFFÉRENT : thé vs café)
 * 3. Verser dans la tasse     (commun)
 * 4. Ajouter les extras       (DIFFÉRENT : lait vs citron)
 *
 * Le SQUELETTE est le même, les DÉTAILS changent.
 *
 * COMMENT ÇA MARCHE ?
 * 1. CLASSE ABSTRAITE : définit le squelette (méthode template)
 * 2. MÉTHODES ABSTRAITES : les étapes que les sous-classes remplissent
 * 3. MÉTHODE TEMPLATE : appelle les étapes dans le bon ordre (final)
 * 4. HOOK : méthode optionnelle que la sous-classe peut surcharger
 *
 * EN PRODUCTION :
 * → Spring : AbstractController, AbstractRoutingDataSource
 * → JUnit : @BeforeEach (setUp) / @AfterEach (tearDown)
 * → Servlet : HttpServlet.service() appelle doGet()/doPost()
 * → Java I/O : InputStream.read(byte[]) appelle read()
 */

// ─── CLASSE ABSTRAITE (le squelette) ───

abstract class RapportGenerateur {

    /**
     * LA MÉTHODE TEMPLATE — Le squelette de l'algorithme.
     *
     * "final" → les sous-classes NE PEUVENT PAS modifier l'ordre.
     * Elles peuvent seulement remplir les étapes abstraites.
     *
     * C'est le PRINCIPE D'HOLLYWOOD :
     * "Ne nous appelez pas, c'est nous qui vous appellerons"
     */
    final void genererRapport(String titre) {
        System.out.println("  --- %s ---".formatted(titre));
        ouvrirFichier();
        ecrireEnTete(titre);
        ecrireContenu();         // ← ABSTRAITE : chaque sous-classe la définit
        if (ajouterGraphiques()) { // ← HOOK : optionnel
            System.out.println("    [Graphiques ajoutes]");
        }
        fermerFichier();
        System.out.println("    Rapport genere avec succes !\n");
    }

    // ─── Étapes COMMUNES (implémentées ici) ───

    private void ouvrirFichier() {
        System.out.println("    1. Ouverture du fichier...");
    }

    private void fermerFichier() {
        System.out.println("    5. Fermeture du fichier.");
    }

    // ─── Étapes ABSTRAITES (à remplir par les sous-classes) ───

    protected abstract void ecrireEnTete(String titre);
    protected abstract void ecrireContenu();

    // ─── HOOK : méthode optionnelle avec comportement par défaut ───

    /**
     * Un HOOK (crochet) a une implémentation par défaut.
     * Les sous-classes PEUVENT la surcharger, mais ne sont pas obligées.
     */
    protected boolean ajouterGraphiques() {
        return false; // Par défaut, pas de graphiques
    }
}

// ─── SOUS-CLASSES CONCRÈTES ───

/** Rapport au format PDF */
class RapportPDF extends RapportGenerateur {
    @Override
    protected void ecrireEnTete(String titre) {
        System.out.println("    2. [PDF] En-tete : <h1>%s</h1>".formatted(titre));
    }

    @Override
    protected void ecrireContenu() {
        System.out.println("    3. [PDF] Contenu avec mise en page riche");
    }

    @Override
    protected boolean ajouterGraphiques() {
        return true; // PDF supporte les graphiques
    }
}

/** Rapport au format CSV */
class RapportCSV extends RapportGenerateur {
    @Override
    protected void ecrireEnTete(String titre) {
        System.out.println("    2. [CSV] En-tete : %s".formatted(titre));
    }

    @Override
    protected void ecrireContenu() {
        System.out.println("    3. [CSV] Donnees separees par des virgules");
    }
    // Pas de graphiques en CSV → on garde le hook par défaut (false)
}

/** Rapport au format HTML */
class RapportHTML extends RapportGenerateur {
    @Override
    protected void ecrireEnTete(String titre) {
        System.out.println("    2. [HTML] En-tete : <head><title>%s</title></head>".formatted(titre));
    }

    @Override
    protected void ecrireContenu() {
        System.out.println("    3. [HTML] Contenu avec balises et styles CSS");
    }

    @Override
    protected boolean ajouterGraphiques() {
        return true; // HTML supporte les graphiques (Chart.js par ex.)
    }
}

// ─── DÉMONSTRATION ───

public class TemplateDemo {

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 9 : TEMPLATE METHOD ===");
        System.out.println("  But : meme squelette, etapes differentes\n");

        // Le MÊME algorithme (genererRapport) produit 3 résultats différents
        RapportGenerateur[] generateurs = {
                new RapportPDF(),
                new RapportCSV(),
                new RapportHTML()
        };

        for (RapportGenerateur gen : generateurs) {
            gen.genererRapport("Ventes Q4 2024");
        }
    }
}
