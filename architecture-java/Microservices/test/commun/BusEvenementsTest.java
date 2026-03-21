package commun;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BusEvenementsTest {

    private BusEvenements bus;

    @BeforeEach
    void setUp() {
        bus = new BusEvenements();
    }

    @Test
    void testAbonnerEtPublier() {
        List<Message> received = new ArrayList<>();
        
        bus.abonner("TEST_EVENT", message -> {
            received.add(message);
        });
        
        Message m = new Message("TEST_EVENT", "TestService", "Data");
        bus.publier(m);
        
        assertEquals(1, received.size());
        assertEquals("Data", received.get(0).getContenu());
    }

    @Test
    void testPublierSansAbonnes() {
        Message m = new Message("ORPHAN_EVENT", "TestService", "Data");
        
        // This should not throw any exception
        assertDoesNotThrow(() -> {
            bus.publier(m);
        });
    }

    @Test
    void testAbonnementMultiple() {
        List<Message> received1 = new ArrayList<>();
        List<Message> received2 = new ArrayList<>();
        
        bus.abonner("MULTIPLE_EVENT", received1::add);
        bus.abonner("MULTIPLE_EVENT", received2::add);
        
        Message m = new Message("MULTIPLE_EVENT", "TestService", "Data");
        bus.publier(m);
        
        assertEquals(1, received1.size());
        assertEquals(1, received2.size());
        assertEquals(received1.get(0), received2.get(0));
    }
}
