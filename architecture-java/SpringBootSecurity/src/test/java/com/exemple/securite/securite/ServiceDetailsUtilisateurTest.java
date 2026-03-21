package com.exemple.securite.securite;

import com.exemple.securite.depot.UtilisateurDepot;
import com.exemple.securite.modele.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceDetailsUtilisateurTest {

    @Mock
    private UtilisateurDepot depot;

    @InjectMocks
    private ServiceDetailsUtilisateur service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UtilisateurTrouve() {
        Utilisateur u = new Utilisateur("admin", "password", "ROLE_ADMIN");
        ReflectionTestUtils.setField(u, "id", 1L);

        when(depot.findByNomUtilisateur("admin")).thenReturn(Optional.of(u));

        UserDetails userDetails = service.loadUserByUsername("admin");

        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        
        verify(depot, times(1)).findByNomUtilisateur("admin");
    }

    @Test
    void testLoadUserByUsername_UtilisateurNonTrouve() {
        when(depot.findByNomUtilisateur("inconnu")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("inconnu");
        });

        assertTrue(exception.getMessage().contains("inconnu"));
        verify(depot, times(1)).findByNomUtilisateur("inconnu");
    }
}
