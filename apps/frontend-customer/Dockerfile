# ----------------------------------------------------
# 1. Builder Stage
# ----------------------------------------------------
# Use a pinned Node.js Alpine image for reproducible builds
FROM node:20.11.1-alpine AS builder

# Set working directory inside the container
WORKDIR /app

# ----------------------------------------------------
# Build-time arguments
# These are injected by Jenkins or local docker build
# and are baked into the Next.js build output.
# ----------------------------------------------------
ARG NEXT_PUBLIC_BASE_PATH=""
ARG NEXT_PUBLIC_API_BASE_URL=""

# Expose build-time variables to Next.js
# NOTE: NEXT_PUBLIC_* variables are compiled into the build
ENV NEXT_PUBLIC_BASE_PATH=${NEXT_PUBLIC_BASE_PATH}
ENV NEXT_PUBLIC_API_BASE_URL=${NEXT_PUBLIC_API_BASE_URL}

# Copy dependency manifests first to leverage Docker layer caching
COPY package.json package-lock.json ./

# Install dependencies deterministically
# --no-audit and --no-fund reduce noise and speed up CI
RUN npm ci --no-audit --no-fund

# Copy application source code
COPY . .

# Build Next.js application using standalone output
RUN npm run build


# ----------------------------------------------------
# 2. Runtime Stage (Slim & Secure)
# ----------------------------------------------------
FROM node:20.11.1-alpine AS runner

# Set runtime environment
ENV NODE_ENV=production

# Ensure the application binds to all interfaces
# Required for Kubernetes Services and Ingress
ENV HOSTNAME=0.0.0.0

# Create a non-root user and group for security hardening
RUN addgroup -S nextjs -g 1001 \
  && adduser -S nextjs -u 1001 -G nextjs

# Set working directory
WORKDIR /app

# Copy ONLY required runtime artifacts from builder
# --chown avoids an extra chown layer and keeps image size small
COPY --from=builder --chown=nextjs:nextjs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nextjs /app/.next/static ./.next/static
COPY --from=builder --chown=nextjs:nextjs /app/public ./public

# Switch to non-root user
USER nextjs

# Expose Next.js application port
EXPOSE 3000

# Start Next.js standalone server
# Exec form ensures proper signal handling in Kubernetes (SIGTERM/SIGINT)
CMD ["node", "server.js"]
