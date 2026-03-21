package com.exemple.securite.controleur;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class APIControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAccesPublicSansAuth() throws Exception {
        mockMvc.perform(get("/api/public/bonjour"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Bonjour (PUBLIC)")));
    }

    @Test
    void testAccesPriveRejeteSansAuth() throws Exception {
        mockMvc.perform(get("/api/prive/profil"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "bob", roles = {"USER"})
    void testAccesPriveAuthentifieEnTantQueUser() throws Exception {
        mockMvc.perform(get("/api/prive/profil"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Bonjour (PRIVE)")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("'bob'")));
    }

    @Test
    @WithMockUser(username = "bob", roles = {"USER"})
    void testAccesAdminRejeteEnTantQueUser() throws Exception {
        mockMvc.perform(get("/api/admin/statistiques"))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @WithMockUser(username = "alice", roles = {"ADMIN"})
    void testAccesAdminAutoriseEnTantQueAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/statistiques"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("STATISTIQUES (ADMIN)")));
    }

    @Test
    @WithMockUser(username = "alice", roles = {"ADMIN"})
    void testAccesPriveEnTantQueAdmin() throws Exception {
        mockMvc.perform(get("/api/prive/profil"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Bonjour (PRIVE)")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("'alice'")));
    }
}
