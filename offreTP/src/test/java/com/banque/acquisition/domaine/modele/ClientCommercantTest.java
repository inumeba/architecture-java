package com.banque.acquisition.domaine.modele;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ClientCommercantTest {

    @Test
    void unCommercantInscritRecemmentEstRecent() {
        ClientCommercant client = new ClientCommercant("123", "Boutique", LocalDate.now().minusMonths(2), false);
        assertTrue(client.estRecent(), "Le client devrait être considéré comme récent");
    }

    @Test
    void unCommercantAncienNestPasRecent() {
        ClientCommercant client = new ClientCommercant("123", "Boutique", LocalDate.now().minusYears(1), false);
        assertFalse(client.estRecent(), "Le client ne devrait pas être considéré comme récent");
    }

    @Test
    void unCommercantRecentSansIncidentEstEligibleStandard() {
        ClientCommercant client = new ClientCommercant("123", "Boutique", LocalDate.now().minusMonths(2), false);
        assertTrue(client.estEligibleStandard(), "Le client récent sans incident doit être éligible à l'offre TPE standard");
    }

    @Test
    void unCommercantAvecIncidentNestPasEligibleStandard() {
        ClientCommercant client = new ClientCommercant("123", "Boutique", LocalDate.now().minusMonths(2), true);
        assertFalse(client.estEligibleStandard(), "Le client avec incident ne doit pas être éligible, même s'il est récent");
    }
}
