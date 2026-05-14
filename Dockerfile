# Copyright (C) 2026 Gabriel Passarinho Garcia and Agenda RPG Team
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.

# 1. BUILD STAGE: Using Azul Zulu JDK 25 on Alpine
FROM azul/zulu-openjdk-alpine:25-latest AS build
WORKDIR /src

# Removido o 'kotlin', mantido o 'bash' que é essencial para o gradlew
RUN apk add --no-cache bash

# O restante segue o baile...
COPY ["gradlew", "build.gradle", "settings.gradle", "./"]
COPY ["gradle/", "gradle/"]

# Fix permissions for the wrapper
RUN chmod +x gradlew

# Pre-download dependencies (caching layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy the rest of the source code
COPY src src

# Build the Spring Boot application
# We use bootJar to create the executable fat jar and skip tests for build speed
# Then we rename the fat jar to a fixed name to simplify the COPY in the next stage
RUN ./gradlew bootJar --no-daemon -x test && \
    cp build/libs/$(ls build/libs | grep -v plain) app.jar

# 2. RUNTIME STAGE: Using a lightweight JRE Headless image for production
# This removes the JDK and build tools, significantly reducing the image size
FROM azul/zulu-openjdk-alpine:25-jre-headless AS final
WORKDIR /app

# Install runtime dependencies (curl for health checks, tzdata)
# and create a dedicated non-root user for security
USER root
RUN apk add --no-cache curl tzdata && \
    rm -rf /var/cache/apk/* && \
    addgroup -S rpgadmin && adduser -S rpgadmin -G rpgadmin

USER rpgadmin

# Enforce the application to listen on port 8080 (Spring Boot default)
EXPOSE 8080
ENV SERVER_PORT=8080

# Copy the executable jar from the build stage
COPY --from=build /src/app.jar .

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
