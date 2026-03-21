## 🏗️ Architecture : Tests Unitaires avec JUnit 5 (Cas d'usages)

Ce projet illustre de façon exhaustive et indépendante toutes les possibilités offertes par l'API moderne de tests en Java : **JUnit 5 (Jupiter)**.

### Schéma

```mermaid
graph TD
    A[Maven Surefire Plugin] --> B(Moteur JUnit Jupiter)
    
    B --> C(Tests de Base)
    B --> D(Tests Paramétrés)
    B --> E(Tests Conditionnels)
    
    C --> F[@Test simples & Cycles de vie]
    C --> G[@Nested : Groupement par état]
    C --> H[Exceptions : assertThrows]
    
    D --> I[@ValueSource]
    D --> J[@CsvSource]
    
    E --> K[Timeouts : assertTimeout]
    E --> L[@Disabled & @EnabledOnOs]
```

### Structure du projet

Voici comment sont agencés les tests avec l'arborescence conventionnelle Maven (`src/main` vs `src/test`).

```text
JUnitCasDeFigures/
├── pom.xml                                   (Pour importer JUnit 5 et son moteur)
├── src/
│   ├── main/java/
│   │   ├── modele/CompteBancaire.java        (Modèle métier avec règles de gestion)
│   │   └── service/Calculatrice.java         (Service mathématique à tester)
│   └── test/java/
│       ├── modele/CompteBancaireTest.java    (Tests basiques, @Nested, @BeforeEach, Assertions)
│       └── service/CalculatriceTest.java     (Tests paramétrés, CSV, Timeout et @Disabled)
```

### Code source

1.  **Le cycle de vie (`@BeforeEach`, `@AfterAll`)** permet de préparer un environnement sain et indépendant avant chaque test (Isolation des données).
2.  **`assertThrows`** est devenu le standard JUnit 5, on arrête en fin de compte d'utiliser le vieux `@Test(expected=Exception.class)`. Ce qui permet de tester les messages d'erreurs en retour.
3.  **Les Tests Paramétrés (`@ParameterizedTest`)** sont vitaux : plutôt que d'écrire 5 tests `testAddition1()`, `testAddition2()`, on injecte un `@CsvSource` sur une fonction unique, accélérant l'écriture.

### 🚀 Exécution Locale (Terminal)

```powershell
# S'assurer d'être à la racine du projet qui a le "pom.xml" (JUnitCasDeFigures)
mvn clean test
```

### Avantages de JUnit 5 par rapport à :
- **Architecture Modulaire :** Sépare l'API de développement des tests (`jupiter-api`) du moteur lourd qui va les exécuter (`jupiter-engine`).
- **Lisibilité :** Les annotations comme `@DisplayName` facilitent la lecture des rapports générés par les serveurs d'intégration continue (CI/CD type Jenkins).
- **Maintenance (DRY) :** Les `@ParameterizedTest` permettent de tester 15 valeurs limites avec seulement 3 lignes de code.

### Inconvénients
- **Le Refactoring des vieux projets :** Migrer tout un code base JUnit 4 vers l'architecture `Jupiter API` demande pas mal d'effort sur les `import` (`org.junit.Test` vers `org.junit.jupiter.api.Test`).
- Par convention, les tests tournent en *Séquentiel* de base. Pour lancer des tests massivement en `Thread-pool` parallèle, il faut configurer un contexte spécial non-natif au premier coup d'œil.

### Frameworks courants
**JUnit 5 (Jupiter)** est devenu l'architecte du Test par défaut au cœur du monde Entreprise en Java. Il est embarqué de manière native et auto-configurée massivement par le sous-framework très connu : **Spring Boot Starter Test**. Souvent marié à **Mockito** et **AssertJ** pour une puissance maximale de bouchonnage (Mocking).
