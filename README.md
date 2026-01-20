Markdown

# ConsumeSafe (Boycott Checker + Tunisian Alternatives)

ConsumeSafe est une application web qui permet de vérifier si un produit est présent sur une liste de boycott et, dans ce cas, d’afficher une alerte et de proposer des alternatives tunisiennes (selon la catégorie du produit).  
Le backend est développé avec **Spring Boot**, les données sont chargées depuis des fichiers **CSV**, la persistance se fait via **MySQL**, et le projet est industrialisé avec **Docker**, **Kubernetes** et une pipeline **Jenkins** incluant un scan sécurité via **Trivy**.

---

## Objectifs
- Vérifier rapidement si un produit/marque est boycotté.
- Mettre en évidence les produits boycottés (message + niveau/raison).
- Proposer des alternatives tunisiennes.
- Automatiser build/test/scan/push/deploy via CI/CD.

---

## Fonctionnalités
- **Vérification d’un produit** via API ou UI web.
- **Liste des produits boycottés** (consultation).
- **Liste des produits tunisiens** (alternatives).
- Import automatique depuis :
  - `boycott_products.csv`
  - `tunisian_products.csv`

---

## Technologies
- **Java**: 17+ (Spring Boot 3.x)
- **Backend**: Spring Web, Spring Data JPA, Validation
- **Template/UI**: HTML/CSS/JS dans `resources/templates` + `resources/static`
- **DB**: MySQL 8
- **Build**: Maven
- **Container**: Docker (multi-stage)
- **Orchestration**: Kubernetes (Minikube/Docker Desktop)
- **CI/CD**: Jenkins pipeline
- **Security**: Trivy scan image

---

## Structure du projet
consumesafe/
├── src/main/java/com/consumesafe/...
├── src/main/resources/
│ ├── application.properties
│ ├── data/
│ │ ├── boycott_products.csv
│ │ └── tunisian_products.csv
│ ├── templates/
│ │ └── index.html
│ └── static/
│ ├── css/style.css
│ └── js/app.js
├── k8s/
│ ├── mysql-deployment.yaml
│ ├── deployment.yaml
│ └── service.yaml
├── Dockerfile
├── Jenkinsfile
└── pom.xml

text


---

## Exécution locale (sans Docker)

### 1) Prérequis
- Java 17+ (ou 21)
- Maven
- MySQL 8 (si vous utilisez MySQL en local)

### 2) Configuration
Dans `src/main/resources/application.properties` :

```properties
server.port=8081

spring.datasource.url=jdbc:mysql://localhost:3306/consumesafe_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
3) Build et run
Bash

mvn clean package -DskipTests
java -jar target/*.jar
Accès UI:

http://localhost:8081
Exécution avec Docker (MySQL + App)
1) Build image
Bash

docker build -t consumesafe:latest .
2) Lancer MySQL + App (réseau Docker)
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
Accès:

http://localhost:8081
Déploiement Kubernetes (Docker Desktop / Minikube)
1) Déployer
Bash

kubectl apply -f k8s/mysql-deployment.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
2) Vérifier
Bash

kubectl get pods
kubectl get svc
kubectl logs -l app=consumesafe --tail=100
3) Accès
Si service NodePort (exemple 30080) :

http://localhost:30080
CI/CD Jenkins (résumé)
Étapes pipeline
Checkout Git
Maven build + tests
Build image Docker
Scan Trivy (HIGH/CRITICAL)
Push Docker Hub
Deploy Kubernetes (kubectl apply)
Verify (pods ready)
Credentials Jenkins requis
docker-credentials : Docker Hub username + Access Token
kubeconfig : kubeconfig du cluster (Secret file)
github-credentials : si repo privé
Endpoints API (exemples)
Vérifier un produit :
GET /api/check?product=Coca-Cola
Liste tunisienne :
GET /api/tunisian-products
Liste boycott :
GET /api/boycott-list
Dépannage rapide
Pod CrashLoopBackOff
Bash

kubectl get pods
kubectl logs -l app=consumesafe --tail=200
kubectl describe pod -l app=consumesafe
Erreur MySQL Public Key Retrieval is not allowed
Ajouter dans l’URL JDBC :
allowPublicKeyRetrieval=true&serverTimezone=UTC

Port 3306 déjà utilisé sur Windows
Mapper MySQL Docker sur 3307:
-p 3307:3306

Jenkins: JAVA_HOME is not defined correctly
Configurer JDK dans:
Manage Jenkins → Tools → JDK installations

Jenkins: kubectl “Authentication required”
Utiliser le kubeconfig dans les stages Deploy et Verify :
withKubeConfig([credentialsId: 'kubeconfig']) { ... }

Auteur / Repo
Repo GitHub : https://github.com/Wajihsaid/consumesafe
