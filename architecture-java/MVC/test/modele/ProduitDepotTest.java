package modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProduitDepotTest {

    private ProduitDepot depot;

    @BeforeEach
    void setUp() {
        depot = new ProduitDepot();
    }

    @Test
    void testAjouterEtObtenirTous() {
        depot.ajouter("PC", 1000.0);
        depot.ajouter("Souris", 25.0);

        List<Produit> produits = depot.obtenirTous();
        assertEquals(2, produits.size());
        assertEquals("PC", produits.get(0).getNom());
    }

    @Test
    void testTrouverParId() {
        Produit p = depot.ajouter("Clavier", 50.0);

        Optional<Produit> trouve = depot.trouverParId(p.getId());
        assertTrue(trouve.isPresent());
        assertEquals("Clavier", trouve.get().getNom());

        Optional<Produit> nonTrouve = depot.trouverParId(99);
        assertFalse(nonTrouve.isPresent());
    }

    @Test
    void testSupprimer() {
        Produit p = depot.ajouter("Ecran", 200.0);

        boolean supprime = depot.supprimer(p.getId());
        assertTrue(supprime);
        assertEquals(0, depot.obtenirTous().size());

        boolean supprimeInexistant = depot.supprimer(p.getId());
        assertFalse(supprimeInexistant);
    }
}
