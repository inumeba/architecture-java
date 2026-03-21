package com.banque.acquisition.domaine.service;

import com.banque.acquisition.domaine.modele.FormulaireSouscriptionTPE;
import com.banque.acquisition.domaine.modele.OffreTPE;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service métier (Domain Service) gérant la logique des Offres Monétiques (TPE, Acquisition).
 * Il orchestre les règles métiers et vérifie si la souscription est matériellement possible.
 */
@Service
public class ServiceOffreMonetique {
    
    /**
     * Récupère l'offre "Standard" dédiée spécifiquement aux nouveaux clients.
     * @return Une instance immuable de l'offre.
     */
    public OffreTPE obtenirOffreStandardRecente() {
        return new OffreTPE("TPE-STD-2024", "Pack TPE Standard (Récent)", 0.0, 1.2);
    }
    
    /**
     * Fournit l'ensemble du catalogue d'acquisition disponible à la banque.
     * @return La liste (immuable) des différentes offres TPE.
     */
    public List<OffreTPE> listerToutesLesOffres() {
        return List.of(
            obtenirOffreStandardRecente(),
            new OffreTPE("TPE-PREMIUM", "Pack Acquisition Premium (Multi-boutiques)", 50.0, 0.8),
            new OffreTPE("TPE-MOBILE", "Solution d'Encaissement Mobile (SoftPOS)", 15.0, 1.5)
        );
    }
    
    /**
     * Valide et crée la souscription au contrat.
     * Applique les règles de validations strictes (Disponibilité matérielle, authentification du conseiller).
     * 
     * @param formulaire L'objet regroupant les informations du formulaire web.
     * @throws IllegalArgumentException si les règles métiers ne sont pas respectées.
     */
    public void souscrireOffre(FormulaireSouscriptionTPE formulaire) {
        // Validation : Le matériel doit être en stock
        if ("RUPTURE".equals(formulaire.referenceOffre())) {
            throw new IllegalArgumentException("Le Terminal de Paiement (TPE) demandé est en rupture de stock.");
        }
        
        // Validation : La trace de l'auteur (Le conseiller) est requise pour l'audit et les primes
        if (formulaire.matriculeConseiller() == null || formulaire.matriculeConseiller().isBlank()) {
            throw new IllegalArgumentException("L'identifiant du Conseiller Agence est obligatoire pour valider le Contrat d'Acquisition.");
        }
        
        // Simulation d'une insertion en base de données ou d'un appel API partenaire (Ingenico...)
        System.out.println("Souscription Contrat Acquisition effectuée pour le SIREN : " + formulaire.siren() + " par le conseiller " + formulaire.matriculeConseiller());
    }
}
