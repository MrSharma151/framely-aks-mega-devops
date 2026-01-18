/*
========================================================================
 ci-stage.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Continuous Integration + Continuous Deployment for STAGE
 - Build, test, scan applications
 - Push Docker images to registry
 - Update Kubernetes manifests (GitOps)
 - Trigger ArgoCD auto-sync via Git change

 Environment Characteristics:
 - Automatic execution
 - No manual approvals
 - Fast feedback loop

 Principle:
 "Stage follows Continuous Deployment"
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
            ENVIRONMENT = "stage"
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
                        echo "Loading pipeline configuration"

                        APPS_CONFIG   = readYaml file: 'jenkins/config/apps.yaml'
                        IMAGES_CONFIG = readYaml file: 'jenkins/config/images.yaml'

                        echo "Applications loaded: ${APPS_CONFIG.apps*.name}"
                    }
                }
            }

            stage('Run Tests') {
                steps {
                    script {
                        APPS_CONFIG.apps.each { app ->
                            load 'jenkins/shared/tests.groovy'
                                .run(app)
                        }
                    }
                }
            }

            stage('Security & Quality Scans') {
                steps {
                    script {
                        APPS_CONFIG.apps.each { app ->
                            load 'jenkins/shared/security.groovy'
                                .scan(app)
                        }
                    }
                }
            }

            stage('Docker Build & Push') {
                steps {
                    script {
                        APPS_CONFIG.apps.each { app ->
                            load 'jenkins/shared/docker.groovy'
                                .build(app, IMAGES_CONFIG, true)
                        }
                    }
                }
            }

            stage('GitOps Update (Stage)') {
                steps {
                    script {
                        APPS_CONFIG.apps.each { app ->
                            load 'jenkins/shared/gitops.groovy'
                                .updateImage(app, ENVIRONMENT)
                        }
                    }
                }
            }
        }

        post {
            success {
                echo "✅ Stage pipeline completed successfully"
                echo "ArgoCD will auto-sync changes to the STAGE cluster"
            }

            failure {
                echo "❌ Stage pipeline failed"
            }

            always {
                cleanWs()
            }
        }
    }
}
