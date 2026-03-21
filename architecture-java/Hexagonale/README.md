# Architecture Hexagonale en Java — Exemple commenté en français

## Qu'est-ce que l'architecture hexagonale ?

L'architecture **hexagonale** (aussi appelée **Ports & Adapters**), inventée par Alistair Cockburn, isole la **logique métier** (le domaine) au centre de l'application. Le domaine ne dépend de RIEN d'extérieur : ni base de données, ni framework, ni interface utilisateur.

Le domaine communique avec l'extérieur uniquement via des **ports** (interfaces) et des **adaptateurs** (implémentations concrètes).

## Schéma de fonctionnement

```
                    ADAPTATEURS ENTRANTS
                  (points d'entrée externes)

              ┌──────────────┐  ┌──────────────┐
              │  API REST    │  │    CLI       │
              │ (Controller) │  │ (Terminal)   │
              └──────┬───────┘  └──────┬───────┘
                     │                 │
                     ▼                 ▼
              ┌─────────────────────────────┐
              │      PORTS ENTRANTS         │
              │   (Interfaces "je peux")    │
              │   GestionProduitPort        │
              ├─────────────────────────────┤
              │                             │
              │     ┌─────────────────┐     │
              │     │    DOMAINE      │     │
              │     │                 │     │
              │     │  Entités        │     │
              │     │  (Produit)      │     │
              │     │                 │     │
              │     │  Use Cases      │     │
              │     │  (Gestion       │     │
              │     │   Produit)      │     │
              │     └─────────────────┘     │
              │                             │
              ├─────────────────────────────┤
              │      PORTS SORTANTS         │
              │   (Interfaces "j'ai besoin")│
              │   ProduitRepositoryPort     │
              │   NotificationPort          │
              └──────┬───────────────┬──────┘
                     │               │
                     ▼               ▼
              ┌──────────────┐  ┌──────────────┐
              │  Mémoire     │  │  Console     │
              │ (Repository) │  │ (Notifs)     │
              └──────────────┘  └──────────────┘

                    ADAPTATEURS SORTANTS
                (ressources techniques externes)
```

### Direction des dépendances (CRUCIAL)

```
[Adaptateur Entrant] ──→ [Port Entrant] ──→ [Domaine] ←── [Port Sortant] ←── [Adaptateur Sortant]

     Dépend de →                                         ← Implémente

Le domaine NE DÉPEND DE RIEN.
Tout le monde dépend du domaine, pas l'inverse.
```

## Structure du projet

```
Hexagonale/
├── src/
│   ├── domaine/                           # L'HEXAGONE CENTRAL (pur, isolé)
│   │   ├── modele/
│   │   │   └── Produit.java              # Entité métier + règles
│   │   ├── port/
│   │   │   ├── entrant/
│   │   │   │   └── GestionProduitPort.java  # "Ce que je peux faire"
│   │   │   └── sortant/
│   │   │       ├── ProduitRepositoryPort.java  # "J'ai besoin de persistance"
│   │   │       └── NotificationPort.java       # "J'ai besoin de notifier"
│   │   └── usecase/
│   │       └── GestionProduitUseCase.java # Orchestrateur (implémente le port entrant)
│   │
│   ├── adaptateur/                        # LE MONDE EXTÉRIEUR
│   │   ├── entrant/                       # Points d'entrée
│   │   │   ├── ApiRestAdaptateur.java     # Simule une API HTTP REST
│   │   │   └── CliAdaptateur.java         # Simule une ligne de commande
│   │   └── sortant/                       # Ressources techniques
│   │       ├── ProduitMemoireAdaptateur.java     # Persistance en mémoire
│   │       └── NotificationConsoleAdaptateur.java # Notifications en console
│   │
│   └── Application.java                  # Assemblage + démonstration
│
├── out/                                    # Classes compilées
└── README.md                               # Ce fichier
```

## Description détaillée de chaque composant

### 1. Domaine — L'hexagone central

Le domaine est le **cœur** de l'application. Il ne dépend d'aucun framework, d'aucune base de données, d'aucune technologie externe.

#### `modele/Produit.java` — Entité métier
Contient les **règles métier** pures :
- Un prix ne peut pas être négatif
- Le stock ne peut pas descendre en dessous de zéro
- Une remise ne peut pas dépasser 50%

Ces règles sont dans l'entité, PAS dans le contrôleur ni dans la base de données.

#### `port/entrant/GestionProduitPort.java` — "Ce que je peux faire"
Interface qui définit les **actions possibles** :
- Créer un produit, lister, chercher, appliquer une remise, vendre
- Implémentée par le **Use Case**
- Appelée par les **adaptateurs entrants**

#### `port/sortant/ProduitRepositoryPort.java` — "J'ai besoin de persistance"
Interface qui définit les **besoins** du domaine :
- Sauvegarder, trouver, supprimer un produit
- **Définie** par le domaine, **implémentée** par un adaptateur sortant
- Le domaine dit "j'ai besoin de stocker", sans savoir comment

#### `port/sortant/NotificationPort.java` — "J'ai besoin de notifier"
Deuxième port sortant pour alertes et notifications :
- Alerter quand le stock est bas
- Notifier les ventes

#### `usecase/GestionProduitUseCase.java` — Orchestrateur
Le use case contient la **logique applicative** (pas métier !) :
- "Quand on vend un produit : retirer du stock → sauvegarder → notifier → alerter si stock bas"
- Il coordonne les entités et les ports sortants

