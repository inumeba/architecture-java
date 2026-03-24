package com.exemple.helloversioning.controleur;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * CONTRÔLEUR — Endpoints de démonstration pour le versioning
 *
 * Ce contrôleur expose des endpoints simples qui affichent
 * la version actuelle de l'application. Utile pour vérifier
 * quelle version est déployée après un tag Git.
 */
@RestController
public class VersionControleur {

    @Value("${app.version}")
    private String appVersion;

    /**
     * GET / — Page d'accueil avec message de bienvenue
     */
    @GetMapping("/")
    public Map<String, String> accueil() {
        return Map.of(
            "message", "Bienvenue sur Hello Versioning — Git Flow Demo",
            "version", appVersion
        );
    }

    /**
     * GET /version — Retourne les infos de version détaillées
     *
     * Cet endpoint est utile pour vérifier en production
     * quelle version exacte est déployée après un git tag.
     */
    @GetMapping("/version")
    public Map<String, Object> version() {
        return Map.of(
            "application", "hello-versioning",
            "version", appVersion,
            "timestamp", LocalDateTime.now().toString(),
            "java", System.getProperty("java.version")
        );
    }

    /**
     * GET /health — Vérification rapide que l'app est en vie
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
