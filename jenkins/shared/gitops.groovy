def updateImage(app, environment) {

    stage("GitOps Update :: ${app.name} :: ${environment}") {

        echo "--------------------------------------------------"
        echo "Updating Kubernetes manifests (GitOps)"
        echo "Application : ${app.name}"
        echo "Environment : ${environment}"
        echo "Image       : ${app.builtImage.name}:${app.builtImage.tag}"
        echo "--------------------------------------------------"

        def gitopsPath = "kubernetes/${environment}/${app.name}"

        // 🔥 FIX: Ensure we are on the correct branch (NOT detached HEAD)
        sh """
            echo "Ensuring correct Git branch: ${environment}"
            git fetch origin ${environment}
            git checkout ${environment}
            git pull origin ${environment}
        """

        dir(gitopsPath) {
            sh """
                echo "Updating image tag in kustomization.yaml"
                kustomize edit set image \
                  ${app.builtImage.name}=${app.builtImage.name}:${app.builtImage.tag}
            """
        }

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
