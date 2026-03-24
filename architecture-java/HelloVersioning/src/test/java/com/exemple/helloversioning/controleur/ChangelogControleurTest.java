package com.exemple.helloversioning.controleur;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChangelogControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void changelog_doitRetournerListeDesVersions() throws Exception {
        mockMvc.perform(get("/changelog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].version").value("1.1.0"))
                .andExpect(jsonPath("$[0].type").value("MINOR"))
                .andExpect(jsonPath("$[1].version").value("1.0.0"))
                .andExpect(jsonPath("$[1].type").value("MAJOR"));
    }
}
