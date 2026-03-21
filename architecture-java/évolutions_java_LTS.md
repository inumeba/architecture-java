# 🚀 Les Évolutions de Java : De Java 8 à Java 21 (LTS)

Java a considérablement évolué depuis Java 8, adoptant un cycle de versionnement plus raproché et introduisant de nombreuses fonctionnalités qui améliorent la lisibilité du code, la productivité des développeurs et les performances globales. Voici un guide détaillé des évolutions majeures à travers les versions LTS (Long-Term Support) : Java 11, 17, et 21.

---

## 1. De Java 8 à Java 11

Le passage de Java 8 à Java 11 a marqué l'introduction de plusieurs améliorations "confort de vie" pour les développeurs, ainsi qu'une nouvelle API HTTP native.

### L'inférence de type avec `var` (Java 10/11)
Java permet à présent d'omettre explicitement le type de la variable locale si le compilateur peut le déduire.

**Approche Java 8 :**
```java
Map<String, List<String>> maMap = new HashMap<String, List<String>>();
```

**Approche Java 11 :**
```java
var maMap = new HashMap<String, List<String>>(); 
// Le compilateur déduit le type automatiquement, allégeant la lecture.
```

### Nouvelles méthodes pour `String` et `Files`
Java 11 a enrichi les classes utilitaires avec des méthodes pratiques, réduisant le besoin de s'appuyer sur des librairies externes comme Apache Commons.

```java
// Côté String
"  bonjour  ".strip();       // "bonjour" (plus intelligent que trim() avec les caractères Unicode)
" ".isBlank();               // true
"Ligne1\nLigne2".lines();    // Crée un Stream de 2 éléments
"Java ".repeat(3);           // "Java Java Java "

// Côté Files
Path path = Path.of("mon_fichier.txt");
Files.writeString(path, "Hello Java 11"); // Écriture en une ligne
String content = Files.readString(path);  // Lecture en une ligne
```

### Le nouveau `HttpClient` natif (Standardisé en Java 11)
Exit la très vieille classe `HttpURLConnection` ou la dépendance obligatoire à Apache HttpClient. Le nouveau `HttpClient` du JDK supporte HTTP/1.1 et HTTP/2, ainsi que des appels synchrones et asynchrones.

**Comparatif :**
```java
var client = HttpClient.newHttpClient();
var request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.github.com"))
    .GET()
    .build();

// Appel de l'API de manière fluide et lisible
HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
System.out.println(response.body());
```

---

## 2. De Java 11 à Java 17

Java 17 a été une étape massive pour réduire le "boilerplate" (code verbeux), souvent reproché au langage.

### Les `Records`
Les `Records` permettent de définir des classes porteuses de données (DTOs, modèles) immuables en une seule ligne. Le compilateur génère automatiquement le constructeur, les getters, `equals()`, `hashCode()`, et `toString()`.

**Approche classique (Avant Java 14) :**
```java
public class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
    // + redéfinition de equals(), hashCode(), toString()... (près de 50 lignes de code)
}
```

**Approche Java 17 avec Records :**
```java
public record Point(int x, int y) {} 
// C'est tout ! Tout est généré.
Point p = new Point(10, 20);
System.out.println(p.x()); // appel direct au getter via la méthode x()
```

### Les Text Blocks (`"""`)
Fini les concaténations lourdes de `String` avec des retours à la ligne `\n` partout quand on tape du JSON, du SQL ou de l'HTML.

**Approche Classique vs Java 17 :**
```java
// Avant
String sql = "SELECT id, nom, email \n" +
             "FROM utilisateurs \n" +
             "WHERE statut = 'ACTIF'";

// Java 17 (Text Blocks)
String newSql = """
                SELECT id, nom, email
                FROM utilisateurs
                WHERE statut = 'ACTIF'
                """;
```

### Les Switch Expressions et Pattern Matching d'`instanceof`
Le `switch` devient une expression capable de retourner une valeur, sans avoir besoin des fastidieux `break` partout (syntaxe lambda `->`).

```java
// Switch Expression
int maxJours = switch (mois) {
    case FEVRIER -> 28;
    case AVRIL, JUIN, SEPTEMBRE, NOVEMBRE -> 30;
    default -> 31;
};

// Pattern Matching pour "instanceof" (plus de cast manuel inutile)
if (obj instanceof String s) {
    // "s" est déjà vu intelligemment comme un String par le compilateur
    System.out.println(s.toUpperCase()); 
}
```

