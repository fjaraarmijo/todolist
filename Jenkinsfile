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
        APP_PORT = "8091"
        INTERNAL_PORT = "8090"
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
                script {
                    sh "docker build -t ${IMAGE_NAME} ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    sh "docker rm -f ${CONTAINER_NAME} || true"

                    def portCheck = sh(script: "lsof -i :${APP_PORT} || true", returnStdout: true).trim()
                    if (portCheck) {
                        error "El puerto ${APP_PORT} ya está en uso. Aborta."
                    }

                    sh "docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:${INTERNAL_PORT} ${IMAGE_NAME}"

                    echo "Esperando que la app esté disponible en http://localhost:${APP_PORT}..."

                    def maxRetries = 30
                    def retryCount = 0
                    def appUp = false

                    while (retryCount < maxRetries) {
                        def code = sh(
                            script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}",
                            returnStdout: true
                        ).trim()

                        if (code == '200') {
                            echo "La aplicación respondió correctamente (HTTP ${code})"
                            appUp = true
                            break
                        }

                        echo "Intento ${retryCount + 1}/${maxRetries}: La app no respondió todavía (HTTP ${code}). Esperando..."
                        sleep 1
                        retryCount++
                    }

                    if (!appUp) {
                        error "La aplicación no respondió en el tiempo esperado."
                    }
                }
            }
        }

        stage('E2E Test: Verificar interfaz web') {
            steps {
                script {
                    def response = sh(
                        script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}",
                        returnStdout: true
                    ).trim()

                    if (response != '200') {
                        error "Test fallido: La app no respondió correctamente (HTTP ${response})"
                    } else {
                        echo "Test exitoso: La app respondió correctamente (HTTP ${response})"
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Build & deployment succeeded!'
        }
        failure {
            echo 'Build or test failed.'
            sh "docker rm -f ${CONTAINER_NAME} || true"
        }
    }
}
