package com.banque.acquisition.domaine.modele;

/**
 * Objet de Transfert de Données (DTO) utilisé pour récupérer les informations saisies 
 * lors de la soumission du formulaire web de contractualisation.
 * 
 * @param siren Le SIREN du client commerçant souscrivant l'offre.
 * @param matriculeConseiller L'identifiant du Conseiller Agence traitant l'opération.
 * @param referenceOffre La référence de l'offre TPE sélectionnée.
 */
public record FormulaireSouscriptionTPE(
    String siren, 
    String matriculeConseiller, 
    String referenceOffre
) {}