### Sealed Classes (Classes scellées)
Permet de restreindre précisément quelles classes ou interfaces peuvent implémenter une interface donnée.
```java
public sealed interface Forme permits Cercle, Rectangle {}
public final class Cercle implements Forme {}
public final class Rectangle implements Forme {}
// Personne d'autre ne peut implémenter "Forme", protégeant vos règles métier.
```

---

## 3. De Java 17 à Java 21

Cette version marque l'aboutissement de projets internes massifs (comme Loom ou Amber).

### Virtual Threads (Projet Loom)
Les `Virtual Threads` sont une révolution pour la programmation concurrente en Java. Autrefois, un thread Java = un thread du système d'exploitation (coûteux en RAM). 
Aujourd'hui, les threads virtuels sont gérés mathématiquement par la JVM. Si un Virtual Thread est bloqué (ex: requête à la Base de données ou I/O), la JVM le met de côté et alloue le thread OS à une autre tâche. **Les applications deviennent ultra concurrentielles sans complexité asynchrone.**

```java
// Lancement de 10 000 processus concurrents sans épuiser la RAM !
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10_000; i++) {
        executor.submit(() -> {
            Thread.sleep(Duration.ofSeconds(1)); // I/O géré intelligemment sans bloquer l'OS
            System.out.println("Tâche exécutée");
            return "OK";
        });
    }
}
```

### Pattern Matching étendu (Switch + Records)
Le `switch` franchit un cap : il peut évaluer des objets complets et déstructurer directement des `Records` (Record Patterns), avec en plus le mot-clé `when` pour greffer des gardes de conditions.

```java
public record Utilisateur(String nom, int age) {}

Object objet = new Utilisateur("Alice", 25);

String description = switch (objet) {
    case Utilisateur(String n, int a) when a >= 18 -> n + " est majeur(e).";
    case Utilisateur(String n, int a)              -> n + " est mineur(e).";
    case String s                                  -> "C'est une chaîne ! Longueur: " + s.length();
    case null                                      -> "Attention, l'objet est nul";
    default                                        -> "Type inconnu";
};
```

### Sequenced Collections
Il manquait aux Listes (List), Ensembles (Set), et Maps (Map) une manière universelle et fiable d'accéder au premier ou dernier élément. C'est désormais unifié :
```java
List<String> list = new ArrayList<>();
list.addFirst("Début");
list.addLast("Fin");
String premier = list.getFirst(); 
String dernier = list.getLast();
```

---

## 4. Évolutions sous le capot (JVM, GC, API Date/Time)

Mais la magie réside aussi sur le fond de l'architecture...

### L'évolution des Garbage Collectors (GC)
Java est réputé pour sa gestion autonome de la mémoire. Avec ces itérations, le but a été d'éliminer le "stop-the-world" (quand l'application gèle pour vider la RAM).
1. **G1 (Garbage First - Défaut depuis Java 9/11)** : Divise le tas en petites régions pour nettoyer prioritairement les espaces les plus remplis d'objets morts. Il réduit significativement les longues pauses.
2. **ZGC & Shenandoah (Produits entre Java 11, 17 puis affinés vers 21)** : Ces GCs dits à *faible latence* opèrent quasiment exclusivement en concurrence. Ils garantissent à présent des temps de pause **inférieurs à une milliseconde**, indépendamment de la taille de la RAM (qu'il s'agisse de 1Go ou d'1 Téraoctet). C'est crucial pour la haute fréquence et l'absence totale de lags d'interface.

### L'API Date/Heure (java.time)
Introduite en Java 8 (qui a mis officiellement au placard les désastreux `java.util.Date` et `SimpleDateFormat` qui n'étaient pas *Thread-Safe*), elle a conquis de bonnes pratiques renforcées au fil du temps.

- **`Instant`** : Toujours utiliser `Instant` en base de données et dans vos Modèles pour persister une temporalité mondiale (UTC), jamais un fuseau horaire propre à la machine.
- **`LocalDateTime` & `ZonedDateTime`** : Utilisés pour l'affichage humain et les cas métiers complexes liés au calendrier.
- Des méthodes de commodités pour les instances temporelles ont été enrichies. Les conversions avec `Duration` (qui calcule le décalage précis avec `.toMillis()` ou `.toHours()`) sont devenues la norme.

```java
// Représenter un moment précis (UTC)
Instant maintenant = Instant.now();

// Calcul de durée sécurisé avec java.time.Duration
Duration duree = Duration.between(maintenant, maintenant.plus(2, ChronoUnit.HOURS));
System.out.println("Durée en minutes : " + duree.toMinutes());
```
