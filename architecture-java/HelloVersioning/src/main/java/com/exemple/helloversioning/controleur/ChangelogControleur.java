package com.exemple.helloversioning.controleur;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * CONTRÔLEUR — Endpoint changelog
 *
 * Retourne l'historique des versions de l'application.
 * Ajouté dans la branche feature/ajout-changelog
 * pour démontrer le workflow Git Flow.
 */
@RestController
public class ChangelogControleur {

    /**
     * GET /changelog — Historique des versions
     */
    @GetMapping("/changelog")
    public List<Map<String, String>> changelog() {
        return List.of(
            Map.of(
                "version", "1.1.0",
                "date", "2026-03-24",
                "type", "MINOR",
                "description", "Ajout endpoint /changelog pour consulter l'historique des versions"
            ),
            Map.of(
                "version", "1.0.0",
                "date", "2026-03-24",
                "type", "MAJOR",
                "description", "Version initiale — endpoints /, /version, /health"
            )
        );
    }
}
