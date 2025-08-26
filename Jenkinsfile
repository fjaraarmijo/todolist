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
        APP_PORT = "8090"
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
                sh 'mvn clean package -DskipTests'  // Solo compila, sin correr tests unitarios aún
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
                    // Eliminar contenedor anterior si existe
                    sh "docker rm -f ${CONTAINER_NAME} || true"

                    // Verifica si el puerto está ocupado
                    def portCheck = sh(script: "lsof -i :${APP_PORT} || true", returnStdout: true).trim()
                    if (portCheck) {
                        error "El puerto ${APP_PORT} ya está en uso. Aborta."
                    }

                    // Corre contenedor
                    sh "docker run -d --name ${CONTAINER_NAME} -p ${APP_PORT}:${APP_PORT} ${IMAGE_NAME}"

                    // Espera que la app esté disponible (hasta 30s)
                    sh """
                        for i in {1..30}; do
                            curl -s http://localhost:${APP_PORT} && break
                            echo "Esperando que la app esté arriba..."
                            sleep 1
                        done
                    """
                }
            }
        }

        stage('E2E Test: Verificar interfaz web') {
            steps {
                script {
                    // Aquí un test simple con curl, puedes reemplazar por Postman, Selenium, etc.
                    def response = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${APP_PORT}", returnStdout: true).trim()
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
            // Detiene y elimina contenedor si hubo fallo
            sh "docker rm -f ${CONTAINER_NAME} || true"
        }
    }
}
