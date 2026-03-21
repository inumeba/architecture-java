package creation.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 3 : BUILDER — Construire un objet complexe         ║
 * ║  étape par étape, de façon lisible.                          ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Objet avec beaucoup de paramètres (> 4-5)
 * → Certains paramètres optionnels
 * → On veut un objet IMMUABLE à la fin
 * → Le constructeur à 10 paramètres est illisible
 *
 * ANALOGIE :
 * Commander un burger personnalisé :
 * "Je veux un burger AVEC fromage, AVEC bacon, SANS oignon, AVEC sauce BBQ"
 * → Chaque étape est optionnelle et claire
 *
 * COMMENT ÇA MARCHE ?
 * 1. La classe COMMANDE est immuable (champs final, pas de setters)
 * 2. Le BUILDER est une classe interne qui accumule les paramètres
 * 3. La méthode build() crée l'objet final
 * 4. Chaînage fluide : commande.avecX().avecY().build()
 *
 * EN PRODUCTION :
 * → Lombok @Builder génère le builder automatiquement
 * → StringBuilder est un builder pour les String
 * → Stream.builder() pour construire des streams
 * → HttpRequest.newBuilder() dans java.net.http
 */
public class Commande {

    // ─── CHAMPS IMMUABLES (tous final) ───

    private final String client;
    private final List<String> articles;
    private final boolean livraison;
    private final String adresse;
    private final double pourboire;
    private final String codePromo;

    // ─── CONSTRUCTEUR PRIVÉ (seul le Builder peut créer) ───

    /**
     * PRIVÉ → on ne peut créer une Commande que via le Builder.
     * Cela GARANTIT que l'objet est toujours valide et complet.
     */
    private Commande(Builder builder) {
        this.client = builder.client;
        this.articles = Collections.unmodifiableList(builder.articles);
        this.livraison = builder.livraison;
        this.adresse = builder.adresse;
        this.pourboire = builder.pourboire;
        this.codePromo = builder.codePromo;
    }

    // ─── GETTERS (pas de setters → immuable) ───

    public String getClient() { return client; }
    public List<String> getArticles() { return articles; }
    public boolean isLivraison() { return livraison; }

    @Override
    public String toString() {
        return """
                   Commande {
                     client     = %s
                     articles   = %s
                     livraison  = %s
                     adresse    = %s
                     pourboire  = %.2f EUR
                     code promo = %s
                   }""".formatted(client, articles, livraison,
                adresse != null ? adresse : "N/A",
                pourboire,
                codePromo != null ? codePromo : "aucun");
    }

    // ─── LE BUILDER (classe interne statique) ───

    /**
     * Le Builder accumule les paramètres un par un.
     * Chaque méthode retourne "this" pour permettre le CHAÎNAGE.
     *
     * Le client est OBLIGATOIRE (passé au constructeur du Builder).
     * Tout le reste est OPTIONNEL (méthodes chaînées).
     */
    public static class Builder {
        // Obligatoire
        private final String client;

        // Optionnels avec valeurs par défaut
        private List<String> articles = new ArrayList<>();
        private boolean livraison = false;
        private String adresse = null;
        private double pourboire = 0.0;
        private String codePromo = null;

        /**
         * Le constructeur du Builder prend les paramètres OBLIGATOIRES.
         */
        public Builder(String client) {
            this.client = client;
        }

        /** Ajouter un article (peut être appelé plusieurs fois) */
        public Builder avecArticle(String article) {
            this.articles.add(article);
            return this; // ← retourne "this" pour le chaînage
        }

        /** Activer la livraison avec une adresse */
        public Builder avecLivraison(String adresse) {
            this.livraison = true;
            this.adresse = adresse;
            return this;
        }

        /** Ajouter un pourboire */
        public Builder avecPourboire(double montant) {
            this.pourboire = montant;
            return this;
        }

        /** Appliquer un code promo */
        public Builder avecCodePromo(String code) {
            this.codePromo = code;
            return this;
        }

        /**
         * CONSTRUIRE l'objet final (immuable).
         * C'est ici qu'on peut ajouter de la VALIDATION.
         */
        public Commande build() {
            if (articles.isEmpty()) {
                throw new IllegalStateException("La commande doit avoir au moins 1 article");
            }
            if (livraison && (adresse == null || adresse.isBlank())) {
                throw new IllegalStateException("L'adresse est obligatoire pour la livraison");
            }
            return new Commande(this);
        }
    }

    // ─── DÉMONSTRATION ───

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 3 : BUILDER ===");
        System.out.println("  But : construire un objet complexe etape par etape\n");

        // Construction FLUIDE et LISIBLE
        Commande commande = new Commande.Builder("Alice")
                .avecArticle("Burger Classic")
                .avecArticle("Frites")
                .avecArticle("Coca-Cola")
                .avecLivraison("12 rue de la Paix, Paris")
                .avecPourboire(2.50)
                .avecCodePromo("BIENVENUE10")
                .build();

        System.out.println(commande);

        // Comparer avec un constructeur classique (ILLISIBLE) :
        // new Commande("Alice", articles, true, "12 rue...", 2.50, "BIENVENUE10")
        // → Quel paramètre est quoi ? Impossible à lire !
    }
}
