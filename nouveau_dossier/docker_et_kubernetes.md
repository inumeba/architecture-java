# 🐳 Docker et ☸️ Kubernetes : Guide Essentiel

Ce guide explique les concepts de Docker et Kubernetes, leurs différences, et fournit des exemples concrets de commandes et de fichiers de configuration pour des applications (comme un projet Java Spring Boot).

---

## 🧐 La Métaphore Simple
- **Docker** : C'est comme un **conteneur maritime**. Il permet d'emballer votre application (la marchandise) avec tout ce dont elle a besoin pour fonctionner (Java, bibliothèques, OS de base) pour qu'elle puisse s'exécuter de manière identique peu importe où on la pose (sur le PC du développeur, sur un serveur de test, etc.).
- **Kubernetes (K8s)** : C'est le **port de commerce et les grues automatiques**. Si vous avez 500 conteneurs à gérer, Docker seul ne suffit plus. Kubernetes s'occupe de répartir les conteneurs sur vos serveurs, de les redémarrer s'ils plantent, ou d'en créer de nouveaux s'il y a un pic de trafic.

---

## 🐳 PARTIE 1 : DOCKER

### 1.1 Le Fichier `Dockerfile` (La recette de cuisine)
Un `Dockerfile` explique comment construire l'image de votre application.

**Exemple pour une application Java Spring Boot :**
```dockerfile
# On part d'une image contenant déjà Java 17
FROM eclipse-temurin:17-jdk-alpine

# On définit le répertoire de travail dans le conteneur
WORKDIR /app

# On copie le fichier .jar généré par Maven/Gradle vers le conteneur
COPY target/mon-application.jar app.jar

# On expose le port sur lequel l'application écoute (ex: Tomcat sur 8080)
EXPOSE 8080

# La commande qui sera exécutée au démarrage du conteneur
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 1.2 Commandes Docker Utiles (Terminal)
```bash
# 1. Construire l'image à partir du Dockerfile (le '.' désigne le dossier courant)
docker build -t mon-app-java:1.0 .

# 2. Lancer un conteneur à partir de l'image (associe le port 8080 de la machine au 8080 du conteneur)
docker run -d -p 8080:8080 --name mon_conteneur_app mon-app-java:1.0

# 3. Voir la liste des conteneurs qui tournent en ce moment
docker ps

# 4. Voir les logs (ce que le programme écrit dans la console)
docker logs -f mon_conteneur_app

# 5. Arrêter le conteneur
docker stop mon_conteneur_app

# 6. Supprimer le conteneur définitivement
docker rm mon_conteneur_app
```

### 1.3 Docker Compose (Pour gérer plusieurs conteneurs)
Très utile pour le développement local, quand l'application a besoin d'une Base de Données (ex: PostgreSQL).

**Fichier `docker-compose.yml` :**
```yaml
version: '3.8'
services:
  ma-base-de-donnees:
    image: postgres:15
    environment:
      POSTGRES_USER: root
      POSTGRES_PASSWORD: password123
      POSTGRES_DB: mabdd
    ports:
      - "5432:5432"

  mon-app-backend:
    build: . # Construit l'image depuis le Dockerfile du même dossier
    ports:
      - "8080:8080"
    depends_on:
      - ma-base-de-donnees # S'assure que la BDD démarre en premier
```
**Commande pour tout lancer d'un coup :** `docker-compose up -d`

---

## ☸️ PARTIE 2 : KUBERNETES (K8s)

Une fois que les images Docker sont poussées sur un registre (comme DockerHub), on utilise Kubernetes pour les déployer sur des clusters (groupes de serveurs).

### Les Concepts Clés :
* **Pod** : La plus petite unité de Kubernetes. Un Pod contient un ou plusieurs conteneurs Docker (souvent un seul).
* **Deployment** : Le contrôleur qui gère les Pods. Il permet de dire "Je veux que 3 copies (replicas) de mon application tournent en permanence".
* **Service** : C'est le point d'accès. Comme les Pods meurent et ressuscitent avec des adresses IP changeantes, le *Service* offre une adresse IP stable pour leur envoyer du trafic.

### 2.1 Le fichier Déploiement : `deployment.yaml`
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-java-deployment
spec:
  replicas: 3 # On veut toujours 3 instances (Pods) en fonctionnement
  selector:
    matchLabels:
      app: mon-app-java
  template:
    metadata:
      labels:
        app: mon-app-java
    spec:
      containers:
      - name: mon-app-java
        image: mon-repo/mon-app-java:1.0 # Nom de l'image (trouvable sur le réseau)
        ports:
        - containerPort: 8080
```

### 2.2 Le fichier Service : `service.yaml`
```yaml
apiVersion: v1
kind: Service
metadata:
  name: app-java-service
spec:
  type: LoadBalancer # Permet d'exposer le service sur internet (via votre Cloud Provider)
  selector:
    app: mon-app-java # Cible les pods qui ont ce label
  ports:
    - protocol: TCP
      port: 80 # Le port externe accessible
      targetPort: 8080 # Le port interne du conteneur (défini dans le Dockerfile)
```

### 2.3 Commandes `kubectl` Utiles (Terminal)
Kubectl est l'outil en ligne de commande pour interagir avec un cluster Kubernetes.

```bash
# 1. Appliquer/Créer les ressources décrites dans les fichiers YAML
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

# 2. Voir l'état des dépendances (Les Pods, les Services)
kubectl get pods
kubectl get services

# 3. Voir les détails si un Pod ne démarre pas (CrashLoopBackOff)
kubectl describe pod <nom-du-pod>

# 4. Voir les logs en direct d'un Pod
kubectl logs -f <nom-du-pod>

# 5. Redémarrer manuellement tous les pods d'un déploiement
kubectl rollout restart deployment app-java-deployment

# 6. Mettre à l'échelle (scale) : passer subitement à 5 instances de l'application
kubectl scale deployment app-java-deployment --replicas=5
```