/*
========================================================================
 gitops.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Update Docker image tags in Kubernetes manifests stored in Git
 - Commit and push changes to Git
 - Act as the CI → CD bridge (GitOps)

 What this file DOES:
 - Modify Kubernetes manifests (image tags only)
 - Commit changes with clear, auditable messages

 What this file DOES NOT do:
 - Deploy to Kubernetes
 - Interact directly with ArgoCD
 - Run kubectl or helm

 Principle:
 "Jenkins updates Git. ArgoCD applies Git."
========================================================================
*/

def updateImage(app, environment) {

    stage("GitOps Update :: ${app.name} :: ${environment}") {

        echo '--------------------------------------------------'
        echo 'Updating Kubernetes manifests (GitOps)'
        echo "Application : ${app.name}"
        echo "Environment : ${environment}"
        echo "Image       : ${app.builtImage.name}:${app.builtImage.tag}"
        echo '#-------------------------------------------------'

        if (!app.builtImage) {
            error "❌ No built image metadata found for ${app.name}"
        }

        /*
         * GitOps manifests live under the 'kubernetes/' directory
         *
         * Expected structure:
         * kubernetes/<env>/<app>/kustomization.yaml
         */
        def manifestsPath = "kubernetes/${environment}/${app.name}"

        dir(manifestsPath) {

            try {
                sh """
                    echo "Updating image tag in kustomization.yaml"

                    kustomize edit set image \
                      ${app.builtImage.name}=${app.builtImage.name}:${app.builtImage.tag}
                """
            }
            catch (Exception e) {
                error """
                ❌ Failed to update Kubernetes manifests for ${app.name}

                Please verify kustomization.yaml exists and is valid.
                """
            }
        }

        // Commit & push GitOps change
        sh """
            git status
            git add kubernetes/${environment}/${app.name}
            git commit -m "gitops(${environment}): update ${app.name} image to ${app.builtImage.tag}"
            git push origin ${environment}
        """

        echo "✅ GitOps update completed for ${app.name} (${environment})"
    }
}

return this
