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

    triggers {
        pollSCM('H/5 * * * *') // Consulta el repositorio cada 5 minutos
    }

    environment {
        SONAR_SCANNER_HOME = tool 'SonarQube Scanner'
        IMAGE_NAME = "todolist-app"
        CONTAINER_NAME = "todolist-container"
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
                sh 'mvn clean package'
            }
        }

        stage('SonarQube Analysis') {
            environment {
                SONARQUBE_SCANNER_HOME = tool 'SonarQube Scanner'
            }
            steps {
                withSonarQubeEnv('My SonarQube Server') {
                    sh "${SONARQUBE_SCANNER_HOME}/bin/sonar-scanner"
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Construir la imagen Docker con la etiqueta IMAGE_NAME
                    sh "docker build -t ${IMAGE_NAME} ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    // Detener y eliminar cualquier contenedor corriendo con el mismo nombre para evitar conflictos
                    sh "docker rm -f ${CONTAINER_NAME} || true"
                    // Ejecutar el contenedor en background
                    sh "docker run -d --name ${CONTAINER_NAME} -p 8080:8080 ${IMAGE_NAME}"
                }
            }
        } 

        stage('Run App (local JAR)') {
            steps {
                sh 'nohup java -jar target/*.jar &'
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Build succeeded!'
        }
        failure {
            echo 'Build failed.'
        }
    }
}
