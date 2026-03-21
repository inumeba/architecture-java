package com.banque.acquisition.infrastructure.donnees.entite;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "CLIENT_COMMERCANT")
public class ClientCommercantEntity {
    
    @Id
    private String siren;
    private String raisonSociale;
    private LocalDate dateOuvertureCompte;
    private boolean incidentPaiement;

    public ClientCommercantEntity() {
    }

    public ClientCommercantEntity(String siren, String raisonSociale, LocalDate dateOuvertureCompte, boolean incidentPaiement) {
        this.siren = siren;
        this.raisonSociale = raisonSociale;
        this.dateOuvertureCompte = dateOuvertureCompte;
        this.incidentPaiement = incidentPaiement;
    }

    public String getSiren() {
        return siren;
    }

    public void setSiren(String siren) {
        this.siren = siren;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public LocalDate getDateOuvertureCompte() {
        return dateOuvertureCompte;
    }

    public void setDateOuvertureCompte(LocalDate dateOuvertureCompte) {
        this.dateOuvertureCompte = dateOuvertureCompte;
    }

    public boolean isIncidentPaiement() {
        return incidentPaiement;
    }

    public void setIncidentPaiement(boolean incidentPaiement) {
        this.incidentPaiement = incidentPaiement;
    }
}
