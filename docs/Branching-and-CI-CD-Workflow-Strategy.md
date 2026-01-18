

---

# Branching & CI/CD Workflow Strategy

### Framely – Mega DevOps AKS Project

---

## 1. Overview

This document defines the **branching model**, **CI/CD workflow**, and **GitOps execution strategy** used in the **Framely Mega DevOps AKS Project**.

The primary objectives of this strategy are:

* Maintain **high code quality and safety**
* Enable **fast feedback** in non-production environments
* Ensure **controlled, auditable production releases**
* Enforce a **clear separation of concerns** between CI and CD

This design follows industry-proven practices inspired by **GitLab Flow**, **Trunk-Based Development**, and **GitOps principles**.

---

## 2. Branching Model

The project follows a **single-repository, multi-branch** strategy with **environment-aligned branches**.

### Permanent Branches

| Branch  | Purpose                                | Environment      |
| ------- | -------------------------------------- | ---------------- |
| `main`  | Source of truth (design & correctness) | None             |
| `stage` | Integration & validation               | Stage / Pre-Prod |
| `prod`  | Stable releases                        | Production       |

* Only these three branches are **long-lived**
* All deployments originate from these branches
* No direct environment manipulation outside Git

---

## 3. High-Level Workflow

```
Feature / Fix
      ↓
main   (CI only – validation)
      ↓ Pull Request
stage  (CI + auto deployment)
      ↓ PR + Approval
prod   (CI + manual delivery)
```

### Core Principle

> **Design flows downward, execution flows forward**

* Design and correctness originate in `main`
* Execution happens progressively via `stage` and `prod`
* Each environment increases in **control and stability**

---

## 4. `main` Branch – Source of Truth

### Purpose

* Represents the **desired system state**
* Contains clean, reviewed code and manifests
* Must always remain **deployable and stable**

### CI Behavior (Automated)

* ✔ Unit & integration tests
* ✔ Code quality checks (SAST)
* ✔ Dependency and vulnerability scanning
* ✔ Docker image build (verification only)

### What Does *Not* Happen

* ❌ No image push
* ❌ No GitOps updates
* ❌ No deployments
* ❌ No infrastructure changes

### Why This Matters

* Provides **early feedback** to developers
* Prevents broken code from reaching environments
* Keeps `main` safe and reviewable at all times

> **`main` validates correctness, not execution**

---

## 5. `stage` Branch – Integration Environment

### Purpose

* First environment where **real execution occurs**
* Closely mirrors production behavior (smaller scale)
* Used for **end-to-end validation**

### CI Behavior (Automated)

* ✔ Full CI (tests, SAST, scans)
* ✔ Docker image build & tagging
* ✔ Image push to **Stage registry**
* ✔ GitOps manifest updates (image tags)

### CD Behavior

* ✔ ArgoCD auto-sync enabled
* ✔ Automatic deployment to Stage

### Why This Matters

* Fast feedback loop
* Early detection of integration issues
* No manual intervention required

> **Stage follows Continuous Deployment**

---

## 6. `prod` Branch – Production Environment

### Purpose

* Represents the **live, customer-facing system**
* Stability and control are the highest priorities

### CI Behavior (Automated)

* ✔ Same CI checks as `stage`
* ✔ Image build and verification

### CD Behavior (Manual Control)

* ✔ GitOps updates require approval
* ✔ ArgoCD deploys only after approval

### Why This Matters

* Prevents accidental production changes
* Creates a **clear audit trail**
* Aligns with enterprise governance models

> **Production follows Continuous Delivery (not Continuous Deployment)**

---

## 7. GitOps Execution Model

This project strictly follows **GitOps principles**.

### Core Rules

* Jenkins **never deploys** to Kubernetes
* Jenkins **only updates Git**
* ArgoCD is the **only component** allowed to apply changes to clusters

### Commit Types

| Commit Type                      | Branch          |
| -------------------------------- | --------------- |
| Design commits (code, manifests) | `main`          |
| Execution commits (image tags)   | `stage`, `prod` |

This explains why `stage` and `prod` may contain **CI-generated commits**.

---

## 8. Terraform & Infrastructure Workflow

Infrastructure changes follow a **stricter control model**.

| Branch  | Terraform Behavior        |
| ------- | ------------------------- |
| `main`  | `terraform plan` only     |
| `stage` | `plan` + **manual apply** |
| `prod`  | `plan` + **manual apply** |

### Benefits

* No accidental infrastructure changes
* Full review before applying changes
* Clear separation between **application** and **infrastructure** lifecycles

---

## 9. Feature Branch Workflow

Feature branches are **short-lived** and optional (single-developer setup).

### Example

```
feature/add-order-search
          ↓
        main
```

### Rules

* Branch from `main`
* CI runs on feature branches
* Merge via Pull Request
* Delete after merge

❌ Feature branches never deploy directly

---

## 10. Hotfix Strategy

Hotfixes are handled without bypassing controls.

### Option A – Preferred (Recommended)

```
hotfix/critical-fix
        ↓
main → stage → prod
```

* Fix merged into `main`
* Promoted through `stage`
* Deployed to `prod` with approval

### Option B – Emergency Only (Rare)

```
hotfix/critical-fix
        ↓
      prod
        ↓
      main
```

Used only when:

* Immediate production fix is required
* Followed by mandatory back-merge to `main`

---

## 11. Why This Strategy Works

* ✔ Matches real-world enterprise workflows
* ✔ Clean GitOps implementation
* ✔ Minimizes production risk
* ✔ Easy to explain and understand
* ✔ Scales from single developer to full team

---


