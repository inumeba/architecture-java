package creation.builder;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandeTest {

    @Test
    void testBuildCommandeValide() {
        Commande commande = new Commande.Builder("Alice")
                .avecArticle("Burger Classic")
                .avecArticle("Frites")
                .avecLivraison("12 rue de la Paix, Paris")
                .avecPourboire(2.50)
                .avecCodePromo("BIENVENUE10")
                .build();

        assertEquals("Alice", commande.getClient());
        assertEquals(2, commande.getArticles().size());
        assertTrue(commande.getArticles().contains("Burger Classic"));
        assertTrue(commande.isLivraison());
        // Verify toString prints elements like address and promo
        assertTrue(commande.toString().contains("12 rue de la Paix, Paris"));
        assertTrue(commande.toString().contains("BIENVENUE10"));
        assertTrue(commande.toString().contains("2,50") || commande.toString().contains("2.50"));
    }

    @Test
    void testBuildCommandeSansArticles() {
        Exception e = assertThrows(IllegalStateException.class, () -> {
            new Commande.Builder("Bob").build();
        });
        assertEquals("La commande doit avoir au moins 1 article", e.getMessage());
    }

    @Test
    void testBuildCommandeLivraisonSansAdresse() {
        Exception e = assertThrows(IllegalStateException.class, () -> {
            new Commande.Builder("Charlie")
                    .avecArticle("Pizza")
                    .avecLivraison("")
                    .build();
        });
        assertEquals("L'adresse est obligatoire pour la livraison", e.getMessage());
    }

    @Test
    void testModificatonArticlesApresBuild() {
        Commande commande = new Commande.Builder("David")
                .avecArticle("Salade")
                .build();
                
        Exception e = assertThrows(UnsupportedOperationException.class, () -> {
            commande.getArticles().add("Soupe");
        });
    }
}
