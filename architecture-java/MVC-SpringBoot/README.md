# MVC Spring Boot + Hibernate

## Vue d'ensemble

Exemple complet d'architecture **MVC** (Model-View-Controller) avec :
- **Spring Boot 3.2** — Framework web avec serveur Tomcat embarqué
- **Hibernate / JPA** — ORM pour le mapping objet-relationnel
- **Spring Data JPA** — Génération automatique des requêtes SQL
- **Bean Validation** — Validation des entrées avec annotations
- **H2** — Base de données en mémoire pour le développement

## Schéma de l'architecture

```
 Client (Postman / navigateur)
       │
       │ HTTP + JSON
       ▼
┌─────────────────────────┐
│  CONTRÔLEUR             │  @RestController
│  ProduitControleur      │  → Reçoit HTTP, valide, délègue
└───────────┬─────────────┘
            │ Java
            ▼
┌─────────────────────────┐
│  SERVICE                │  @Service + @Transactional
│  ProduitService         │  → Logique métier, conversion DTO ↔ Entité
└───────────┬─────────────┘
            │ Java
            ▼
┌─────────────────────────┐
│  REPOSITORY             │  extends JpaRepository
│  ProduitRepository      │  → Spring Data génère le SQL
└───────────┬─────────────┘
            │ SQL (Hibernate)
            ▼
┌─────────────────────────┐
│  BASE DE DONNÉES        │  H2 (dev) / PostgreSQL (prod)
│  Table : produits       │
└─────────────────────────┘
```

## Structure du projet

```
MVC-SpringBoot/
├── pom.xml                                    ← Dépendances Maven
├── src/main/
│   ├── java/com/exemple/mvcspringboot/
│   │   ├── Application.java                  ← Point d'entrée (@SpringBootApplication)
│   │   ├── modele/
│   │   │   └── Produit.java                  ← Entité JPA (@Entity, @Table)
│   │   ├── depot/
│   │   │   └── ProduitRepository.java        ← Accès données (JpaRepository)
│   │   ├── service/
│   │   │   └── ProduitService.java           ← Logique métier (@Service)
│   │   ├── controleur/
│   │   │   └── ProduitControleur.java        ← API REST (@RestController)
│   │   ├── dto/
│   │   │   ├── ProduitDTO.java               ← DTO d'entrée (validation)
│   │   │   └── ProduitReponseDTO.java        ← DTO de sortie (réponse)
│   │   ├── exception/
│   │   │   ├── RessourceNonTrouveeException.java ← Exception 404
│   │   │   └── GestionnaireGlobalExceptions.java ← @ControllerAdvice
│   │   └── config/
│   │       └── DonneesInitiales.java         ← Données de test au démarrage
│   └── resources/
│       └── application.properties            ← Configuration (BDD, Hibernate, logging)
└── README.md
```

## Prérequis

- **Java 17+**
- **Maven 3.8+**

## Lancement

```bash
cd MVC-SpringBoot

# Compiler et lancer
mvn spring-boot:run

# Ou en deux étapes :
mvn clean package
java -jar target/mvc-springboot-1.0.0.jar
```

L'application démarre sur http://localhost:8080

## API REST — Endpoints

### Lister tous les produits
```bash
GET http://localhost:8080/api/produits
```

### Rechercher par nom
```bash
GET http://localhost:8080/api/produits?recherche=macbook
```

### Obtenir un produit
```bash
GET http://localhost:8080/api/produits/1
```

### Créer un produit
```bash
POST http://localhost:8080/api/produits
Content-Type: application/json

{
  "nom": "Casque Audio",
  "description": "Casque sans fil avec réduction de bruit",
  "prix": 299.99,
  "stock": 20
}
```

### Modifier un produit
```bash
PUT http://localhost:8080/api/produits/1
Content-Type: application/json

{
  "nom": "MacBook Pro 16\" (modifié)",
  "description": "Nouveau modèle avec puce M4",
  "prix": 2999.99,
  "stock": 10
}
```

### Supprimer un produit
```bash
DELETE http://localhost:8080/api/produits/1
```

## Console H2 (base de données)

Accessible à http://localhost:8080/h2-console

| Paramètre   | Valeur                |
|-------------|----------------------|
| JDBC URL    | jdbc:h2:mem:produits_db |
| User Name   | sa                    |
| Password    | (vide)                |

## Concepts clés expliqués

### Injection de dépendances (IoC)
Spring crée et gère les instances (`@Service`, `@Repository`, `@RestController`).
Les dépendances sont injectées via le **constructeur** — pas de `new` dans le code métier.

### DTO vs Entité
- **Entité** (`Produit`) — Représentation en base, gérée par Hibernate
- **DTO entrée** (`ProduitDTO`) — Ce que le client envoie (avec validation)
- **DTO sortie** (`ProduitReponseDTO`) — Ce que le client reçoit

### @Transactional
Chaque méthode du service s'exécute dans une transaction SQL.
Si une exception est lancée → ROLLBACK automatique.

### @ControllerAdvice
Intercepte les exceptions de tous les contrôleurs et les convertit en réponses HTTP structurées (JSON).

## Avantages

- **Séparation des responsabilités** — Chaque couche a un rôle précis
- **Productivité** — Spring Boot auto-configure presque tout
- **Spring Data JPA** — Pas de SQL à écrire pour les opérations CRUD
- **Validation déclarative** — Annotations `@NotBlank`, `@Min`...
- **Transactions automatiques** — `@Transactional` gère commit/rollback
- **Testabilité** — Injection de dépendances = facile à mocker

## Inconvénients

- **Couplage au framework** — Le code dépend fortement de Spring
- **Magie implicite** — L'auto-configuration peut être difficile à débugger
- **Monolithique** — Tout dans un seul JAR (à moins de migrer vers microservices)
- **Performances Hibernate** — Le lazy loading peut causer des problèmes N+1
- **Complexité DTO** — Beaucoup de conversion si le modèle est gros (→ MapStruct)

## Pour aller plus loin

- **Tests** : `@WebMvcTest` (contrôleur), `@DataJpaTest` (repository), `@SpringBootTest` (intégration)
- **Sécurité** : Spring Security pour l'authentification/autorisation
- **Documentation** : SpringDoc OpenAPI (Swagger) pour documenter l'API
- **Migration BDD** : Flyway ou Liquibase pour gérer les scripts SQL
- **Cache** : `@Cacheable` pour les lectures fréquentes
