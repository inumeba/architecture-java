package domaine.usecase;

import domaine.modele.Produit;
import domaine.port.sortant.NotificationPort;
import domaine.port.sortant.ProduitRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionProduitUseCaseTest {

    @Mock
    private ProduitRepositoryPort repository;

    @Mock
    private NotificationPort notification;

    @InjectMocks
    private GestionProduitUseCase useCase;

    @Test
    void testCreerProduit() {
        Produit expectedProduit = new Produit(12345, "Moniteur", 250.0, 50);
        when(repository.sauvegarder(any(Produit.class))).thenReturn(expectedProduit);

        Produit produitCree = useCase.creerProduit("Moniteur", 250.0, 50);

        assertNotNull(produitCree);
        assertEquals(12345, produitCree.getId());
        assertEquals("Moniteur", produitCree.getNom());
        assertEquals(250.0, produitCree.getPrix());
        assertEquals(50, produitCree.getStock());

        verify(repository, times(1)).sauvegarder(any(Produit.class));
    }

    @Test
    void testListerProduits() {
        Produit p1 = new Produit(1, "Prod1", 10.0, 5);
        Produit p2 = new Produit(2, "Prod2", 20.0, 10);
        when(repository.trouverTous()).thenReturn(List.of(p1, p2));

        List<Produit> produits = useCase.listerProduits();

        assertEquals(2, produits.size());
        verify(repository, times(1)).trouverTous();
    }

    @Test
    void testTrouverProduitParId_Existe() {
        Produit p = new Produit(1, "Prod1", 10.0, 5);
        when(repository.trouverParId(1)).thenReturn(Optional.of(p));

        Optional<Produit> produit = useCase.trouverProduit(1);

        assertTrue(produit.isPresent());
        assertEquals("Prod1", produit.get().getNom());
    }

    @Test
    void testAppliquerRemise_ProduitExiste_Valide() {
        Produit p = new Produit(1, "Clavier", 100.0, 10);
        when(repository.trouverParId(1)).thenReturn(Optional.of(p));
        when(repository.sauvegarder(any(Produit.class))).thenReturn(p);

        Produit produitApresRemise = useCase.appliquerRemise(1, 20.0);

        assertEquals(80.0, produitApresRemise.getPrix());
        verify(repository, times(1)).sauvegarder(p);
    }

    @Test
    void testAppliquerRemise_ProduitIntrouvable() {
        when(repository.trouverParId(99)).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class, () -> useCase.appliquerRemise(99, 20.0));
        assertEquals("Produit introuvable : ID 99", e.getMessage());

        verify(repository, never()).sauvegarder(any(Produit.class));
    }

    @Test
    void testVendre_StockSuffisant() {
        Produit p = new Produit(1, "Casque", 50.0, 10);
        when(repository.trouverParId(1)).thenReturn(Optional.of(p));
        when(repository.sauvegarder(any(Produit.class))).thenReturn(p);

        Produit produitApresVente = useCase.vendre(1, 4);

        assertEquals(6, produitApresVente.getStock());
        verify(repository, times(1)).sauvegarder(p);
        verify(notification, times(1)).notifierVente("Casque", 4, 200.0);
        verify(notification, never()).alerterStockBas(anyString(), anyInt()); // Le stock est Ã  6, le seuil est 3
    }
    
    @Test
    void testVendre_StockBas_DoitAlerter() {
        // Le seuil est 3. Si on vend et que le stock tombe Ã  <= 3, uen alerte doit Ãªtre envoyÃ©e.
        Produit p = new Produit(1, "Casque", 50.0, 5);
        when(repository.trouverParId(1)).thenReturn(Optional.of(p));
        when(repository.sauvegarder(any(Produit.class))).thenReturn(p);

        Produit produitApresVente = useCase.vendre(1, 3); // Reste 2 en stock
        
        assertEquals(2, produitApresVente.getStock());
        verify(repository, times(1)).sauvegarder(p);
        verify(notification, times(1)).notifierVente("Casque", 3, 150.0);
        verify(notification, times(1)).alerterStockBas("Casque", 2);
    }

    @Test
    void testVendre_ProduitIntrouvable() {
        when(repository.trouverParId(99)).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class, () -> useCase.vendre(99, 2));
        assertEquals("Produit introuvable : ID 99", e.getMessage());

        verify(repository, never()).sauvegarder(any(Produit.class));
        verify(notification, never()).notifierVente(anyString(), anyInt(), anyDouble());
    }
}
