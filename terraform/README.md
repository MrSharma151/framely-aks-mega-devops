bilkul bhai 👍
Root `README.md` **sabse zyada important file** hoti hai — ye hi first impression deta hai.
Tumhara current README **outdated + misaligned** tha (status, branching, scope sab change ho chuka hai).

Neeche main **completely rewritten, final, professional root README** de raha hoon.
Tone वही है jo baaki docs me set ho chuki hai: **clean, factual, enterprise-style**.

Tum isse **direct replace** kar sakte ho.

---

# 📦 Framely – Mega DevOps AKS Project

This repository contains a **production-grade, cloud-native DevOps implementation** of the **Framely e-commerce platform**, designed around **Kubernetes, GitOps, and modern CI/CD practices** on Azure.

The project demonstrates **how to design, build, and operate a real-world application platform** using:

* Containerized workloads
* Kubernetes (AKS)
* GitOps-based delivery
* Strong separation of concerns across CI, CD, and infrastructure

This repository is intended as a **hands-on DevOps reference project**, not a demo or toy implementation.

---

## 🎯 Purpose of This Repository

The goal of this project is to showcase:

* End-to-end DevOps architecture on Azure
* Production-aligned CI/CD workflows
* GitOps-based Continuous Delivery using ArgoCD
* Secure, deterministic Kubernetes deployments
* Environment isolation and promotion strategies
* Infrastructure-as-Code with clear operational boundaries

This repository focuses on **how the platform is built and delivered**, not on application feature development.

---

## 🔗 Related Project (PaaS-Based Implementation)

The original Framely application is implemented using **Azure PaaS services**.

👉 **Original PaaS Repository**
[https://github.com/MrSharma151/Framely.git](https://github.com/MrSharma151/Framely.git)

That repository demonstrates:

* Azure App Service
* Azure Static Web Apps
* Azure SQL Database
* Azure Blob Storage
* GitHub Actions–based CI/CD

The PaaS implementation remains unchanged.
This repository explores the **Kubernetes + GitOps alternative** for the same application.

---

## 🧱 High-Level Platform Overview

### Application Layer

* Backend API: ASP.NET Core (stateless)
* Frontend (Customer): Next.js
* Frontend (Admin): Next.js
* Database: Azure SQL Database
* Object Storage: Azure Blob Storage

### DevOps & Platform Stack

* Docker (containerization)
* Kubernetes (AKS)
* Kustomize (application manifests)
* Jenkins (Continuous Integration)
* ArgoCD (GitOps-based Continuous Delivery)
* Terraform (Azure infrastructure provisioning)
* Ansible (Jenkins VM configuration)
* Prometheus & Grafana (application observability)
* Azure Log Analytics (infrastructure monitoring)

---

## 🌿 Repository Structure (Overview)

```text
framely/
├── apps/           # Application source code and Docker images
├── jenkins/        # CI pipelines and shared libraries
├── argocd/         # GitOps configuration (projects & applications)
├── kubernetes/     # Kubernetes manifests (stage & prod)
├── terraform/      # Azure infrastructure (IaC)
├── ansible/        # Jenkins VM configuration
├── monitoring/     # Observability documentation
├── docs/           # Architecture and workflow documentation
└── Jenkinsfile     # CI entry point
```

Each directory contains its own `README.md` with detailed, authoritative documentation.

---

## 🌿 Branching & Environment Model

This repository uses an **environment-aligned branching strategy**:

| Branch  | Purpose                               | Environment |
| ------- | ------------------------------------- | ----------- |
| `main`  | Design validation and source of truth | None        |
| `stage` | Integration and pre-production        | Stage       |
| `prod`  | Stable, controlled releases           | Production  |

* CI behavior varies by branch
* All deployments are driven via **GitOps**
* Jenkins never deploys directly to Kubernetes
* ArgoCD is the only deployment engine

Details are documented in:
📘 `docs/BRANCHING-AND-CI-CD-WORKFLOW-STRATEGY.md`

---

## ☸️ GitOps Delivery Model

* Git defines the desired state
* Jenkins updates Git (image tags only)
* ArgoCD reconciles Kubernetes clusters
* No manual `kubectl apply` for application workloads
* Environment promotion occurs via Git commits

This ensures deterministic, auditable deployments.

---

## 🧪 Local Development & Validation

The project can be fully validated on a **single Linux machine** without AKS.

Supported local workflows include:

* Docker Compose–based application testing
* Local Kubernetes using KIND
* Jenkins CI execution
* ArgoCD-based GitOps validation

Setup instructions are documented in:
📘 `docs/LOCAL-DEV-SETUP.md`

---

## 📚 Documentation Index

Authoritative documentation is available under the `docs/` directory:

* `ARCHITECTURE-OVERVIEW.md` – High-level system architecture
* `BRANCHING-AND-CI-CD-WORKFLOW-STRATEGY.md` – Code flow and CI/CD behavior
* `LOCAL-DEV-SETUP.md` – End-to-end local setup guide

Each major module (`apps`, `jenkins`, `argocd`, `kubernetes`, `terraform`, `ansible`) also contains its own README.

---

## 🧠 Design Philosophy

This project prioritizes:

* Clear separation of concerns
* Git as the control plane
* Deterministic and reproducible workflows
* Cost-aware infrastructure design
* Production realism over over-engineering

The architecture is intentionally minimal, explicit, and auditable.

---

## 🧾 License

This project is licensed under the **MIT License**.

---

## 👤 Author

**Rohit Sharma**
Devops Engineer - https://rohitsharma.org

---

