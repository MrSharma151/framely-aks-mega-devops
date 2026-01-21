

---

# 🧠 Framely Backend API

**Framely Backend API** is the **core service** of the Framely eyewear platform.
It acts as the **single source of truth** for authentication, authorization, products, categories, orders, users, and file uploads, serving both the **Customer** and **Admin** frontends.

The backend is built using **ASP.NET Core + Entity Framework Core**, fully **containerized**, and designed for **cloud-native deployments using Docker, AKS, and GitOps workflows**.

---

## 📌 Project Status

* ✅ Core APIs implemented
* ✅ JWT authentication & role-based authorization
* ✅ Entity Framework Core with SQL Server
* ✅ Multi-stage Docker build
* ✅ Automatic database migrations & role seeding
* 🚀 **Production-ready for AKS deployments**

---

## 🧱 Tech Stack

* **ASP.NET Core 9**
* **Entity Framework Core**
* **SQL Server**
* **ASP.NET Identity**
* **JWT Authentication (HS256)**
* **AutoMapper**
* **Swagger / OpenAPI**
* **Docker (multi-stage builds)**

---

## 📂 Directory Structure

```bash
apps/backend/
├── Dockerfile                # Production-grade multi-stage Dockerfile
├── README.md                 # Backend documentation
├── VERSION                   # Backend versioning
├── Framely.API               # API layer (Controllers, Program.cs)
├── Framely.Core              # Domain models & interfaces
├── Framely.Infrastructure    # EF Core, Identity, services
└── Framely.Tests             # Unit & integration tests
```

### 📁 Layered Architecture

| Layer                      | Responsibility                            |
| -------------------------- | ----------------------------------------- |
| **Framely.API**            | Controllers, middleware, Swagger, startup |
| **Framely.Core**           | Domain entities, DTOs, interfaces         |
| **Framely.Infrastructure** | EF Core, Identity, DB context, services   |
| **Framely.Tests**          | Unit & service-level tests                |

---

## 🔐 Authentication & Authorization

* JWT-based authentication
* Role-based access control using **ASP.NET Identity**

### Roles

| Role    | Usage             |
| ------- | ----------------- |
| `USER`  | Customer frontend |
| `ADMIN` | Admin dashboard   |

Roles are **automatically created at application startup** if missing.

---

## 🗄️ Database

* **SQL Server**
* Managed via **Entity Framework Core**

### Automatic on Startup

* Database creation
* EF Core migrations
* Role seeding (`USER`, `ADMIN`)

This makes the service **safe for restarts and redeployments** in Docker, Azure, and AKS.

---

## 🌐 API Endpoints (High Level)

### 🔑 Authentication

* `POST /api/v1/Auth/register`
* `POST /api/v1/Auth/login`

### 📂 Categories

* `GET /api/v1/Categories`
* `POST /api/v1/Categories`
* `PUT /api/v1/Categories/{id}`
* `DELETE /api/v1/Categories/{id}`

### 🛍️ Products

* `GET /api/v1/Products`
* `GET /api/v1/Products/{id}`
* `POST /api/v1/Products`
* `PUT /api/v1/Products/{id}`
* `DELETE /api/v1/Products/{id}`
* Filtering & search supported

### 📦 Orders

* `GET /api/v1/Orders`
* `GET /api/v1/Orders/my`
* `PUT /api/v1/Orders/{id}/status`
* `DELETE /api/v1/Orders/{id}`

---

## 🧪 Testing

Tests are located under:

```bash
apps/backend/Framely.Tests/
```

Includes:

* Service-level tests
* Controller validation
* Authentication & authorization scenarios

---

## 🐳 Docker Support

The backend uses a **multi-stage Dockerfile** optimized for production.

### Build Image

```bash
docker build -t framely-api .
```

### Run Container

```bash
docker run -p 8080:8080 framely-api
```

### Health Checks

Docker `HEALTHCHECK` is intentionally disabled.
**Kubernetes liveness and readiness probes** are used instead when deployed to AKS.

---

## ⚙️ Configuration & Runtime Environment Variables (UPDATED ✅)

All application configuration is **externalized** and provided at **runtime via environment variables**.
This design ensures the backend is fully compatible with **Docker Compose**, **Kubernetes ConfigMaps**, and **Kubernetes Secrets**.

> ⚠️ **No secrets are hardcoded** in source code or Docker images.

---

