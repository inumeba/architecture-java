# Application Bancaire : Offre TPE & Acquisition Monétique 🏦

Ce projet `offreTP` (Spring Boot 3 + Java 17) est le module de contractualisation dédié aux **Conseillers en agence bancaire** permettant de proposer et de signer des offres de Terminaux de Paiement Électronique (TPE) avec des **Clients Commerçants**.

## 🏗️ Architecture (Domain-Driven Design)

L'application a été expressément divisée pour respecter une **Clean Architecture** (Séparation entre l'approche "Web" pour les conseillers, la couche de règles métiers, et l'API pour les clients et systèmes tiers).

### 1. La Couche Modèle (`com.banque.acquisition.domaine.modele`)
- Écrite intégralement en **Records Java 17** pour assurer l'immuabilité et réduire le code de plomberie (*Boilerplate*).
- **`ClientCommercant`** : Représente le commerce. Possède sa logique inhérente (ex: *Est-il un client "récent" ?*).
- **`OffreTPE`** : Contient les informations contractuelles brutes (Frais, Nom, Commission).
- **`FormulaireSouscriptionTPE`** : C'est un *DTO* (Data Transfer Object) utilisé pour réceptionner les champs du formulaire rempli par le conseiller en front-end.

### 2. La Couche Service / Règles Métier (`com.banque.acquisition.domaine.service`)
- **`ServiceCommercant`** : Joue le rôle de répertoire (Repository bouchonné pour le moment) afin de trouver le profil monétique du commerçant (Recherche par SIREN).
- **`ServiceOffreMonetique`** : Agit comme le coordinateur. C'est ici que l'on vérifie si le TPE n'est pas en *"RUPTURE"* de stock. S'il l'est, une erreur maîtrisée (Exception) est remontée aux applications clientes / web.

### 3. La Couche Web / Vues / UI Conseiller (`com.banque.acquisition.web.controleur`)
- Contient le **`ContractualisationTPEControleur`** et les modèles HTML intégrés **`src/main/resources/templates/`**.
- Technologie utilisée : **Thymeleaf**.
- Fonctionne sur le principe MVC classique. Il récupère les informations des "Services", et va d'abord auditer la requête (Le Siren est-il bon ? Le client est-il éligible ?). Si c'est ok, la vue `formulaire-tpe-standard` est renvoyée au conseiller avec tous les prix et champs injectés dynamiquement.

### 4. La Couche API / REST Publique (`com.banque.acquisition.api.controleur`)
- Complètement indépendante du serveur MVC Web classique.
- Contient le **`OffreMonetiqueAPI`**.
- Conçue pour qu'un outil "Tiers" ou qu'une App Mobile consomme la donnée en flux JSON (Ex: Lister toutes les offres bancaires TPE sur une tablette pour un comparateur B2B).

---

## 🚀 Tester le projet localement
Le projet inclut un fichier de paramétrage `application.properties` configuré pour forcer le port d'exécution sur le **8083**.

1. **Lancement du serveur :**  
   Depuis la racine `C:\Users\abbac\Documents\GitHub\IA\offreTP`, ouvrez un terminal :
   ```bash
   mvn clean spring-boot:run
   ```

2. **Accès et Sécurité (Nouveau 🔒) :**
   Le portail est protégé par Spring Security. L'API est publique, mais l'interface Conseiller exige de s'authentifier.
   - **Login** : `conseiller`
   - **Mot de passe** : `banque123`

3. **Test N°1 (Le site web des Conseillers Bancaires) :**  
   Ouvrir un navigateur web pour visualiser l'interface de souscription de contrat avec un SIREN valide (simulé) :
   - [http://localhost:8083/agence/contractualisation/tpe/standard?siren=123456789](http://localhost:8083/agence/contractualisation/tpe/standard?siren=123456789)
   - *Test d'erreur fonctionnelle :* [http://localhost:8083/agence/contractualisation/tpe/standard?siren=000000000](http://localhost:8083/agence/contractualisation/tpe/standard?siren=000000000)

4. **Test N°2 (L'API Rest) :**  
   Consulter le catalogue de solutions Monétiques en JSON :
   - [http://localhost:8083/api/v1/acquisition/offres-monetiques](http://localhost:8083/api/v1/acquisition/offres-monetiques)

5. **Test N°3 (Console Base de Données H2) :**  
   - URL : [http://localhost:8083/h2-console](http://localhost:8083/h2-console)
   - *JDBC URL* : `jdbc:h2:mem:banquedb` ,  *User* : `sa` ,  *Mot de passe* : `(vide)`

## 💡 Notes sur la stack Technique

- **Java 17 (LTS)** : Choisi prioritairement pour activer la "Feature" `record` sur le modèle fonctionnel.
- **Spring Boot 3.2+** : Sécurité, configuration minimale requise, serveur Tomcat embarqué, et intégration Web MVC rapide.
- **Thymeleaf** : Standard éprouvé d'intégration UI côté serveur, permettant un rendu sécurisé des informations sensibles que manipule un Conseiller (Anti failles XSS, etc...).