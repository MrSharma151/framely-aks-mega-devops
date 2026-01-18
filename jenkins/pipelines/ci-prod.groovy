/*
========================================================================
 ci-prod.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Continuous Delivery pipeline for PRODUCTION
 - Build, test, scan applications
 - Build Docker images (push only after approval)
 - Update Kubernetes manifests via GitOps (manual control)

 Environment Characteristics:
 - Highest stability and control
 - Manual approval required
 - Full audit trail

 Principle:
 "Production follows Continuous Delivery, not Continuous Deployment"
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
            ENVIRONMENT = "prod"
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

            stage('Docker Build (No Push Yet)') {
                steps {
                    script {
                        APPS_CONFIG.apps.each { app ->
                            load 'jenkins/shared/docker.groovy'
                                .build(app, IMAGES_CONFIG, false)
                        }
                    }
                }
            }

            stage('Manual Approval for Production Release') {
                steps {
                    input message: """
                    Approve PRODUCTION release?

                    Applications : ${APPS_CONFIG.apps*.name.join(', ')}
                    Commit SHA   : ${env.GIT_COMMIT}

                    This action will:
                    - Push Docker images
                    - Update Kubernetes manifests (GitOps)
                    - Trigger ArgoCD production sync
                    """,
                    ok: 'Approve Release'
                }
            }

            stage('Docker Push (Post-Approval)') {
                steps {
                    script {
                        APPS_CONFIG.apps.each { app ->
                            // Push the same image that was already built
                            sh """
                                echo "Pushing Docker image for ${app.name}"
                                docker push ${app.builtImage.name}:${app.builtImage.tag}
                            """
                        }
                    }
                }
            }

            stage('GitOps Update (Production)') {
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
                echo "✅ Production pipeline completed successfully"
                echo "ArgoCD will deploy changes after manual sync approval (if configured)"
            }

            failure {
                echo "❌ Production pipeline failed"
            }

            always {
                cleanWs()
            }
        }
    }
}
