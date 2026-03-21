package com.exemple.mvcspringboot.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO — Data Transfer Object (Objet de Transfert de Données)
 *
 * POURQUOI un DTO séparé de l'entité ?
 *
 * 1. SÉCURITÉ : ne pas exposer les champs internes (id, dates) au client
 *    → Le client ne devrait PAS pouvoir envoyer un id ou une date
 *    → Le DTO ne contient que les champs modifiables
 *
 * 2. VALIDATION : les annotations @NotBlank, @Min... sont sur le DTO
 *    → La validation se fait AVANT d'atteindre le service
 *    → L'entité reste propre (pas de logique de validation)
 *
 * 3. DÉCOUPLAGE : on peut changer l'entité sans casser l'API
 *    → Ajouter un champ en base n'oblige pas à modifier le JSON
 *    → Le DTO est le CONTRAT avec le client
 *
 * 4. PERFORMANCE : on peut ne retourner qu'une partie des champs
 *    → Évite de sérialiser des relations JPA inutiles
 *    → Évite les problèmes de lazy loading Jackson
 *
 * RECORD Java 17+ :
 * → Parfait pour un DTO : immuable, concis, toString/equals gratuits
 * → Les annotations de validation fonctionnent sur les composants du record
 *
 * FLUX : Client → JSON → DTO → Contrôleur → Service → Entité → Hibernate → BD
 */
public record ProduitDTO(

        /**
         * NOM — Obligatoire, entre 2 et 100 caractères
         *
         * @NotBlank = @NotNull + @NotEmpty + trim().length() > 0
         * → Refuse null, "", et "   " (espaces seuls)
         */
        @NotBlank(message = "Le nom du produit est obligatoire")
        @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
        String nom,

        /**
         * DESCRIPTION — Optionnelle
         *
         * Pas de @NotBlank → accepte null et ""
         * @Size limite la taille maximum
         */
        @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
        String description,

        /**
         * PRIX — Obligatoire, minimum 0.01€
         *
         * @NotNull → le champ doit être présent dans le JSON
         * @DecimalMin → valeur minimale (en String pour la précision BigDecimal)
         * @Digits → limite le nombre de chiffres (8 entiers + 2 décimales)
         */
        @NotNull(message = "Le prix est obligatoire")
        @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
        @Digits(integer = 8, fraction = 2, message = "Le prix ne peut pas avoir plus de 2 décimales")
        BigDecimal prix,

        /**
         * STOCK — Obligatoire, minimum 0
         *
         * @Min(0) → pas de stock négatif
         */
        @NotNull(message = "Le stock est obligatoire")
        @Min(value = 0, message = "Le stock ne peut pas être négatif")
        Integer stock

) {
    // Record = pas besoin de getters, constructeur, toString, equals, hashCode
    // Tout est généré automatiquement par le compilateur Java
}
