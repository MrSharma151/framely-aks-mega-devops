/*
========================================================================
 docker.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Centralized Docker image build logic
 - Support verification-only builds (main)
 - Support build + push (stage / prod)
 - Enforce consistent tagging strategy

 Design Principles:
 - Config-driven (images.yaml, registries.yaml)
 - Environment-agnostic
 - No deployment responsibility
========================================================================
*/

def build(app, imagesConfig, pushImage = false) {
    stage("Docker Build :: ${app.name}") {
        echo '--------------------------------------------------'
        echo "Docker build started for application: ${app.name}"
        echo "Push image enabled: ${pushImage}"
        echo '--------------------------------------------------'

        // Resolve image name from images.yaml
        def imageName = imagesConfig.images[app.name]
        if (!imageName) {
            error "❌ No image name defined for ${app.name} in images.yaml"
        }

        // Read VERSION from app directory
        def appVersion = sh(
            script: "cat ${app.path}/VERSION",
            returnStdout: true
        ).trim()

        // Get Git commit short SHA
        def gitSha = sh(
            script: 'git rev-parse --short HEAD',
            returnStdout: true
        ).trim()

        // Final immutable + readable tag
        def imageTag = "${appVersion}-${gitSha}"

        dir(app.path) {
            try {
                sh """
                    echo "Building Docker image"
                    docker build -t ${imageName}:${imageTag} .
                """

                if (pushImage) {
                    sh """
                        echo "Pushing Docker image"
                        docker push ${imageName}:${imageTag}
                    """
                } else {
                    echo 'Docker image built for verification only (not pushed)'
                }
            }
            catch (Exception e) {
                error """
                ❌ Docker build failed for application: ${app.name}

                Please review Dockerfile and build logs above.
                """
            }
        }

        // Expose image metadata for downstream steps (GitOps)
        app.builtImage = [
            name: imageName,
            tag : imageTag
        ]

        echo "Docker image prepared: ${imageName}:${imageTag}"
    }
}

return this
