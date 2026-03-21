# Architecture MVC en Java — Exemple commenté en français

## Qu'est-ce que MVC ?

**MVC** (Model-View-Controller) est un patron d'architecture qui sépare une application en **3 composants distincts**, chacun ayant une responsabilité unique :

| Composant | Rôle | Dans cet exemple |
|-----------|------|------------------|
| **Modèle** (Model) | Gère les données et la logique métier | `Produit.java`, `ProduitDepot.java` |
| **Vue** (View) | Gère l'affichage et la présentation | `ProduitVue.java` |
| **Contrôleur** (Controller) | Orchestre le modèle et la vue, traite les actions utilisateur | `ProduitControleur.java` |

## Schéma de fonctionnement

```
┌─────────────┐       ┌──────────────────┐       ┌─────────────────┐
│             │       │                  │       │                 │
│    VUE      │◄──────│   CONTRÔLEUR     │──────►│    MODÈLE       │
│ (Affichage) │       │ (Logique de      │       │ (Données +      │
│             │       │  routage)        │       │  Règles métier) │
└─────────────┘       └──────────────────┘       └─────────────────┘
       ▲                      ▲
       │                      │
       └─── Utilisateur ──────┘
```

### Flux d'une action typique

```
1. L'utilisateur déclenche une action (ex: "créer un produit")
2. Le CONTRÔLEUR reçoit cette demande
3. Le CONTRÔLEUR appelle le MODÈLE pour traiter les données
4. Le MODÈLE effectue l'opération et renvoie le résultat
5. Le CONTRÔLEUR transmet le résultat à la VUE
6. La VUE affiche le résultat à l'utilisateur
```

## Structure du projet

```
MVC/
├── src/
│   ├── modele/                      # COUCHE MODÈLE
│   │   ├── Produit.java             # Entité métier (les données d'un produit)
│   │   └── ProduitDepot.java        # Repository (accès et stockage des données)
│   │
│   ├── vue/                         # COUCHE VUE
│   │   └── ProduitVue.java          # Affichage console (présentation)
│   │
│   ├── controleur/                  # COUCHE CONTRÔLEUR
│   │   └── ProduitControleur.java   # Chef d'orchestre (relie modèle ↔ vue)
│   │
│   └── Application.java             # Point d'entrée (assemblage des composants)
│
├── out/                              # Classes compilées (.class)
└── README.md                         # Ce fichier
```

## Description détaillée de chaque fichier

### 1. `modele/Produit.java` — L'entité métier

C'est la classe qui représente **un produit** avec ses attributs :
- `id` — identifiant unique (final, non modifiable)
- `nom` — nom du produit
- `prix` — prix en euros

**Points clés :**
- L'entité est **indépendante** : elle ne connaît ni la vue, ni le contrôleur
- Elle contient une **règle métier** : le prix ne peut pas être négatif (`Math.max(0, prix)`)
- Elle utilise Java moderne avec `String.formatted()` (Java 17+)

### 2. `modele/ProduitDepot.java` — Le dépôt de données (Repository)

Cette classe gère le **stockage et la récupération** des produits :
- `ajouter(nom, prix)` — crée un produit avec un ID auto-incrémenté
- `obtenirTous()` — retourne une copie immuable de la liste (`List.copyOf`)
- `trouverParId(id)` — retourne un `Optional<Produit>` (pas de `null` !)
- `supprimer(id)` — supprime un produit par son ID

**Points clés :**
- Simule une base de données avec une `ArrayList` en mémoire
- Utilise `Optional` pour éviter les `NullPointerException`
- Retourne des copies immuables pour **protéger les données internes** (encapsulation)

### 3. `vue/ProduitVue.java` — L'affichage

La vue est **uniquement responsable de la présentation** :
- `afficherListeProduits(liste)` — affiche un tableau formaté de tous les produits
- `afficherDetailProduit(produit)` — affiche les détails d'un seul produit
- `afficherMessage(message)` — affiche une confirmation (`[OK]`)
- `afficherErreur(message)` — affiche une erreur (`[ERREUR]`)

**Points clés :**
- La vue **ne modifie jamais** les données
- La vue **ne prend aucune décision** sur ce qu'il faut afficher
- Elle reçoit les données **prêtes à afficher** du contrôleur

### 4. `controleur/ProduitControleur.java` — Le chef d'orchestre

