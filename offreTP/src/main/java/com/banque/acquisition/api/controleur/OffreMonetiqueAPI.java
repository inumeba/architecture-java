package com.banque.acquisition.api.controleur;

import com.banque.acquisition.domaine.modele.OffreTPE;
import com.banque.acquisition.domaine.service.ServiceOffreMonetique;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST séparée de la contractualisation Web.
 * Cette API peut être appelée par une application externe (ex: un comparateur d'offres)
 * ou par une application mobile conseiller (`ConseillerAgence`).
 */
@RestController
@RequestMapping("/api/v1/acquisition/offres-monetiques")
public class OffreMonetiqueAPI {

    private final ServiceOffreMonetique serviceOffreMonetique;

    public OffreMonetiqueAPI(ServiceOffreMonetique serviceOffreMonetique) {
        this.serviceOffreMonetique = serviceOffreMonetique;
    }

    /**
     * Liste toutes les offres de type "Terminal de Paiement" et de contrats d'acquisition.
     */
    @GetMapping
    public ResponseEntity<List<OffreTPE>> listerOffresDisponibles() {
        List<OffreTPE> catalogue = serviceOffreMonetique.listerToutesLesOffres();
        return ResponseEntity.ok(catalogue);
    }
}