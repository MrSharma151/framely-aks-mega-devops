

---

# 🧩 Framely Applications – Source of Truth

This directory contains **all application services** of the Framely platform.
It serves as the **single source of truth** for:

* Application responsibilities
* Docker images & Dockerfiles
* Runtime environment variables
* Service boundaries
* Kubernetes (AKS) readiness assumptions

This directory is **finalized and stable**.
No structural or architectural changes are expected here.

---

## 📂 Applications Overview

```bash
apps/
├── README.md                # This document (source of truth)
├── docker-compose.yml       # Local orchestration (development & validation only)
├── backend                  # ASP.NET Core Backend API
├── frontend-admin           # Admin Dashboard (Next.js)
└── frontend-customer        # Customer Storefront (Next.js)
```

---

## 🧱 Services & Responsibilities

### 🧠 Backend API (`apps/backend`)

**Type:** ASP.NET Core API
**Role:** Central business & data service (single source of truth)

**Responsibilities**

* Authentication & authorization (JWT + Roles)
* User, product, category & order management
* Database access via Entity Framework Core
* Blob / file storage integration
* CORS enforcement for frontend apps

**Key Characteristics**

* Stateless
* Database-driven
* Safe for restarts
* Idempotent startup (migrations + role seeding)

**Docker**

* Multi-stage Dockerfile
* Non-root runtime user
* Production-ready image

📘 Detailed reference: `apps/backend/README.md`

---

### 🛍️ Frontend – Customer (`apps/frontend-customer`)

**Type:** Next.js (App Router)
**Role:** Public customer-facing storefront

**Responsibilities**

* Product browsing & search
* User authentication
* Cart & checkout flow
* Order history & tracking

**Key Characteristics**

* Fully stateless
* Consumes backend APIs only
* No direct database access
* CDN / Ingress friendly

**Docker**

* Next.js standalone build
* Build-time environment injection
* Minimal runtime image
* Non-root container execution

📘 Detailed reference: `apps/frontend-customer/README.md`

---

### 🧑‍💼 Frontend – Admin (`apps/frontend-admin`)

**Type:** Next.js (App Router)
**Role:** Internal admin dashboard

**Responsibilities**

* Product & category management
* Order management
* Admin-only operations
* Role-protected UI

**Key Characteristics**

* Fully stateless
* Admin-only access
* Consumes backend APIs only

**Docker**

* Next.js standalone build
* Build-time environment injection
* Minimal runtime image
* Non-root container execution

📘 Detailed reference: `apps/frontend-admin/README.md`

---

## 🐳 Docker & Image Contracts

Each application:

* Has **exactly one Dockerfile**
* Builds a **single immutable image**
* Receives configuration **only via environment variables**

Dockerfiles are:

* Production-grade
* AKS-ready
* **Frozen** (no future changes expected)

---

## ⚙️ Environment Variable Contracts (CRITICAL)

This section defines the **stable configuration contract** for all apps.

---

### 🔐 Backend Environment Variables

| Variable                               | Required | Description                |
| -------------------------------------- | -------- | -------------------------- |
| `ASPNETCORE_ENVIRONMENT`               | ✅        | Runtime environment        |
| `ASPNETCORE_URLS`                      | ✅        | HTTP bind address          |
| `ConnectionStrings__DefaultConnection` | ✅        | Database connection string |
| `JwtSettings__Secret`                  | ✅        | JWT signing key            |
| `JwtSettings__Issuer`                  | ✅        | JWT issuer                 |
| `JwtSettings__Audience`                | ✅        | JWT audience               |
| `JwtSettings__ExpiresInMinutes`        | ❌        | Token expiry               |
| `FrontendOrigins__*`                   | ✅        | Allowed CORS origins       |
| `SeedAdmin`                            | ❌        | Admin seeding flag         |
| `Storage__ConnectionString`            | ✅        | Blob storage connection    |
| `Storage__Container`                   | ✅        | Storage container name     |
| `Storage__Name`                        | ✅        | Storage account name       |
| `Storage__Key`                         | ✅        | Storage access key         |

📌 **Rules**

* Secrets → Kubernetes Secrets
* Non-sensitive → ConfigMaps
* No defaults assumed in production

---

### 🌐 Frontend Environment Variables (Customer & Admin)

| Variable                   | Required | Description                   |
| -------------------------- | -------- | ----------------------------- |
| `NEXT_PUBLIC_API_BASE_URL` | ✅        | Backend API base URL          |
| `NEXT_PUBLIC_BASE_PATH`    | ❌        | Local path-based routing only |

⚠️ **Important**

* `NEXT_PUBLIC_*` variables are **build-time**
* Any change requires a **new image build**
* This behavior is intentional and expected

---

## 🧪 docker-compose.yml (Local Validation Only)

`apps/docker-compose.yml` exists to:

* Validate service integration
* Test environment variable contracts
* Run the complete system locally

It is **NOT** used in production or Kubernetes.

What it provides:

* SQL Server container (local only)
* Backend API
* Customer frontend
* Admin frontend

---

## 🧠 Design Rules (Non-Negotiable)

1️⃣ **Applications are stateless**
2️⃣ **No configuration inside images**
3️⃣ **Database & storage are external**
4️⃣ **Environment variables are the contract**
5️⃣ **Same image runs in all environments**

These rules are already enforced by the codebase.

---

## 🧪 Testing Expectations

* Backend:

  * Unit & integration tests
* Frontends:

  * Jest unit & component tests
* CI:

  * Tests run before image build
* CD:

  * Only tested images are deployed

---

## 🏁 Final Notes

* This `apps/` directory is **finalized**
* It represents a **production-grade application layer**
* No re-architecture or refactor is expected
* Safe to use as **long-term documentation**

---


