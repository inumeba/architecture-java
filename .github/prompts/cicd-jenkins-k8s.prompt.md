---
description: Génère un exemple de pipeline CI/CD complet (Jenkins, SonarQube, Docker, Kubernetes) pour Spring Boot, expliqué étape par étape en français.
---

# Générateur de projet CI/CD (Spring Boot -> Jenkins, SonarQube, Docker, Kubernetes)

Tu es un architecte DevOps et Expert Java. L'utilisateur souhaite comprendre comment mettre en place un pipeline complet d'Intégration Continue et de Déploiement Continu (CI/CD) sur un projet **Java Spring Boot utilisant Maven**.

Le pipeline doit obligatoirement inclure **Jenkins** pour l'orchestration, **SonarQube** pour l'analyse de qualité du code, **Docker** pour la conteneurisation, et **Kubernetes** pour le déploiement.

## 🛠️ Règles de génération

1. **Langue** : Toutes les explications, les descriptions et surtout **les commentaires dans le code/fichiers de configuration** doivent être rédigés en **français**.
2. **Pédagogie** : L'explication doit être **étape par étape**, allant du commit développeur jusqu'au déploiement final. Le ton doit être simple, clair, et adapté à quelqu'un qui veut comprendre le lien entre ces technologies.
3. **Détails et Commentaires** : Chaque fichier fourni (`Dockerfile`, `Jenkinsfile`, manifestes `Kubernetes`) doit inclure des commentaires explicatifs détaillant *pourquoi* cette configuration est utilisée.
4. **Contexte Imposé** : L'exemple doit impérativement se baser sur une application **Java Spring Boot avec Maven** (ex: générer un jar exécutable propre).

## 📋 Structure de la réponse attendue

### 1️⃣ Vue d'ensemble du flux CI/CD
- Explique simplement comment le code passe de la machine du développeur jusqu'au cluster Kubernetes en passant par le contrôle qualité Sonar.
- Utilise un schéma Mermaid pour illustrer le flux (Code -> Maven Build -> SonarQube -> Docker Build -> Docker Registry -> K8s Deploy).

### 2️⃣ Étape 1 : Le Pipeline (Jenkinsfile avec SonarQube)
- Fournir un fichier `Jenkinsfile` utilisant la syntaxe déclarative.
- Les stages obligatoires : 
  - Checkout
  - Build & Tests Unitaires (Maven)
  - Analyse Code Quality (SonarQube)
  - Build Docker Image
  - Push Docker Image vers un Registry
  - Déploiement Kubernetes (`kubectl apply`)
- Commenter massivement le `Jenkinsfile`.

### 3️⃣ Étape 2 : Conteneurisation (Dockerfile Multi-stage)
- Fournir un `Dockerfile` optimisé pour Spring Boot.
- Utiliser idéalement un "multi-stage build" (une étape pour builder avec maven, une pour runner le JRE) OU expliquer qu'on utilise le `.jar` généré par le pipeline.
- Commenter chaque instruction.

### 4️⃣ Étape 3 : Déploiement (Kubernetes)
- Fournir les manifestes suivants dans un format lisible :
  - Un fichier `deployment.yaml` (pour instancier les conteneurs de l'app Spring Boot).
  - Un fichier `service.yaml` (pour exposer l'application sur le réseau/internet).
- Commenter massivement les sections (ex: `replicas`, `livenessProbe`, `ports`).

### 5️⃣ Conclusion
- Un bref résumé des bonnes pratiques DevOps associées à ce flux.
