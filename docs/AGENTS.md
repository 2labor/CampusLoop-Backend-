# AGENTS.md — CampusLoop (`CampusMates`) Agent Context & Operating Guide

> *Note: This file is mirrored in both the repository root (`../AGENTS.md`) and `docs/AGENTS.md` so AI coding agents immediately discover it whether scanning the root or the `docs/` folder.*

## Quick Orientation

* **Documentation Hub:** [`docs/README.md`](README.md)
* **Primary Playbook & Next Steps:** [`docs/AGENTIC_WORKFLOW_NEXT_STEPS.md`](AGENTIC_WORKFLOW_NEXT_STEPS.md)
* **Backend API Contracts (`23b61dc`):** [`docs/BACKEND_API_DELTA_AND_CONTRACTS.md`](BACKEND_API_DELTA_AND_CONTRACTS.md)
* **Cloudflare Deployment Guide (`hazed0m`):** [`docs/CLOUDFLARE_DEPLOYMENT_ARCHITECTURE.md`](CLOUDFLARE_DEPLOYMENT_ARCHITECTURE.md)
* **Change History & Completed Work:** [`CHANGES_SUMMARY.md`](../CHANGES_SUMMARY.md)
* **Companion Backend Repository:** `C:\Users\rusla\AndroidStudioProjects\CampusLoop-Backend` (`origin/main` @ commit `23b61dc`)

## Key Codebase Map (`app/src/main/java/com/example/campusmates/`)

* **`data/local/`**: Room SQLite database (`CampusDatabase.kt`), entities (`Entities.kt`), reactive DAOs (`Daos.kt`), and enum converters (`RoomConverters.kt`).
* **`data/remote/`**:
  * `api/CampusApiInterfaces.kt`: Retrofit REST API interfaces for `/api/v1/**`.
  * `model/RemoteModels.kt`: DTOs and Spring Data `SliceResponse<T>`.
  * `client/NetworkClient.kt`: Dynamic Retrofit + OkHttp client with JWT `Authorization: Bearer` interceptor and hot-swappable base URL.
  * `client/AuthSessionManager.kt`: Persistent JWT token and server URL manager (`SharedPreferences`).
  * `client/CampusWebSocketClient.kt`: Native OkHttp STOMP 1.2 protocol engine (`CONNECT`, `SUBSCRIBE`, `MESSAGE`, `DISCONNECT`).
* **`data/repository/CampusRepository.kt`**: Offline-first single source of truth blending Room flows with remote REST calls and STOMP real-time triggers.
* **`sensor/`**: Hardware GPS (`CampusLocationManager.kt`) and compass orientation (`CampusCompassSensorManager.kt`).
* **`ui/`**: Jetpack Compose screens (`SocialMapScreen.kt`, `EventsScreens.kt`, `DiscussionsFeedScreen.kt`, `ProfileAndSettingsScreens.kt`) and `CampusViewModel.kt`.

## Build & Verification Commands (Windows PowerShell)

```powershell
# Run unit tests
.\gradlew.bat testDebugUnitTest

# Build debug APK
.\gradlew.bat assembleDebug

# Check Cloudflare account (hazed0m / hazekk94@gmail.com)
npx wrangler whoami
```

## Agent Guardrails
1. **Offline-First Guarantee:** Never block UI rendering on network calls. Always read from Room `Flow`s and synchronize remote responses into Room tables inside `try/catch` blocks.
2. **Gradle / AGP 9.5 + KSP:** Keep `android.disallowKotlinSourceSets=false` in `gradle.properties`.
3. **Android ViewModel Reflection:** Keep `@JvmOverloads` on `CampusViewModel` constructor so `AndroidViewModelFactory` reflection succeeds.
