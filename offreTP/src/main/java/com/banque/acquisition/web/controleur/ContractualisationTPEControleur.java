package com.banque.acquisition.web.controleur;

import com.banque.acquisition.domaine.modele.ClientCommercant;
import com.banque.acquisition.domaine.modele.FormulaireSouscriptionTPE;
import com.banque.acquisition.domaine.modele.OffreTPE;
import com.banque.acquisition.domaine.service.ServiceCommercant;
import com.banque.acquisition.domaine.service.ServiceOffreMonetique;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur Web (MVC) responsable de l'IHM des conseillers en agence bancaire.
 * Ce contrôleur gère uniquement l'affichage des pages Thymeleaf et la réception des formulaires.
 */
@Controller
@RequestMapping("/agence/contractualisation/tpe")
public class ContractualisationTPEControleur {

    private final ServiceCommercant serviceCommercant;
    private final ServiceOffreMonetique serviceOffreMonetique;

    public ContractualisationTPEControleur(ServiceCommercant serviceCommercant, 
                                           ServiceOffreMonetique serviceOffreMonetique) {
        this.serviceCommercant = serviceCommercant;
        this.serviceOffreMonetique = serviceOffreMonetique;
    }

    /**
     * Affiche l'écran de synthèse (formulaire) préparant le contrat d'acquisition TPE.
     * Cette méthode bloque l'affichage si le client ne respecte pas les critères métiers.
     * 
     * @param siren Le SIREN du commerce visé, passé en paramètre d'URL (ex: ?siren=123..)
     * @param modele Le modèle Spring Thymeleaf (injection automatique)
     * @return Le nom du template HTML à rendre.
     */
    @GetMapping("/standard")
    public String afficherInterfaceContractualisation(
            @RequestParam("siren") String siren,
            Model modele) {

        // 1. Récupération des informations sur l'entreprise du client
        ClientCommercant client = serviceCommercant.trouverParSiren(siren);

        // 2. Vérification robuste : Le client existe-il en base de données ?
        if (client == null) {
            modele.addAttribute("erreurMessage", "Commerçant introuvable dans le système bancaire.");
            return "erreurs/client-invalide";
        }
        
        // 3. Vérification de l'éligibilité métier du domaine "Acquisition"
        if (!client.estEligibleStandard()) {
            modele.addAttribute("erreurMessage", 
                "Ce client n'est pas éligible à l'offre TPE Standard. " +
                "Cause : Le client n'est pas un commerçant récemment installé, ou présente un incident monétique avéré."
            );
            return "erreurs/non-eligible";
        }

        // 4. Si tout est valide, proposition de l'offre et montage de la vue Thymeleaf
        OffreTPE offreStandard = serviceOffreMonetique.obtenirOffreStandardRecente();

        modele.addAttribute("client", client);
        modele.addAttribute("offre", offreStandard);
        modele.addAttribute("formulaire", new FormulaireSouscriptionTPE(client.siren(), "", offreStandard.referenceContrat()));

        return "contractualisation/formulaire-tpe-standard"; 
    }

    /**
     * Intercepte la soumission (le POST) du conseiller depuis l'interface web pour valider le contrat.
     * 
     * @param formulaire Le DTO rempli avec les données (matricule conseiller, siren client, référence).
     * @param modele Le modèle pour afficher les éventuelles erreurs d'enregistrement (ex: TPE indisponible).
     * @return Une redirection (302) HTTP en cas de succès, ou recharge la page initiale avec l'erreur.
     */
    @PostMapping("/standard/souscrire")
    public String validerContractualisation(
            @ModelAttribute("formulaire") FormulaireSouscriptionTPE formulaire, 
            Model modele) {
        
        try {
            // Le service métier orchestre la validation
            serviceOffreMonetique.souscrireOffre(formulaire);
            
            // Pattern PRG (Post-Redirect-Get) : On redirige pour empêcher le double-soumission du formulaire
            return "redirect:/agence/contractualisation/tpe/succes";
            
        } catch (IllegalArgumentException ex) {
            // 5. UX : Gestion propre des erreurs métiers (Matricule oublié, rupture TPE...) sans "Planter" le serveur
            modele.addAttribute("erreurMessage", ex.getMessage());
            ClientCommercant client = serviceCommercant.trouverParSiren(formulaire.siren());
            modele.addAttribute("client", client);
            modele.addAttribute("offre", serviceOffreMonetique.obtenirOffreStandardRecente());
            return "contractualisation/formulaire-tpe-standard";
        }
    }
    
    /**
     * Affiche un simple écran de validation positif.
     */
    @GetMapping("/succes")
    public String succes() {
        return "contractualisation/succes";
    }
}
