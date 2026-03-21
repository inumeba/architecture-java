package service;

import exception.metier.ErreurTechniqueException;
import exception.metier.RegleMetierException;
import exception.metier.RessourceNonTrouveeException;
import exception.metier.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProduitServiceTest {

    private ProduitService service;

    @BeforeEach
    void setUp() {
        service = new ProduitService();
    }

    @Test
    void testCreerProduitSucces() throws ValidationException {
        ProduitService.Produit p = service.creerProduit("PC", 500.0, 10);
        assertNotNull(p);
        assertEquals("PC", p.nom());
        assertEquals(500.0, p.prix());
        assertEquals(10, p.stock());
    }

    @Test
    void testCreerProduitNomInvalide() {
        ValidationException e = assertThrows(ValidationException.class, () -> {
            service.creerProduit("", 500.0, 10);
        });
        assertEquals("nom", e.getChamp());
        assertTrue(e.getMessage().contains("vide"));
    }

    @Test
    void testCreerProduitPrixInvalide() {
        ValidationException e = assertThrows(ValidationException.class, () -> {
            service.creerProduit("PC", -50.0, 10);
        });
        assertEquals("prix", e.getChamp());
        assertTrue(e.getMessage().contains("negatif"));
    }

    @Test
    void testTrouverParIdSucces() throws ValidationException, RessourceNonTrouveeException {
        ProduitService.Produit pCree = service.creerProduit("PC", 500.0, 10);
        ProduitService.Produit pTrouve = service.trouverParId(pCree.id());
        
        assertEquals(pCree.id(), pTrouve.id());
    }

    @Test
    void testTrouverParIdEchec() {
        RessourceNonTrouveeException e = assertThrows(RessourceNonTrouveeException.class, () -> {
            service.trouverParId(999);
        });
        assertEquals("Produit", e.getTypeRessource());
        assertEquals(999, e.getIdentifiant());
    }

    @Test
    void testVendreSucces() throws ValidationException, RegleMetierException, RessourceNonTrouveeException {
        ProduitService.Produit p = service.creerProduit("Souris", 25.0, 20);
        ProduitService.Produit maj = service.vendre(p.id(), 5);
        
        assertEquals(15, maj.stock());
    }

    @Test
    void testVendreStockInsuffisant() throws ValidationException {
        ProduitService.Produit p = service.creerProduit("Casque", 80.0, 2);
        
        RegleMetierException e = assertThrows(RegleMetierException.class, () -> {
            service.vendre(p.id(), 5);
        });
        assertEquals("STOCK_INSUFFISANT", e.getRegle());
    }

    @Test
    void testSauvegarderEnBase_DeclencheErreurTechnique() throws ValidationException {
        ProduitService.Produit p = service.creerProduit("Clavier", 40.0, 10);
        
        ErreurTechniqueException e = assertThrows(ErreurTechniqueException.class, () -> {
            service.sauvegarderEnBase(p);
        });
        
        assertNotNull(e.getCause(), "L'exception cause (RuntimeException) doit etre presente pour le chainage.");
        assertTrue(e.getCause().getMessage().contains("Connection refused"));
    }
}
