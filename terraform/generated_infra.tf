# __generated__ by Antigravity (Mestre do Bigode Edition)
# Adapted from EleveRats infrastructure

# --- Upstash Redis (Cache) ---
resource "upstash_redis_database" "cache" {
  auto_scale     = false
  database_name  = "AgendaRPG-cache"
  eviction       = true
  primary_region = "us-west-2"
  read_regions   = []
  region         = "global"
  tls            = true

  lifecycle {
    prevent_destroy = true
    ignore_changes  = [budget]
  }
}

# --- Neon Project (Postgres) ---
resource "neon_project" "main" {
  compute_provisioner                 = "k8s-neonvm"
  history_retention_seconds           = 21600
  name                                = "AgendaRPG"
  org_id                              = var.neon_org_id
  pg_version                          = 17
  region_id                           = "aws-us-west-2"
  store_password                      = "yes"
  
  branch {
    database_name = "neondb"
    name          = "production"
    role_name     = "neondb_owner"
  }

  default_endpoint_settings {
    autoscaling_limit_max_cu = 2
    autoscaling_limit_min_cu = 0.25
    suspend_timeout_seconds  = 0
  }

  lifecycle {
    prevent_destroy = true
  }
}

# --- Render Web Service (Hosting) ---
resource "render_web_service" "api" {
  name                = "AgendaRPG"
  environment_id      = "evm-d72ns77fte5s73aear60" # Replicated from EleveRats
  region              = "oregon"
  plan                = "free"
  health_check_path   = "/api/health" # Spring Boot context path /api

  custom_domains = [
    {
      name = "agendarpg-api-render.gabspassarinhogarcia.uk"
    },
  ]

  env_vars = {
    SPRING_DATASOURCE_URL = {
      value = var.render_database_url
    }
    SPRING_DATA_REDIS_URL = {
      value = var.render_redis_connection_string
    }
    # OpenTelemetry (Optional)
    OTEL_EXPORTER_OTLP_ENDPOINT = {
      value = var.render_otel_exporter_otlp_endpoint
    }
    OTEL_EXPORTER_OTLP_HEADERS = {
      value = var.render_otel_exporter_otlp_headers
    }
    OTEL_EXPORTER_OTLP_PROTOCOL = {
      value = var.render_otel_exporter_otlp_protocol
    }
    OTEL_RESOURCE_ATTRIBUTES = {
      value = var.render_otel_resource_attributes
    }
    OTEL_SERVICE_NAME = {
      value = var.render_otel_service_name
    }
  }

  runtime_source = {
    docker = {
      auto_deploy            = false
      auto_deploy_trigger    = "off"
      branch                 = "main"
      context                = "."
      dockerfile_path        = "Dockerfile"
      repo_url               = "https://github.com/Lucas-S-Canno/agenda-rpg-api"
    }
  }

  lifecycle {
    prevent_destroy = true
    ignore_changes = [
      env_vars,
      runtime_source
    ]
  }
}
