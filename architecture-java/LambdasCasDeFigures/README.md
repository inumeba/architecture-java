# Java Lambdas API (Java 8+) - Guide Complet des Cas d'Usage
> Projet généré de référence couvrant la totalité des fonctions de base et structurantes des Lambdas en Java.

## 🎯 Architecture du Projet
Ce projet est construit pour illustrer au maximum l'exploitation de la programmation fonctionnelle dans la machine virtuelle Java sans utiliser aucun Framework externe.

```mermaid
graph TD
    A[Application Point d'Entrée] --> B(Interfaces Pré-Défines)
    A --> C(Interfaces Personnalisées & Scopes)
    A --> D(Method References & BiFonctions)
    
    B --> E[Predicate | Test]
    B --> F[Function | Map]
    B --> G[Consumer | Action void]
    B --> H[Supplier | Fournisseur Null-safe]
    
    C --> I[@FunctionalInterface Custom]
    C --> J[Variable 'Effectively Final']

    D --> K[Class::method]
    D --> L[BiFunction / BiPredicate]
```

### Dossiers
```text
src/
├── modele/                  
│   └── Utilisateur.java     (Modèle central immuable de type Record)
├── interfaces/             
│   └── ValidateurUtilisateur.java (@FunctionalInterface personnalisée)
├── exemples/               
│   ├── InterfacesPredefinies.java         (Predicate, Function, Consumer, Supplier)
│   ├── LambdasPersonnaliseesEtScope.java  (Closure, capture locale "effectively final")
│   └── ReferencesDeMethodesEtBiFonctions.java (Method Referencing "::" et BiConsumer)
└── main/
    └── Application.java     (Main runner)
```

## 🛠 Compilation Locale & Lancement
### Pré-requis : JavaJDK 17 min (pour "Record"). (Testé sur 21).

**Étape 1 : Compiler**
(Dans la racine du module `LambdasCasDeFigures`)
```powershell
javac -d out src/modele/*.java src/interfaces/*.java src/exemples/*.java src/main/*.java
```

**Étape 2 : Lancer**
```powershell
java -cp out main.Application
```

## 🧠 Concepts Démontrés
*   **The Big Four :** `Predicate`, `Supplier`, `Function`, `Consumer`. Les interfaces noyaux que tout dev Java devrait connaitre.
*   **L'annotation @FunctionalInterface :** Garantit la règle du **S.A.M** (*Single Abstract Method*).
*   **Les closures :** Pourquoi et comment une variable capturée hors d'une Lambda se doit d'être virtuellement constante (*effectively final*).
*   **Method Referencing (`::`) :** Syntactic sugar suprême pour remplacer `x -> System.out.println(x)` par `System.out::println`.

### Avantages
- Code nettement **plus concis**, purement déclaratif (On dit *QUOI* faire, pas *COMMENT* l'itérer).
- Permet de passer des comportements complets comme des variables (Fonctions de premiere classe).
- Évite la verbosité des classes Anonymes pré-Java 8.

### Inconvénients
- Plus délicat à debugger dans les StackTraces (lignes générées dynamiquement).
- Concept de Closure restrictif face à JavaScript (mutation externe bloquée = *effectively final exception*).

### Frameworks courants
Toute l'architecture moderne de **Spring WebFlux / Project Reactor**, les configurations de **Spring Security** récentes (qui deprecient les `WebSecurityConfigurerAdapter` en faveur de chaines configurées via Lambda DSL), ou encore l'API **Streams** native l'utilisent à 100%.
