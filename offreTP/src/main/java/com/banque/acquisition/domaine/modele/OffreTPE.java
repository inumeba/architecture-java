package com.banque.acquisition.domaine.modele;

/**
 * Modèle de Domaine représentant une Offre de Terminal de Paiement Électronique (TPE).
 * 
 * @param referenceContrat La référence technique/interne du contrat.
 * @param nomCommercial Le nom affiché à l'écran pour le Conseiller Agence.
 * @param fraisMiseEnService Le coût d'installation initiale à facturer au commerçant.
 * @param tauxCommissionParTransaction Le pourcentage que la banque prélève sur chaque transaction validée par le TPE.
 */
public record OffreTPE(
    String referenceContrat, 
    String nomCommercial, 
    double fraisMiseEnService, 
    double tauxCommissionParTransaction
) {}
