/**
 * Next.js Configuration – Framely Customer Frontend
 *
 * Key Design Goals:
 * - Clean separation between local (path-based) and production (subdomain-based) routing
 * - AKS / Kubernetes Ingress compatibility
 * - Deterministic Docker builds using standalone output
 * - Secure and predictable image handling
 */

const withBundleAnalyzer = require('@next/bundle-analyzer')({
  /**
   * Bundle analyzer is enabled only when explicitly requested.
   * Example: ANALYZE=true npm run build
   */
  enabled: process.env.ANALYZE === 'true',
});

/**
 * Base Path Strategy
 *
 * - Local / path-based routing:
 *   NEXT_PUBLIC_BASE_PATH=/app
 *
 * - Production (AKS):
 *   Customer app runs on a dedicated subdomain (e.g. app.framely.com),
 *   so basePath MUST remain undefined to keep routing clean at '/'.
 *
 * Any invalid value such as "/" is automatically ignored.
 */
const rawBasePath = process.env.NEXT_PUBLIC_BASE_PATH || '';

const BASE_PATH =
  rawBasePath && rawBasePath !== '/' ? rawBasePath : undefined;

const nextConfig = {
  /**
   * Enables additional React runtime checks in development.
   * Safe and recommended for production builds.
   */
  reactStrictMode: true,

  /**
   * Trailing slash support ensures consistent routing
   * behind Kubernetes Ingress and reverse proxies.
   */
  trailingSlash: true,

  /**
   * Enable basePath and assetPrefix ONLY when BASE_PATH is defined.
   * This prevents accidental breakage in production subdomain deployments.
   */
  ...(BASE_PATH && {
    basePath: BASE_PATH,
    assetPrefix: BASE_PATH,
  }),

  /**
   * Compiler optimizations
   */
  compiler: {
    styledComponents: true,
  },

  /**
   * Image configuration
   *
   * - unoptimized: true
   *   Disables Next.js image optimization to avoid issues
   *   when running behind AKS Ingress.
   *
   * - remotePatterns explicitly whitelist external image sources.
   */
  images: {
    unoptimized: true,
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'framelystorage.blob.core.windows.net',
      },
      {
        protocol: 'https',
        hostname: 'images.unsplash.com',
      },
      {
        protocol: 'https',
        hostname: 'cdn-icons-png.flaticon.com',
      },
    ],
  },

  /**
   * Required for certain shared client-side libraries
   */
  transpilePackages: ['react-icons'],

  /**
   * ESLint is enforced in CI pipelines.
   * Docker builds should not fail due to lint warnings.
   */
  eslint: {
    ignoreDuringBuilds: true,
  },

  /**
   * Standalone output is REQUIRED for minimal Docker images
   * and fast container startup in AKS.
   */
  output: 'standalone',
};

module.exports = withBundleAnalyzer(nextConfig);
