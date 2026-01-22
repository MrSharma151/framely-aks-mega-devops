terraform {
  # Configure Azure Storage as the remote backend for Terraform state.
  # This ensures state is stored safely and supports locking.
  backend "azurerm" {

    # Resource Group that contains the Terraform state storage account.
    # This RG is shared across environments and should never be deleted.
    resource_group_name = "rg-framely-tfstate"

    # Azure Storage Account that stores Terraform state files.
    # Must be globally unique and created once during bootstrap.
    storage_account_name = "stframelystatetf"

    # Blob container inside the storage account where state files live.
    container_name = "tfstate"

    # Path (key) of the Terraform state file for the Stage environment.
    # This keeps Stage and Prod states completely isolated.
    key = "stage/terraform.tfstate"
  }
}
