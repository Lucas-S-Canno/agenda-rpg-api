# --- Neon (Postgres) ---
output "neon_project_id" {
  description = "ID do Projeto no Neon"
  value       = neon_project.main.id
}

# --- Render (Hosting) ---
output "render_api_service_id" {
  description = "ID do Serviço no Render"
  value       = render_web_service.api.id
}

# --- Upstash (Redis) ---
output "upstash_redis_endpoint" {
  description = "Endpoint do cache Redis"
  value       = upstash_redis_database.cache.endpoint
}
