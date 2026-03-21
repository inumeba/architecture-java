package creation.singleton;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN 1 : SINGLETON — Une seule instance dans tout le    ║
 * ║  programme. Comme un président : il n'y en a qu'UN.         ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * QUAND L'UTILISER ?
 * → Connexion à la base de données (une seule connexion partagée)
 * → Logger (un seul fichier de log)
 * → Cache (un seul cache en mémoire)
 * → Configuration (lue une seule fois)
 *
 * COMMENT ÇA MARCHE ?
 * 1. Constructeur PRIVÉ → personne ne peut faire "new"
 * 2. Instance STATIQUE → stockée dans la classe elle-même
 * 3. Méthode getInstance() → point d'accès unique
 *
 * VARIANTES :
 * - Eager (ci-dessous) : créé au chargement de la classe
 * - Lazy : créé au premier appel de getInstance()
 * - Thread-safe : avec synchronized ou double-checked locking
 * - Enum : la façon la plus sûre en Java (recommandée par Joshua Bloch)
 *
 * EN PRODUCTION :
 * → Spring crée des singletons par défaut (@Scope("singleton"))
 * → Pas besoin de coder ce pattern manuellement avec Spring
 */
public class BaseDeDonnees {

    // ─── L'UNIQUE INSTANCE (créée au chargement de la classe) ───

    /**
     * "static final" = créée UNE SEULE FOIS quand la JVM charge la classe.
     * C'est la version "eager" (empressée) : pas de problème de thread.
     */
    private static final BaseDeDonnees INSTANCE = new BaseDeDonnees();

    // Simule l'état de la connexion
    private boolean connectee = false;
    private int nombreRequetes = 0;

    // ─── CONSTRUCTEUR PRIVÉ ───

    /**
     * PRIVÉ → interdit "new BaseDeDonnees()" depuis l'extérieur.
     * C'est LA CLÉ du Singleton : contrôler la création.
     */
    private BaseDeDonnees() {
        System.out.println("    [Singleton] Connexion a la base de donnees...");
        this.connectee = true;
    }

    // ─── POINT D'ACCÈS UNIQUE ───

    /**
     * La SEULE façon d'obtenir l'instance.
     * Peu importe combien de fois on appelle getInstance(),
     * on obtient TOUJOURS le même objet.
     */
    public static BaseDeDonnees getInstance() {
        return INSTANCE;
    }

    // ─── MÉTHODES MÉTIER ───

    public String executerRequete(String sql) {
        nombreRequetes++;
        return "Resultat de [%s] (requete #%d)".formatted(sql, nombreRequetes);
    }

    public boolean estConnectee() {
        return connectee;
    }

    public int getNombreRequetes() {
        return nombreRequetes;
    }

    // ─── DÉMONSTRATION ───

    public static void demonstrer() {
        System.out.println("\n=== PATTERN 1 : SINGLETON ===");
        System.out.println("  But : garantir UNE SEULE instance\n");

        // Obtenir l'instance (la connexion est créée ici)
        BaseDeDonnees db1 = BaseDeDonnees.getInstance();
        BaseDeDonnees db2 = BaseDeDonnees.getInstance();

        // Preuve : db1 et db2 sont le MÊME objet
        System.out.println("  db1 == db2 ? " + (db1 == db2)); // true !

        // Utiliser la connexion partagée
        System.out.println("  " + db1.executerRequete("SELECT * FROM produits"));
        System.out.println("  " + db2.executerRequete("INSERT INTO produits..."));

        // Les deux références partagent le même compteur
        System.out.println("  Requetes totales : " + db1.getNombreRequetes()); // 2
    }
}
