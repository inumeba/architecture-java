package com.exemple.mvcspringboot.service;

import com.exemple.mvcspringboot.depot.ProduitRepository;
import com.exemple.mvcspringboot.dto.ProduitDTO;
import com.exemple.mvcspringboot.dto.ProduitReponseDTO;
import com.exemple.mvcspringboot.exception.RessourceNonTrouveeException;
import com.exemple.mvcspringboot.modele.Produit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SERVICE MÉTIER — Couche de logique métier
 *
 * Le service est le CŒUR de l'application. Il contient :
 * - Les règles métier (validation avancée, calculs...)
 * - La conversion DTO ↔ Entité
 * - La gestion des transactions
 *
 * @Service : marque cette classe comme composant Spring de la couche service.
 * Spring crée UNE SEULE instance (singleton) et l'injecte partout où c'est nécessaire.
 *
 * @Transactional : chaque méthode publique s'exécute dans une TRANSACTION SQL.
 * → Si une exception est lancée → ROLLBACK automatique
 * → Si tout se passe bien → COMMIT automatique
 * → readOnly = true → optimise les SELECT (pas de dirty checking Hibernate)
 *
 * INJECTION DE DÉPENDANCES (DI) :
 * → Le constructeur reçoit ProduitRepository en paramètre
 * → Spring l'injecte automatiquement (pas besoin de @Autowired avec un seul constructeur)
 * → C'est le principe d'INVERSION DE CONTRÔLE (IoC) : le service ne crée pas ses dépendances,
 *   il les reçoit de l'extérieur = TESTABLE (on peut injecter un mock dans les tests)
 */
@Service
@Transactional(readOnly = true)
public class ProduitService {

    private final ProduitRepository produitRepository;

    /**
     * INJECTION PAR CONSTRUCTEUR — La méthode recommandée par Spring
     *
     * Pourquoi pas @Autowired sur un champ ?
     * → Le constructeur rend la dépendance OBLIGATOIRE (impossible d'oublier)
     * → Le champ peut être final → IMMUABLE (thread-safe)
     * → Facilite les tests unitaires (on passe un mock au constructeur)
     */
    public ProduitService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    /**
     * LISTER — Récupère tous les produits
     *
     * readOnly = true (hérité de la classe) :
     * → Hibernate ne vérifie PAS si les objets ont changé (dirty checking)
     * → Plus performant pour les lectures
     *
     * .stream().map().toList() :
     * → Convertit chaque Entité en DTO de réponse
     * → Le client ne voit JAMAIS l'entité JPA directement
     */
    public List<ProduitReponseDTO> listerTous() {
        return produitRepository.findAll()
                .stream()
                .map(this::versReponseDTO)
                .toList();
    }

    /**
     * TROUVER PAR ID — Retourne un produit ou lance une exception
     *
     * orElseThrow() :
     * → findById retourne Optional<Produit>
     * → Si vide → lance RessourceNonTrouveeException
     * → Le @ControllerAdvice la convertira en HTTP 404
     */
    public ProduitReponseDTO trouverParId(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Produit", id));
        return versReponseDTO(produit);
    }

    /**
     * RECHERCHER — Cherche des produits par mot-clé dans le nom
     */
    public List<ProduitReponseDTO> rechercherParNom(String motCle) {
        return produitRepository.findByNomContainingIgnoreCase(motCle)
                .stream()
                .map(this::versReponseDTO)
                .toList();
    }

    /**
     * CRÉER — Persiste un nouveau produit en base
     *
     * @Transactional (sans readOnly) car on MODIFIE la base :
     * → Ouvre une transaction SQL
     * → Exécute le INSERT
     * → COMMIT si tout va bien, ROLLBACK si exception
     *
     * Le DTO est déjà validé par @Valid dans le contrôleur.
     * Ici on pourrait ajouter des règles métier supplémentaires.
     */
    @Transactional
    public ProduitReponseDTO creer(ProduitDTO dto) {
        Produit produit = versEntite(dto);
        Produit sauvegarde = produitRepository.save(produit);
        return versReponseDTO(sauvegarde);
    }

    /**
     * MODIFIER — Met à jour un produit existant
     *
     * 1. Charger l'entité existante (ou 404)
     * 2. Appliquer les modifications
     * 3. save() → Hibernate fait un UPDATE (car l'entité a déjà un ID)
     *
     * Note : avec Hibernate, on pourrait aussi modifier l'entité
     * sans appeler save() grâce au DIRTY CHECKING :
     * → Hibernate détecte que l'objet a changé et fait le UPDATE au commit
     * → Mais save() est plus explicite et lisible
     */
    @Transactional
    public ProduitReponseDTO modifier(Long id, ProduitDTO dto) {
        Produit existant = produitRepository.findById(id)
                .orElseThrow(() -> new RessourceNonTrouveeException("Produit", id));

        // Mettre à jour les champs
        existant.setNom(dto.nom());
        existant.setDescription(dto.description());
        existant.setPrix(dto.prix());
        existant.setStock(dto.stock());

        // @PreUpdate sera appelé automatiquement par Hibernate
        Produit sauvegarde = produitRepository.save(existant);
        return versReponseDTO(sauvegarde);
    }

    /**
     * SUPPRIMER — Supprime un produit par ID
     *
     * On vérifie d'abord que le produit existe (sinon 404).
     * deleteById lancerait EmptyResultDataAccessException si introuvable,
     * mais notre message d'erreur est plus clair.
     */
    @Transactional
    public void supprimer(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new RessourceNonTrouveeException("Produit", id);
        }
        produitRepository.deleteById(id);
    }

    // ─── MÉTHODES DE CONVERSION (Mapper) ───

    /**
     * ENTITÉ → DTO DE RÉPONSE
     *
     * En production, on utiliserait MapStruct ou ModelMapper
     * pour automatiser ces conversions. Ici on le fait manuellement
     * pour comprendre le mécanisme.
     */
    private ProduitReponseDTO versReponseDTO(Produit produit) {
        return new ProduitReponseDTO(
                produit.getId(),
                produit.getNom(),
                produit.getDescription(),
                produit.getPrix(),
                produit.getStock(),
                produit.getDateCreation(),
                produit.getDateModification()
        );
    }

    /**
     * DTO D'ENTRÉE → ENTITÉ
     *
     * Le DTO ne contient PAS l'ID ni les dates
     * → L'ID sera généré par la base
     * → Les dates seront remplies par @PrePersist
     */
    private Produit versEntite(ProduitDTO dto) {
        return new Produit(
                dto.nom(),
                dto.description(),
                dto.prix(),
                dto.stock()
        );
    }
}
