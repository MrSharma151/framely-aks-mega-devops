# ----------------------------------------------------
# 1. Builder Stage
# ----------------------------------------------------
# Use a pinned Node.js Alpine image for reproducibility
FROM node:20.11.1-alpine AS builder

# Set working directory inside container
WORKDIR /app

# Copy only dependency manifests first (better layer caching)
COPY package.json package-lock.json ./

# Install dependencies in a clean, deterministic way
# --no-audit and --no-fund reduce noise and speed up CI
RUN npm ci --no-audit --no-fund

# Copy application source code
COPY . .

# Build Next.js application (standalone output)
RUN npm run build


# ----------------------------------------------------
# 2. Production Runtime Stage
# ----------------------------------------------------
FROM node:20.11.1-alpine AS runner

# Set production environment
ENV NODE_ENV=production

# Create a non-root user and group for security
RUN addgroup -S nextjs -g 1001 \
  && adduser -S nextjs -u 1001 -G nextjs

# Set working directory
WORKDIR /app

# Copy only required runtime artifacts from builder
# --chown avoids an extra chown layer (image size optimization)
COPY --from=builder --chown=nextjs:nextjs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nextjs /app/.next/static ./.next/static
COPY --from=builder --chown=nextjs:nextjs /app/public ./public

# Switch to non-root user
USER nextjs

# Expose Next.js application port
EXPOSE 3000

# Healthcheck for Kubernetes / Docker monitoring
# HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
#   CMD wget -qO- http://localhost:3000/health || exit 1

# Start the Next.js server
# Exec form ensures proper signal handling (SIGTERM/SIGINT)
CMD ["node", "server.js"]
