package com.monprojet.config;

import com.monprojet.domaine.UtilisateurService;
import com.monprojet.port.UtilisateurRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fichier de configuration Spring.
 * 
 * L'une des règles d'or de l'architecture hexagonale est que le Domaine métier
 * ne doit dépendre d'AUCUN framework technique, pas même Spring.
 * 
 * Nous ne mettons donc pas d'annotation @Service sur la classe UtilisateurService.
 * Au lieu de cela, nous configurons son Bean ici pour le rendre disponible dans Spring.
 */
@Configuration
public class HexagonalConfig {

    @Bean
    public UtilisateurService utilisateurService(UtilisateurRepository utilisateurRepository) {
        // Câblage manuel isolé dans la configuration Spring
        return new UtilisateurService(utilisateurRepository);
    }
}