# =========================================================================
# DOCKERFILE MULTI-STAGE POUR SPRING BOOT
# 
# Objectif : Créer une image légère et sécurisée sans y inclure le code source
# ni les outils de build volumineux (Maven).
# =========================================================================

# -------------------------------------------------------------------------
# ETAPE 1 : BUILDER (Construction de l'application)
# -------------------------------------------------------------------------
# On utilise l'image officielle Maven basée sur Eclipse Temurin (Java 17)
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# On définit le répertoire de travail dans ce conteneur temporaire
WORKDIR /app

# Optimisation du cache Docker : On copie d'abord uniquement le fichier pom.xml.
# Si le pom.xml ne change pas, Docker mettra en cache le téléchargement des dépendances Maven (très long sinon).
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Ensuite, on copie le reste du code source
COPY src ./src

# On compile l'application (crée un fichier .jar dans le dossier /app/target)
RUN mvn clean package -DskipTests

# -------------------------------------------------------------------------
# ETAPE 2 : RUNNER (L'image finale de production)
# -------------------------------------------------------------------------
# On utilise une image très légère ("alpine") avec uniquement Java 17 (pas de Maven)
FROM eclipse-temurin:17-jre-alpine

# Création d'un utilisateur non-root pour des raisons de sécurité
# Ne jamais exécuter un conteneur en "root" en production !
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# On copie EXCLUSIVEMENT le fichier .jar généré à l'étape 1 (builder) 
# vers notre image finale allégée.
COPY --from=builder /app/target/*.jar app.jar

# On expose le port 8080 (port par défaut de Spring Boot)
EXPOSE 8080

# Commande de démarrage du conteneur
ENTRYPOINT ["java", "-jar", "app.jar"]