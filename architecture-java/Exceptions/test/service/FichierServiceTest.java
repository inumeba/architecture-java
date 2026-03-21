package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FichierServiceTest {

    private FichierService service;

    @BeforeEach
    void setUp() {
        service = new FichierService();
    }

    @Test
    void testLireFichier() throws IOException {
        File tempFile = File.createTempFile("test_lecture", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Hello World\nLine 2");
        }

        String contenu = service.lireFichier(tempFile.getAbsolutePath());
        assertTrue(contenu.contains("Hello World"));
        assertTrue(contenu.contains("Line 2"));
    }

    @Test
    void testDemonstrerExceptionSupprimee() {
        Exception e = assertThrows(Exception.class, () -> {
            service.demonstrerExceptionSupprimee();
        });

        assertEquals("Erreur dans faireQuelqueChose()", e.getMessage());
        assertEquals(1, e.getSuppressed().length, "Il doit y avoir une exception supprimee");
        assertEquals("Erreur dans close()", e.getSuppressed()[0].getMessage());
    }
}
