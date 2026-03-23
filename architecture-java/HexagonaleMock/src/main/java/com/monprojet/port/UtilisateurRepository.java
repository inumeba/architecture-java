package com.monprojet.port;

import com.monprojet.domaine.Utilisateur;
import java.util.Optional;

/**
 * Port Sortant (Interface).
 * Définit le contrat que le domaine exige pour sauvegarder ou récupérer des données.
 * Le domaine se fiche de savoir si l'implémentation est une vraie BDD ou un Mock.
 */
public interface UtilisateurRepository {
    void sauvegarder(Utilisateur utilisateur);
    Optional<Utilisateur> trouverParId(String id);
}