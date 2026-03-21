package structure.facade;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 6 : FACADE — Simplifier un système complexe        ║
 * ║  derrière une interface simple.                              ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Un sous-système a beaucoup de classes complexes
 * → On veut offrir une interface simple au code appelant
 * → On veut découpler le client du sous-système
 *
 * ANALOGIE :
 * Regarder un film avec un home cinéma :
 * SANS facade : allumer la TV, changer l'entrée HDMI, allumer l'ampli,
 *               régler le volume, allumer le lecteur Blu-ray, insérer le disque...
 * AVEC facade : appuyer sur le bouton "Regarder un film"
 *
 * COMMENT ÇA MARCHE ?
 * 1. Plusieurs SOUS-SYSTÈMES complexes (Stock, Paiement, Livraison)
 * 2. Une FACADE qui orchestre les sous-systèmes
 * 3. Le client n'utilise QUE la facade
 *
 * EN PRODUCTION :
 * → Spring : @Service est souvent une facade
 * → JDBC : DataSource est une facade sur le pool de connexions
 * → SLF4J : facade sur les loggers (Log4j, Logback...)
 * → javax.faces : FacesContext (facade du cycle JSF)
 */

// ─── SOUS-SYSTÈME 1 : Vérification du stock ───

class ServiceStock {
    boolean verifierDisponibilite(String produit, int quantite) {
        System.out.println("    [Stock] Verification : %d x %s...".formatted(quantite, produit));
        return true; // Simulé : toujours disponible
    }

    void reserverStock(String produit, int quantite) {
        System.out.println("    [Stock] Reserve : %d x %s".formatted(quantite, produit));
    }
}

// ─── SOUS-SYSTÈME 2 : Paiement ───

class ServicePaiementInterne {
    boolean verifierSolde(String client, double montant) {
        System.out.println("    [Paiement] Verification solde de %s pour %.2f EUR...".formatted(client, montant));
        return true;
    }

    String debiter(String client, double montant) {
        String transaction = "TXN-" + System.currentTimeMillis();
        System.out.println("    [Paiement] Debit de %.2f EUR -> %s".formatted(montant, transaction));
        return transaction;
    }
}

// ─── SOUS-SYSTÈME 3 : Livraison ───

class ServiceLivraison {
    String calculerDelai(String adresse) {
        System.out.println("    [Livraison] Calcul du delai pour %s...".formatted(adresse));
        return "2-3 jours ouvrables";
    }

    String creerExpedition(String produit, String adresse) {
        String tracking = "EXP-" + System.currentTimeMillis();
        System.out.println("    [Livraison] Expedition creee : %s".formatted(tracking));
        return tracking;
    }
}

// ─── SOUS-SYSTÈME 4 : Email de confirmation ───

class ServiceEmailInterne {
    void envoyerConfirmation(String client, String transaction, String tracking) {
        System.out.println("    [Email] Confirmation envoyee a %s (TXN: %s, Tracking: %s)"
                .formatted(client, transaction, tracking));
    }
}

// ─── LA FACADE ───

/**
 * La facade SIMPLIFIE l'achat en orchestrant tous les sous-systèmes.
 *
 * AVANT (sans facade) : le client doit appeler 6 méthodes
 * dans le bon ordre, sur 4 services différents.
 *
 * APRÈS (avec facade) : UNE seule méthode "passer commande".
 */
public class FacadeDemo {

    private final ServiceStock stock = new ServiceStock();
    private final ServicePaiementInterne paiement = new ServicePaiementInterne();
    private final ServiceLivraison livraison = new ServiceLivraison();
    private final ServiceEmailInterne email = new ServiceEmailInterne();

    /**
     * UNE SEULE méthode qui orchestre tout le processus d'achat.
     * Le client n'a pas besoin de connaître les 4 sous-systèmes.
     */
    public boolean passerCommande(String client, String produit, int quantite,
                                  double montant, String adresse) {
        System.out.println("  --- Commande de %s ---".formatted(client));

        // Étape 1 : Vérifier le stock
        if (!stock.verifierDisponibilite(produit, quantite)) {
            System.out.println("  ECHEC : produit indisponible");
            return false;
        }

        // Étape 2 : Vérifier le paiement
        if (!paiement.verifierSolde(client, montant)) {
            System.out.println("  ECHEC : solde insuffisant");
            return false;
        }

        // Étape 3 : Exécuter la commande
        stock.reserverStock(produit, quantite);
        String transaction = paiement.debiter(client, montant);
        String tracking = livraison.creerExpedition(produit, adresse);

        // Étape 4 : Confirmer
        email.envoyerConfirmation(client, transaction, tracking);

        System.out.println("  SUCCES : commande completee !\n");
        return true;
    }

    // ─── DÉMONSTRATION ───

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 6 : FACADE ===");
        System.out.println("  But : simplifier un systeme complexe\n");

        FacadeDemo boutique = new FacadeDemo();

        // UNE seule méthode au lieu de 6 appels manuels
        boutique.passerCommande("Alice", "MacBook Pro", 1, 2499.00, "12 rue de la Paix, Paris");
        boutique.passerCommande("Bob", "AirPods", 2, 259.00, "5 avenue des Champs-Elysees");
    }
}
