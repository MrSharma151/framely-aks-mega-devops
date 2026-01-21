// next.config.ts

import type { NextConfig } from "next";
import withBundleAnalyzer from "@next/bundle-analyzer";

/**
 * Base Path Strategy
 *
 * - Local / path-based routing:
 *   NEXT_PUBLIC_BASE_PATH=/admin
 *
 * - Production (AKS):
 *   Admin runs on a dedicated subdomain (e.g. admin.framely.com),
 *   so basePath MUST remain undefined to keep routing clean at '/'.
 *
 * Any invalid value such as "/" is automatically ignored.
 */

const rawBasePath = process.env.NEXT_PUBLIC_BASE_PATH || "";

const BASE_PATH =
  rawBasePath && rawBasePath !== "/" ? rawBasePath : undefined;

const nextConfig: NextConfig = {
  /**
   * Enables additional React runtime checks in development.
   * Safe for production builds.
   */
  reactStrictMode: true,

  /**
   * Trailing slash support ensures consistent routing
   * behind Kubernetes Ingress and reverse proxies.
   */
  trailingSlash: true,

  /**
   * Enable basePath and assetPrefix ONLY when BASE_PATH is defined.
   * This avoids breaking production subdomain-based routing.
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
   * Image handling configuration
   *
   * - unoptimized: true
   *   Image optimization is disabled to avoid issues
   *   when running behind AKS Ingress.
   *
   * - remotePatterns explicitly whitelist external image sources.
   */
  images: {
    unoptimized: true,
    remotePatterns: [
      {
        protocol: "https",
        hostname: "framelystorage.blob.core.windows.net",
      },
      {
        protocol: "https",
        hostname: "cdn-icons-png.flaticon.com",
      },
    ],
  },

  /**
   * Required for some shared client-side libraries
   */
  transpilePackages: ["react-icons"],

  /**
   * ESLint is enforced in CI pipelines.
   * Docker builds should not fail due to lint warnings.
   */
  eslint: {
    ignoreDuringBuilds: true,
  },

  /**
   * Standalone output is REQUIRED for minimal Docker images
   * and faster container startup in AKS.
   */
  output: "standalone",
};

export default withBundleAnalyzer({
  /**
   * Bundle analyzer is enabled only when explicitly requested.
   * Example: ANALYZE=true npm run build
   */
  enabled: process.env.ANALYZE === "true",
})(nextConfig);
