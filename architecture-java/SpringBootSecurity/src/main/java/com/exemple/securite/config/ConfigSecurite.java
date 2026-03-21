package com.exemple.securite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CONFIGURATION SÉCURITÉ : LA CHAÎNE DE FILTRES (FilterChain)║
 * ╚══════════════════════════════════════════════════════════════╝
 * C'est le cœur de l'architecture Spring Security.
 * Il définit QUELLES URLs sont protégées, COMMENT on s'authentifie,
 * et QUI a le droit d'y accéder (gestion des rôles).
 */
@Configuration
@EnableWebSecurity // Active la sécurité au démarrage de Spring
public class ConfigSecurite {

    /**
     * Configure la chaîne de filtres de sécurité.
     * Tout le trafic HTTP entrant passe par ce "videur".
     */
    @Bean
    public SecurityFilterChain filtreSecurite(HttpSecurity http) throws Exception {
        
        // 1. Configuration des droits d'accès
        http.authorizeHttpRequests(auth -> auth
            // L'API /public est accessible par tout le monde, même non connecté
            .requestMatchers("/api/public/**").permitAll()
            
            // Console H2 accessible pour tout le monde (pour notre environnement de dev)
            .requestMatchers("/h2-console/**").permitAll()
            
            // Seuls les utilisateurs ayant le rôle "ADMIN" peuvent accéder à /api/admin
            // ATTENTION : Spring ajoute implicitement le préfixe "ROLE_" lors de la vérification.
            // Donc hasRole("ADMIN") cherche un utilisateur avec "ROLE_ADMIN".
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            
            // Toute autre requête nécessite d'être au moins connecté
            .anyRequest().authenticated()
        );

        // 2. Type d'authentification : Basic Auth
        // Le client doit envoyer un Header "Authorization: Basic <base64>" avec login/mot de passe.
        // Idéal pour les API REST simples.
        http.httpBasic(Customizer.withDefaults());

        // 3. Configurations techniques requises (pour l'accessibilité de la base H2 en mémoire)
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")); // Désactiver CSRF pour H2
        http.headers(headers -> headers.frameOptions(frame -> frame.disable())); // Permettre les iFrames (H2 console)

        return http.build();
    }

    /**
     * Définit l'algorithme utilisé pour hacher les mots de passe.
     * BCrypt est le standard actuel (hachage lent avec salt généré automatiquement).
     */
    @Bean
    public PasswordEncoder encodeurMotDePasse() {
        return new BCryptPasswordEncoder();
    }
}
