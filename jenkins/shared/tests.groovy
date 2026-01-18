/*
========================================================================
 tests.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Centralized test execution logic for all applications
 - Support multiple tech stacks (.NET, Node.js)
 - Fail fast on test failure

 Design Principles:
 - One responsibility: testing only
 - Config-driven (apps.yaml)
 - Minimal app-specific branching
========================================================================
*/

def run(app) {

    stage("Tests :: ${app.name}") {

        echo "--------------------------------------------------"
        echo "Running tests for application: ${app.name}"
        echo "Application path: ${app.path}"
        echo "--------------------------------------------------"

        dir(app.path) {

            try {

                /*
                 * Backend (.NET) special handling:
                 * - Multi-project solution
                 * - Tests are discovered via solution file
                 */
                if (app.name == 'backend') {

                    echo "Detected .NET backend application"
                    echo "Running tests via solution file"

                    sh '''
                        dotnet test Framely/Framely.sln
                    '''

                }
                /*
                 * All other applications (Node.js, etc.)
                 * - Test command comes from apps.yaml
                 */
                else {

                    if (!app.testCommand) {
                        error "❌ No testCommand defined for ${app.name} in apps.yaml"
                    }

                    echo "Executing test command: ${app.testCommand}"

                    sh """
                        ${app.testCommand}
                    """
                }

            } catch (Exception e) {
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
