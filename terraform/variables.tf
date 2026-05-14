# Copyright (C) 2026 Gabriel Passarinho Garcia and Agenda RPG Team
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.

variable "cloudflare_api_token" {
  description = "Token de API do Cloudflare"
  type        = string
  sensitive   = true
}

variable "cloudflare_account_id" {
  description = "Account ID do Cloudflare"
  type        = string
}

variable "neon_api_key" {
  description = "Token de API do Neon"
  type        = string
  sensitive   = true
}

variable "neon_org_id" {
  description = "ID da organização no Neon"
  type        = string
}

variable "render_api_key" {
  description = "Token de API do Render"
  type        = string
  sensitive   = true
}

variable "render_owner_id" {
  description = "Owner ID do Render"
  type        = string
}

variable "upstash_api_key" {
  description = "Token de API do Upstash"
  type        = string
  sensitive   = true
}

variable "upstash_email" {
  description = "Email do Upstash"
  type        = string
}

# --- Render Environment Variables ---

variable "render_redis_connection_string" {
  description = "String de conexão do Redis para o Render"
  type        = string
  sensitive   = true
}

variable "render_database_url" {
  description = "URL do Banco de Dados para o Render"
  type        = string
  sensitive   = true
}

variable "render_otel_exporter_otlp_endpoint" {
  description = "Endpoint do OTEL Exporter para o Render"
  type        = string
  default     = ""
}

variable "render_otel_exporter_otlp_headers" {
  description = "Headers do OTEL Exporter para o Render"
  type        = string
  sensitive   = true
  default     = ""
}

variable "render_otel_exporter_otlp_protocol" {
  description = "Protocolo do OTEL Exporter para o Render"
  type        = string
  default     = ""
}

variable "render_otel_resource_attributes" {
  description = "Atributos de recurso do OTEL para o Render"
  type        = string
  default     = ""
}

variable "render_otel_service_name" {
  description = "Nome do serviço OTEL para o Render"
  type        = string
  default     = "AgendaRPG"
}

variable "render_jwt_secret_key" {
  description = "Chave secreta do JWT para o Render"
  type        = string
  sensitive   = true
  default     = ""
}

variable "cloudflare_zone_id" {
  description = "O Zone ID do domínio raiz (gabspassarinhogarcia.uk)"
  type        = string
}
variable "render_mail_host" {
  description = "Host do servidor SMTP para o Render"
  type        = string
  default     = "smtp.gmail.com"
}

variable "render_mail_port" {
  description = "Porta do servidor SMTP para o Render"
  type        = string
  default     = "587"
}

variable "render_mail_username" {
  description = "Usuário do SMTP para o Render"
  type        = string
  default     = ""
}

variable "render_mail_password" {
  description = "Senha do SMTP para o Render"
  type        = string
  sensitive   = true
  default     = ""
}

variable "render_frontend_base_url" {
  description = "URL do Frontend para o Render"
  type        = string
  default     = "http://localhost:3000"
}
