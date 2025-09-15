# =================================
# CREDIYA APPLICATION SERVICE
# =================================

# Stage 1: Build
FROM gradle:8.5-jdk17-alpine AS builder
LABEL stage=builder

# Set working directory
WORKDIR /app

# Copy Gradle configuration files first (for better layer caching)
COPY build.gradle settings.gradle gradle.properties main.gradle ./
COPY gradle/ gradle/

# Download dependencies (cached layer if no gradle files change)
RUN gradle dependencies --no-daemon

# Copy all source modules for Bancolombia structure
COPY domain/ domain/
COPY infrastructure/ infrastructure/
COPY applications/ applications/

# Build the application (skip validation for Docker build)
RUN gradle clean build --no-daemon -x test -x validateStructure

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Create user and group for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Install curl for health checks and create app directory
RUN apk add --no-cache curl && \
    mkdir -p /app && \
    chown -R appuser:appgroup /app

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage (Bancolombia structure)
COPY --from=builder --chown=appuser:appgroup /app/applications/app-service/build/libs/*.jar ApplicationService.jar

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8081

# Set JVM options for container environment
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar ApplicationService.jar"]