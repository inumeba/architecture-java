package service;

import java.io.*;

/**
 * SERVICE — Lecture/écriture de fichiers
 *
 * Illustre les cas d'exceptions liés aux RESSOURCES (I/O) :
 * - try-with-resources (Java 7+)
 * - AutoCloseable
 * - IOException (checked)
 * - Gestion des ressources imbriquées
 */
public class FichierService {

    /**
     * CAS 1 : TRY-WITH-RESOURCES — La façon MODERNE de gérer les ressources
     *
     * Avant Java 7, il fallait fermer les ressources manuellement
     * dans un bloc finally. C'était verbeux et source de bugs.
     *
     * try-with-resources FERME AUTOMATIQUEMENT les ressources
     * qui implémentent AutoCloseable, même si une exception est lancée.
     *
     * BONNE PRATIQUE : TOUJOURS utiliser try-with-resources
     * pour les fichiers, connexions, streams, etc.
     */
    public String lireFichier(String chemin) throws IOException {
        // La ressource (BufferedReader) est déclarée dans le try(...)
        // Elle sera fermée automatiquement à la sortie du bloc
        try (BufferedReader lecteur = new BufferedReader(new FileReader(chemin))) {
            StringBuilder contenu = new StringBuilder();
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                contenu.append(ligne).append("\n");
            }
            return contenu.toString();
        }
        // Pas de finally nécessaire !
        // Le lecteur est fermé automatiquement, que ça réussisse ou non
    }

    /**
     * CAS 2 : RESSOURCES MULTIPLES dans un seul try-with-resources
     *
     * On peut déclarer PLUSIEURS ressources séparées par des points-virgules.
     * Elles sont fermées dans l'ordre INVERSE de leur déclaration.
     */
    public void copierFichier(String source, String destination) throws IOException {
        // Deux ressources : un lecteur ET un écriveur
        // Fermés automatiquement dans l'ordre : ecriveur → lecteur
        try (
                BufferedReader lecteur = new BufferedReader(new FileReader(source));
                BufferedWriter ecriveur = new BufferedWriter(new FileWriter(destination))
        ) {
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                ecriveur.write(ligne);
                ecriveur.newLine();
            }
        }
    }

    /**
     * CAS 3 : EXCEPTION SUPPRIMÉE (Suppressed Exception)
     *
     * Quand une exception se produit dans le bloc try ET une autre
     * dans la fermeture de la ressource (close()), Java conserve
     * LES DEUX grâce aux "suppressed exceptions".
     *
     * L'exception du try est la PRINCIPALE.
     * L'exception du close() est SUPPRIMÉE (attachée à la principale).
     *
     * On peut les récupérer avec getSuppressed().
     */
    public void demonstrerExceptionSupprimee() throws Exception {
        try (RessourceCapricieuse ressource = new RessourceCapricieuse()) {
            ressource.faireQuelqueChose();
            // ← Cette ligne lance "Erreur dans faireQuelqueChose"
        }
        // close() lance aussi "Erreur dans close()"
        // → L'exception principale est celle du try
        // → L'exception du close est "suppressed"
    }

    /**
     * CAS 4 : RESSOURCE PERSONNALISÉE implémentant AutoCloseable
     *
     * Toute classe qui possède des ressources à libérer
     * devrait implémenter AutoCloseable pour être compatible
     * avec try-with-resources.
     */
    public static class RessourceCapricieuse implements AutoCloseable {

        public RessourceCapricieuse() {
            System.out.println("    [Ressource] Ouverte");
        }

        public void faireQuelqueChose() {
            System.out.println("    [Ressource] Travail en cours...");
            throw new RuntimeException("Erreur dans faireQuelqueChose()");
        }

        /**
         * close() est appelé AUTOMATIQUEMENT par try-with-resources.
         * Même si le bloc try lance une exception, close() est exécuté.
         */
        @Override
        public void close() {
            System.out.println("    [Ressource] Fermeture...");
            throw new RuntimeException("Erreur dans close()");
        }
    }

    /**
     * CAS 5 : L'ANCIEN try-finally (AVANT Java 7) — à NE PLUS UTILISER
     *
     * Montré ici uniquement pour comprendre POURQUOI try-with-resources existe.
     * Ce code est verbeux, fragile et source de bugs.
     */
    public String lireFichierAncienneMethode(String chemin) throws IOException {
        BufferedReader lecteur = null;
        try {
            lecteur = new BufferedReader(new FileReader(chemin));
            return lecteur.readLine();
        } finally {
            // PROBLÈME : si readLine() lance une exception ET close() aussi,
            // l'exception de close() ÉCRASE celle de readLine() !
            // Avec try-with-resources, les deux sont conservées.
            if (lecteur != null) {
                lecteur.close(); // ← peut lancer une IOException aussi !
            }
        }
    }
}
