package com.banque.acquisition.domaine.service;

import com.banque.acquisition.domaine.modele.ClientCommercant;
import com.banque.acquisition.infrastructure.donnees.depot.ClientCommercantRepository;
import org.springframework.stereotype.Service;

/**
 * Service métier exposant les opérations relatives aux Clients Commerçants de la banque.
 */
@Service
public class ServiceCommercant {
    
    private final ClientCommercantRepository depot;

    public ServiceCommercant(ClientCommercantRepository depot) {
        this.depot = depot;
    }

    /**
     * Recherche un client commerçant par son SIREN depuis la base de données.
     * Cette méthode convertit l'entité Infrastructure JPA en objet Produit Métier (Record).
     * 
     * @param siren Le numéro d'identification SIREN de l'entreprise.
     * @return Le ClientCommercant s'il est trouvé, sinon null.
     */
    public ClientCommercant trouverParSiren(String siren) {
        return depot.findById(siren)
            .map(entite -> new ClientCommercant(
                entite.getSiren(),
                entite.getRaisonSociale(),
                entite.getDateOuvertureCompte(),
                entite.isIncidentPaiement()
            ))
            .orElse(null); // Client non trouvé en base
    }
}
