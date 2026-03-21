package com.exemple.securite.securite;

import com.exemple.securite.modele.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  ADAPTATEUR : DetailsUtilisateurImpl                       ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Spring Security ne comprend pas notre classe personnalisée 'Utilisateur'.
 * Il s'attend à recevoir une interface 'UserDetails'.
 * 
 * Cette classe agit comme un ADAPTATEUR (Design Pattern Adapter)
 * entre notre entité métier et l'interface de sécurité de Spring.
 */
public class DetailsUtilisateurImpl implements UserDetails {

    private final Utilisateur utilisateur;

    public DetailsUtilisateurImpl(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    /**
     * Convertit le champ "role" de notre entité en "GrantedAuthority" compris par Spring.
     * Exemple : Si role="ROLE_USER", il crée une autorité "ROLE_USER".
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(utilisateur.getRole()));
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();
    }

    @Override
    public String getUsername() {
        return utilisateur.getNomUtilisateur();
    }

    // ─── GESTION DU CYCLE DE VIE DU COMPTE ───
    // Pour simplifier l'exemple, tous les comptes sont considérés actifs et valides.

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
