package com.monprojet.adaptateur;

import com.monprojet.domaine.Utilisateur;
import com.monprojet.port.UtilisateurRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Adaptateur Secondaire : Le Service Mock.
 * Cette classe implémente le Port pour simuler le comportement d'une base de données.
 * Utile pour les tests unitaires ou pour démarrer le développement frontend
 * avant que la vraie base de données ne soit prête.
 *
 * (Avec Spring Boot, on ajoute @Repository pour qu'il soit détecté comme Bean)
 */
@Repository
public class UtilisateurRepositoryMock implements UtilisateurRepository {
    
    // Utilisation d'une structure en mémoire pour simuler le stockage
    private final Map<String, Utilisateur> baseDeDonneesEnMemoire = new HashMap<>();

    @Override
    public void sauvegarder(Utilisateur utilisateur) {
        baseDeDonneesEnMemoire.put(utilisateur.getId(), utilisateur);
        System.out.println("[MOCK DB par Spring] Sauvegarde en mémoire de l'utilisateur : " + utilisateur.getNom());
    }

    @Override
    public Optional<Utilisateur> trouverParId(String id) {
        System.out.println("[MOCK DB] Recherche de l'utilisateur avec l'ID : " + id);
        return Optional.ofNullable(baseDeDonneesEnMemoire.get(id));
    }
}