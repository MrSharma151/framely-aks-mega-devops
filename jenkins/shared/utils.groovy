/*
========================================================================
 utils.groovy
 Framely – Mega DevOps AKS Project

 Purpose:
 - Provide reusable helper utilities for Jenkins pipelines
 - Centralize logging, validation, and safety checks

 Design Principles:
 - Zero business logic
 - Readability over cleverness
 - Safe defaults
========================================================================
*/

def logInfo(message) {
    echo "ℹ️  ${message}"
}

def logWarn(message) {
    echo "⚠️  ${message}"
}

def logError(message) {
    echo "❌ ${message}"
}

def validateEnvironment(env) {
    def allowedEnvs = ['main', 'stage', 'prod']
    if (!allowedEnvs.contains(env)) {
        error "Invalid environment '${env}'. Allowed: ${allowedEnvs.join(', ')}"
    }
}

def requireApprovalForProd(env) {
    if (env == 'prod') {
        error "Manual approval is required before proceeding in production."
    }
}

def printHeader(title) {
    echo """
==================================================
 ${title}
==================================================
"""
}

return this