Le contrôleur **coordonne** le modèle et la vue :
- `creerProduit(nom, prix)` — demande au modèle de créer, puis à la vue de confirmer
- `listerProduits()` — demande les données au modèle, puis les transmet à la vue
- `afficherProduit(id)` — cherche dans le modèle, affiche le détail ou une erreur
- `supprimerProduit(id)` — supprime via le modèle, confirme via la vue

**Points clés :**
- Utilise l'**injection de dépendances** via le constructeur (reçoit le dépôt et la vue)
- Ne contient **aucune logique métier** (c'est le rôle du modèle)
- Ne formate **aucun affichage** (c'est le rôle de la vue)

### 5. `Application.java` — Le point d'entrée

C'est ici que les 3 composants sont **assemblés et connectés** :
1. Crée le modèle (`ProduitDepot`)
2. Crée la vue (`ProduitVue`)
3. Crée le contrôleur en lui injectant le modèle et la vue
4. Simule des actions utilisateur (créer, lister, consulter, supprimer)

**En production** avec Spring, cet assemblage serait automatique grâce aux annotations `@Component`, `@Autowired`, etc.

## Comment compiler et exécuter

### Prérequis
- **Java 17** ou supérieur (`java --version` pour vérifier)

### Compilation
```bash
cd architecture-java/MVC
javac -d out src/modele/*.java src/vue/*.java src/controleur/*.java src/Application.java
```

### Exécution
```bash
java -cp out Application
```

### Sortie attendue
```
[OK] Produit cree : Clavier (ID: 1)
[OK] Produit cree : Souris (ID: 2)
[OK] Produit cree : Ecran 27 pouces (ID: 3)

══════════════════════════════════════
         LISTE DES PRODUITS
══════════════════════════════════════
  ID   | Nom             |     Prix
  -----|-----------------|----------
  1    | Clavier         |   49,99 EUR
  2    | Souris          |   29,99 EUR
  3    | Ecran 27 pouces |  349,00 EUR
══════════════════════════════════════

-- Detail du produit --
  ID   : 2
  Nom  : Souris
  Prix : 29,99 EUR
[OK] Produit #1 supprime.

══════════════════════════════════════
         LISTE DES PRODUITS
══════════════════════════════════════
  ID   | Nom             |     Prix
  -----|-----------------|----------
  2    | Souris          |   29,99 EUR
  3    | Ecran 27 pouces |  349,00 EUR
══════════════════════════════════════

[ERREUR] Aucun produit trouve avec l'ID 99
```

## Avantages de MVC

- **Séparation des responsabilités** : chaque composant a un rôle unique et clair
- **Testabilité** : on peut tester le modèle sans la vue, et le contrôleur avec des mocks
- **Réutilisabilité** : le même modèle peut servir à une vue console, web ou mobile
- **Maintenabilité** : modifier l'affichage ne touche pas la logique métier, et vice-versa
- **Travail en équipe** : un développeur travaille sur la vue pendant qu'un autre travaille sur le modèle

## Inconvénients de MVC

- **Surcharge pour les petits projets** : 3 couches pour un simple CRUD peut sembler excessif
- **Contrôleur "obèse"** : sans discipline, le contrôleur accumule trop de logique
- **Couplage indirect** : la vue dépend de la structure du modèle (elle doit connaître `Produit`)
- **Complexité croissante** : avec beaucoup de vues et de modèles, le nombre de contrôleurs explose

## Frameworks courants qui implémentent MVC

| Framework | Comment ils implémentent MVC |
|-----------|------------------------------|
| **Spring MVC** | `@Controller`, `@RequestMapping`, `Model`, templates Thymeleaf |
| **Jakarta EE (JSF)** | Managed Beans (contrôleur), Facelets (vue), JPA (modèle) |
| **JavaFX** | FXML (vue), Controller classes, POJO (modèle) |
| **Vaadin** | Composants UI (vue), Presenter/Binder (contrôleur), entités JPA (modèle) |

## Pour aller plus loin

D'autres architectures Java courantes à explorer :
- **Architecture en couches** (Layered / N-Tier)
- **Architecture hexagonale** (Ports & Adapters)
- **Microservices**
- **CQRS** (Command Query Responsibility Segregation)
- **Event-Driven** (Architecture événementielle)
