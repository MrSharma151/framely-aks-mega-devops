

## 📊 Observability – Framely Mega DevOps AKS Project

This directory contains **monitoring and observability components** for the Framely platform.

The observability stack is intentionally **split by responsibility** to follow real-world, production-grade best practices.

---

## 🔍 Observability Architecture Overview

Framely uses a **hybrid observability model**:

* **Azure-native monitoring** for infrastructure and cluster-level visibility
* **Cloud-native tooling (Prometheus & Grafana)** for application-level metrics

This separation ensures:

* Clear ownership boundaries
* Reduced coupling between cloud provider and application monitoring
* Easier portability and scalability

---

## 📘 Azure Log Analytics Workspace

Azure Log Analytics is used for **infrastructure and cluster observability**.

### Scope

* AKS cluster monitoring
* Node and node pool metrics
* Control plane diagnostics
* Azure resource logs

### Key Characteristics

* Integrated directly with AKS during cluster provisioning (Terraform)
* Used for:

  * Cluster health
  * Node availability
  * Resource utilization
  * Infrastructure-level diagnostics
* **Not used for application metrics**

> Application-level metrics are deliberately excluded from Log Analytics to avoid vendor lock-in and high ingestion costs.

---

## 📈 Prometheus & Grafana (Application Observability)

Application metrics are handled using **Prometheus and Grafana**, deployed **directly inside the AKS cluster**.

### Deployment Model

* Installed via **Helm charts**
* Deployed into a dedicated Kubernetes namespace
* Managed as platform-level components (not application workloads)

### Responsibilities

* Application metrics collection
* Service-level monitoring
* Dashboard visualization
* Alerting (future extension)

### Why Prometheus & Grafana?

* Kubernetes-native
* Industry standard for cloud-native observability
* Decoupled from cloud provider
* Widely understood and interview-relevant

---

## 📁 Directory Purpose

This `monitoring/` directory serves as:

* A **logical boundary** for observability-related resources
* Documentation and configuration reference for:

  * Prometheus
  * Grafana
  * Monitoring architecture decisions

At this stage, it contains **documentation only**.

> Helm charts and values may be added later as the platform evolves.

---

## 🎯 Design Principles

* Infrastructure monitoring ≠ Application monitoring
* Observability tooling is treated as **platform infrastructure**
* GitOps-friendly and AKS-native approach
* Cost-aware and production-aligned

---

## 🚀 Future Enhancements (Optional)

* Prometheus alert rules
* Grafana dashboards as code
* Alertmanager integration
* External metric adapters for HPA

---

## 🧠 Summary

* **Log Analytics** → Infrastructure & AKS monitoring
* **Prometheus & Grafana** → Application metrics
* **Helm-based deployment** → Cluster-native & scalable
* **Clean separation of concerns** → Production-grade design

---

