package com.exemple.mvcspringboot.controleur;

import com.exemple.mvcspringboot.dto.ProduitDTO;
import com.exemple.mvcspringboot.dto.ProduitReponseDTO;
import com.exemple.mvcspringboot.service.ProduitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProduitControleur.class)
@DisplayName("Tests du ProduitControleur (Web Layer)")
class ProduitControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProduitService produitService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Doit retourner un status 201 lors de la création")
    void testCreerProduit() throws Exception {
        ProduitDTO requete = new ProduitDTO("Tablette", "Apple iPad", new BigDecimal("400.00"), 5);

        ProduitReponseDTO reponse = new ProduitReponseDTO(
                10L, "Tablette", "Apple iPad", new BigDecimal("400.00"), 5, 
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(produitService.creer(any(ProduitDTO.class))).thenReturn(reponse);

        mockMvc.perform(post("/api/produits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requete)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nom").value("Tablette"));
    }
}
