package domaine.modele;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProduitTest {

    @Test
    void testCreerProduitValide() {
        Produit p = new Produit(1, "Clavier", 49.99, 10);
        assertEquals(1, p.getId());
        assertEquals("Clavier", p.getNom());
        assertEquals(49.99, p.getPrix());
        assertEquals(10, p.getStock());
    }

    @Test
    void testCreerProduitNomInvalide() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new Produit(1, "", 49.99, 10));
        assertEquals("Le nom du produit ne peut pas etre vide", e.getMessage());
    }

    @Test
    void testCreerProduitPrixNegatif() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new Produit(1, "Clavier", -10.0, 10));
        assertEquals("Le prix ne peut pas etre negatif", e.getMessage());
    }

    @Test
    void testCreerProduitStockNegatif() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> new Produit(1, "Clavier", 49.99, -5));
        assertEquals("Le stock ne peut pas etre negatif", e.getMessage());
    }

    @Test
    void testAppliquerRemiseValide() {
        Produit p = new Produit(1, "Clavier", 100.0, 10);
        p.appliquerRemise(20.0);
        assertEquals(80.0, p.getPrix());
    }

    @Test
    void testAppliquerRemiseInvalideTropGrosse() {
        Produit p = new Produit(1, "Clavier", 100.0, 10);
        Exception e = assertThrows(IllegalArgumentException.class, () -> p.appliquerRemise(60.0));
        assertTrue(e.getMessage().contains("La remise doit etre entre 0% et 50%"));
    }

    @Test
    void testAppliquerRemiseInvalideNegative() {
        Produit p = new Produit(1, "Clavier", 100.0, 10);
        Exception e = assertThrows(IllegalArgumentException.class, () -> p.appliquerRemise(-10.0));
        assertTrue(e.getMessage().contains("La remise doit etre entre 0% et 50%"));
    }

    @Test
    void testRetirerDuStockValide() {
        Produit p = new Produit(1, "Clavier", 100.0, 10);
        p.retirerDuStock(3);
        assertEquals(7, p.getStock());
    }

    @Test
    void testRetirerDuStockInvalideNegatif() {
        Produit p = new Produit(1, "Clavier", 100.0, 10);
        Exception e = assertThrows(IllegalArgumentException.class, () -> p.retirerDuStock(-2));
        assertEquals("La quantite doit etre positive", e.getMessage());
    }

    @Test
    void testRetirerDuStockInvalideInsuffisant() {
        Produit p = new Produit(1, "Clavier", 100.0, 5);
        Exception e = assertThrows(IllegalArgumentException.class, () -> p.retirerDuStock(10));
        assertEquals("Stock insuffisant : demande 10, disponible 5", e.getMessage());
    }

    @Test
    void testAjouterAuStockValide() {
        Produit p = new Produit(1, "Clavier", 100.0, 10);
        p.ajouterAuStock(5);
        assertEquals(15, p.getStock());
    }

    @Test
    void testAjouterAuStockInvalideNegatif() {
        Produit p = new Produit(1, "Clavier", 100.0, 10);
        Exception e = assertThrows(IllegalArgumentException.class, () -> p.ajouterAuStock(-5));
        assertEquals("La quantite doit etre positive", e.getMessage());
    }

    @Test
    void testEstDisponible() {
        Produit p1 = new Produit(1, "Clavier", 100.0, 10);
        assertTrue(p1.estDisponible());
        
        Produit p2 = new Produit(2, "Souris", 50.0, 0);
        assertFalse(p2.estDisponible());
    }
}
