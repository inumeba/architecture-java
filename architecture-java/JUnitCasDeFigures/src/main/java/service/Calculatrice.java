package service;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  SERVICE: CALCULATRICE (Pour les tests paramétrés et le temps)║
 * ╚══════════════════════════════════════════════════════════════╝
 * Ce service offre des méthodes simples, principalement pour illustrer
 * la puissance des tests paramétrés (jouer 10 fois le même test avec
 * des valeurs différentes) et les tests de Timeout.
 */
public class Calculatrice {

    public int additionner(int a, int b) {
        return a + b;
    }

    public boolean estPair(int nombre) {
        return nombre % 2 == 0;
    }

    public double diviser(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division par zéro impossible.");
        }
        return (double) a / b;
    }

    /**
     * Simule une opération longue et asynchrone, comme un appel réseau.
     */
    public void traitementLourd() {
        try {
            Thread.sleep(1500); // Fait dormir le thread 1.5 seconde
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
