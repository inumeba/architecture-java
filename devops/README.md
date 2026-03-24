# 🚀 Projet : Usine logicielle CI/CD (Spring Boot + Jenkins + Docker + Kubernetes)

Bienvenue dans ce projet de démonstration DevOps ! L'objectif est d'automatiser le cycle de vie d'une application Java (Spring Boot) depuis le code source jusqu'au déploiement.

## 🌟 Vue d'ensemble de l'architecture

``mermaid
graph LR
    Dev[Développeur] -->|git push| Git[(Dépôt Git)]
    Git -->|Webhook| Jenkins((Jenkins CI/CD))
    
    subgraph "Pipeline Jenkins"
        Jenkins -->|1. mvn clean test| Maven[Build & Tests]
        Maven -->|2. mvn sonar:sonar| Sonar[Analyse SonarQube]
        Sonar -->|3. docker build| Docker[Construction Image]
    end
    
    Docker -->|4. docker push| Registry[(Docker Registry)]
    Jenkins -->|5. kubectl apply| K8s{Cluster Kubernetes}
``

## 🛠️ Outils utilisés
- **Java 17 & Spring Boot 3.2+** : Application backend avec une simple API REST /.
- **Maven Wrapper (mvnw)** : Pour compiler sans obliger l'agent CI à pré-installer Maven.
- **Docker & Docker Compose** : Containerisation de l'application et lancement local de l'infrastructure CI/CD (Jenkins, SonarQube, PostgreSQL).
- **Jenkins** : Orchestrateur principal défini "As Code" via un Jenkinsfile déclaratif.
- **Git / GitHub** : Stockage du code source et déclencheur des pipelines.

## 📂 Structure du dossier devops/
- src/, pom.xml, mvnw : Le code source Spring Boot et sa configuration Maven.
- Dockerfile : Recette multi-étapes pour packager l'application en image légère (base openjdk ou alpine).
- docker-compose.yml : Lance facilement un serveur Jenkins local (relié au Docker de l'hôte) et un SonarQube local.
- Jenkinsfile : Le script étape par étape de notre pipeline CI/CD (Pipeline As Code).
- k8s/ : (Bonus) Fichiers de déploiement pour un cluster Kubernetes (deployment.yaml, service.yaml).

## 🚀 1. Lancer l'infrastructure CI/CD localement
Pour démarrer Jenkins et SonarQube localement sur votre machine :
`ash
cd devops
docker compose up -d
`
- **Jenkins** sera disponible sur : [http://localhost:8080](http://localhost:8080)
- **SonarQube** sera disponible sur : [http://localhost:9000](http://localhost:9000)

*(Pour stopper les services sans perdre vos données : docker compose stop)*

## ⚙️ 2. Lancer l'application manuellement avec Docker
Si vous voulez voir le livrable en action sans passer par Jenkins :
`ash
# 1. Compiler l'image Docker
docker build -t mon-app-springboot:latest .

# 2. Lancer le conteneur sur le port 8081
docker run -d -p 8081:8080 mon-app-springboot:latest
`
Allez sur [http://localhost:8081](http://localhost:8081) pour voir le résultat en direct !

---

## 🧠 Leçons apprises & Résolution de problèmes (Troubleshooting)
Lors de la configuration de ce pipeline, nous avons surmonté plusieurs défis très classiques et structurants du DevOps :

1. **Chemins Windows vs Environnement Linux (\ vs /)**  
   Sous Windows, les chemins utilisent des backslashes \. Or, Jenkins clone le code de GitHub et tourne sous Linux (/var/jenkins_home/workspace/...). Par exemple, configurer devops\Jenkinsfile n'a pas fonctionné ; il fallait obligatoirement utiliser les conventions Unix : devops/Jenkinsfile.

2. **Droits d'exécution sous Linux (Maven Wrapper)**  
   Créer un script local mvnw sur Windows ne lui donne pas automatiquement les permissions Unix pour s'exécuter. Une fois que Jenkins tente de courir un ./mvnw, cela retourne l'erreur "Permission denied" ou "Command not found". La solution a été d'utiliser l'index Git pour forcer les droits d'exécution sur le fichier cible :
   `ash
   git update-index --chmod=+x devops/mvnw
   `

3. **Contexte de dossier dans les jobs Jenkins**  
   Jenkins clone l'entièreté du dossier rchitecture-java à la racine de son espace de travail. Si les commandes mvnw sont exécutées telles quelles, Jenkins cherchera le fichier pom.xml manquant à la racine. Solution : il est nécessaire de se placer explicitement dans les sous-dossiers :
   `groovy
   sh "cd devops && ./mvnw clean package"
   `

4. **Encodage et piège du BOM (Byte Order Mark)**  
   L'enregistrement et l'usage de commandes Set-Content dans Windows PowerShell inclut souvent par défaut un caractère encodé invisible (le BOM UTF-8) en tout premier. Le Moteur Groovy qui analyse le Jenkinsfile de façon brute n'a pas réussi à lire le mot-clé pipeline (NoSuchMethodError: pipeline). Résolu en forçant explicitement un encodage strict ASCII / ou utf-8 sans BOM pour le code source du pipeline.

5. **Accès au moteur Docker hôte (Docker Socket Binding)**  
   Pour que l'image Jenkins exécute en toute sécurité des commandes docker build (sans utiliser une complexe configuration "Docker-in-Docker"), il a été nécessaire de monter la socket de l'hôte dans les volumes du docker-compose.yml (- /var/run/docker.sock:/var/run/docker.sock) et de donner des droits admin à l'image (user: root).
