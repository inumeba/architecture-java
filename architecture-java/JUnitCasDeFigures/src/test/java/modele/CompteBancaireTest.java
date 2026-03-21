package modele;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TESTS: CYCLE DE VIE, EXCEPTIONS, ET TESTS IMBRIQUÉS (@Nested)║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@DisplayName("Tests sur le Compte Bancaire") // Renomme la classe dans le rapport de test
class CompteBancaireTest {

    private CompteBancaire compte;

    // ─── 1. CYCLE DE VIE DES TESTS ───

    @BeforeAll
    static void setUpAll() {
        System.out.println("⏳ @BeforeAll : Exécuté UNE SEULE FOIS pour toute la classe (ex: Démarrage BDD).");
    }

    @BeforeEach
    void setUp() {
        System.out.println("  ➜ @BeforeEach : Exécuté AVANT CHAQUE méthode de test.");
        // Réinitialisation de l'état pour que les tests soient indépendants (Isolation)
        compte = new CompteBancaire("Alice", 100.0);
    }

    @AfterEach
    void tearDown() {
        System.out.println("  ➜ @AfterEach : Exécuté APRÈS CHAQUE méthode de test.");
        compte = null;
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("🛑 @AfterAll : Exécuté UNE SEULE FOIS à la fin (ex: Fermeture de la connexion).");
    }


    // ─── 2. ASSERTIONS DE BASE ET EXCEPTIONS ───

    @Test
    @DisplayName("Le dépôt augmente le solde et l'historique")
    void testDepotValide() {
        // Arrange (Préparer) - Fait dans @BeforeEach

        // Act (Agir)
        compte.deposer(50.0);

        // Assert (Vérifier)
        assertEquals(150.0, compte.getSolde(), "Le solde devrait être 150.0 après 50.0 de dépôt.");
        assertTrue(compte.getHistorique().contains("Dépôt: 50.0"), "L'historique doit enregistrer le dépôt.");
    }

    @Test
    @DisplayName("Un dépôt négatif doit jeter une IllegalArgumentException")
    void testDepotInvalideLanceException() {
        // On vérifie que le code lance bien l'exception attendue
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            compte.deposer(-10.0);
        });
        
        // On peut même vérifier le message de l'exception
        assertEquals("Le montant du dépôt doit être positif.", exception.getMessage());
    }


    // ─── 3. TESTS IMBRIQUÉS (Regroupement Logique) ───

    @Nested
    @DisplayName("Lorsqu'un retrait est effectué")
    class TestsRetrait {

        @Test
        @DisplayName("Le retrait diminue le solde s'il y a assez d'argent")
        void testRetraitValide() {
            compte.retirer(40.0);
            assertEquals(60.0, compte.getSolde());
        }

        @Test
        @DisplayName("Le retrait lève une IllegalStateException si les fonds manquent")
        void testRetraitFondsInsuffisants() {
            assertThrows(IllegalStateException.class, () -> compte.retirer(200.0));
        }
    }
    
    // ─── 4. ASSERT ALL (Exécution de plusieurs vérifications sans s'arrêter à la première) ───
    @Test
    @DisplayName("Vérifie plusieurs conditions en même temps sans fail-fast")
    void testProprietesInitiales() {
        assertAll("Vérification globale du compte",
             () -> assertEquals("Alice", compte.getTitulaire()),
             () -> assertEquals(100.0, compte.getSolde()),
             () -> assertFalse(compte.getHistorique().isEmpty())
        );
    }
}
