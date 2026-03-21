package com.banque.acquisition.infrastructure.donnees;

import com.banque.acquisition.infrastructure.donnees.entite.ClientCommercantEntity;
import com.banque.acquisition.infrastructure.donnees.depot.ClientCommercantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Initialiseur de données servant à remplir la base de données mémoire H2 
 * au démarrage de l'application pour remplaçer les bouchons.
 */
@Component
public class InitialiseurDonnees implements CommandLineRunner {
    
    private final ClientCommercantRepository depot;

    public InitialiseurDonnees(ClientCommercantRepository depot) {
        this.depot = depot;
    }

    @Override
    public void run(String... args) {
        // Enregistrement d'un client récent (éligible)
        depot.save(new ClientCommercantEntity("123456789", "Boulangerie Dupont", LocalDate.now().minusMonths(2), false));
        
        // Enregistrement d'un ancien client (non éligible à l'offre récente)
        depot.save(new ClientCommercantEntity("000000000", "Garage Ancien", LocalDate.now().minusYears(2), false));
        
        // Enregistrement d'un client avec incident de paiement (non éligible)
        depot.save(new ClientCommercantEntity("111111111", "StartUp Tech", LocalDate.now().minusMonths(1), true));
        
        System.out.println("Base de données H2 initialisée avec les commerçants de test.");
    }
}
