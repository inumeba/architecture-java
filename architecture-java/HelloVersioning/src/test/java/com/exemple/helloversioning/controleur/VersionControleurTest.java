package com.exemple.helloversioning.controleur;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TESTS — Vérification des endpoints de versioning
 *
 * Ces tests garantissent que l'API répond correctement
 * et affiche la bonne version. Ils sont exécutés à chaque
 * build Maven (mvn clean test) dans la pipeline CI/CD.
 */
@SpringBootTest
@AutoConfigureMockMvc
class VersionControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accueil_doitRetournerMessageEtVersion() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.version").value("1.1.0"));
    }

    @Test
    void version_doitRetournerInfosDetaillees() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application").value("hello-versioning"))
                .andExpect(jsonPath("$.version").value("1.1.0"))
                .andExpect(jsonPath("$.java").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void health_doitRetournerStatusUp() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
