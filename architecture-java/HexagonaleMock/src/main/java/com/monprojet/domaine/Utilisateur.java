package com.monprojet.domaine;

/**
 * Représente un utilisateur dans notre domaine métier.
 * Cette classe ne dépend d'aucune technologie externe (pas d'annotations JPA, etc.).
 */
public class Utilisateur {
    private final String id;
    private final String nom;

    public Utilisateur(String id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
}