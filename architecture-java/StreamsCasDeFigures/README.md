# Java Streams API (Java 8+) - Guide Complet des Cas d'Usage
> Projet généré de référence couvrant la totalité des fonctions de base et structurantes des Java Streams.

## 🎯 Architecture du Projet
Ce projet purement fonctionnel cible spécifiquement la couverture totale des usages de l'API Stream de Java.
Il contourne complètement les anciennes boucles `for/while` "impératifs" au profit d'un code totalement déclaratif, purement descriptif et garantit être **side-effect free** (sans effet de bord avec l'API standard).

### Dossiers
```text
src/
├── modele/                  (Modèle métier : Record immuable)
│   ├── Produit.java      
│   └── Commande.java      (List de produit = pour test flatMap)
├── exemples/                (Couches par type de Stream)
│   ├── CreationStreams.java      (List.of, int[], Stream.builder)
│   ├── OperationsIntermediaires.java (filter, map, sorted, distinct)
│   ├── OperationsTerminales.java     (reduce, collect, anyMatch, first)
│   ├── GroupementEtPartition.java    (groupingBy, partitioningBy)
│   └── CasAvances.java               (IntStream.range, parallelStream)
└── main/
    └── Application.java     (Point central, Factory des données de test)
```

## 🛠 Compilation Locale & Lancement
### Pré-requis : JavaJDK 17 min (pour "Record"). (Testé sur 21).

**Étape 1 : Compiler**
(Dans la racine du module `StreamsCasDeFigures`)
```powershell
javac -d out src/modele/*.java src/exemples/*.java src/main/*.java
```

**Étape 2 : Lancer**
```powershell
java -cp out main.Application
```

## 🧠 Concepts Démontrés
*   **Immutabilité via Record :** Tous les objets métiers sont de type `record`.
*   **Filter/Map/FlatMap :** La trinité sacrée pour sélectionner, transformer une données 1-pour-1, puis 1-pour-N (des tableaux dans des listes).
*   **Group / Partition:** Les Group By de SQL émulés par la classe `Collectors`. Très utiles pour faire des tris.
*   **Les Primitifs et Parallèles:** Traiter de lourdes charges massive en mode asynchrone pour la vitesse (`.parallelStream()`) et manipuler directement du bas-niveau sans auto-boxing avec les `IntStream`.