### 🔹 Core Runtime Variables

```env
ASPNETCORE_ENVIRONMENT=Production
ASPNETCORE_URLS=http://+:8080
```

| Variable                 | Description                                                  |
| ------------------------ | ------------------------------------------------------------ |
| `ASPNETCORE_ENVIRONMENT` | Application environment (Production / Staging / Development) |
| `ASPNETCORE_URLS`        | HTTP binding used by the container                           |

---

### 🔹 Database Configuration (SQL Server)

```env
ConnectionStrings__DefaultConnection=Server=sqlserver,1433;Database=FramelyDb;User Id=sa;Password=StrongPassword@123;TrustServerCertificate=True;
```

| Variable                               | Description                                                     |
| -------------------------------------- | --------------------------------------------------------------- |
| `ConnectionStrings__DefaultConnection` | SQL Server connection string (Docker / Azure SQL / Managed SQL) |

✔ Used by Entity Framework Core
✔ Migrations & schema creation run automatically on startup

---

### 🔹 JWT Authentication Settings

```env
JwtSettings__Secret=very_long_secret_key_min_32_chars
JwtSettings__Issuer=FramelyAPI
JwtSettings__Audience=FramelyUsers
JwtSettings__ExpiresInMinutes=60
```

| Variable                        | Description                        |
| ------------------------------- | ---------------------------------- |
| `JwtSettings__Secret`           | Secret key used to sign JWT tokens |
| `JwtSettings__Issuer`           | Token issuer                       |
| `JwtSettings__Audience`         | Token audience                     |
| `JwtSettings__ExpiresInMinutes` | Token expiry duration              |

🔐 **JWT secret must always be injected via Kubernetes Secret or secure CI/CD variables**

---

### 🔹 Frontend CORS Configuration

```env
FrontendOrigins__0=http://localhost:3000
FrontendOrigins__1=http://localhost:3001
```

| Variable             | Description                       |
| -------------------- | --------------------------------- |
| `FrontendOrigins__*` | Allowed frontend origins for CORS |

✔ Supports multiple frontends (Admin & Customer)
✔ Easily extendable per environment

---

### 🔹 Storage Configuration (Blob / File Storage)

Used for **product image uploads** and other file assets.

```env
Storage__ConnectionString=DefaultEndpointsProtocol=...
Storage__Container=products
Storage__Name=framelyblob
Storage__Key=storage_access_key
```

| Variable                    | Description                       |
| --------------------------- | --------------------------------- |
| `Storage__ConnectionString` | Storage account connection string |
| `Storage__Container`        | Container name used for uploads   |
| `Storage__Name`             | Storage account name              |
| `Storage__Key`              | Storage access key                |

📌 In AKS:

* Non-sensitive values → **ConfigMap**
* Sensitive values → **Kubernetes Secret**

---

### 🔹 Feature Flags

```env
SeedAdmin=true
```

| Variable    | Description                                            |
| ----------- | ------------------------------------------------------ |
| `SeedAdmin` | Enables automatic admin user & role seeding on startup |

---

### ✅ Configuration Principles (Important)

✔ No environment-specific values in code
✔ Same container image runs across **local, stage, and prod**
✔ Configuration injected at runtime
✔ Fully compatible with **GitOps & ArgoCD**

---

## 📘 Swagger / API Documentation

Swagger UI is available at:

```
GET /swagger
```

Used for:

* API testing
* Contract validation
* Frontend integration

---

## 🚀 Deployment Strategy

### Local Development

* Docker
* Docker Compose

### CI/CD

* **Jenkins-based pipelines**
* Automated Docker image builds
* GitOps-driven image tag updates

### Kubernetes / AKS

* Deployed via **ArgoCD**
* Environment-specific manifests using **Kustomize**
* Stage environment fully provisioned
* Production designed with manual approval gates

---

## 🛡️ Security Practices

* Secrets injected via environment variables
* Role-based authorization
* Non-root container execution
* HTTPS enforced at Kubernetes ingress

---

## 📝 Notes

* Backend is **stateless and cloud-native**
* Safe to restart and redeploy
* Designed for **GitOps-first workflows**
* Fully compatible with **AKS + ArgoCD**

---

## 🎯 Future Enhancements

* Refresh tokens
* Rate limiting
* Redis caching
* Observability (Prometheus + Grafana)
* Background jobs (Hangfire / Azure Jobs)

---

