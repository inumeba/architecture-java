package com.banque.acquisition.infrastructure.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // L'API REST et la console H2 sont accessibles librement (pour l'exemple)
                .requestMatchers("/api/**", "/h2-console/**").permitAll()
                // Les parties /agence (UI Conseiller bancaire) exigent d'être authentifié en tant que CONSEILLER
                .requestMatchers("/agence/**").hasRole("CONSEILLER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .permitAll() // Active un formulaire de login basique fourni par Spring
            )
            .logout(logout -> logout
                .permitAll()
            );

        // Désactive la protection CSRF et Header Frame Options spécifiquement pour la console H2
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/h2-console/**"));
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Mock de conseillers dans la mémoire pour les tests de l'application
        UserDetails conseiller = User.withDefaultPasswordEncoder()
            .username("conseiller")
            .password("banque123")
            .roles("CONSEILLER")
            .build();

        return new InMemoryUserDetailsManager(conseiller);
    }
}
