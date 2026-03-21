package com.banque.acquisition.infrastructure.donnees.depot;

import com.banque.acquisition.infrastructure.donnees.entite.ClientCommercantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface Spring Data JPA permettant de communiquer avec la base de données 
 * pour interroger la table des Clients Commerçants.
 */
@Repository
public interface ClientCommercantRepository extends JpaRepository<ClientCommercantEntity, String> {
}
