pipeline {
    agent any

    environment {
        // ⚠️ MODIFIER: Remplacer par votre username Docker Hub
        DOCKER_HUB_USERNAME = "wajihsaid"  // 🔴 À MODIFIER
        DOCKER_IMAGE = "${DOCKER_HUB_USERNAME}/consumesafe"
        DOCKER_TAG = "${BUILD_NUMBER}"
        TRIVY_VERSION = "0.48.0"
    }

    tools {
        maven 'Maven-3.9'  // Doit correspondre au nom dans Jenkins
        jdk 'JDK-21'       // Doit correspondre au nom dans Jenkins
    }

    stages {
        stage('🔍 Checkout') {
            steps {
                echo '📥 Cloning repository...'
                // Option 1: Repo Public
                git branch: 'main', url: 'https://github.com/Wajihsaid/consumesafe.git'  // 🔴 À MODIFIER

                // Option 2: Repo Privé (décommenter si nécessaire)
                // git branch: 'main',
                //     url: 'https://github.com/votreusername/consumesafe.git',
                //     credentialsId: 'github-credentials'
            }
        }

        stage('🏗️ Build') {
            steps {
                echo '🔨 Building with Maven...'
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('🧪 Unit Tests') {
            steps {
                echo '🧪 Running tests...'
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('📊 Code Quality - SonarQube') {
            steps {
                script {
                    echo '📊 Analyzing code quality...'
                    // Décommenter si SonarQube est configuré
                    // withSonarQubeEnv('SonarQube') {
                    //     sh 'mvn sonar:sonar'
                    // }
                }
            }
        }

        stage('🐳 Build Docker Image') {
            steps {
                echo '🐳 Building Docker image...'
                script {
                    // Build avec le numéro de build
                    dockerImage = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    // Build avec tag latest
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }

        stage('🔒 Security Scan - Trivy') {
            steps {
                echo '🔒 Scanning Docker image with Trivy...'
                script {
                    sh """
                        # Install Trivy if not exists
                        if ! command -v trivy &> /dev/null; then
                            echo "Installing Trivy..."
                            wget -q https://github.com/aquasecurity/trivy/releases/download/v${TRIVY_VERSION}/trivy_${TRIVY_VERSION}_Linux-64bit.tar.gz
                            tar zxvf trivy_${TRIVY_VERSION}_Linux-64bit.tar.gz
                            sudo mv trivy /usr/local/bin/ || mv trivy /tmp/trivy
                            export PATH=\$PATH:/tmp
                        fi

                        # Scan image
                        trivy image --severity HIGH,CRITICAL --format json --output trivy-report.json ${DOCKER_IMAGE}:${DOCKER_TAG} || true
                        trivy image --severity HIGH,CRITICAL ${DOCKER_IMAGE}:${DOCKER_TAG} || true
                    """
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'trivy-report.json', allowEmptyArchive: true
                }
            }
        }

        stage('📤 Push to Docker Hub') {
            steps {
                echo '📤 Pushing Docker image to Docker Hub...'
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-credentials') {
                        // Push avec le tag du build
                        sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                        // Push avec tag latest
                        sh "docker push ${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }

        stage('☸️ Deploy to Kubernetes') {
          steps {
            echo '☸️ Deploying to Kubernetes...'
            withKubeConfig([credentialsId: 'kubeconfig']) {
              sh '''
                kubectl version --client
                kubectl cluster-info
                kubectl get nodes

                kubectl apply -f k8s/mysql-deployment.yaml
                kubectl apply -f k8s/configmap.yaml
                kubectl apply -f k8s/deployment.yaml
                kubectl apply -f k8s/service.yaml

                kubectl rollout status deployment/mysql --timeout=5m || true
                kubectl rollout status deployment/consumesafe --timeout=5m
                kubectl get pods
                kubectl get svc
              '''
            }
          }
        }

        stage('✅ Verify Deployment') {
            steps {
                echo '✅ Verifying deployment...'
                script {
                    sh """
                        echo "=== Pods Status ==="
                        kubectl get pods -l app=consumesafe

                        echo "=== Services ==="
                        kubectl get services

                        echo "=== Deployment Status ==="
                        kubectl get deployment consumesafe

                        # Wait for pods to be ready
                        kubectl wait --for=condition=ready pod -l app=consumesafe --timeout=300s || true
                    """
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline completed successfully!'
            script {
                // Option 1: Email simple (décommenter si configuré)
                // emailext (
                //     subject: "✅ ConsumeSafe Build #${BUILD_NUMBER} - SUCCESS",
                //     body: """
                //         Build successful!
                //
                //         Build Number: ${BUILD_NUMBER}
                //         Docker Image: ${DOCKER_IMAGE}:${DOCKER_TAG}
                //
                //         Check console output at: ${BUILD_URL}
                //     """,
                //     to: "wajih.said@ensi-uma.tn"
                // )

                echo "✅ Build #${BUILD_NUMBER} completed successfully!"
                echo "🐳 Docker Image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
            }
        }

        failure {
            echo '❌ Pipeline failed!'
            script {
                // emailext (
                //     subject: "❌ ConsumeSafe Build #${BUILD_NUMBER} - FAILED",
                //     body: """
                //         Build failed!
                //
                //         Build Number: ${BUILD_NUMBER}
                //
                //         Check console output at: ${BUILD_URL}
                //     """,
                //     to: "wajih.said@ensi-uma.tn"
                // )

                echo "❌ Build #${BUILD_NUMBER} failed!"
            }
        }

        always {
            echo '🧹 Cleaning workspace...'
            cleanWs()
        }
    }
}