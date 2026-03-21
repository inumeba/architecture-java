import controleur.ProduitControleur;
import modele.ProduitDepot;
import vue.ProduitVue;

/**
 * POINT D'ENTRÉE — Assemblage de l'application MVC
 *
 * C'est ici qu'on "branche" les 3 composants ensemble.
 * Cette classe crée les instances et les connecte.
 *
 * En production avec Spring, ce câblage serait automatique
 * grâce à l'injection de dépendances (@Autowired, @Component, etc.).
 */
public class Application {

    public static void main(String[] args) {

        // === ÉTAPE 1 : Créer les composants MVC ===

        // Le modèle — stocke et gère les données
        ProduitDepot depot = new ProduitDepot();

        // La vue — gère tout l'affichage
        ProduitVue vue = new ProduitVue();

        // Le contrôleur — relie le modèle à la vue
        ProduitControleur controleur = new ProduitControleur(depot, vue);

        // === ÉTAPE 2 : Simuler des actions utilisateur ===
        // (En production, ces appels viendraient d'un routeur HTTP
        //  ou d'une interface graphique)

        // L'utilisateur crée 3 produits
        controleur.creerProduit("Clavier", 49.99);
        controleur.creerProduit("Souris", 29.99);
        controleur.creerProduit("Ecran 27 pouces", 349.00);

        // L'utilisateur demande la liste complète
        controleur.listerProduits();

        // L'utilisateur consulte le détail du produit #2
        controleur.afficherProduit(2);

        // L'utilisateur supprime le produit #1
        controleur.supprimerProduit(1);

        // L'utilisateur vérifie la liste après suppression
        controleur.listerProduits();

        // L'utilisateur cherche un produit qui n'existe pas
        controleur.afficherProduit(99);
    }
}
