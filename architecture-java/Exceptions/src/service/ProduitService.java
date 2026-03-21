package service;

import exception.metier.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SERVICE — Gestion des produits avec exceptions métier
 *
 * Ce service illustre comment LANCER (throw) des exceptions
 * dans un contexte métier réaliste.
 *
 * Chaque méthode déclare ses exceptions CHECKED dans le "throws" :
 * → Le compilateur OBLIGE l'appelant à les gérer
 * → L'appelant sait exactement ce qui peut mal tourner
 * → C'est un CONTRAT : "cette méthode peut échouer de ces façons"
 */
public class ProduitService {

    // Simulation de base de données
    private final List<Produit> produits = new ArrayList<>();
    private int prochainId = 1;

    // Record immuable pour les produits (Java 17+)
    public record Produit(int id, String nom, double prix, int stock) {
        @Override
        public String toString() {
            return "Produit{id=%d, nom='%s', prix=%.2f EUR, stock=%d}".formatted(id, nom, prix, stock);
        }
    }

    /**
     * Crée un produit avec VALIDATION des entrées.
     *
     * THROWS :
     * - ValidationException → si les données sont invalides
     *
     * Notez la déclaration "throws" : le compilateur oblige
     * l'appelant à gérer ou propager cette exception.
     */
    public Produit creerProduit(String nom, double prix, int stock) throws ValidationException {

        // --- VALIDATION DES ENTRÉES ---
        // Chaque validation lance une exception spécifique

        if (nom == null || nom.isBlank()) {
            // MAUVAIS : throw new Exception("nom invalide")  → trop vague
            // BON    : throw new ValidationException(...)      → précis et typé
            throw new ValidationException("nom", nom, "le nom ne peut pas etre vide");
        }

        if (nom.length() > 50) {
            throw new ValidationException("nom", nom, "le nom ne peut pas depasser 50 caracteres");
        }

        if (prix < 0) {
            throw new ValidationException("prix", prix, "le prix ne peut pas etre negatif");
        }

        if (stock < 0) {
            throw new ValidationException("stock", stock, "le stock ne peut pas etre negatif");
        }

        // Tout est valide → créer le produit
        Produit produit = new Produit(prochainId++, nom, prix, stock);
        produits.add(produit);
        return produit;
    }

    /**
     * Recherche un produit par ID.
     *
     * THROWS :
     * - RessourceNonTrouveeException → si le produit n'existe pas
     *
     * ALTERNATIVE : on pourrait retourner Optional<Produit> au lieu de lancer.
     * Le choix dépend du contexte :
     * - Optional → si "pas trouvé" est un résultat NORMAL
     * - Exception → si "pas trouvé" est une ERREUR (l'ID devrait exister)
     */
    public Produit trouverParId(int id) throws RessourceNonTrouveeException {
        return produits.stream()
                .filter(p -> p.id() == id)
                .findFirst()
                .orElseThrow(() -> new RessourceNonTrouveeException("Produit", id));
    }

    /**
     * Effectue une vente — peut lancer PLUSIEURS types d'exceptions.
     *
     * THROWS :
     * - RessourceNonTrouveeException → si le produit n'existe pas
     * - RegleMetierException → si le stock est insuffisant
     *
     * L'appelant doit gérer les DEUX cas (ou propager).
     */
    public Produit vendre(int idProduit, int quantite)
            throws RessourceNonTrouveeException, RegleMetierException {

        Produit produit = trouverParId(idProduit);

        // Règle métier : stock suffisant
        if (quantite > produit.stock()) {
            throw new RegleMetierException(
                    "STOCK_INSUFFISANT",
                    "Stock insuffisant pour '%s' : demande %d, disponible %d"
                            .formatted(produit.nom(), quantite, produit.stock())
            );
        }

        if (quantite <= 0) {
            throw new RegleMetierException(
                    "QUANTITE_INVALIDE",
                    "La quantite doit etre positive (recue : %d)".formatted(quantite)
            );
        }

        // Mettre à jour le stock (on recrée le record car il est immuable)
        Produit maj = new Produit(produit.id(), produit.nom(), produit.prix(), produit.stock() - quantite);
        produits.removeIf(p -> p.id() == idProduit);
        produits.add(maj);
        return maj;
    }

    /**
     * Méthode qui simule une erreur technique (base de données HS).
     *
     * THROWS :
     * - ErreurTechniqueException → encapsule l'erreur technique
     *
     * CHAINAGE D'EXCEPTION illustré ici :
     * L'exception originale (RuntimeException) est ENCAPSULÉE
     * dans notre exception métier, sans perdre l'information.
     */
    public void sauvegarderEnBase(Produit produit) throws ErreurTechniqueException {
        try {
            // Simule une erreur de connexion à la base
            simulerErreurBDD();
        } catch (RuntimeException e) {
            // CHAINAGE : on encapsule l'exception technique
            // Le message est métier, la CAUSE est technique
            throw new ErreurTechniqueException(
                    "Impossible de sauvegarder le produit '%s' en base de donnees"
                            .formatted(produit.nom()),
                    e  // ← La cause originale est conservée !
            );
        }
    }

    private void simulerErreurBDD() {
        throw new RuntimeException("Connection refused: localhost:5432 (base PostgreSQL inaccessible)");
    }

    public List<Produit> listerTous() {
        return List.copyOf(produits);
    }
}
