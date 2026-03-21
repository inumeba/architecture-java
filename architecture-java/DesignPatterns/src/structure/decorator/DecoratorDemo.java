package structure.decorator;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 5 : DECORATOR — Ajouter des fonctionnalités        ║
 * ║  dynamiquement, comme des couches d'un gâteau.              ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Ajouter des comportements sans modifier la classe existante
 * → Les fonctionnalités sont combinables (A+B, A+C, A+B+C...)
 * → Alternative à l'héritage (plus flexible)
 *
 * ANALOGIE :
 * Un café de base (expresso).
 * On DÉCORE avec du lait → café au lait.
 * On DÉCORE encore avec de la chantilly → café au lait + chantilly.
 * Chaque décorateur AJOUTE quelque chose sans modifier le café original.
 *
 * COMMENT ÇA MARCHE ?
 * 1. COMPOSANT : interface commune (Boisson)
 * 2. COMPOSANT CONCRET : l'objet de base (Expresso, The)
 * 3. DÉCORATEUR ABSTRAIT : contient une référence au composant
 * 4. DÉCORATEURS CONCRETS : ajoutent des fonctionnalités
 *
 * EN PRODUCTION :
 * → Java I/O : BufferedReader(FileReader(new File("...")))
 * → Spring Security : FilterChain (chaîne de filtres décoratifs)
 * → Collections : Collections.unmodifiableList() décore une List
 */

// ─── COMPOSANT : interface commune ───

interface Boisson {
    String getDescription();
    double getPrix();
}

// ─── COMPOSANTS CONCRETS : les boissons de base ───

class Expresso implements Boisson {
    @Override
    public String getDescription() { return "Expresso"; }
    @Override
    public double getPrix() { return 2.00; }
}

class The implements Boisson {
    @Override
    public String getDescription() { return "The vert"; }
    @Override
    public double getPrix() { return 1.80; }
}

// ─── DÉCORATEUR ABSTRAIT ───

/**
 * Le décorateur IMPLÉMENTE Boisson (il EST une boisson)
 * et CONTIENT une Boisson (il DÉCORE une boisson).
 *
 * C'est la clé : le décorateur peut remplacer une boisson
 * partout où une boisson est attendue.
 */
abstract class DecorateurBoisson implements Boisson {
    protected final Boisson boisson; // la boisson décorée

    DecorateurBoisson(Boisson boisson) {
        this.boisson = boisson;
    }
}

// ─── DÉCORATEURS CONCRETS ───

class AvecLait extends DecorateurBoisson {
    AvecLait(Boisson boisson) { super(boisson); }

    @Override
    public String getDescription() {
        return boisson.getDescription() + " + Lait";
    }

    @Override
    public double getPrix() {
        return boisson.getPrix() + 0.50; // +0.50€ pour le lait
    }
}

class AvecChantilly extends DecorateurBoisson {
    AvecChantilly(Boisson boisson) { super(boisson); }

    @Override
    public String getDescription() {
        return boisson.getDescription() + " + Chantilly";
    }

    @Override
    public double getPrix() {
        return boisson.getPrix() + 0.80;
    }
}

class AvecCaramel extends DecorateurBoisson {
    AvecCaramel(Boisson boisson) { super(boisson); }

    @Override
    public String getDescription() {
        return boisson.getDescription() + " + Caramel";
    }

    @Override
    public double getPrix() {
        return boisson.getPrix() + 0.60;
    }
}

// ─── DÉMONSTRATION ───

public class DecoratorDemo {

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 5 : DECORATOR ===");
        System.out.println("  But : ajouter des fonctionnalites comme des couches\n");

        // 1. Boisson simple
        Boisson expresso = new Expresso();
        System.out.println("  %s -> %.2f EUR".formatted(expresso.getDescription(), expresso.getPrix()));

        // 2. On DÉCORE avec du lait
        Boisson avecLait = new AvecLait(expresso);
        System.out.println("  %s -> %.2f EUR".formatted(avecLait.getDescription(), avecLait.getPrix()));

        // 3. On DÉCORE encore avec de la chantilly
        Boisson complet = new AvecChantilly(avecLait);
        System.out.println("  %s -> %.2f EUR".formatted(complet.getDescription(), complet.getPrix()));

        // 4. Un thé avec caramel et chantilly
        Boisson theSpecial = new AvecChantilly(new AvecCaramel(new The()));
        System.out.println("  %s -> %.2f EUR".formatted(theSpecial.getDescription(), theSpecial.getPrix()));

        // POINT CLÉ : on combine librement les décorateurs
        // sans créer de classes comme "ExpressoAvecLaitEtChantilly"
    }
}
