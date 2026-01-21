

---

# 👓 Framely Admin Frontend (Admin Dashboard)

**Framely Admin Frontend** is the **Admin Dashboard application** for managing the Framely eyewear platform.
This application is designed to be **containerized, testable, and deployable via CI/CD** as part of the **Framely Mega DevOps Project**.

It allows **ADMIN users** to manage:

* Categories
* Products
* Orders
* Product images (via backend Blob Storage APIs)

The application is built using **Next.js (App Router)** and is designed to run consistently across:

* Local development using **Docker / Docker Compose**
* Kubernetes (**AKS**) using **GitOps (ArgoCD)**
* Cloud-native production environments

---

## 📌 Project Status

* ✅ Core admin features implemented
* ✅ Fully Dockerized (multi-stage, standalone build)
* ✅ Unit & component tests added
* ✅ Integrated with Framely Backend API
* 🚀 **Production-ready for AKS deployments**

---

## 🧱 Tech Stack

* **Next.js 14 (App Router)**
* **TypeScript**
* **Tailwind CSS**
* **Axios** – centralized API client
* **JWT-based authentication (Admin only)**
* **Jest** – unit & component testing
* **Docker** – containerized build & runtime

---

## 📂 Directory Structure

```bash
apps/frontend-admin/
├── Dockerfile            # Production-ready Dockerfile (Next.js standalone)
├── README.md             # Project documentation
├── VERSION               # Application version
├── package.json
├── package-lock.json
├── jest.config.js        # Jest configuration
├── babel.config.js
├── next.config.ts        # Routing & build configuration
├── tailwind.config.js
├── tsconfig.json
├── src/                  # Application source code
└── test/                 # Unit & component tests
```

### Important Notes

* All test cases are located under `/test`
* Dockerfile is maintained at the root of this directory
* No additional setup is required to containerize this application

---

## ✅ Core Features

### 📊 Admin Dashboard

* High-level system overview
* Reusable UI components (cards, tables)
* Responsive and mobile-friendly layout

### 📂 Category Management

* Create / update / delete categories
* Search and filter support
* Modal-driven UI for clean UX

### 🛍️ Product Management

* Full CRUD operations
* Category-based filtering
* Pagination & search
* Product image upload via backend APIs

### 📦 Order Management

* View and manage all orders
* Update order status
* View order details
* Admin-only delete operations

### 🔐 Authentication & Authorization

* Admin-only access
* JWT-based authentication
* Route-level protection

---

## 🧪 Testing

Frontend tests are implemented using **Jest**.

```bash
npm install
npm test
```

Tests are located at:

```bash
apps/frontend-admin/test/
```

These tests are:

* CI-friendly
* Fast to execute
* Useful for catching UI regressions early

---

## 🐳 Docker Support

The Admin frontend is **fully Dockerized** using a **Next.js standalone build**.

### Build Image

```bash
docker build -t framely-admin .
```

### Run Container

```bash
docker run -p 3001:3000 framely-admin
```

### Docker Compose

This service is designed to run using the **root `docker-compose.yml`** together with:

* Framely Backend API
* Database service
* Other frontend applications

---

## ⚙️ Runtime Configuration & Environment Variables (IMPORTANT)

All configuration is **injected at build or runtime via environment variables**.
No environment-specific values are hardcoded in the source code or Docker image.

---

### 🔹 Required Environment Variables

```env
NEXT_PUBLIC_API_BASE_URL=http://localhost:8081/api/v1
```

| Variable                   | Description                         |
| -------------------------- | ----------------------------------- |
| `NEXT_PUBLIC_API_BASE_URL` | Base URL of the Framely Backend API |

📌 This value is:

* Injected during Docker build (CI / Jenkins)
* Defined in `docker-compose.yml` for local usage
* Controlled via CI/CD pipelines for AKS

---

### 🔹 Optional Environment Variables

```env
NEXT_PUBLIC_BASE_PATH=/admin
```

| Variable                | Description                                                  |
| ----------------------- | ------------------------------------------------------------ |
| `NEXT_PUBLIC_BASE_PATH` | Enables path-based routing (used only for local development) |

📌 **Important Behavior**

* Local / Docker Compose → `/admin`
* AKS / Production → **unset** (admin runs on a subdomain)

---

### 🔹 Build-Time vs Runtime Configuration

⚠️ **Important Next.js Behavior**

* `NEXT_PUBLIC_*` variables are **baked into the build**
* Any change requires a **new Docker image build**
* This behavior is **intentional and expected**

---

### 🔐 Security Notes

* No secrets are stored in this frontend
* JWT tokens are handled client-side
* All sensitive logic is enforced by the backend API

---

## 🚀 Deployment Strategy

### Local Development

* Docker
* Docker Compose

### CI/CD

* **Jenkins-based pipelines**
* Versioned Docker images
* GitOps-driven deployments

### Kubernetes (AKS)

* Deployed via **ArgoCD**
* Ingress-managed routing
* Environment parity across stage & prod
* Controlled rollout using GitOps

---

## 📝 Notes

* This Admin frontend is part of a **larger end-to-end DevOps project**
* Designed for **AKS, GitOps, and CI/CD-first workflows**
* Safe to rebuild, redeploy, and scale horizontally
* No environment-specific code paths

---

## 🎯 Future Enhancements

* Advanced analytics dashboard
* Role-based UI permissions
* Audit logs
* Performance optimizations

---

