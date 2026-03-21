package interfaces;

import modele.Utilisateur;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidateurUtilisateurTest {

    @Test
    void testValidationPersonnalisee() {
        Utilisateur adulte = new Utilisateur(1, "Alice", 25, true, 100.0);
        Utilisateur mineur = new Utilisateur(2, "Bob", 16, true, 50.0);

        ValidateurUtilisateur estAdulte = u -> u.age() >= 18;

        assertTrue(estAdulte.valider(adulte));
        assertFalse(estAdulte.valider(mineur));
    }

    @Test
    void testCombinaisonValidateurs() {
        Utilisateur cible = new Utilisateur(1, "Alice", 25, true, 100.0);
        Utilisateur inactif = new Utilisateur(2, "Bob", 30, false, 50.0);

        ValidateurUtilisateur estAdulte = u -> u.age() >= 18;
        ValidateurUtilisateur estActif = Utilisateur::estActif;

        ValidateurUtilisateur estAdulteEtActif = estAdulte.et(estActif);

        assertTrue(estAdulteEtActif.valider(cible));
        assertFalse(estAdulteEtActif.valider(inactif));
    }
}
