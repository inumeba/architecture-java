package exemples;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  1. CRÉATION DE STREAMS                                    ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Un Stream ne stocke pas de données, il "transporte" des données
 * depuis une source (Collection, Tableau, Générateur) vers un 
 * pipeline de traitement ("pipeline").
 */
public class CreationStreams {

    public static void demonstrer() {
        System.out.println("\n=== 1. CRÉATION DE STREAMS ===");

        // Cas 1 : Depuis une Collection (Le plus courant)
        List<String> liste = List.of("Java", "Spring", "Docker");
        Stream<String> streamDepuisListe = liste.stream();
        System.out.println("  - Depuis une List : " + streamDepuisListe.count() + " elements");

        // Cas 2 : Depuis des éléments isolés (Stream.of)
        Stream<String> streamOf = Stream.of("Un", "Deux", "Trois");
        System.out.println("  - Depuis Stream.of() : " + streamOf.toList());

        // Cas 3 : Depuis un tableau
        int[] tableau = {1, 2, 3, 4, 5};
        IntStream streamTableau = Arrays.stream(tableau);
        System.out.println("  - Depuis un tableau (Arrays.stream) : max = " + streamTableau.max().getAsInt());

        // Cas 4 : Stream généré (infini, il faut le limiter !)
        Stream<Double> streamGenere = Stream.generate(Math::random).limit(3);
        System.out.println("  - Stream.generate() (limité à 3) : " + streamGenere.toList());

        // Cas 5 : Stream itératif (boucle for fonctionnelle)
        Stream<Integer> streamIteratif = Stream.iterate(0, n -> n + 2).limit(4);
        System.out.println("  - Stream.iterate() (pairs jusqu'a 4 elements) : " + streamIteratif.toList());
    }
}
