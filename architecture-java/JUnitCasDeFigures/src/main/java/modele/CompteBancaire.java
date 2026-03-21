package modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MODÈLE: COMPTE BANCAIRE (Pour les tests d'états et erreurs) ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Ce modèle métier est utilisé pour tester les états d'un objet, 
 * le comportement des exceptions et les tests imbriqués de JUnit.
 */
public class CompteBancaire {
    private String titulaire;
    private double solde;
    private List<String> historique;

    public CompteBancaire(String titulaire, double soldeInitial) {
        this.titulaire = titulaire;
        this.solde = soldeInitial;
        this.historique = new ArrayList<>();
        this.historique.add("Création du compte avec solde: " + soldeInitial);
    }

    public void deposer(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant du dépôt doit être positif.");
        }
        this.solde += montant;
        this.historique.add("Dépôt: " + montant);
    }

    public void retirer(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant à retirer doit être positif.");
        }
        if (montant > this.solde) {
            throw new IllegalStateException("Solde insuffisant pour ce retrait.");
        }
        this.solde -= montant;
        this.historique.add("Retrait: " + montant);
    }

    public double getSolde() {
        return solde;
    }

    public String getTitulaire() {
        return titulaire;
    }

    public List<String> getHistorique() {
        return Collections.unmodifiableList(historique);
    }
}
