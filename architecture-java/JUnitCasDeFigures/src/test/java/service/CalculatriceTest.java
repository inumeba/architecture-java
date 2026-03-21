package service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TESTS AVANCÉS: PARAMÉTRÉS, TIMEOUTS, CONDITIONNELS          ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@DisplayName("Tests Avancés de la Calculatrice")
class CalculatriceTest {

    private final Calculatrice calculatrice = new Calculatrice();

    // ─── 1. TESTS PARAMÉTRÉS (Gains de lignes énormes) ───

    @ParameterizedTest(name = "Le nombre {0} doit être pair.")
    @ValueSource(ints = {2, 4, 6, 8, 100, 1024}) // Le test va tourner 6 fois en tout
    @DisplayName("Tester plusieurs nombres pairs")
    void testEstPair_Vrai(int nombre) {
        assertTrue(calculatrice.estPair(nombre));
    }

    @ParameterizedTest(name = "Add: {0} + {1} = {2}")
    @CsvSource({
            "1, 2, 3",
            "5, 5, 10",
            "100, -50, 50",
            "-5, -5, -10"
    }) // Chaque ligne est passée en arguments (a, b, resultatAttendu)
    @DisplayName("Test paramétré d'addition depuis un tableau CSV factice")
    void testAdditionMultiples(int a, int b, int resultatAttendu) {
        assertEquals(resultatAttendu, calculatrice.additionner(a, b));
    }

    // ─── 2. TESTS DE TEMPS (Timeouts) ───

    @Test
    @DisplayName("S'assurer que le traitement répond en moins de 2 secondes")
    void testTraitementLourdTimeout() {
        // assertTimeoutPreemptively s'arrête brutalement si le temps est dépassé,
        // assertTimeout attend la fin de l'exécution puis crashe en cas de débordement.
        assertTimeout(Duration.ofSeconds(2), () -> {
            calculatrice.traitementLourd();
        }, "Le traitement lourd a dépassé le seuil attendu de 2 secondes.");
    }


    // ─── 3. TESTS CONDITIONNELS ET DÉSACTIVÉS ───

    @Test
    @Disabled("Ce test est désactivé parce que le ticket JIRA #458 n'est pas encore résolu.")
    void testEnAttenteDeBugFix() {
        fail("Ce test échoue car il n'est pas prêt, mais @Disabled l'empêche de tourner.");
    }

    @Test
    @EnabledOnOs(OS.WINDOWS) // Ne sera vert ('Success') que sous Windows. Ignoré sur le reste.
    @DisplayName("Test exécuté uniquement sous Windows")
    void testConditionnelOS() {
        assertTrue(true, "Si vous voyez ceci, c'est que ce test a tourné sur Windows.");
    }
}
