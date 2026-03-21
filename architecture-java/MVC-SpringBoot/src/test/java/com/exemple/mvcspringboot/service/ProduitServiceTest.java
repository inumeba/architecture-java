package com.exemple.mvcspringboot.service;

import com.exemple.mvcspringboot.depot.ProduitRepository;
import com.exemple.mvcspringboot.dto.ProduitDTO;
import com.exemple.mvcspringboot.dto.ProduitReponseDTO;
import com.exemple.mvcspringboot.exception.RessourceNonTrouveeException;
import com.exemple.mvcspringboot.modele.Produit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests Unitaires pour la couche Service de l'architecture MVC.
 * Utilise Mockito pour "bouchonner" (mock) le Repository.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du ProduitService")
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitService produitService;

    private Produit produitMock;

    @BeforeEach
    void setUp() {
        produitMock = new Produit("Ordinateur", "PC Gamer", new BigDecimal("1500.00"), 10);
        // On utilise la réflexion pour injecter l'ID puisque le setteur n'existe pas
        ReflectionTestUtils.setField(produitMock, "id", 1L);
    }

    @Test
    @DisplayName("Création d'un produit avec succès")
    void testCreerProduit() {
        // Arrange
        ProduitDTO dto = new ProduitDTO("Ordinateur", "PC Gamer", new BigDecimal("1500.00"), 10);
        when(produitRepository.save(any(Produit.class))).thenReturn(produitMock);

        // Act
        ProduitReponseDTO reponse = produitService.creer(dto);

        // Assert
        assertNotNull(reponse);
        assertEquals(1L, reponse.id());
        assertEquals("Ordinateur", reponse.nom());
        verify(produitRepository, times(1)).save(any(Produit.class));
    }

    @Test
    @DisplayName("Génère une erreur si le produit n'est pas trouvé")
    void testObtenirProduitParId_NonTrouve() {
        // Arrange
        when(produitRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RessourceNonTrouveeException.class, () -> {
            produitService.trouverParId(99L);
        });

        assertEquals("Produit avec l'identifiant '99' n'existe pas", exception.getMessage());
        verify(produitRepository, times(1)).findById(99L);
    }
}
