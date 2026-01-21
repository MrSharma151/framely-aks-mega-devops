

---

# 👓 Framely Customer Frontend

**Framely Customer Frontend** is the **customer-facing web application** of the Framely eyewear platform.
It allows end users to **browse products, search and filter items, place orders, and manage their account**.

This application is designed to be **cloud-native, containerized, and CI/CD friendly**, and is a core part of the **Framely Mega DevOps Project**, where the complete system (frontend, backend, database) is deployed using **Docker, GitOps, and Kubernetes (AKS)**.

---

## 📌 Project Status

* ✅ Core customer features implemented
* ✅ Fully Dockerized (Next.js standalone build)
* ✅ Unit & component tests added
* ✅ Integrated with Framely Backend API
* 🚀 **Production-ready for AKS deployments**

---

## 🧱 Tech Stack

* **Next.js 14 (App Router)**
* **TypeScript**
* **Tailwind CSS**
* **Axios** – centralized API client
* **JWT Authentication** (client-side)
* **Jest** – unit & component testing
* **Docker** – containerized build & runtime

---

## 📂 Directory Structure

```bash
apps/frontend-customer/
├── Dockerfile            # Production-ready Dockerfile (standalone build)
├── README.md             # Project documentation
├── VERSION               # Application versioning
├── package.json
├── package-lock.json
├── jest.config.js        # Jest configuration
├── babel.config.js
├── next.config.js        # Routing & build configuration
├── postcss.config.mjs
├── tsconfig.json
├── public/               # Static assets
├── src/                  # Application source code
└── tests/                # Unit & component tests
```

### Important Notes

* All test cases are located under `/tests`
* Dockerfile is maintained at the root of this directory
* The application is fully runnable using **Docker / Docker Compose**

---

## ✅ Core Features

### 🔐 Authentication & Routing

* User registration & login
* JWT-based authentication
* Protected routes with redirect handling
* Centralized auth logic via hooks & API interceptors

### 🛍️ Product Browsing

* Paginated product listing (backend-driven)
* Category-based filtering
* Client-side search
* Sorting support (price / default)

### 📦 Product Details

* Individual product detail pages
* Backend-driven data fetching
* Graceful fallback handling for missing images

### 🛒 Cart & Checkout

* Client-side cart management
* Basic checkout flow
* Order placement via backend API

### 📑 My Orders

* View logged-in user’s orders
* Order status tracking:

  * `Pending`
  * `Processing`
  * `Completed`
  * `Cancelled`
* Cancel pending orders
* Displays order items and total price

### 🔔 Notifications

* Success and error notifications
* User-friendly feedback for API actions

---

## 🧪 Testing

Frontend tests are implemented using **Jest**.

```bash
npm install
npm test
```

Tests are located at:

```bash
apps/frontend-customer/tests/
```

These tests are:

* CI-friendly
* Fast to execute
* Useful for catching UI regressions early

---

## 🐳 Docker Support

The Customer frontend is **fully Dockerized** using a **Next.js standalone build**.

### Build Image

```bash
docker build -t framely-frontend-customer .
```

### Run Container

```bash
docker run -p 3000:3000 framely-frontend-customer
```

### Docker Compose

This service is designed to run using the **root `docker-compose.yml`** together with:

* Framely Backend API
* Database service
* Admin frontend

---

## ⚙️ Runtime Configuration & Environment Variables (IMPORTANT)

All configuration is **externalized** and provided via **environment variables**.
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
* Controlled via CI/CD pipelines for AKS deployments

---

### 🔹 Optional Environment Variables

```env
NEXT_PUBLIC_BASE_PATH=/app
```

| Variable                | Description                                                  |
| ----------------------- | ------------------------------------------------------------ |
| `NEXT_PUBLIC_BASE_PATH` | Enables path-based routing (used only for local development) |

📌 **Routing Behavior**

* Local / Docker / KIND → `/app`
* AKS / Production → **unset** (customer app runs on a subdomain)

---

### 🔹 Build-Time vs Runtime Configuration

⚠️ **Important Next.js Behavior**

* `NEXT_PUBLIC_*` variables are **compiled into the build**
* Any change requires a **new Docker image build**
* This behavior is **intentional and expected**

---

## 🔐 Security Notes

* No secrets are stored in this frontend
* JWT tokens are handled client-side
* All authorization and validation logic is enforced by the backend API

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
* Same container image across environments
* Production-grade rollout control via GitOps

---

## 📝 Notes

* This is the **customer-facing frontend** of the Framely platform
* Built as part of an **end-to-end DevOps Mega Project**
* Designed for **Docker, AKS, and GitOps-first workflows**
* Safe to rebuild, redeploy, and scale horizontally

---

## 🎯 Future Enhancements

* Payment gateway integration
* Advanced checkout flow
* Wishlist & recommendations
* Performance optimizations
* Analytics & tracking

---


