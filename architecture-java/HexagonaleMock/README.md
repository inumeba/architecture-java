# 🏗️ Architecture : Hexagonale (Ports & Adapters) avec Mock Service & Spring Boot

L'architecture hexagonale permet d'isoler le domaine métier du reste du système (bases de données, interfaces utilisateur, API externes) via des **Ports** (interfaces) et des **Adaptateurs** (implémentations). 

Ce projet illustre la création d'un **Adaptateur Mock** (un mock service en mémoire) branché à une application **Spring Boot**. Cela permet de tester ou développer la logique métier de manière totalement isolée, sans avoir besoin d'une vraie base de données.

## 🚀 Comment lancer le projet

Le projet utilise Maven et Spring Boot. Ouvrez un terminal dans le dossier `HexagonaleMock` et exécutez :

```bash
mvn spring-boot:run
```

Vous verrez dans les logs (console) le service métier s'exécuter et faire appel au mock injecté par Spring.

## 🧪 Comment lancer les tests

L'architecture hexagonale brille particulièrement lors des tests. Ce projet comprend deux types de tests :
1. **Tests unitaires (Domaine)** : Valident la logique métier sans charger Spring (ultra-rapides).
2. **Tests d'intégration (Application)** : Valident que Spring arrive bien à injecter toutes les dépendances.

Pour exécuter tous les tests :
```bash
mvn test
```

## 📊 Schéma

```mermaid
graph TD
    subgraph "Adaptateurs Primaires (Entrantes)"
        API[Spring Boot CLI / REST]
    end

    subgraph "Hexagone (Cœur de Métier)"
        Service[UtilisateurService<br/>(Logique métier pure)]
        Port[UtilisateurRepository<br/>(Port Sortant / Interface)]
        Entite[Utilisateur<br/>(Entité)]
        
        API -->|Utilise| Service
        Service -->|Manipule| Entite
        Service -->|Appelle| Port
    end

    subgraph "Adaptateurs Secondaires (Sortantes)"
        Mock[UtilisateurRepositoryMock<br/>(Mock avec @Repository)]
        DB[(Vraie Base de Données<br/>PostgreSQL / MySQL)]
        
        Port <|-- Mock
        Port <|-- DB
    end
    
    style Mock fill:#bbf,stroke:#333,stroke-width:2px;
```

## 📂 Structure du projet

```text
HexagonaleMock/
 ├── pom.xml                                     # Dépendances Maven (Spring Boot)
 └── src/
      ├── main/java/com/monprojet/
      │    ├── domaine/
      │    │    ├── Utilisateur.java             # Entité métier (Sans Spring)
      │    │    └── UtilisateurService.java      # Logique métier (Sans Spring)
      │    ├── port/
      │    │    └── UtilisateurRepository.java   # Interface (Le contrat)
      │    ├── adaptateur/
      │    │    └── UtilisateurRepositoryMock.java # Mock en mémoire (@Repository Spring)
      │    ├── config/
      │    │    └── HexagonalConfig.java         # Câblage via @Bean pour protéger le domaine
      │    └── Application.java                  # Point d'entrée (@SpringBootApplication)
      │
      └── test/java/com/monprojet/
           ├── domaine/
           │    └── UtilisateurServiceTest.java  # Test ultra-rapide avec le Mock sans Spring
           └── ApplicationTests.java             # Test de chargement du contexte Spring
```

## ✅ Avantages du Mock Service couplé à cette architecture

- **Développement parallèle** : Le frontend ou le métier peut avancer pendant que l'équipe Base de Données configure PostgreSQL.
- **Tests ultra-rapides** : L'exécution du test du domaine (`UtilisateurServiceTest`) prend quelques millisecondes.
- **Isolation du Framework** : Spring est confiné dans l'`Application.java`, dans `HexagonalConfig.java` et dans les *Adaptateurs*. Le domaine n'importe **jamais** de classes `org.springframework.*`.

## ❌ Inconvénients

- **Illusion de sécurité** : Un mock ne reproduit pas les comportements réels (latence réseau, verrous transactionnels, contraintes SQL). Il faudra de vrais tests d'intégration avec Testcontainers plus tard.
- **Maintenance** : Si la vraie base de données modifie son comportement, le Mock doit être mis à jour manuellement pour refléter cette réalité.
