# ------------------------------------------------------------
# Storage Account Module
# ------------------------------------------------------------
# This module provisions:
# - Azure Storage Account
# - One or more Blob containers
#
# The storage account is intended for:
# - Application file uploads
# - Media storage
# - Exports / reports
#
# Private endpoints and advanced security
# are intentionally deferred for simplicity.
# ------------------------------------------------------------


# -------------------------
# Storage Account
# -------------------------
resource "azurerm_storage_account" "this" {

  # Name of the storage account (must be globally unique)
  name = var.storage_account_name

  # Azure region
  location = var.location

  # Resource Group
  resource_group_name = var.resource_group_name

  # Storage account kind
  account_kind = "StorageV2"

  # Performance tier
  account_tier = "Standard"

  # Replication type
  # LRS is sufficient for stage and interview use
  account_replication_type = "LRS"

  # Enforce HTTPS for security
  enable_https_traffic_only = true

  # Minimum TLS version
  min_tls_version = "TLS1_2"

  tags = var.tags
}


# -------------------------
# Blob Containers
# -------------------------
# Create one or more blob containers inside the storage account
resource "azurerm_storage_container" "containers" {

  for_each = toset(var.container_names)

  # Container name
  name = each.value

  # Storage account name
  storage_account_name = azurerm_storage_account.this.name

  # Access level: private (no anonymous access)
  container_access_type = "private"
}
