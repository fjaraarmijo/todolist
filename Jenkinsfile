pipeline {
    agent {
        docker {
            image 'maven:3.8.4-jdk-11'
        }
    }

    environment {
        // Puedes agregar aquí variables de entorno necesarias
    }

    options {
        // Descartar ejecuciones antiguas
        buildDiscarder(logRotator(numToKeepStr: '10'))

        // Permitir ejecuciones concurrentes
        disableConcurrentBuilds()
    }

    triggers {
        // Lanzar al detectar cambios en el repositorio
        pollSCM('H/5 * * * *') // Consulta el repositorio cada 5 minutos
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
                // Se asume que Jenkins ya tiene configurado el scanner
                // y se está usando el SonarQube integrado con Jenkins
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
                    dockerImage = docker.build("todolist-app")
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                sh 'docker run -d -p 8090:8090 todolist-app'
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
