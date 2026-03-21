package com.exemple.mvcspringboot.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO DE RÉPONSE — Ce que le client REÇOIT
 *
 * Séparé du DTO d'entrée (ProduitDTO) car la réponse
 * contient des champs en plus : id, dates, etc.
 *
 * Le client envoie un ProduitDTO (sans id)
 * et reçoit un ProduitReponseDTO (avec id + dates)
 *
 * RECORD : parfait ici aussi car la réponse est immuable
 */
public record ProduitReponseDTO(
        Long id,
        String nom,
        String description,
        BigDecimal prix,
        Integer stock,
        LocalDateTime dateCreation,
        LocalDateTime dateModification
) {
}
