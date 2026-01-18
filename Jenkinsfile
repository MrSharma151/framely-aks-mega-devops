/*
========================================================================
 Jenkinsfile
 Framely – Mega DevOps AKS Project

 Purpose:
 - Single entry point for Jenkins Multibranch Pipeline
 - Detect branch and route execution to the correct pipeline
 - Maintain strict separation of concerns

 Design Principles:
 - Branch = Environment
 - Jenkins handles CI + GitOps only
 - ArgoCD handles deployments
 - No business logic in this file
========================================================================
*/

pipeline {
    agent any

    options {
        disableConcurrentBuilds()          // Prevent parallel runs on same branch
        timestamps()                       // Add timestamps to logs
        ansiColor('xterm')                 // Better log readability
    }

    environment {
        PROJECT_NAME = "framely"
        JENKINS_DIR  = "jenkins/pipelines"
    }

    stages {

        stage('Branch Validation') {
            steps {
                script {
                    echo "Running pipeline for branch: ${env.BRANCH_NAME}"

                    // Allow only defined long-lived branches
                    def allowedBranches = ['main', 'stage', 'prod']

                    if (!allowedBranches.contains(env.BRANCH_NAME)) {
                        error("""
                        ❌ Invalid branch '${env.BRANCH_NAME}'

                        This Jenkins pipeline only supports:
                        - main   (CI validation only)
                        - stage  (CI + auto GitOps)
                        - prod   (CI + manual delivery)

                        Feature branches are validated via PRs only.
                        """)
                    }
                }
            }
        }

        stage('Pipeline Routing') {
            steps {
                script {

                    if (env.BRANCH_NAME == 'main') {
                        echo "Routing to MAIN CI pipeline (validation only)"
                        load "${JENKINS_DIR}/ci-main.groovy"
                    }

                    else if (env.BRANCH_NAME == 'stage') {
                        echo "Routing to STAGE pipeline (CI + auto GitOps)"
                        load "${JENKINS_DIR}/ci-stage.groovy"
                    }

                    else if (env.BRANCH_NAME == 'prod') {
                        echo "Routing to PROD pipeline (CI + manual approval)"
                        load "${JENKINS_DIR}/ci-prod.groovy"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Pipeline completed successfully for branch: ${env.BRANCH_NAME}"
        }

        failure {
            echo "❌ Pipeline failed for branch: ${env.BRANCH_NAME}"
        }

        always {
            cleanWs()   // Clean workspace after every run
        }
    }
}
