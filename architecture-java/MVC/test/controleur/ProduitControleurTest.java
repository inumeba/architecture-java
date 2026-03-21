package controleur;

import modele.Produit;
import modele.ProduitDepot;
import vue.ProduitVue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduitControleurTest {

    @Mock
    private ProduitDepot depot;

    @Mock
    private ProduitVue vue;

    @InjectMocks
    private ProduitControleur controleur;

    @Test
    void testCreerProduit() {
        Produit p = new Produit(1, "Clavier", 50.0);
        when(depot.ajouter("Clavier", 50.0)).thenReturn(p);

        controleur.creerProduit("Clavier", 50.0);

        verify(depot, times(1)).ajouter("Clavier", 50.0);
        verify(vue, times(1)).afficherMessage(contains("Produit cree"));
    }

    @Test
    void testListerProduits() {
        List<Produit> liste = List.of(
            new Produit(1, "Clavier", 50.0),
            new Produit(2, "Souris", 25.0)
        );
        when(depot.obtenirTous()).thenReturn(liste);

        controleur.listerProduits();

        verify(depot, times(1)).obtenirTous();
        verify(vue, times(1)).afficherListeProduits(liste);
    }

    @Test
    void testAfficherProduit_Trouve() {
        Produit p = new Produit(1, "Clavier", 50.0);
        when(depot.trouverParId(1)).thenReturn(Optional.of(p));

        controleur.afficherProduit(1);

        verify(depot, times(1)).trouverParId(1);
        verify(vue, times(1)).afficherDetailProduit(p);
        verify(vue, never()).afficherErreur(anyString());
    }

    @Test
    void testAfficherProduit_NonTrouve() {
        when(depot.trouverParId(99)).thenReturn(Optional.empty());

        controleur.afficherProduit(99);

        verify(depot, times(1)).trouverParId(99);
        verify(vue, never()).afficherDetailProduit(any());
        verify(vue, times(1)).afficherErreur(contains("Aucun produit trouve"));
    }

    @Test
    void testSupprimerProduit_Existant() {
        when(depot.supprimer(1)).thenReturn(true);

        controleur.supprimerProduit(1);

        verify(depot, times(1)).supprimer(1);
        verify(vue, times(1)).afficherMessage(contains("supprime"));
    }

    @Test
    void testSupprimerProduit_Inexistant() {
        when(depot.supprimer(99)).thenReturn(false);

        controleur.supprimerProduit(99);

        verify(depot, times(1)).supprimer(99);
        verify(vue, times(1)).afficherErreur(contains("Impossible de supprimer"));
    }
}
