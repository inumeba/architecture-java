# Exceptions Java — Tous les Cas de Figures

## Vue d'ensemble

Ce projet couvre **l'intégralité** des mécanismes d'exceptions en Java
avec des exemples concrets et commentés en français.

## Hiérarchie des exceptions en Java

```
Throwable
├── Error (FATAL — ne pas catcher)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── VirtualMachineError
└── Exception
    ├── Checked (compilateur oblige à gérer)
    │   ├── IOException
    │   ├── SQLException
    │   └── ApplicationException ← notre hiérarchie métier
    │       ├── ValidationException
    │       ├── RessourceNonTrouveeException
    │       ├── RegleMetierException
    │       └── ErreurTechniqueException
    └── RuntimeException (unchecked — pas obligé de catcher)
        ├── NullPointerException
        ├── IllegalArgumentException
        ├── IndexOutOfBoundsException
        ├── ArithmeticException
        ├── ClassCastException
        ├── UnsupportedOperationException
        └── ConcurrentModificationException
```

## Structure du projet

```
Exceptions/
├── src/
│   ├── Application.java              ← Point d'entrée principal
│   ├── exception/
│   │   └── metier/
│   │       ├── ApplicationException.java      ← Exception de base (checked)
│   │       ├── ValidationException.java       ← Données invalides
│   │       ├── RessourceNonTrouveeException.java  ← 404
│   │       ├── RegleMetierException.java      ← Règle métier violée
│   │       └── ErreurTechniqueException.java  ← Erreur infrastructure
│   ├── service/
│   │   ├── ProduitService.java        ← Service lançant des exceptions
│   │   └── FichierService.java        ← Try-with-resources
│   └── exemples/
│       ├── TryCatchExemples.java      ← 10 patterns try-catch
│       ├── GestionnaireGlobal.java    ← Gestion centralisée
│       └── AntiPatterns.java          ← Ce qu'il ne faut PAS faire
└── README.md
```

## Cas couverts

### 1. Hiérarchie d'exceptions personnalisées
- Exception de base avec **code d'erreur**
- **Chaînage** de causes (`initCause`, constructeur)
- Sous-classes spécialisées avec **données contextuelles**
- Checked vs Unchecked : quand utiliser chaque type

### 2. Try-catch — 10 patterns
| # | Pattern | Fichier |
|---|---------|---------|
| 1 | Try-catch basique | `TryCatchExemples.java` |
| 2 | Catch multiples (du + spécifique au + général) | `TryCatchExemples.java` |
| 3 | Multi-catch avec `\|` (Java 7+) | `TryCatchExemples.java` |
| 4 | Try-catch-finally | `TryCatchExemples.java` |
| 5 | Re-lancer une exception | `TryCatchExemples.java` |
| 6 | Chaînage d'exceptions (cause) | `TryCatchExemples.java` |
| 7 | Exceptions supprimées (`getSuppressed()`) | `TryCatchExemples.java` |
| 8 | Toutes les RuntimeException courantes | `TryCatchExemples.java` |
| 9 | Pattern matching `instanceof` (Java 16+) | `TryCatchExemples.java` |
| 10 | Assertions (`assert`) | `TryCatchExemples.java` |

### 3. Try-with-resources
- Ressource simple (`AutoCloseable`)
- Ressources multiples
- Exceptions supprimées vs exception principale
- Comparaison avec l'ancien try-finally

### 4. Gestion centralisée
- `Thread.setDefaultUncaughtExceptionHandler()`
- Simulation d'un `@ControllerAdvice` (Spring Boot)
- Conversion exception → code HTTP

### 5. Anti-patterns (6 erreurs à éviter)
| # | Anti-pattern | Pourquoi c'est mauvais |
|---|-------------|----------------------|
| 1 | `catch(Exception e)` | Trop large, masque les bugs |
| 2 | Catch vide `{ }` | Exception silencieusement perdue |
| 3 | Exceptions pour le flux | Coûteux, code illisible |
| 4 | Perdre la cause | Stack trace originale perdue |
| 5 | `catch(Throwable)` | Attrape les Error fatales |
| 6 | Throw dans finally | Écrase l'exception du try |

## Compilation et exécution

```bash
cd Exceptions
javac -d out src/exception/metier/*.java src/service/*.java src/exemples/*.java src/Application.java
java -cp out Application
```

## Bonnes pratiques résumées

1. **Spécificité** — Catcher le type le plus précis possible
2. **Cause** — Toujours conserver la cause (`throw new X(msg, e)`)
3. **Ressources** — Utiliser `try-with-resources` (jamais try-finally)
4. **Silence** — Ne JAMAIS avaler une exception (catch vide)
5. **Flux** — Ne PAS utiliser les exceptions pour du contrôle de flux
6. **Hiérarchie** — Créer une hiérarchie d'exceptions métier avec codes
7. **Centralisation** — Gérer les exceptions à un seul endroit (`@ControllerAdvice`)

## En production (Spring Boot)

```java
@ControllerAdvice
public class GestionnaireGlobalExceptions {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErreurReponse> gererValidation(ValidationException e) {
        return ResponseEntity.status(400).body(new ErreurReponse(e.getCodeErreur(), e.getMessage()));
    }

    @ExceptionHandler(RessourceNonTrouveeException.class)
    public ResponseEntity<ErreurReponse> gererNonTrouvee(RessourceNonTrouveeException e) {
        return ResponseEntity.status(404).body(new ErreurReponse(e.getCodeErreur(), e.getMessage()));
    }
}
```