### 2. Adaptateurs entrants — Points d'entrée

#### `ApiRestAdaptateur.java` — API REST simulée
Traduit les requêtes HTTP en appels au port entrant :
- `GET /api/produits` → `gestionProduit.listerProduits()`
- `POST /api/produits/{id}/vente` → `gestionProduit.vendre(id, qte)`

#### `CliAdaptateur.java` — Ligne de commande
Deuxième point d'entrée, même domaine :
- `$ catalogue --lister` → `gestionProduit.listerProduits()`
- Les deux adaptateurs partagent la **même logique métier**

### 3. Adaptateurs sortants — Ressources techniques

#### `ProduitMemoireAdaptateur.java` — Persistance en mémoire
Implémente `ProduitRepositoryPort` avec une `ArrayList` :
- En production : `ProduitPostgresAdaptateur`, `ProduitMongoAdaptateur`...
- Aucun changement dans le domaine pour changer de base de données

#### `NotificationConsoleAdaptateur.java` — Notification console
Implémente `NotificationPort` en affichant dans la console :
- En production : `NotificationEmailAdaptateur`, `NotificationSlackAdaptateur`...

## Concepts clés illustrés

| Concept | Description | Où dans le code |
|---------|-------------|-----------------|
| **Inversion de dépendances** | Le domaine définit les interfaces, les adaptateurs les implémentent | Ports sortants (interfaces) dans `domaine/`, implémentations dans `adaptateur/` |
| **Port entrant** | "Ce que l'application peut faire" | `GestionProduitPort` |
| **Port sortant** | "Ce dont l'application a besoin" | `ProduitRepositoryPort`, `NotificationPort` |
| **Adaptateur entrant** | Traduit les requêtes externes vers le domaine | `ApiRestAdaptateur`, `CliAdaptateur` |
| **Adaptateur sortant** | Fournit les ressources techniques au domaine | `ProduitMemoireAdaptateur`, `NotificationConsoleAdaptateur` |
| **Isolation du domaine** | Le domaine ne dépend d'aucune technologie | Aucun import de framework dans `domaine/` |
| **Logique métier vs applicative** | Règles métier dans l'entité, orchestration dans le use case | `Produit.appliquerRemise()` vs `GestionProduitUseCase.vendre()` |

## Comment compiler et exécuter

### Prérequis
- **Java 17** ou supérieur (`java --version` pour vérifier)

### Compilation
```bash
cd architecture-java/Hexagonale
javac -d out src/domaine/modele/*.java src/domaine/port/entrant/*.java src/domaine/port/sortant/*.java src/domaine/usecase/*.java src/adaptateur/entrant/*.java src/adaptateur/sortant/*.java src/Application.java
```

### Exécution
```bash
java -cp out Application
```

## Avantages

- **Domaine isolé et testable** : la logique métier ne dépend d'aucun framework → tests unitaires purs
- **Interchangeabilité** : changer de base de données = créer un nouvel adaptateur, rien d'autre
- **Multi-entrée** : le même domaine accessible via REST, CLI, GraphQL, WebSocket, tests...
- **Longévité du code** : les frameworks changent, le domaine reste stable
- **Clarté des responsabilités** : chaque couche a un rôle bien défini
- **Facilité de test** : injecter des mocks pour les ports sortants, tester le domaine en isolation

## Inconvénients

- **Beaucoup d'interfaces** : chaque interaction avec l'extérieur nécessite un port + un adaptateur
- **Complexité initiale** : la structure est plus lourde que MVC pour des projets simples
- **Indirection** : il faut naviguer entre port → use case → entité pour comprendre un flux
- **Surcharge pour les CRUD simples** : si l'application ne fait que lire/écrire sans logique métier
- **Courbe d'apprentissage** : les développeurs juniors peuvent être déroutés par l'inversion de dépendances

## Quand utiliser l'hexagonale ?

| Situation | Recommandation |
|-----------|----------------|
| Logique métier complexe et évolutive | **OUI** — c'est fait pour ça |
| Application longue durée (> 2 ans) | **OUI** — protège contre les changements techniques |
| Simple CRUD sans logique métier | **NON** — MVC ou en couches suffisent |
| Prototype / MVP rapide | **NON** — trop de structure pour un prototype |
| Équipe expérimentée | **OUI** — ils apprécieront la clarté |

## Frameworks courants

| Framework / Outil | Rôle dans l'hexagonale |
|-------------------|------------------------|
| **Spring Boot** | Assemblage automatique (@Component, @Autowired) |
| **Spring Data JPA** | Adaptateur sortant de persistance |
| **Spring Web MVC** | Adaptateur entrant REST (@RestController) |
| **Quarkus** | Alternative à Spring avec architecture hexagonale native |
| **ArchUnit** | Vérifier que les dépendances respectent l'hexagone (tests d'architecture) |
| **Mockito** | Mocker les ports sortants pour tester le domaine en isolation |

## Pour aller plus loin

D'autres architectures Java à explorer :
- **MVC** — plus simple, bonne introduction
- **En couches** (Layered / N-Tier) — étape intermédiaire avant l'hexagonale
- **CQRS** — séparer lecture et écriture (souvent combiné avec l'hexagonale)
- **Clean Architecture** — similaire, formalisée par Robert C. Martin (Uncle Bob)
