# Resource Group Outputs
output "resource_group_id" {
  description = "The ID of the Resource Group."
  value       = azurerm_resource_group.main.id
}
output "resource_group_name" {
  description = "The name of the Resource Group."
  value       = azurerm_resource_group.main.name
}