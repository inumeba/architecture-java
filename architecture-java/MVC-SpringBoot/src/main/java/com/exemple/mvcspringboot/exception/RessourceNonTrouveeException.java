package com.exemple.mvcspringboot.exception;

/**
 * EXCEPTION MÉTIER — Ressource non trouvée (HTTP 404)
 *
 * Lancée quand on cherche une entité qui n'existe pas en base.
 *
 * Extends RuntimeException (UNCHECKED) car :
 * → Dans Spring Boot, les exceptions métier sont généralement unchecked
 * → Le @ControllerAdvice les intercepte et les convertit en réponse HTTP
 * → Pas besoin de "throws" dans les signatures de méthodes
 *
 * En Spring MVC, on aurait pu utiliser @ResponseStatus(HttpStatus.NOT_FOUND)
 * directement sur la classe, mais le @ControllerAdvice est plus flexible.
 */
public class RessourceNonTrouveeException extends RuntimeException {

    private final String nomRessource;
    private final Object identifiant;

    public RessourceNonTrouveeException(String nomRessource, Object identifiant) {
        super("%s avec l'identifiant '%s' n'existe pas".formatted(nomRessource, identifiant));
        this.nomRessource = nomRessource;
        this.identifiant = identifiant;
    }

    public String getNomRessource() {
        return nomRessource;
    }

    public Object getIdentifiant() {
        return identifiant;
    }
}
