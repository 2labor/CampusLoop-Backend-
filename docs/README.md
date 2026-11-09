# CampusLoop (`CampusMates`) — Documentation Index

> **AI Agent Entry Point:** Start with [`AGENTS.md`](../AGENTS.md) or [`docs/AGENTIC_WORKFLOW_NEXT_STEPS.md`](AGENTIC_WORKFLOW_NEXT_STEPS.md) for the ordered task queue, codebase map, and build guardrails.

---

## 📚 Markdown Documentation Suite (`docs/**/*.md`)

| Document | Purpose & Audience |
| :--- | :--- |
| [**`AGENTIC_WORKFLOW_NEXT_STEPS.md`**](AGENTIC_WORKFLOW_NEXT_STEPS.md) | **Master AI Agent Playbook:** Ordered implementation tasks (Tasks 1–5), target file paths, offline-first Room sync rules, and verification commands. |
| [**`BACKEND_API_DELTA_AND_CONTRACTS.md`**](BACKEND_API_DELTA_AND_CONTRACTS.md) | **Complete REST & STOMP API Reference (`23b61dc`):** Exact HTTP endpoints, Kotlin DTO schemas, Snowflake `Long` vs `UUID` types, and STOMP WebSocket destinations for all 18 Spring Boot controllers. |
| [**`CLOUDFLARE_DEPLOYMENT_ARCHITECTURE.md`**](CLOUDFLARE_DEPLOYMENT_ARCHITECTURE.md) | **Cloudflare Deployment Guide (`hazed0m` / `rohovskyi.dev`):** Dual-mode architecture (Live 24/7 Cloudflare Edge Worker + D1 AND Native Spring Boot + Docker Compose + Cloudflare Tunnel). |
| [**`CHANGES_SUMMARY.md`**](CHANGES_SUMMARY.md) | **Complete MVP & Backend Integration Change Log:** Inventory of all Room SQLite, sensor, state-machine, Retrofit, and STOMP WebSocket work. |
| [**`MILESTONE_1_DESIGN_AND_ARCHITECTURE.md`**](MILESTONE_1_DESIGN_AND_ARCHITECTURE.md) | **Milestone 1 Architecture Proposal:** Four-Part Model (`Input → Processing → Storage → Output`), §3 i–viii compliance matrix, and UX rationale. |
| [**`AGENTS.md`**](AGENTS.md) | **Agent Operating Guide (Mirror of Root `AGENTS.md`):** Quick reference for codebase structure, Gradle/KSP configuration, and test commands. |
| [**`PRODUCT_DESCRIPTION.md`**](PRODUCT_DESCRIPTION.md) | **Product Vision & UX Overview:** High-level product features, spatial campus zones, and student safety controls. |
| [**`BUG_REPORT_LIST.md`**](BUG_REPORT_LIST.md) | **MVP Audit & Bug Resolution Log:** Record of all 16 initial architectural gaps and state-machine bugs resolved in the MVP release. |
| [**`backend/CLOUDFLARE_EDGE_WORKER_IMPLEMENTATION.md`**](backend/CLOUDFLARE_EDGE_WORKER_IMPLEMENTATION.md) | **Live Cloudflare Edge + D1 Reference:** Details on the deployed `campusloop-backend` worker (`https://campusloop-api.rohovskyi.dev`) and 14-table D1 schema. |
| [**`backend/CampusLoop-Backend-Specification.md`**](backend/CampusLoop-Backend-Specification.md) | **Original Backend Architecture Specification:** Domain modeling and initial 13-controller specification. |
| [**`backend/CampusLoop-Security-Audit.md`**](backend/CampusLoop-Security-Audit.md) | **Security Audit Report:** JWT authentication, authorization boundaries, and WebSocket handshake security analysis. |
| [**`reference/README.md`**](reference/README.md) | **UI & Design Reference Notes:** Spatial radar, quick meetups, and elemental mascot specifications. |
