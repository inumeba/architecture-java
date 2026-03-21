# Architecture Microservices en Java — Exemple commenté en français

## Qu'est-ce que l'architecture Microservices ?

L'architecture **Microservices** décompose une application en **petits services autonomes**, chacun responsable d'une fonctionnalité métier précise. Chaque service :
- A sa **propre base de données**
- Peut être **déployé indépendamment**
- Communique avec les autres via des **messages** ou des **API**
- Peut être écrit dans un **langage différent**

## Schéma de fonctionnement

```
                        ┌──────────────────┐
                        │    CLIENT        │
                        │  (Navigateur,    │
                        │   App mobile)    │
                        └────────┬─────────┘
                                 │
                                 ▼
                    ┌────────────────────────┐
                    │    PASSERELLE API       │
                    │    (API Gateway)        │
                    │                        │
                    │  - Routage             │
                    │  - Agrégation          │
                    │  - Sécurité            │
                    └───┬───────────────┬────┘
                        │               │
            ┌───────────┘               └───────────┐
            ▼                                       ▼
┌───────────────────────┐           ┌───────────────────────┐
│  SERVICE PRODUIT      │           │  SERVICE COMMANDE     │
│                       │           │                       │
│  - Catalogue          │           │  - Prise de commande  │
│  - Stock              │           │  - Suivi des statuts  │
│  - Vérification       │           │  - Validation         │
│                       │           │                       │
│  [Base de données     │           │  [Base de données     │
│   propre]             │           │   propre]             │
└───────────┬───────────┘           └───────────┬───────────┘
            │                                   │
            └──────────┐       ┌────────────────┘
                       ▼       ▼
              ┌──────────────────────┐
              │  BUS D'ÉVÉNEMENTS    │
              │  (Message Broker)    │
              │                      │
              │  Kafka / RabbitMQ    │
              └──────────────────────┘
```

### Flux d'une commande

```
1. Le CLIENT envoie une requête à la PASSERELLE      → POST /commandes
2. La PASSERELLE redirige vers le SERVICE COMMANDE    → passerCommande(idProduit, quantité)
3. Le SERVICE COMMANDE crée la commande (EN_ATTENTE)
4. Le SERVICE COMMANDE publie "VERIFIER_PRODUIT"      → via le BUS
5. Le SERVICE PRODUIT reçoit le message et vérifie
6. Le SERVICE PRODUIT publie "PRODUIT_VERIFIE"         → via le BUS
7. Le SERVICE COMMANDE reçoit la réponse → VALIDÉE
8. Le SERVICE COMMANDE publie "COMMANDE_VALIDEE"       → via le BUS
9. Le SERVICE PRODUIT reçoit et diminue le stock
```

## Structure du projet

```
Microservices/
├── src/
│   ├── commun/                          # INFRASTRUCTURE PARTAGÉE
│   │   ├── Message.java                 # Message de communication inter-services
│   │   └── BusEvenements.java           # Bus d'événements (simule Kafka/RabbitMQ)
│   │
│   ├── service_produit/                 # MICROSERVICE PRODUIT (autonome)
│   │   ├── Produit.java                 # Entité propre au service
│   │   ├── ProduitDepot.java            # Base de données locale
│   │   └── ServiceProduit.java          # Logique métier + abonnements
│   │
│   ├── service_commande/                # MICROSERVICE COMMANDE (autonome)
│   │   ├── Commande.java                # Entité propre au service
│   │   ├── CommandeDepot.java           # Base de données locale
│   │   └── ServiceCommande.java         # Logique métier + abonnements
│   │
│   ├── passerelle/                      # API GATEWAY
│   │   └── Passerelle.java              # Point d'entrée unique pour les clients
│   │
│   └── Application.java                 # Démonstration (assemblage et simulation)
│
├── out/                                  # Classes compilées
└── README.md                             # Ce fichier
```

## Description détaillée de chaque composant

### 1. Infrastructure — `commun/`

#### `Message.java` — Unité de communication
Les services ne s'appellent pas directement. Ils échangent des **messages** via le bus :
- **type** — identifie la nature du message (ex: `VERIFIER_PRODUIT`, `COMMANDE_VALIDEE`)
- **expediteur** — quel service a envoyé le message
- **contenu** — les données transportées (en production : JSON ou Protobuf)

#### `BusEvenements.java` — Système nerveux (Event Bus)
Le bus implémente le pattern **Publish/Subscribe** (Pub/Sub) :
- Un service **publie** un message sur le bus
- Les services intéressés **s'abonnent** à un type de message
- Le bus **distribue** à tous les abonnés

En production : Apache Kafka, RabbitMQ, Redis Pub/Sub, AWS EventBridge.

### 2. Service Produit — `service_produit/`

