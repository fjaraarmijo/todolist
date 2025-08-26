pipeline {
    agent any

    tools {
        maven 'Maven 3.8.4'
        jdk 'JDK 11'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    environment {
        IMAGE_NAME = "todolist-app"
        CONTAINER_NAME = "todolist-app"
        APP_PORT = "8091"         // Puerto del host
        INTERNAL_PORT = "8090"    // Puerto dentro del contenedor
        DEPENDENCY_PORT = "8081"  // Puerto del microservicio dependiente
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/fjaraarmijo/todolist.git'
            }
        }

        stage('Build with Maven') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker rmi ${CONTAINER_NAME} || true"
                sh "docker build -t ${IMAGE_NAME} ."
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    // Elimina contenedor anterior si existe
                    sh "docker rm -f ${CONTAINER_NAME} || true"

                    // Verifica si el puerto APP_PORT ya está ocupado
                    def portCheck = sh(script: "lsof -i :${APP_PORT} || true", returnStdout: true).trim()
                    if (portCheck) {
                        error "El puerto ${APP_PORT} ya está en uso. Aborta."
                    }

                    // Ejecuta el contenedor
                    sh "docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:${INTERNAL_PORT} ${IMAGE_NAME}"
                }
            }
        }
    }


    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo '✅ Build & deployment succeeded!'
        }
        failure {
            echo '❌ Build or test failed.'
            sh "docker rm -f ${CONTAINER_NAME} || true"
        }
    }
}
