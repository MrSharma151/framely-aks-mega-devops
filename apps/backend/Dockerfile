# ----------------------------------------------------
# STAGE 1: Build & Publish
# ----------------------------------------------------
# Use official .NET SDK image (version pinned for stability)
# Note: Image digest pinning can be added for stricter supply-chain security
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build

# Set working directory inside the container
WORKDIR /src

# Copy project files first to leverage Docker layer caching
COPY Framely.API/Framely.API.csproj Framely.API/
COPY Framely.Core/Framely.Core.csproj Framely.Core/
COPY Framely.Infrastructure/Framely.Infrastructure.csproj Framely.Infrastructure/

# Restore dependencies (safe and deterministic for Release builds)
RUN dotnet restore Framely.API/Framely.API.csproj

# Copy the remaining source code
COPY . .

# Publish the application in Release mode
# --no-restore avoids redundant restore during publish
WORKDIR /src/Framely.API
RUN dotnet publish \
    --configuration Release \
    --no-restore \
    --output /app/publish


# ----------------------------------------------------
# STAGE 2: Runtime (Slim & Secure)
# ----------------------------------------------------
# Use official ASP.NET runtime image
FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS runtime

# Default runtime environment variables
# These can be overridden via Kubernetes ConfigMaps or environment settings
ENV ASPNETCORE_ENVIRONMENT=Production
ENV ASPNETCORE_URLS=http://+:8080

# Create a non-root user for security hardening
RUN addgroup --system dotnet \
  && adduser --system --ingroup dotnet dotnet

# Set working directory
WORKDIR /app

# Copy published output from build stage
# --chown avoids an extra chown layer and improves build efficiency
COPY --from=build --chown=dotnet:dotnet /app/publish .

# Switch to non-root user
USER dotnet

# Expose application port (documentational; actual exposure handled by Kubernetes Service)
EXPOSE 8080

# Docker HEALTHCHECK is intentionally disabled.
# Kubernetes liveness/readiness probes will be used instead in AKS.
# HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
#   CMD wget -qO- http://localhost:8080/health || exit 1

# Start the ASP.NET Core application
# Exec form ensures proper signal handling (SIGTERM) in containers
ENTRYPOINT ["dotnet", "Framely.API.dll"]