Microservice **autonome** responsable du catalogue :
- `Produit.java` — entité propre (id, nom, prix, stock)
- `ProduitDepot.java` — base de données locale (Database per Service)
- `ServiceProduit.java` — logique métier :
  - Gère le catalogue (CRUD)
  - S'abonne à `VERIFIER_PRODUIT` → vérifie la disponibilité
  - S'abonne à `COMMANDE_VALIDEE` → diminue le stock
  - Publie `PRODUIT_VERIFIE` ou `PRODUIT_INDISPONIBLE`

### 3. Service Commande — `service_commande/`

Microservice **autonome** responsable des commandes :
- `Commande.java` — entité propre avec cycle de vie (EN_ATTENTE → VALIDÉE / REFUSÉE)
- `CommandeDepot.java` — base de données locale, séparée
- `ServiceCommande.java` — logique métier :
  - Crée des commandes en état EN_ATTENTE
  - Publie `VERIFIER_PRODUIT` pour demander au service Produit
  - S'abonne à `PRODUIT_VERIFIE` → valide la commande
  - S'abonne à `PRODUIT_INDISPONIBLE` → refuse la commande
  - Publie `COMMANDE_VALIDEE` pour notifier la mise à jour du stock

### 4. Passerelle — `passerelle/`

**Point d'entrée unique** pour les clients externes :
- **Routage** : redirige `/produits` vers ServiceProduit, `/commandes` vers ServiceCommande
- **Agrégation** : `/resume` combine les données de plusieurs services en une réponse
- En production : Spring Cloud Gateway, Kong, Nginx, AWS API Gateway

### 5. Application — Point d'entrée

Assemble tous les composants et simule des requêtes client pour montrer le flux complet de communication entre les services.

## Concepts clés illustrés

| Concept | Description | Où dans le code |
|---------|-------------|-----------------|
| **Database per Service** | Chaque service a sa propre base de données | `ProduitDepot` et `CommandeDepot` séparés |
| **Event-Driven Communication** | Communication asynchrone via événements | `BusEvenements` + `Message` |
| **Publish/Subscribe** | Les services publient et s'abonnent sans se connaître | `bus.publier()` / `bus.abonner()` |
| **API Gateway** | Point d'entrée unique pour les clients | `Passerelle.java` |
| **Bounded Context (DDD)** | Chaque service définit ses propres entités | `Produit` ≠ `Commande` (classes séparées) |
| **Loose Coupling** | Les services ne dépendent pas les uns des autres | Communication uniquement via le bus |
| **Autonomie** | Chaque service gère son propre cycle de vie | Démarrage et initialisation indépendants |

## Comment compiler et exécuter

### Prérequis
- **Java 17** ou supérieur (`java --version` pour vérifier)

### Compilation
```bash
cd architecture-java/Microservices
javac -d out src/commun/*.java src/service_produit/*.java src/service_commande/*.java src/passerelle/*.java src/Application.java
```

### Exécution
```bash
java -cp out Application
```

## Avantages

- **Déploiement indépendant** : mettre à jour un service sans redéployer toute l'application
- **Scalabilité ciblée** : scaler uniquement le service surchargé (ex: 10 instances du service Commande)
- **Résilience** : si un service tombe, les autres continuent de fonctionner
- **Liberté technologique** : chaque service peut utiliser un langage/framework différent
- **Équipes autonomes** : une équipe par service, développement en parallèle
- **Évolutivité** : ajouter un nouveau service sans modifier les existants

## Inconvénients

- **Complexité opérationnelle** : déployer et monitorer N services au lieu d'un seul
- **Communication réseau** : latence, pannes réseau, sérialisation/désérialisation
- **Cohérence des données** : pas de transactions ACID entre services (cohérence éventuelle)
- **Débogage difficile** : une requête traverse plusieurs services (besoin de tracing distribué)
- **Duplication de code** : chaque service peut redéfinir des entités similaires
- **Surcharge pour les petits projets** : un monolithe est souvent plus adapté pour commencer

## Frameworks courants

| Framework / Outil | Rôle |
|-------------------|------|
| **Spring Boot** | Créer chaque microservice (serveur embarqué, API REST) |
| **Spring Cloud** | Écosystème microservices (Gateway, Config, Discovery) |
| **Apache Kafka** | Bus d'événements / streaming distribué |
| **RabbitMQ** | File de messages (Message Queue) |
| **Eureka / Consul** | Service Discovery (registre de services) |
| **Docker** | Conteneurisation de chaque service |
| **Kubernetes** | Orchestration des conteneurs (déploiement, scaling) |
| **Zipkin / Jaeger** | Tracing distribué (suivre une requête entre services) |
| **gRPC** | Communication synchrone haute performance entre services |

## Pour aller plus loin

D'autres architectures Java à explorer :
- **Architecture en couches** (Layered / N-Tier) — plus simple, idéale pour débuter
- **Architecture hexagonale** (Ports & Adapters) — isoler la logique métier
- **CQRS** (Command Query Responsibility Segregation) — séparer lecture et écriture
- **Event-Driven** — basée entièrement sur les événements (Event Sourcing)
