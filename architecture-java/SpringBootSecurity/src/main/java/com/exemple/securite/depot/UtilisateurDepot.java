package com.exemple.securite.depot;

import com.exemple.securite.modele.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  DÉPÔT : COMMUNICATION AVEC LA BASE DE DONNÉES             ║
 * ╚══════════════════════════════════════════════════════════════╝
 * Permet de chercher les utilisateurs en base de données.
 * Cette interface sera utilisée par Spring Security pour vérifier
 * si le compte existe lors de la connexion.
 */
public interface UtilisateurDepot extends JpaRepository<Utilisateur, Long> {
    
    /**
     * Recherche un utilisateur selon son nom de compte.
     * @param nomUtilisateur le login tapé par l'utilisateur
     * @return L'utilisateur s'il existe (Optional)
     */
    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);
}
