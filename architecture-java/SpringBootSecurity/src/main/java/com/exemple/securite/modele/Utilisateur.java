package com.exemple.securite.modele;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  MODÈLE : ENTITÉ UTILISATEUR                               ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Représente un compte utilisateur dans la base de données.
 * Spring Security lira ces informations pour authentifier l'utilisateur.
 */
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom d'utilisateur (doit être unique)
    private String nomUtilisateur;

    // Mot de passe haché (NE JAMAIS STOCKER EN CLAIR)
    private String motDePasse;

    // Rôle de l'utilisateur (ex: "ROLE_USER", "ROLE_ADMIN")
    private String role;

    // Constructeur par défaut requis par JPA
    protected Utilisateur() {}

    public Utilisateur(String nomUtilisateur, String motDePasse, String role) {
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    // --- Getters ---
    public Long getId() { return id; }
    public String getNomUtilisateur() { return nomUtilisateur; }
    public String getMotDePasse() { return motDePasse; }
    public String getRole() { return role; }
}
