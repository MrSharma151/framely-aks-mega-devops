/*
========================================================================
 security.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Centralized security and quality scanning logic
 - Run dependency and static analysis checks per application
 - Ensure security issues are detected early (shift-left)

 Design Principles:
 - Tool-agnostic (can plug Trivy, Snyk, Sonar later)
 - App-agnostic (driven via config)
 - Fail-fast on critical issues
========================================================================
*/

def scan(app) {

    stage("Security Scan :: ${app.name}") {

        echo "--------------------------------------------------"
        echo "Running security scans for application: ${app.name}"
        echo "Application path: ${app.path}"
        echo "--------------------------------------------------"

        dir(app.path) {

            /*
             * Scan command strategy:
             * - Defined per app in apps.yaml
             * - Allows different tools per tech stack
             */

            if (!app.securityCommand) {
                echo "⚠️ No securityCommand defined for ${app.name}. Skipping scan."
                return
            }

            try {
                sh """
                    echo "Executing security scan for ${app.name}"
                    ${app.securityCommand}
                """
            }
            catch (Exception e) {
                error """
                ❌ Security scan failed for application: ${app.name}

                Critical or high severity issues detected.
                Please review the scan output above.
                """
            }
        }
    }
}

return this
