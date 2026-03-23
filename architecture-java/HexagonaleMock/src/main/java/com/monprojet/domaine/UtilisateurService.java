package com.monprojet.domaine;

import com.monprojet.port.UtilisateurRepository;

/**
 * Service métier contenant la logique principale.
 * Il dépend uniquement de l'interface (Port), jamais de l'implémentation (Adaptateur).
 */
public class UtilisateurService {
    
    // Injection du port (l'abstraction)
    private final UtilisateurRepository repository;

    public UtilisateurService(UtilisateurRepository repository) {
        this.repository = repository;
    }

    /**
     * Règle métier : créer un utilisateur et le sauvegarder.
     */
    public void creerUtilisateur(String id, String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        
        Utilisateur nouvelUtilisateur = new Utilisateur(id, nom);
        // Le service appelle le port, ignorant ce qui se passe derrière
        repository.sauvegarder(nouvelUtilisateur);
        System.out.println("Domaine : L'utilisateur " + nom + " a été traité.");
    }
}