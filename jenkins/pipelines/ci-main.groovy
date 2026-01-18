/*
========================================================================
 ci-main.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Continuous Integration pipeline for the 'main' branch
 - Validate correctness and quality of the system
 - Provide fast feedback without executing changes

 What this pipeline DOES:
 - Run tests
 - Run security and quality scans
 - Build Docker images for verification

 What this pipeline DOES NOT do:
 - Push Docker images
 - Update GitOps repositories
 - Deploy to any environment
 - Apply infrastructure changes

 Principle:
 "main validates correctness, not execution"
========================================================================
*/

def call() {

    pipeline {
        agent any

        options {
            disableConcurrentBuilds()
            timestamps()
            ansiColor('xterm')
        }

        environment {
            ENVIRONMENT = "main"
        }

        stages {

            stage('Checkout Source Code') {
                steps {
                    checkout scm
                }
            }

            stage('Load Configuration') {
                steps {
                    script {
                        echo "Loading application configuration"

                        // Read declarative configuration files
                        APPS_CONFIG       = readYaml file: 'jenkins/config/apps.yaml'
                        IMAGES_CONFIG     = readYaml file: 'jenkins/config/images.yaml'

                        echo "Applications loaded: ${APPS_CONFIG.apps*.name}"
                    }
                }
            }

            stage('Run Tests') {
                steps {
                    script {
                        echo "Running unit and integration tests"

                        APPS_CONFIG.apps.each { app ->
                            echo "Executing tests for: ${app.name}"
                            load 'jenkins/shared/tests.groovy'
                                .run(app)
                        }
                    }
                }
            }

            stage('Security & Quality Scans') {
                steps {
                    script {
                        echo "Running security and quality scans"

                        APPS_CONFIG.apps.each { app ->
                            echo "Scanning application: ${app.name}"
                            load 'jenkins/shared/security.groovy'
                                .scan(app)
                        }
                    }
                }
            }

            stage('Docker Build (Verification Only)') {
                steps {
                    script {
                        echo "Building Docker images for verification only"

                        APPS_CONFIG.apps.each { app ->
                            echo "Building image for: ${app.name}"
                            load 'jenkins/shared/docker.groovy'
                                .build(app, IMAGES_CONFIG, pushImage = false)
                        }
                    }
                }
            }
        }

        post {
            success {
                echo "✅ CI validation successful for main branch"
            }

            failure {
                echo "❌ CI validation failed for main branch"
            }

            always {
                cleanWs()
            }
        }
    }
}
