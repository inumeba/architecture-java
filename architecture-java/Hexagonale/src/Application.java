import adaptateur.entrant.ApiRestAdaptateur;
import adaptateur.entrant.CliAdaptateur;
import adaptateur.sortant.NotificationConsoleAdaptateur;
import adaptateur.sortant.ProduitMemoireAdaptateur;
import domaine.port.entrant.GestionProduitPort;
import domaine.port.sortant.NotificationPort;
import domaine.port.sortant.ProduitRepositoryPort;
import domaine.usecase.GestionProduitUseCase;

/**
 * POINT D'ENTRÉE — Assemblage de l'architecture hexagonale
 *
 * C'est ici qu'on connecte tous les hexagones :
 * les adaptateurs sortants → le domaine → les adaptateurs entrants.
 *
 * En production avec Spring, cet assemblage est AUTOMATIQUE :
 * - @Repository sur les adaptateurs sortants
 * - @Service sur les use cases
 * - @RestController sur les adaptateurs entrants
 * - Spring injecte tout via @Autowired
 *
 * L'ordre d'assemblage reflète la DIRECTION DES DÉPENDANCES :
 * 1. D'abord les adaptateurs sortants (pas de dépendance)
 * 2. Puis le domaine (dépend des ports sortants)
 * 3. Enfin les adaptateurs entrants (dépendent des ports entrants)
 */
public class Application {

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println("  ARCHITECTURE HEXAGONALE");
        System.out.println("  (Ports & Adapters)");
        System.out.println("====================================\n");

        // =============================================================
        // ÉTAPE 1 : Créer les adaptateurs SORTANTS
        // =============================================================
        // Ce sont les ressources techniques dont le domaine a besoin.
        // Ils implémentent les ports sortants (interfaces du domaine).

        System.out.println("--- ASSEMBLAGE DES COMPOSANTS ---\n");

        // Adaptateur de persistance : stockage en mémoire
        // (en production : PostgresAdaptateur, MongoAdaptateur...)
        ProduitRepositoryPort repository = new ProduitMemoireAdaptateur();
        System.out.println("[OK] Adaptateur sortant : ProduitMemoireAdaptateur (persistance)");

        // Adaptateur de notification : sortie console
        // (en production : EmailAdaptateur, SlackAdaptateur...)
        NotificationPort notification = new NotificationConsoleAdaptateur();
        System.out.println("[OK] Adaptateur sortant : NotificationConsoleAdaptateur (alertes)");

        // =============================================================
        // ÉTAPE 2 : Créer le DOMAINE (use case)
        // =============================================================
        // Le use case reçoit les ports sortants via le constructeur.
        // Il ne sait PAS quelle implémentation est derrière.

        GestionProduitPort gestionProduit = new GestionProduitUseCase(repository, notification);
        System.out.println("[OK] Domaine : GestionProduitUseCase (logique metier)");

        // =============================================================
        // ÉTAPE 3 : Créer les adaptateurs ENTRANTS
        // =============================================================
        // Ils reçoivent le port entrant (interface du domaine).
        // Plusieurs adaptateurs entrants partagent le MÊME domaine.

        ApiRestAdaptateur apiRest = new ApiRestAdaptateur(gestionProduit);
        System.out.println("[OK] Adaptateur entrant : ApiRestAdaptateur (API HTTP)");

        CliAdaptateur cli = new CliAdaptateur(gestionProduit);
        System.out.println("[OK] Adaptateur entrant : CliAdaptateur (ligne de commande)");

        // =============================================================
        // DÉMONSTRATION — Via l'API REST
        // =============================================================

        System.out.println("\n\n========================================");
        System.out.println("  DEMONSTRATION VIA L'API REST");
        System.out.println("========================================");

        // Créer des produits
        apiRest.postProduit("Clavier mecanique", 89.99, 10);
        apiRest.postProduit("Souris sans fil", 39.99, 25);
        apiRest.postProduit("Ecran 27 pouces", 349.00, 3);

        // Lister les produits
        apiRest.getProduits();

        // Appliquer une remise de 20% au clavier
        // (la règle "max 50%" est dans l'ENTITÉ, pas dans l'adaptateur)
        apiRest.patchRemise(findId(gestionProduit, "Clavier mecanique"), 20);

        // Tenter une remise excessive (60%)
        // → Le DOMAINE refuse, pas l'adaptateur
        apiRest.patchRemise(findId(gestionProduit, "Souris sans fil"), 60);

        // Effectuer des ventes
        // → Le use case orchestre : retirer stock + notifier + alerter si bas
        int idEcran = findId(gestionProduit, "Ecran 27 pouces");
        apiRest.postVente(idEcran, 1); // Stock passe à 2 → alerte stock bas !
        apiRest.postVente(idEcran, 1); // Stock passe à 1 → alerte encore

        // Tenter de vendre plus que le stock
        apiRest.postVente(idEcran, 5); // → Le DOMAINE refuse

        // Chercher un produit inexistant
        apiRest.getProduit(99999);

        // =============================================================
        // DÉMONSTRATION — Via la CLI (même domaine, autre entrée !)
        // =============================================================

        System.out.println("\n\n========================================");
        System.out.println("  DEMONSTRATION VIA LA CLI");
        System.out.println("  (meme domaine, autre adaptateur)");
        System.out.println("========================================");

        // Le même catalogue, les mêmes données, la même logique
        // mais un affichage différent (format CLI vs format REST)
        cli.commandeLister();

        // Vendre via la CLI → même effet sur le stock
        cli.commandeVendre(findId(gestionProduit, "Souris sans fil"), 5);

        // Vérifier que la vente CLI est visible via l'API REST
        System.out.println("\n--- Verification croisee (REST apres vente CLI) ---");
        apiRest.getProduits();

        // =============================================================
        // RÉSUMÉ
        // =============================================================
        System.out.println("\n========================================");
        System.out.println("  RESUME DE L'ARCHITECTURE");
        System.out.println("========================================");
        System.out.println("  Le DOMAINE (hexagone central) est isole.");
        System.out.println("  2 adaptateurs ENTRANTS (REST + CLI) utilisent");
        System.out.println("  le MEME port et la MEME logique metier.");
        System.out.println("  2 adaptateurs SORTANTS (memoire + console)");
        System.out.println("  peuvent etre remplaces sans toucher au domaine.");
        System.out.println("========================================\n");
    }

    /**
     * Utilitaire pour trouver l'ID d'un produit par son nom.
     */
    private static int findId(GestionProduitPort port, String nom) {
        return port.listerProduits().stream()
                .filter(p -> p.getNom().equals(nom))
                .findFirst()
                .map(p -> p.getId())
                .orElse(-1);
    }
}
