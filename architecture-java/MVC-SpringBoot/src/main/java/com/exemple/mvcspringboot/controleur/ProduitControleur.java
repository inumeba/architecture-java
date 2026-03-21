package com.exemple.mvcspringboot.controleur;

import com.exemple.mvcspringboot.dto.ProduitDTO;
import com.exemple.mvcspringboot.dto.ProduitReponseDTO;
import com.exemple.mvcspringboot.service.ProduitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTRÔLEUR REST (C de MVC) — Point d'entrée de l'API
 *
 * Le contrôleur fait le pont entre le MONDE EXTÉRIEUR (HTTP/JSON)
 * et la LOGIQUE MÉTIER (Service). Il ne contient PAS de logique métier.
 *
 * Ses responsabilités :
 * 1. Recevoir les requêtes HTTP (GET, POST, PUT, DELETE)
 * 2. Valider les données d'entrée (@Valid)
 * 3. Déléguer au service
 * 4. Retourner la réponse HTTP avec le bon code (200, 201, 204, 404...)
 *
 * @RestController = @Controller + @ResponseBody
 * → @Controller  : marque cette classe comme contrôleur Spring MVC
 * → @ResponseBody : les retours sont sérialisés en JSON automatiquement
 *    (Jackson convertit les objets Java en JSON)
 *
 * @RequestMapping("/api/produits") : préfixe d'URL commun à toutes les méthodes
 *
 * CONVENTION REST :
 * ┌──────────┬──────────────────┬──────────────────────────────┐
 * │ Méthode  │ URL              │ Action                       │
 * ├──────────┼──────────────────┼──────────────────────────────┤
 * │ GET      │ /api/produits    │ Lister tous les produits     │
 * │ GET      │ /api/produits/1  │ Obtenir le produit #1        │
 * │ POST     │ /api/produits    │ Créer un produit             │
 * │ PUT      │ /api/produits/1  │ Modifier le produit #1       │
 * │ DELETE   │ /api/produits/1  │ Supprimer le produit #1      │
 * └──────────┴──────────────────┴──────────────────────────────┘
 */
@RestController
@RequestMapping("/api/produits")
public class ProduitControleur {

    private final ProduitService produitService;

    /**
     * INJECTION PAR CONSTRUCTEUR
     *
     * Spring injecte automatiquement le ProduitService.
     * Le contrôleur ne sait PAS comment le service fonctionne,
     * il sait juste qu'il peut appeler ses méthodes.
     * → COUPLAGE FAIBLE entre les couches
     */
    public ProduitControleur(ProduitService produitService) {
        this.produitService = produitService;
    }

    /**
     * GET /api/produits — Lister tous les produits
     *
     * @GetMapping = @RequestMapping(method = GET)
     *
     * Retourne HTTP 200 (OK) avec la liste JSON des produits.
     * Si la liste est vide → retourne 200 avec []
     *
     * Paramètre optionnel : ?recherche=laptop
     * → Si présent, filtre par nom
     * → Si absent, retourne tous les produits
     */
    @GetMapping
    public ResponseEntity<List<ProduitReponseDTO>> listerTous(
            @RequestParam(required = false) String recherche) {

        List<ProduitReponseDTO> produits;

        if (recherche != null && !recherche.isBlank()) {
            produits = produitService.rechercherParNom(recherche);
        } else {
            produits = produitService.listerTous();
        }

        return ResponseEntity.ok(produits);
    }

    /**
     * GET /api/produits/{id} — Obtenir un produit par ID
     *
     * @PathVariable extrait la valeur de {id} dans l'URL.
     * Exemple : GET /api/produits/42 → id = 42
     *
     * Si le produit n'existe pas :
     * → Le service lance RessourceNonTrouveeException
     * → Le @ControllerAdvice la convertit en HTTP 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProduitReponseDTO> trouverParId(@PathVariable Long id) {
        ProduitReponseDTO produit = produitService.trouverParId(id);
        return ResponseEntity.ok(produit);
    }

    /**
     * POST /api/produits — Créer un nouveau produit
     *
     * @RequestBody : Spring/Jackson désérialise le JSON du body en ProduitDTO
     *
     * @Valid : déclenche la validation des annotations (@NotBlank, @Min...)
     * → Si la validation échoue, Spring lance MethodArgumentNotValidException
     * → Le @ControllerAdvice la convertit en HTTP 400 avec les détails
     *
     * Retourne HTTP 201 (Created) avec le produit créé.
     * En REST strict, on devrait aussi ajouter un header "Location"
     * avec l'URL du nouveau produit : /api/produits/42
     */
    @PostMapping
    public ResponseEntity<ProduitReponseDTO> creer(@Valid @RequestBody ProduitDTO produitDTO) {
        ProduitReponseDTO cree = produitService.creer(produitDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    /**
     * PUT /api/produits/{id} — Modifier un produit existant
     *
     * PUT = remplacement COMPLET de la ressource.
     * Tous les champs doivent être fournis.
     *
     * Pour une modification PARTIELLE (quelques champs seulement),
     * on utiliserait PATCH au lieu de PUT.
     *
     * Retourne HTTP 200 (OK) avec le produit modifié.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProduitReponseDTO> modifier(
            @PathVariable Long id,
            @Valid @RequestBody ProduitDTO produitDTO) {

        ProduitReponseDTO modifie = produitService.modifier(id, produitDTO);
        return ResponseEntity.ok(modifie);
    }

    /**
     * DELETE /api/produits/{id} — Supprimer un produit
     *
     * Retourne HTTP 204 (No Content) :
     * → La suppression a réussi
     * → Pas de body dans la réponse (le produit n'existe plus)
     *
     * Si le produit n'existe pas → HTTP 404 (via @ControllerAdvice)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        produitService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
