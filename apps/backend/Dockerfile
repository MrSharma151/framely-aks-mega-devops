# ----------------------------------------------------
# STAGE 1: Build & Publish
# ----------------------------------------------------
# Use pinned .NET SDK image for reproducible builds
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build

# Set working directory
WORKDIR /src

# Copy project files first (better Docker layer caching)
COPY Framely.API/Framely.API.csproj Framely.API/
COPY Framely.Core/Framely.Core.csproj Framely.Core/
COPY Framely.Infrastructure/Framely.Infrastructure.csproj Framely.Infrastructure/

# Restore dependencies (safe for Release builds)
RUN dotnet restore Framely.API/Framely.API.csproj

# Copy the remaining source code
COPY . .

# Publish the application in Release mode
# --no-restore ensures no extra restore at publish time
WORKDIR /src/Framely.API
RUN dotnet publish \
    --configuration Release \
    --no-restore \
    --output /app/publish


# ----------------------------------------------------
# STAGE 2: Runtime (Slim & Secure)
# ----------------------------------------------------
# Use pinned ASP.NET runtime image
FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS runtime

# Set runtime environment variables
ENV ASPNETCORE_ENVIRONMENT=Production
ENV ASPNETCORE_URLS=http://+:8080

# Create non-root user for security hardening
RUN addgroup --system dotnet \
  && adduser --system --ingroup dotnet dotnet

# Set working directory
WORKDIR /app

# Copy published output from build stage
# --chown avoids an extra chown layer
COPY --from=build --chown=dotnet:dotnet /app/publish .

# Switch to non-root user
USER dotnet

# Expose application port
EXPOSE 8080

# Healthcheck for Kubernetes / AKS readiness
# HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
#   CMD wget -qO- http://localhost:8080/health || exit 1

# Start the ASP.NET Core application
# Exec form ensures proper signal handling
ENTRYPOINT ["dotnet", "Framely.API.dll"]
