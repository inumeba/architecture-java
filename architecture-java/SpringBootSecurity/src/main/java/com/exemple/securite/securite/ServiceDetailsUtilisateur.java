package com.exemple.securite.securite;

import com.exemple.securite.depot.UtilisateurDepot;
import com.exemple.securite.modele.Utilisateur;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  SERVICE SÉCURITÉ : ServiceDetailsUtilisateur              ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Spring Security utilise l'interface 'UserDetailsService' lors du login
 * pour aller chercher les informations de l'utilisateur.
 * 
 * On implémente ici la méthode loadUserByUsername pour lui dire de
 * chercher dans notre propre base de données.
 */
@Service
public class ServiceDetailsUtilisateur implements UserDetailsService {

    private final UtilisateurDepot depot;

    public ServiceDetailsUtilisateur(UtilisateurDepot depot) {
        this.depot = depot;
    }

    /**
     * Cette méthode est appelée automatiquement par Spring Security
     * quand quelqu'un essaie de se connecter.
     * 
     * @param username Le nom tapé dans le formulaire ou le client HTTP
     * @return Les détails de l'utilisateur (adaptés pour Spring Security)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Chercher dans la base de données
        Utilisateur utilisateur = depot.findByNomUtilisateur(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));
        
        // 2. Transformer notre 'Utilisateur' en 'UserDetails' (Pattern adapter)
        return new DetailsUtilisateurImpl(utilisateur);
    }
}
