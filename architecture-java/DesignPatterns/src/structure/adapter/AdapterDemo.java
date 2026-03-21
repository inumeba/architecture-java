package structure.adapter;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 4 : ADAPTER — Rendre compatible deux interfaces    ║
 * ║  incompatibles. Comme un adaptateur de prise électrique.     ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Intégrer une librairie externe dont l'API ne correspond pas
 * → Connecter un ancien système à un nouveau
 * → Utiliser une classe existante sans modifier son code
 *
 * ANALOGIE :
 * Votre chargeur français a une prise ronde.
 * La prise anglaise a 3 broches carrées.
 * L'ADAPTATEUR convertit l'un vers l'autre sans modifier ni la prise ni le chargeur.
 *
 * COMMENT ÇA MARCHE ?
 * 1. CIBLE (Target) : l'interface attendue par le client
 * 2. ADAPTEE : la classe existante qu'on veut utiliser
 * 3. ADAPTER : convertit les appels de la Cible vers l'Adaptee
 *
 * EN PRODUCTION :
 * → Arrays.asList() adapte un tableau en List
 * → InputStreamReader adapte InputStream en Reader
 * → Spring : HandlerAdapter adapte les controllers
 */

// ─── CIBLE : l'interface que le système moderne attend ───

/**
 * Notre application attend un service de paiement avec CETTE interface.
 * Tous les composants utilisent paierEnEuros().
 */
interface ServicePaiement {
    void paierEnEuros(double montant);
    String getNomService();
}

// ─── ADAPTEE : le système existant avec une API différente ───

/**
 * L'ancien système Stripe utilise des noms de méthodes différents.
 * On ne peut PAS modifier cette classe (c'est une lib externe).
 */
class AncienSystemeStripe {
    void processPaymentUSD(double amountInCents) {
        System.out.println("    [Stripe Legacy] Paiement de %.0f cents USD".formatted(amountInCents));
    }
}

// ─── L'ADAPTATEUR : fait le pont entre les deux ───

/**
 * L'adaptateur IMPLÉMENTE l'interface cible (ServicePaiement)
 * et CONTIENT l'ancien système (AncienSystemeStripe).
 *
 * Il traduit les appels :
 * - Euros → Dollars → Cents
 * - paierEnEuros() → processPaymentUSD()
 */
class AdaptateurStripe implements ServicePaiement {

    private final AncienSystemeStripe ancienStripe;
    private static final double TAUX_EUR_USD = 1.08;

    AdaptateurStripe(AncienSystemeStripe ancienStripe) {
        this.ancienStripe = ancienStripe;
    }

    @Override
    public void paierEnEuros(double montant) {
        // Conversion : Euros → Dollars → Cents
        double enDollars = montant * TAUX_EUR_USD;
        double enCents = enDollars * 100;
        // Appel du système legacy avec le bon format
        ancienStripe.processPaymentUSD(enCents);
    }

    @Override
    public String getNomService() {
        return "Stripe (via adaptateur)";
    }
}

// ─── Un service moderne qui implémente directement l'interface ───

class ServicePayPal implements ServicePaiement {
    @Override
    public void paierEnEuros(double montant) {
        System.out.println("    [PayPal] Paiement de %.2f EUR".formatted(montant));
    }

    @Override
    public String getNomService() { return "PayPal (natif)"; }
}

// ─── DÉMONSTRATION ───

public class AdapterDemo {

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 4 : ADAPTER ===");
        System.out.println("  But : rendre compatible deux interfaces differentes\n");

        // PayPal implémente directement notre interface → OK
        ServicePaiement paypal = new ServicePayPal();

        // Stripe a une API incompatible → on utilise l'ADAPTATEUR
        AncienSystemeStripe stripeExistant = new AncienSystemeStripe();
        ServicePaiement stripe = new AdaptateurStripe(stripeExistant);

        // Le code appelant traite les deux DE LA MÊME FAÇON
        // Il ne sait pas que Stripe passe par un adaptateur !
        ServicePaiement[] services = {paypal, stripe};
        for (ServicePaiement service : services) {
            System.out.println("  Via " + service.getNomService() + " :");
            service.paierEnEuros(49.99);
        }
    }
}
