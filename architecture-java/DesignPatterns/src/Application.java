import creation.singleton.BaseDeDonnees;
import creation.factory.NotificationFactory;
import creation.builder.Commande;
import structure.adapter.AdapterDemo;
import structure.decorator.DecoratorDemo;
import structure.facade.FacadeDemo;
import comportement.strategy.StrategyDemo;
import comportement.observer.ObserverDemo;
import comportement.template.TemplateDemo;
import comportement.command.CommandDemo;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║         LES 10 DESIGN PATTERNS LES PLUS UTILISÉS               ║
 * ║                     (Gang of Four)                              ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                  ║
 * ║  CRÉATION (comment créer les objets)                            ║
 * ║  ├── 1. Singleton     → UNE seule instance (BDD, Cache)         ║
 * ║  ├── 2. Factory       → Déléguer la création (Notification)     ║
 * ║  └── 3. Builder       → Construction fluide (Commande)          ║
 * ║                                                                  ║
 * ║  STRUCTURE (comment organiser les classes)                       ║
 * ║  ├── 4. Adapter       → Rendre compatible (Stripe → Paiement)   ║
 * ║  ├── 5. Decorator     → Ajouter des couches (Boisson + extras)  ║
 * ║  └── 6. Facade        → Simplifier (Boutique en ligne)          ║
 * ║                                                                  ║
 * ║  COMPORTEMENT (comment les objets communiquent)                  ║
 * ║  ├── 7. Strategy      → Changer l'algo (Tri)                    ║
 * ║  ├── 8. Observer      → Notifier les abonnés (YouTube)          ║
 * ║  ├── 9. Template      → Squelette d'algorithme (Rapport)        ║
 * ║  └── 10. Command      → Encapsuler une action (Undo/Redo)       ║
 * ║                                                                  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     LES 10 DESIGN PATTERNS LES PLUS UTILISES           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        // ═══════════════════════════════════════════
        //  PATTERNS DE CRÉATION
        // ═══════════════════════════════════════════

        BaseDeDonnees.demonstrer();               // 1. Singleton
        NotificationFactory.demonstrer();          // 2. Factory Method
        Commande.demonstrer();                     // 3. Builder

        // ═══════════════════════════════════════════
        //  PATTERNS DE STRUCTURE
        // ═══════════════════════════════════════════

        AdapterDemo.demonstrer();                  // 4. Adapter
        DecoratorDemo.demonstrer();                // 5. Decorator
        FacadeDemo.demonstrer();                   // 6. Facade

        // ═══════════════════════════════════════════
        //  PATTERNS DE COMPORTEMENT
        // ═══════════════════════════════════════════

        StrategyDemo.demonstrer();                 // 7. Strategy
        ObserverDemo.demonstrer();                 // 8. Observer
        TemplateDemo.demonstrer();                 // 9. Template Method
        CommandDemo.demonstrer();                  // 10. Command

        // ═══════════════════════════════════════════

        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║     FIN — 10 patterns demontres avec succes !           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }
}
