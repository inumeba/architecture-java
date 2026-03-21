package modele;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MODÈLE : UTILISATEUR (Record Java)                          ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Les Records sont très pratiques pour être manipulés par des 
 * expressions lambdas car ils sont immuables (Thread-safe).
 */
public record Utilisateur(
    long id,
    String nom,
    int age,
    boolean estActif,
    double solde
) {
    // Méthode standard qui pourra être utilisée comme "Reference de méthode"
    public void afficherConsole() {
        System.out.printf("[%d] %s (Age: %d) - Actif: %b - Solde: %.2f€%n", 
                          id, nom, age, estActif, solde);
    }
}
