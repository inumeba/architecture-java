package com.banque.acquisition.domaine.modele;

import java.time.LocalDate;

/**
 * Représente le Client (un Commerçant) au sein de la banque.
 * L'utilisation d'un `record` (Java 14+) garantit l'immuabilité (immutable) de la donnée métier
 * et offre automatiquement les méthodes equals, hashCode et toString.
 *
 * @param siren Identifiant d'entreprise du commerçant.
 * @param raisonSociale Nom de l'entreprise.
 * @param dateOuvertureCompte Date à laquelle le compte bancaire a été ouvert.
 * @param incidentPaiement Indique si le client a récemment eu des incidents de paiement (ex: découverts).
 */
public record ClientCommercant(
    String siren, 
    String raisonSociale, 
    LocalDate dateOuvertureCompte,
    boolean incidentPaiement
) {
    /**
     * Règle Métier : Un client est considéré comme "récent" si son compte 
     * a été ouvert il y a moins de 6 mois.
     *
     * @return true si le compte a été ouvert durant les 6 derniers mois.
     */
    public boolean estRecent() {
        return dateOuvertureCompte.isAfter(LocalDate.now().minusMonths(6));
    }
    
    /**
     * Règle Métier : Détermine si ce client est éligible à l'offre TPE "Standard".
     * Il doit être récent AND ne pas avoir d'incidents de paiement.
     *
     * @return true si toutes les conditions d'éligibilité sont respectées.
     */
    public boolean estEligibleStandard() {
        return !incidentPaiement && estRecent();
    }
}
