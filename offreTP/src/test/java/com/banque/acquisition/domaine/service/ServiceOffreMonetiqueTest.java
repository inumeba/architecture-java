package com.banque.acquisition.domaine.service;

import com.banque.acquisition.domaine.modele.FormulaireSouscriptionTPE;
import com.banque.acquisition.domaine.modele.OffreTPE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServiceOffreMonetiqueTest {

    private ServiceOffreMonetique service;

    @BeforeEach
    void setUp() {
        service = new ServiceOffreMonetique();
    }

    @Test
    void testListerToutesLesOffresDoitRetournerLeCatalogueComplet() {
        List<OffreTPE> offres = service.listerToutesLesOffres();
        assertEquals(3, offres.size(), "Le catalogue doit contenir exactement 3 offres TPE");
    }

    @Test
    void testSouscrireOffreEchoueSiTpeEnRupture() {
        FormulaireSouscriptionTPE formulaireRupture = new FormulaireSouscriptionTPE("123", "CP999", "RUPTURE");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.souscrireOffre(formulaireRupture);
        });
        
        assertTrue(exception.getMessage().contains("rupture de stock"));
    }

    @Test
    void testSouscrireOffreEchoueSiMatriculeConseillerAbsent() {
        FormulaireSouscriptionTPE formulaireSansConseiller = new FormulaireSouscriptionTPE("123", "", "TPE-STD-2024");
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.souscrireOffre(formulaireSansConseiller);
        });
        
        assertTrue(exception.getMessage().contains("identifiant du Conseiller Agence est obligatoire"));
    }

    @Test
    void testSouscrireOffreReussitAvecUnBonFormulaire() {
        FormulaireSouscriptionTPE formulaire = new FormulaireSouscriptionTPE("123456789", "CP9876", "TPE-STD-2024");
        // Si le formulaire est valide, aucune exception n'est lancée par le test.
        assertDoesNotThrow(() -> service.souscrireOffre(formulaire));
    }
}