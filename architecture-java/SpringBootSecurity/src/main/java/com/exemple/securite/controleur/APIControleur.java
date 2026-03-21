package com.exemple.securite.controleur;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  CONTRÔLEUR : API DE TEST SÉCURITÉ                           ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Ce contrôleur expose trois points d'accès avec différents
 * niveaux d'exigence en matière de sécurité.
 */
@RestController
@RequestMapping("/api")
public class APIControleur {

    /**
     * Endpoint public : Accessible à TOUS (sans mot de passe)
     * Configuré via `.requestMatchers("/api/public/**").permitAll()`
     */
    @GetMapping("/public/bonjour")
    public String bonjourPublic() {
        return "👋 Bonjour (PUBLIC) ! N'importe qui peut voir ce message.";
    }

    /**
     * Endpoint privé : Accessible à tout utilisateur CONNECTÉ (User ou Admin)
     * Configuré via `.anyRequest().authenticated()`
     * 
     * @param auth L'objet injecté par Spring qui contient les détails de la personne connectée
     */
    @GetMapping("/prive/profil")
    public String profilPrive(Authentication auth) {
        String nomOuIdentifiant = auth.getName();
        String roles = auth.getAuthorities().toString();
        
        return "🔒 Bonjour (PRIVE) ! Vous êtes connecté en tant que '%s' avec les droits %s"
                .formatted(nomOuIdentifiant, roles);
    }

    /**
     * Endpoint administrateur : Accessible UNIQUEMENT aux comptes "ROLE_ADMIN".
     * Configuré via `.requestMatchers("/api/admin/**").hasRole("ADMIN")`
     */
    @GetMapping("/admin/statistiques")
    public String donneesAdmin() {
        return "👑 STATISTIQUES (ADMIN) : Accès top secret réservé aux administrateurs !";
    }
}
