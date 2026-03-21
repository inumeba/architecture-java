package creation.singleton;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaseDeDonneesTest {

    @Test
    void testSingletonInstanceUnique() {
        BaseDeDonnees instance1 = BaseDeDonnees.getInstance();
        BaseDeDonnees instance2 = BaseDeDonnees.getInstance();
        
        assertSame(instance1, instance2, "Les deux references doivent pointer vers la meme instance");
    }

    @Test
    void testEstConnectee() {
        BaseDeDonnees instance = BaseDeDonnees.getInstance();
        assertTrue(instance.estConnectee(), "La base de donnees doit etre connectee des la creation");
    }
    
    @Test
    void testExecuterRequete() {
        BaseDeDonnees instance = BaseDeDonnees.getInstance();
        int initialCount = instance.getNombreRequetes();
        
        String resultat = instance.executerRequete("SELECT 1");
        
        assertTrue(resultat.contains("SELECT 1"));
        assertEquals(initialCount + 1, instance.getNombreRequetes());
    }
}
