package com.banque.acquisition.web.controleur;

import com.banque.acquisition.domaine.modele.ClientCommercant;
import com.banque.acquisition.domaine.modele.OffreTPE;
import com.banque.acquisition.domaine.service.ServiceCommercant;
import com.banque.acquisition.domaine.service.ServiceOffreMonetique;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContractualisationTPEControleur.class)
@WithMockUser(username = "conseiller", roles = {"CONSEILLER"}) // Simule un conseiller connecté
class ContractualisationTPEControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceCommercant serviceCommercant;

    @MockBean
    private ServiceOffreMonetique serviceOffreMonetique;

    @Test
    void quandSirenNExistePas_RedirigeVersVueInvalide() throws Exception {
        when(serviceCommercant.trouverParSiren("999")).thenReturn(null);

        mockMvc.perform(get("/agence/contractualisation/tpe/standard").param("siren", "999"))
               .andExpect(status().isOk())
               .andExpect(view().name("erreurs/client-invalide"));
    }

    @Test
    void quandClientEstAncien_RedirigeVersVueNonEligible() throws Exception {
        ClientCommercant ancienClient = new ClientCommercant("555", "Vieux Garage", LocalDate.now().minusYears(5), false);
        when(serviceCommercant.trouverParSiren("555")).thenReturn(ancienClient);

        mockMvc.perform(get("/agence/contractualisation/tpe/standard").param("siren", "555"))
               .andExpect(status().isOk())
               .andExpect(view().name("erreurs/non-eligible"));
    }

    @Test
    void quandClientEligible_AfficheFomulaireTPE() throws Exception {
        ClientCommercant bonClient = new ClientCommercant("123", "Boulangerie", LocalDate.now().minusMonths(1), false);
        OffreTPE offre = new OffreTPE("TPE1", "TPE Standard", 0.0, 1.2);
        
        when(serviceCommercant.trouverParSiren("123")).thenReturn(bonClient);
        when(serviceOffreMonetique.obtenirOffreStandardRecente()).thenReturn(offre);

        mockMvc.perform(get("/agence/contractualisation/tpe/standard").param("siren", "123"))
               .andExpect(status().isOk())
               .andExpect(view().name("contractualisation/formulaire-tpe-standard"))
               .andExpect(model().attributeExists("client"))
               .andExpect(model().attributeExists("formulaire"));
    }

    @Test
    void quandSouscriptionCorrecte_RedirigeVersSucces() throws Exception {
        mockMvc.perform(post("/agence/contractualisation/tpe/standard/souscrire")
                .with(csrf()) // Ajout du jeton CSRF obligatoire pour valider la sécurité en POST
                .param("siren", "123")
                .param("matriculeConseiller", "AG01")
                .param("referenceOffre", "TPE-STD"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/agence/contractualisation/tpe/succes"));
    }
}