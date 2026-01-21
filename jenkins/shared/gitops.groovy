/*
========================================================================
 gitops.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Update Kubernetes manifests via GitOps
 - Modify image tags in Kustomize configuration
 - Trigger ArgoCD reconciliation (indirectly)
========================================================================
*/

def updateImage(app, environment) {

    stage("GitOps Update :: ${app.name} :: ${environment}") {

        // --------------------------------------------------
        // Defensive checks
        // --------------------------------------------------
        if (!app.builtImage) {
            error """
❌ No built image metadata found for application: ${app.name}

Ensure Docker build & push stage completed successfully
before attempting GitOps update.
"""
        }

        echo "--------------------------------------------------"
        echo "Starting GitOps image update"
        echo "Application : ${app.name}"
        echo "Environment : ${environment}"
        echo "Image       : ${app.builtImage.name}:${app.builtImage.tag}"
        echo "--------------------------------------------------"

        // --------------------------------------------------
        // GitOps repository structure
        // --------------------------------------------------
        def gitopsPath = "kubernetes/${environment}"

        // --------------------------------------------------
        // Kustomize image handling (CRITICAL)
        // LEFT  = image name WITHOUT tag (must match Deployment.yaml)
        // RIGHT = full image WITH tag
        // --------------------------------------------------
        def imageWithTag = "${app.builtImage.name}:${app.builtImage.tag}"
        def imageWithoutTag = app.builtImage.name.split(":")[0]

        // --------------------------------------------------
        // Ensure correct Git branch
        // --------------------------------------------------
        sh """
            echo "Switching to GitOps branch: ${environment}"
            git fetch origin ${environment}
            git checkout ${environment}
            git pull origin ${environment}
        """

        // --------------------------------------------------
        // Update Kustomize image mapping (OVERWRITE, NOT APPEND)
        // --------------------------------------------------
        dir(gitopsPath) {
            sh """
                echo "Updating image in kustomization.yaml"
                echo "Kustomize Image Name : ${imageWithoutTag}"
                echo "Docker Image         : ${imageWithTag}"

                kustomize edit set image \
                  ${imageWithoutTag}=${imageWithTag}
            """
        }

        // --------------------------------------------------
        // Commit GitOps change
        // --------------------------------------------------
        sh """
            git config user.name "Jenkins"
            git config user.email "jenkins@framely.local"

            git status
            git add kubernetes/${environment}/kustomization.yaml
            git commit -m "gitops(${environment}): update ${app.name} image to ${app.builtImage.tag} [skip ci]"
        """

        // --------------------------------------------------
        // Push changes using GitHub PAT
        // --------------------------------------------------
        withCredentials([
            usernamePassword(
                credentialsId: 'github-pat',
                usernameVariable: 'GIT_USERNAME',
                passwordVariable: 'GIT_TOKEN'
            )
        ]) {
            sh """
                git push https://${GIT_USERNAME}:${GIT_TOKEN}@github.com/MrSharma151/framely-aks-mega-devops.git ${environment}
            """
        }

        echo "✅ GitOps image update completed for ${app.name} (${environment})"
    }
}

return this
