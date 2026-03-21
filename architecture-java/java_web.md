# 🌐 Qu'est-ce que le Java Web ?

L'expression **"Java Web"** (ou "Développement Web en Java") décrit la création de sites internet, d'applications web ou d'API où **le langage Java est utilisé côté serveur (le Back-End)**. 

Concrètement, c'est un programme Java qui tourne en continu sur un serveur, écoute les requêtes HTTP (lorsqu'un utilisateur navigue sur une page web ou clique sur un bouton) et lui renvoie une réponse.

L'écosystème Java Web a beaucoup évolué. Voici comment on peut le découper pour bien comprendre :

## 1. Les fondations historiques et techniques (Le bas niveau)
Tout le "Java Web" repose sur une base standardisée appelée **Java EE** (aujourd'hui renommée **Jakarta EE**) :
* **Les Servlets :** Ce sont de petites classes Java qui reçoivent une requête HTTP (un objet `HttpServletRequest`) et construisent une réponse (un objet `HttpServletResponse`).
* **Les JSP (JavaServer Pages) :** Historiquement, cela permettait de mélanger du code HTML et du code Java (un peu comme on le ferait en PHP) pour générer des pages dynamiques.
* **Le Serveur Web (ou Conteneur de Servlets) :** Le code Java ne peut pas écouter les requêtes web tout seul. Il a besoin d'un serveur comme **Apache Tomcat**, **Jetty** ou **Undertow** pour faire le pont entre le réseau internet et vos classes Java.

## 2. Le Java Web moderne (Exemple : Spring Boot)
Aujourd'hui, presque plus personne n'écrit de "Servlets" à la main, car c'est trop verbeux et répétitif. On utilise des **Frameworks** qui cachent toute cette complexité.

C'est exactement l'architecture utilisée de nos jours sur la majorité des nouveaux projets :
* **Spring Boot (et Spring Web) :** C'est le framework dominant. Au lieu d'écrire des dizaines de lignes pour intercepter une URL, vous mettez simplement une annotation `@GetMapping("/agence/tpe")` au-dessus d'une méthode. Spring Boot s'occupe de faire le lien avec un serveur Tomcat (qui est caché *à l'intérieur* de l'application).
* **Moteur de Templates :** Au lieu des vieilles JSP, on utilise de nos jours **Thymeleaf** par exemple. Cela permet au Java de remplacer des variables dans des fichiers HTML avant de les envoyer au navigateur.
* **API REST :** Quand le serveur Java ne renvoie pas du HTML, mais directement des données en **JSON** avec l'annotation `@RestController`. Ces API sont ensuite lues par des applications mobiles (iOS/Android) ou des front-ends en JavaScript (React, Angular, Vue).

## En résumé :
Faire du **Java Web**, c'est utiliser la robustesse de Java pour fabriquer le "moteur" caché d'un site internet. 

Aujourd'hui, un développeur "Java Web" écrit principalement :
1. Des **Contrôleurs** pour définir des adresses (URL) web.
2. Des **Services** pour appliquer des règles métier (ex: calculer les frais d'une offre bancaire).
3. Des liaisons avec une **Base de données** (via JPA/Hibernate) pour sauvegarder les actions. 

C'est un incontournable du monde de l'entreprise : la grande majorité des banques, des assurances, et des géants du web utilisent des applications Java Web dans leurs coulisses.