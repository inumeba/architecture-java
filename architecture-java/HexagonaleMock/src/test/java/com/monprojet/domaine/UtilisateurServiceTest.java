package com.monprojet.domaine;

import com.monprojet.adaptateur.UtilisateurRepositoryMock;
import com.monprojet.port.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitaire du Service Métier.
 * 
 * L'avantage majeur de l'architecture hexagonale se voit ici :
 * Nous pouvons tester la logique métier (le Domaine) TRES RAPIDEMENT et
 * indépendamment de Spring Boot ou d'une vraie Base de Données,
 * en injectant simplement notre Mock !
 */
class UtilisateurServiceTest {

    private UtilisateurRepository mockRepository;
    private UtilisateurService utilisateurService;

    @BeforeEach
    void setUp() {
        // Initialisation de notre adaptateur Mock
        mockRepository = new UtilisateurRepositoryMock();
        // Injection manuelle dans le service pour tester la logique pure
        utilisateurService = new UtilisateurService(mockRepository);
    }

    @Test
    void quandCreerUtilisateurValide_alorsIlEstSauvegardeDansLeMock() {
        // 1. Act (Exécution)
        utilisateurService.creerUtilisateur("3C", "Charlie");

        // 2. Assert (Vérification)
        assertTrue(mockRepository.trouverParId("3C").isPresent(), "L'utilisateur devrait être dans le mock.");
        assertEquals("Charlie", mockRepository.trouverParId("3C").get().getNom());
    }

    @Test
    void quandCreerUtilisateurAvecNomVide_alorsLeveException() {
        // Validation de notre règle métier (nom non vide)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            utilisateurService.creerUtilisateur("4D", "   ");
        });

        assertEquals("Le nom ne peut pas être vide", exception.getMessage());
    }
}