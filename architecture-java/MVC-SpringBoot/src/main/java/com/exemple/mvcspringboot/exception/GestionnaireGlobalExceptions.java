package com.exemple.mvcspringboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GESTIONNAIRE GLOBAL D'EXCEPTIONS — @RestControllerAdvice
 *
 * C'est le FILET DE SÉCURITÉ de l'application Spring Boot.
 * Toute exception non catchée dans un contrôleur arrive ici.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * → Intercepte les exceptions de TOUS les @RestController
 * → Convertit l'exception en réponse JSON structurée
 * → Évite d'avoir des try-catch dans chaque méthode du contrôleur
 *
 * FLOW :
 * 1. Le contrôleur lance une exception
 * 2. Spring cherche un @ExceptionHandler correspondant ici
 * 3. La méthode handler crée une réponse HTTP avec le bon code
 * 4. Le client reçoit un JSON propre au lieu d'une stack trace
 *
 * SANS @ControllerAdvice :
 * → Le client recevrait la page d'erreur par défaut de Spring (Whitelabel)
 * → Ou pire, une stack trace Java complète (fuite d'information)
 */
@RestControllerAdvice
public class GestionnaireGlobalExceptions {

    /**
     * GÈRE : RessourceNonTrouveeException → HTTP 404
     *
     * @ExceptionHandler indique à Spring quelle exception cette méthode gère.
     * Spring choisit le handler le plus SPÉCIFIQUE pour chaque exception.
     */
    @ExceptionHandler(RessourceNonTrouveeException.class)
    public ResponseEntity<Map<String, Object>> gererRessourceNonTrouvee(
            RessourceNonTrouveeException exception) {

        Map<String, Object> erreur = new HashMap<>();
        erreur.put("statut", HttpStatus.NOT_FOUND.value());
        erreur.put("erreur", "Ressource non trouvée");
        erreur.put("message", exception.getMessage());
        erreur.put("ressource", exception.getNomRessource());
        erreur.put("identifiant", exception.getIdentifiant());
        erreur.put("horodatage", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erreur);
    }

    /**
     * GÈRE : Erreurs de validation (@Valid) → HTTP 400
     *
     * Quand @Valid échoue dans le contrôleur, Spring lance
     * MethodArgumentNotValidException avec TOUS les champs invalides.
     *
     * On transforme ça en un JSON lisible pour le client :
     * { "nom": "Le nom est obligatoire", "prix": "Le prix doit être > 0" }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> gererValidation(
            MethodArgumentNotValidException exception) {

        // Collecter TOUTES les erreurs de validation (champ → message)
        Map<String, String> champsInvalides = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(erreur ->
                champsInvalides.put(erreur.getField(), erreur.getDefaultMessage())
        );

        Map<String, Object> erreur = new HashMap<>();
        erreur.put("statut", HttpStatus.BAD_REQUEST.value());
        erreur.put("erreur", "Données invalides");
        erreur.put("champs", champsInvalides);
        erreur.put("horodatage", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur);
    }

    /**
     * GÈRE : Toute autre exception → HTTP 500
     *
     * C'est le handler par DÉFAUT (catch-all).
     * En production, on loggerait l'erreur et on ne renverrait
     * PAS les détails techniques au client (risque de sécurité).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> gererErreurGenerale(Exception exception) {

        Map<String, Object> erreur = new HashMap<>();
        erreur.put("statut", HttpStatus.INTERNAL_SERVER_ERROR.value());
        erreur.put("erreur", "Erreur interne du serveur");
        // En production : ne PAS exposer exception.getMessage() !
        erreur.put("message", "Une erreur inattendue s'est produite");
        erreur.put("horodatage", LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
    }
}
