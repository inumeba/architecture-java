# 🎨 Design Patterns — Les 10 plus utilisés en Java

## Vue d'ensemble

Ce projet démontre les **10 Design Patterns les plus utilisés** du Gang of Four (GoF), avec des exemples concrets et des commentaires détaillés en français.

## Architecture

```
DesignPatterns/
└── src/
    ├── Application.java                    ← Point d'entrée
    │
    ├── creation/                           ← PATTERNS DE CRÉATION
    │   ├── singleton/
    │   │   └── BaseDeDonnees.java          → 1. Singleton (connexion BDD unique)
    │   ├── factory/
    │   │   ├── Notification.java           → 2. Factory (interface scellée)
    │   │   ├── NotificationEmail.java      →    Implémentation Email
    │   │   ├── NotificationSMS.java        →    Implémentation SMS
    │   │   ├── NotificationPush.java       →    Implémentation Push
    │   │   └── NotificationFactory.java    →    La Factory
    │   └── builder/
    │       └── Commande.java               → 3. Builder (commande fluide)
    │
    ├── structure/                           ← PATTERNS DE STRUCTURE
    │   ├── adapter/
    │   │   └── AdapterDemo.java            → 4. Adapter (Stripe Legacy → Paiement)
    │   ├── decorator/
    │   │   └── DecoratorDemo.java          → 5. Decorator (Boisson + extras)
    │   └── facade/
    │       └── FacadeDemo.java             → 6. Facade (Boutique en ligne)
    │
    └── comportement/                       ← PATTERNS DE COMPORTEMENT
        ├── strategy/
        │   └── StrategyDemo.java           → 7. Strategy (algorithmes de tri)
        ├── observer/
        │   └── ObserverDemo.java           → 8. Observer (chaîne YouTube)
        ├── template/
        │   └── TemplateDemo.java           → 9. Template Method (générateur de rapports)
        └── command/
            └── CommandDemo.java            → 10. Command (éditeur texte undo/redo)
```

## Les 10 Patterns

### 🏭 Patterns de Création

| # | Pattern | Problème | Solution | Exemple |
|---|---------|----------|----------|---------|
| 1 | **Singleton** | Garantir une seule instance | Constructeur privé + getInstance() | Connexion BDD |
| 2 | **Factory** | Découpler la création d'objets | Méthode qui choisit la classe concrète | Notifications (Email/SMS/Push) |
| 3 | **Builder** | Objet complexe avec paramètres optionnels | Construction étape par étape fluide | Commande de restaurant |

### 🏗️ Patterns de Structure

| # | Pattern | Problème | Solution | Exemple |
|---|---------|----------|----------|---------|
| 4 | **Adapter** | Interfaces incompatibles | Classe intermédiaire qui traduit | Ancien Stripe → nouveau système |
| 5 | **Decorator** | Ajouter des fonctionnalités dynamiquement | Emboîtement de classes | Boisson + Lait + Chantilly |
| 6 | **Facade** | Système complexe difficile à utiliser | Interface simplifiée | Passer une commande (stock + paiement + livraison) |

### 🔄 Patterns de Comportement

| # | Pattern | Problème | Solution | Exemple |
|---|---------|----------|----------|---------|
| 7 | **Strategy** | Plusieurs algorithmes interchangeables | Interface commune, implémentations variées | Algorithmes de tri |
| 8 | **Observer** | Notifier plusieurs objets d'un changement | Liste d'abonnés + notification | Chaîne YouTube + abonnés |
| 9 | **Template** | Même squelette, étapes différentes | Classe abstraite + méthodes abstraites | Générateur de rapports PDF/CSV/HTML |
| 10 | **Command** | Encapsuler une action (undo/redo) | Objet commande avec executer/annuler | Éditeur de texte |

## Compilation et exécution

```bash
cd architecture-java/DesignPatterns
javac -d out src/creation/singleton/*.java src/creation/factory/*.java src/creation/builder/*.java src/structure/adapter/*.java src/structure/decorator/*.java src/structure/facade/*.java src/comportement/strategy/*.java src/comportement/observer/*.java src/comportement/template/*.java src/comportement/command/*.java src/Application.java
java -cp out Application
```

## Quand utiliser quel pattern ?

```
Besoin de...                          → Pattern
─────────────────────────────────────────────────
UNE seule instance                    → Singleton
Créer sans connaître le type          → Factory
Objet avec beaucoup de paramètres     → Builder
Rendre 2 API compatibles             → Adapter
Ajouter des comportements dynamiques  → Decorator
Simplifier un système complexe        → Facade
Changer d'algorithme au runtime       → Strategy
Réagir aux changements d'état         → Observer
Même algo, étapes différentes         → Template Method
Undo/Redo, file de tâches            → Command
```

## Technologies

- **Java 17+** (sealed interfaces, records, text blocks, pattern matching)
- Aucune dépendance externe (Java pur)
