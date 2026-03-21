package modele;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.stream.Collectors;

class ProduitTest {

    @Test
    void testProduitRecordAttributes() {
        Produit p = new Produit("Ordinateur", "Electronique", 1200.0);
        assertEquals("Ordinateur", p.nom());
        assertEquals("Electronique", p.categorie());
        assertEquals(1200.0, p.prix());
        assertTrue(p.toString().contains("Ordinateur"));
        assertTrue(p.toString().contains("Electronique"));
    }

    @Test
    void testStreamFilterProducts() {
        List<Produit> catalogue = List.of(
            new Produit("Ordinateur", "Electronique", 1200.0),
            new Produit("Souris", "Accessoire", 25.0),
            new Produit("Clavier", "Accessoire", 75.0),
            new Produit("Ecran", "Electronique", 300.0)
        );

        List<Produit> accessoires = catalogue.stream()
            .filter(p -> p.categorie().equals("Accessoire"))
            .collect(Collectors.toList());

        assertEquals(2, accessoires.size());
        assertEquals("Souris", accessoires.get(0).nom());
        assertEquals("Clavier", accessoires.get(1).nom());
    }
}
