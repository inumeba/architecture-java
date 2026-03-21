package com.exemple.mvcspringboot.depot;

import com.exemple.mvcspringboot.modele.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * REPOSITORY — Couche d'accès aux données (DAO)
 *
 * Spring Data JPA génère AUTOMATIQUEMENT l'implémentation
 * de cette interface au démarrage. On n'écrit PAS de SQL manuellement.
 *
 * JpaRepository<Produit, Long> fournit GRATUITEMENT :
 * ┌────────────────────────┬──────────────────────────────────┐
 * │ Méthode                │ SQL généré                       │
 * ├────────────────────────┼──────────────────────────────────┤
 * │ save(produit)          │ INSERT INTO produits ...          │
 * │ findById(id)           │ SELECT * WHERE id = ?            │
 * │ findAll()              │ SELECT * FROM produits           │
 * │ deleteById(id)         │ DELETE FROM produits WHERE id=?  │
 * │ count()                │ SELECT COUNT(*) FROM produits    │
 * │ existsById(id)         │ SELECT 1 WHERE id = ?           │
 * └────────────────────────┴──────────────────────────────────┘
 *
 * QUERY METHODS — Spring déduit le SQL depuis le NOM de la méthode :
 * findByNom(nom)           → SELECT * WHERE nom = ?
 * findByPrixLessThan(prix) → SELECT * WHERE prix < ?
 * findByNomContaining(mot) → SELECT * WHERE nom LIKE '%mot%'
 *
 * COMMENT ÇA MARCHE ?
 * 1. Spring scanne les interfaces qui étendent JpaRepository
 * 2. Il crée un PROXY dynamique (classe générée au runtime)
 * 3. Ce proxy traduit les noms de méthodes en requêtes JPQL/SQL
 * 4. Hibernate exécute le SQL et mappe le résultat en objets Java
 *
 * @Repository marque cette interface comme composant Spring de la couche données.
 * (Optionnel car JpaRepository est déjà détecté par Spring Data)
 */
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    /**
     * QUERY METHOD — Recherche par nom (correspondance exacte)
     *
     * Spring traduit automatiquement en :
     * SELECT p FROM Produit p WHERE p.nom = :nom
     */
    List<Produit> findByNom(String nom);

    /**
     * QUERY METHOD — Recherche par nom contenant un mot-clé
     *
     * Le mot-clé "Containing" ajoute des % (LIKE '%mot%')
     * "IgnoreCase" rend la recherche insensible à la casse
     *
     * SQL généré : SELECT * WHERE LOWER(nom) LIKE LOWER('%mot%')
     */
    List<Produit> findByNomContainingIgnoreCase(String motCle);

    /**
     * QUERY METHOD — Produits moins chers qu'un prix donné
     *
     * "LessThan" → opérateur < en SQL
     * Autres opérateurs disponibles :
     * - LessThanEqual   → <=
     * - GreaterThan      → >
     * - GreaterThanEqual → >=
     * - Between          → BETWEEN x AND y
     */
    List<Produit> findByPrixLessThan(BigDecimal prixMax);

    /**
     * QUERY METHOD — Produits avec stock faible
     *
     * Utile pour les alertes de réapprovisionnement
     */
    List<Produit> findByStockLessThan(Integer seuilMinimum);

    /**
     * REQUÊTE JPQL PERSONNALISÉE — Quand le nom de méthode ne suffit pas
     *
     * @Query permet d'écrire du JPQL (Java Persistence Query Language)
     * JPQL ressemble à SQL mais utilise les NOMS DES CLASSES Java
     * au lieu des noms de tables SQL.
     *
     * "Produit" ici = la classe Java, PAS le nom de la table
     * "p.prix" = le champ Java, PAS la colonne SQL
     *
     * Hibernate traduit le JPQL en SQL natif selon le dialecte configuré.
     */
    @Query("SELECT p FROM Produit p WHERE p.prix BETWEEN :prixMin AND :prixMax ORDER BY p.prix ASC")
    List<Produit> trouverParFourchetteDePrix(
            @Param("prixMin") BigDecimal prixMin,
            @Param("prixMax") BigDecimal prixMax
    );

    /**
     * REQUÊTE JPQL — Compter les produits en rupture de stock
     */
    @Query("SELECT COUNT(p) FROM Produit p WHERE p.stock = 0")
    long compterRupturesDeStock();
}
