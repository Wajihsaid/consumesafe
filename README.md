# ConsumeSafe 🛡️

ConsumeSafe est une application web qui permet de **vérifier si un produit est boycotté** et, si oui, de **le mettre en évidence** et **proposer des alternatives tunisiennes**.  
Le projet est conçu avec **Spring Boot (Backend)**, **Frontend simple (HTML/CSS/JS dans `resources/templates`)**, une base **MySQL**, conteneurisation **Docker**, déploiement **Kubernetes (Minikube)**, et une pipeline **CI/CD Jenkins** incluant un scan sécurité **Trivy**.

---

## Table des matières
- [Fonctionnalités](#fonctionnalités)
- [Stack technique](#stack-technique)
- [Architecture du projet](#architecture-du-projet)
- [Pré-requis](#pré-requis)
- [Démarrage rapide (Local)](#démarrage-rapide-local)
- [Configuration Base de données MySQL](#configuration-base-de-données-mysql)
- [Données CSV](#données-csv)
- [Exécution via Docker](#exécution-via-docker)
- [Déploiement Kubernetes (Minikube)](#déploiement-kubernetes-minikube)
- [Pipeline Jenkins CI/CD](#pipeline-jenkins-cicd)
- [Sécurité / Hardening](#sécurité--hardening)
- [API](#api)
- [Troubleshooting](#troubleshooting)

---

## Fonctionnalités
- Vérification si un produit est sur une **liste de boycott**.
- Affichage d’un message **Boycotté / Non boycotté**.
- Suggestion d’**alternatives tunisiennes** (basées sur catégorie).
- Import des produits depuis fichiers **CSV** au démarrage.
- UI simple en **HTML/CSS/JavaScript** servie par Spring Boot (Thymeleaf templates).

---

## Stack technique
- **Backend** : Java 17+ / Spring Boot 3.x, Spring Web, Spring Data JPA
- **DB** : MySQL 8
- **Frontend** : HTML/CSS/JS dans `src/main/resources/templates` + `static`
- **Build** : Maven
- **CI/CD** : Jenkins Pipeline
- **Container** : Docker
- **Kubernetes** : Minikube + manifests YAML
- **Security scan** : Trivy

---

## Architecture du projet
consumesafe/
├── src/
│ ├── main/
│ │ ├── java/com/consumesafe/...
│ │ └── resources/
│ │ ├── application.properties
│ │ ├── data/
│ │ │ ├── boycott_products.csv
│ │ │ └── tunisian_products.csv
│ │ ├── templates/
│ │ │ └── index.html
│ │ └── static/
│ │ ├── css/style.css
│ │ └── js/app.js
├── k8s/
│ ├── mysql-deployment.yaml
│ ├── deployment.yaml
│ ├── service.yaml
│ └── configmap.yaml
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── README.md

text


---

## Pré-requis
### Local
- Java 17+ (ou 21)
- Maven 3.8+
- MySQL 8 (optionnel si vous utilisez Docker)
- Git

### Docker / Kubernetes
- Docker Desktop (Windows) ou Docker Engine (Linux)
- kubectl
- minikube

### Jenkins
- Jenkins installé + plugins :
  - Pipeline
  - Git
  - Docker Pipeline
  - Kubernetes CLI (optionnel)
  - Email Extension (optionnel)
- Credentials :
  - `docker-credentials` (Docker Hub token)
  - `github-credentials` (si repo privé)
  - `kubeconfig` (si déploiement kubectl via Jenkins)

---

## Démarrage rapide (Local)

### 1) Cloner le projet
```bash
git clone https://github.com/Wajihsaid/consumesafe.git
cd consumesafe
2) Lancer MySQL (si installé en local)
Assurez-vous que MySQL tourne sur localhost:3306.

3) Configurer application.properties
Exemple MySQL :

properties

spring.datasource.url=jdbc:mysql://localhost:3306/consumesafe_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
server.port=8081
4) Build + Run
Bash

mvn clean package -DskipTests
java -jar target/*.jar
Accès :

http://localhost:8081
Configuration Base de données MySQL
Création DB (exemple)
SQL

CREATE DATABASE consumesafe_db;
Optionnel : utilisateur dédié

SQL

CREATE USER 'consumesafe'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON consumesafe_db.* TO 'consumesafe'@'localhost';
FLUSH PRIVILEGES;
Données CSV
Les données sont chargées au démarrage via CsvLoaderService :

src/main/resources/data/boycott_products.csv
src/main/resources/data/tunisian_products.csv
Vous pouvez enrichir ces fichiers pour ajouter plus de produits.

Exécution via Docker
Build image
Bash

docker build -t consumesafe:latest .
Lancer MySQL + App (Docker network)
Bash

docker network create consumesafe-net

docker run -d --name consumesafe-mysql --network consumesafe-net \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=consumesafe_db \
  -p 3307:3306 mysql:8.0

docker run -d --name consumesafe --network consumesafe-net \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://consumesafe-mysql:3306/consumesafe_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME="root" \
  -e SPRING_DATASOURCE_PASSWORD="rootpassword" \
  consumesafe:latest
Accès :

http://localhost:8081
Déploiement Kubernetes (Minikube)
1) Démarrer Minikube
Bash

minikube start --driver=docker
2) (Important) Charger l’image dans Minikube
Si vous avez buildé l’image sur votre machine :

Bash

minikube image load consumesafe:latest
Ou builder directement dans l’environnement minikube :

Bash

eval $(minikube -p minikube docker-env)
docker build -t consumesafe:latest .
3) Déployer
Bash

kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
4) Vérifier
Bash

kubectl get pods
kubectl get svc
kubectl logs -l app=consumesafe --tail=100
5) Accéder à l’application
Bash

minikube service consumesafe-service --url
Pipeline Jenkins CI/CD
Étapes typiques
Checkout Git
Maven build + tests
Docker build
Scan Trivy
Push Docker Hub
Deploy Kubernetes
Problèmes courants
JAVA_HOME is not defined correctly : configurer JDK dans Jenkins Tools.
401 Unauthorized pendant docker build : Docker Hub token incorrect ou docker login absent.
Corriger en utilisant docker.withRegistry(..., 'docker-credentials') même pour le build.
Sécurité / Hardening
Scan Docker image avec Trivy (HIGH/CRITICAL).
User non-root recommandé dans Dockerfile.
Kubernetes securityContext : drop capabilities, disallow privilege escalation.
Secrets Kubernetes pour les mots de passe DB.
API
Page web :

GET /
Vérifier un produit :

GET /api/check?product=Coca-Cola
Liste boycott :

GET /api/boycott-list
Produits tunisiens :

GET /api/tunisian-products
Troubleshooting
Pod CrashLoopBackOff (Kubernetes)
Vérifier MySQL Ready :
Bash

kubectl get pods -l app=mysql
kubectl logs deployment/mysql --tail=100
Vérifier service DNS :
Bash

kubectl run dns-test --rm -it --image=busybox:1.36 --restart=Never -- sh
nslookup mysql
Erreur MySQL Public Key Retrieval is not allowed
Ajouter dans JDBC :
allowPublicKeyRetrieval=true

Port 3306 déjà utilisé (Windows)
Utiliser un autre port :
-p 3307:3306

Auteur
Wajih Said
Repo: https://github.com/Wajihsaid/consumesafe.git
