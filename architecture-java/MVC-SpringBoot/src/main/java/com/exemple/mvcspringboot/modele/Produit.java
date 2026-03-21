package com.exemple.mvcspringboot.modele;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MODÈLE (M de MVC) — Entité JPA mappée sur la table "produits"
 *
 * Cette classe représente un PRODUIT dans la base de données.
 * Hibernate (implémentation de JPA) se charge de :
 * - Créer la table automatiquement à partir des annotations
 * - Convertir les objets Java ↔ lignes SQL (ORM)
 * - Gérer le cache de premier niveau (session)
 *
 * ANNOTATIONS JPA (Jakarta Persistence API) :
 * - @Entity    → Cette classe est une entité persistante (= une table)
 * - @Table     → Nom de la table en base (par défaut = nom de la classe)
 * - @Id        → Clé primaire
 * - @Column    → Configuration d'une colonne (nom, contraintes...)
 *
 * POURQUOI pas un record ?
 * → JPA/Hibernate a besoin d'un constructeur sans argument
 * → Et de setters pour hydrater l'objet depuis la base
 * → Les records sont immuables, donc incompatibles avec JPA
 */
@Entity
@Table(name = "produits")
public class Produit {

    /**
     * CLÉ PRIMAIRE — Identifiant unique auto-généré
     *
     * @GeneratedValue(IDENTITY) :
     * → La base de données génère l'ID (AUTO_INCREMENT en MySQL, SERIAL en PostgreSQL)
     * → Hibernate ne génère PAS l'ID lui-même, il laisse la base faire
     *
     * Autres stratégies possibles :
     * - SEQUENCE → Utilise une séquence SQL (recommandé pour PostgreSQL)
     * - TABLE    → Utilise une table dédiée (portable mais lent)
     * - UUID     → Génère un UUID côté Java (pas de requête pour obtenir l'ID)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * NOM DU PRODUIT
     *
     * @Column personnalise la colonne SQL :
     * - nullable = false → NOT NULL en SQL
     * - length = 100     → VARCHAR(100)
     *
     * Sans @Column, Hibernate utilise les valeurs par défaut :
     * VARCHAR(255), nullable
     */
    @Column(nullable = false, length = 100)
    private String nom;

    /**
     * DESCRIPTION (optionnelle, texte long)
     *
     * columnDefinition = "TEXT" :
     * → Force le type SQL à TEXT au lieu de VARCHAR
     * → Permet des descriptions longues sans limite de taille
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * PRIX — Utilise BigDecimal pour la précision monétaire
     *
     * POURQUOI BigDecimal et pas double ?
     * → double a des erreurs d'arrondi : 0.1 + 0.2 = 0.30000000000000004
     * → BigDecimal est EXACT : 0.1 + 0.2 = 0.3
     * → TOUJOURS utiliser BigDecimal pour l'argent !
     *
     * precision = 10 → 10 chiffres au total
     * scale = 2      → 2 chiffres après la virgule
     * → Maximum : 99 999 999,99
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    /**
     * STOCK — Quantité disponible en entrepôt
     */
    @Column(nullable = false)
    private Integer stock;

    /**
     * DATE DE CRÉATION — Remplie automatiquement avant la 1ère insertion
     *
     * @PrePersist est un CALLBACK JPA :
     * → Exécuté automatiquement AVANT que Hibernate fasse le INSERT
     * → Pas besoin de le remplir manuellement dans le service
     */
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    /**
     * DATE DE DERNIÈRE MODIFICATION
     *
     * @PreUpdate est exécuté AVANT chaque UPDATE en base
     */
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    // ─── CALLBACKS JPA (cycle de vie de l'entité) ───

    /**
     * Appelé automatiquement AVANT le premier INSERT
     */
    @PrePersist
    protected void avantCreation() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    /**
     * Appelé automatiquement AVANT chaque UPDATE
     */
    @PreUpdate
    protected void avantModification() {
        this.dateModification = LocalDateTime.now();
    }

    // ─── CONSTRUCTEURS ───

    /**
     * Constructeur SANS ARGUMENT — OBLIGATOIRE pour JPA/Hibernate
     *
     * Hibernate crée les objets par réflexion avec ce constructeur,
     * puis remplit les champs via les setters.
     * Sans lui → erreur au démarrage : "No default constructor"
     */
    protected Produit() {
    }

    /**
     * Constructeur pour créer un nouveau produit (sans ID, il sera auto-généré)
     */
    public Produit(String nom, String description, BigDecimal prix, Integer stock) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
    }

    // ─── GETTERS ET SETTERS ───
    // JPA/Hibernate a besoin des getters pour lire et des setters pour écrire

    public Long getId() {
        return id;
    }

    // Pas de setId() → l'ID est géré par la base de données

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    @Override
    public String toString() {
        return "Produit{id=%d, nom='%s', prix=%s, stock=%d}"
                .formatted(id, nom, prix, stock);
    }
}
