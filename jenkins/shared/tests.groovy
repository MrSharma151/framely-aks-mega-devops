/*
========================================================================
 tests.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Centralized test execution logic for all applications
 - Run unit and integration tests per application
 - Fail the pipeline immediately if any test fails

 Design Principles:
 - One responsibility: testing only
 - App-agnostic logic (config-driven)
 - No environment-specific behavior
========================================================================
*/

def run(app) {

    stage("Tests :: ${app.name}") {

        echo "--------------------------------------------------"
        echo "Running tests for application: ${app.name}"
        echo "Application path: ${app.path}"
        echo "--------------------------------------------------"

        dir(app.path) {

            /*
             * Test command strategy:
             * - Command is defined per app in apps.yaml
             * - Allows different tech stacks (Node, .NET, etc.)
             */

            if (!app.testCommand) {
                error "❌ No testCommand defined for ${app.name} in apps.yaml"
            }

            try {
                sh """
                    echo "Executing test command for ${app.name}"
                    ${app.testCommand}
                """
            }
            catch (Exception e) {
                error """
                ❌ Tests failed for application: ${app.name}

                Please check the test logs above.
                Pipeline execution stopped.
                """
            }
        }
    }
}

return this
