package com.monprojet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test d'intégration Spring Boot.
 * Permet de s'assurer que notre application complète (Context Spring, Configuration, Beans) 
 * arrive à démarrer sans erreur de dépendances.
 */
@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() {
        // Si la configuration (HexagonalConfig) ou l'injection (UtilisateurRepositoryMock)
        // échoue, ce test plantera au chargement du contexte.
    }
}