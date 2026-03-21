---
description: "Explique en détail les nouveautés Java (8 -> 11 -> 17 -> 21) avec des exemples de code."
---

Agis comme un expert Java. Le but de ce prompt est de fournir une explication claire, détaillée et en français des évolutions majeures du langage Java, en partant de Java 8 jusqu'à Java 21 (nouvelles versions LTS).

À chaque étape, explique ce qui a changé, les bénéfices pour le développeur, et illustre TOUJOURS tes propos avec des exemples de code complets et concrets.

Structure impérativement ta réponse de la manière suivante :

1. **De Java 8 à Java 11** :
   - Présente les nouveautés clés (ex: inférence de type avec `var`, nouvelles méthodes `String` et `Files`, le nouveau `HttpClient`).
   - Fournis des exemples de code comparant l'approche Java 8 vs Java 11.

2. **De Java 11 à Java 17** :
   - Présente les nouveautés clés (ex: les `Records`, les `Switch Expressions`, les `Text Blocks`, le `Pattern Matching` pour `instanceof`, les `Sealed Classes`).
   - Illustre largement l'impact des Records et des Text Blocks sur la lisibilité du code.

3. **De Java 17 à Java 21** :
   - Présente les nouveautés clés (ex: `Virtual Threads`, `Pattern Matching` pour les `switch`, `Record Patterns`, `Sequenced Collections`).
   - Explique simplement le concept des Virtual Threads et montre un exemple de syntaxe avec le Pattern Matching avancé.

4. **Évolutions sous le capot (JVM, GC, API Date/Time)** :
   - Explique les évolutions majeures des Garbage Collectors (le passage à G1 par défaut, l'introduction de ZGC et Shenandoah pour les pauses très courtes).
   - Fais un point sur les améliorations et bonnes pratiques d'utilisation de la nouvelle API Date/Heure (introduite en Java 8) à travers les versions LTS.

**Règles à respecter :**
- Rédige l'intégralité de ton explication en **français**.
- Sois très pédagogique.
- Les extraits de code doivent être clairs, modernes et montrer les meilleures pratiques.
- Associe les nouveautés de syntaxe aux améliorations internes (performances, gestion mémoire) lorsque c'est pertinent.
