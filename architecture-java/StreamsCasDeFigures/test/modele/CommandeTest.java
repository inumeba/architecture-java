package modele;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class CommandeTest {

    @Test
    void testCalculTotalCommande() {
        Produit p1 = new Produit("Souris", "Accessoire", 25.0);
        Produit p2 = new Produit("Clavier", "Accessoire", 75.0);
        Commande cmd = new Commande("CMD-001", "Alice", List.of(p1, p2), true);
        
        assertEquals(100.0, cmd.getTotal());
        assertEquals("CMD-001", cmd.id());
        assertEquals("Alice", cmd.nomClient());
        assertTrue(cmd.estLivree());
        
        // Assert containsString pattern for unicode / characters
        assertTrue(cmd.toString().contains("CMD-001"));
        assertTrue(cmd.toString().contains("Alice"));
    }
}
