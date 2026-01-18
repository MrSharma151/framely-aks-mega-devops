# ----------------------------------------------------
# 1. Builder Stage
# ----------------------------------------------------
# Use pinned Node.js Alpine image for reproducibility
FROM node:20.11.1-alpine AS builder

# Set working directory
WORKDIR /app

# Copy dependency manifests first for better caching
COPY package.json package-lock.json ./

# Install dependencies deterministically
RUN npm ci --no-audit --no-fund

# Copy application source code
COPY . .

# Build Next.js application (standalone output)
RUN npm run build


# ----------------------------------------------------
# 2. Runtime Stage (Slim & Secure)
# ----------------------------------------------------
FROM node:20.11.1-alpine AS runner

# Set production environment (must be lowercase)
ENV NODE_ENV=production

# Create non-root user for security hardening
RUN addgroup -S nextjs -g 1001 \
  && adduser -S nextjs -u 1001 -G nextjs

# Set working directory
WORKDIR /app

# Copy ONLY required runtime files from builder
# --chown avoids extra chown layer (image size optimization)
COPY --from=builder --chown=nextjs:nextjs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nextjs /app/.next/static ./.next/static

# Optional: copy public assets if used
# COPY --from=builder --chown=nextjs:nextjs /app/public ./public

# Switch to non-root user
USER nextjs

# Expose application port
EXPOSE 3000

# Healthcheck for Kubernetes / AKS readiness
# HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
#   CMD wget -qO- http://localhost:3000/health || exit 1

# Start Next.js standalone server
# Exec form ensures proper signal handling
CMD ["node", "server.js"]
