package exemples;

import java.util.List;
import java.util.stream.IntStream;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  5. CAS SPÉCIFIQUES : PARALLÈLES ET PRIMITIFS              ║
 * ╚══════════════════════════════════════════════════════════════╝
 * - Streams Primitives : Évitent "l'autoboxing" (Integer, Double) 
 *   pour plus de performance. (IntStream, DoubleStream...).
 * - Streams Parallèles : Utilisent le multi-threading automatiquement.
 */
public class CasAvances {

    public static void demonstrer() {
        System.out.println("\n=== 5. STREAMS PRIMITIFS ET PARALLÈLES ===");

        // ─── INTSTREAM (Range) ───
        System.out.println("\n  >> INTSTREAM (Boucle for en mode fonctionnel) :");
        // IntStream.rangeClosed(1, 5) équivaut à  for(int i = 1; i <= 5; i++)
        int somme = IntStream.rangeClosed(1, 5).sum();
        System.out.println("     Somme de 1 à 5 : " + somme);

        // ─── PARALLEL STREAM ───
        // Découpe la charge de travail entre les CPU disponibles.
        // ATTENTION : Utile uniquement pour d'ENORMES volumes de données
        // ou des traitements lents (I/O, gros calcul), sinon la "création"
        // des threads prend plus de temps que le stream lui-même !
        System.out.println("\n  >> PARALLEL STREAM :");
        
        List<String> mots = List.of("A", "B", "C", "D", "E", "F", "G", "H");
        
        System.out.print("     Exécution Parallèle (ordre NON garanti) : ");
        mots.parallelStream().forEach(mot -> {
            // Ces impressions vont s'afficher dans tous les sens car 
            // plusieurs cores les impriment en même temps.
            System.out.print(mot + " ");
        });
        System.out.println();
    }
}
