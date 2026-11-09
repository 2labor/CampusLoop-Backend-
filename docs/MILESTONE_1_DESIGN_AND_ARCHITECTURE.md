# CampusLoop — Milestone 1 Design & Architecture Proposal

> **Module:** Mobile Software Development (TU Dublin — Grangegorman Campus)  
> **Lecturer:** Abubakr Siddig  
> **Platform:** Android (Kotlin, Jetpack Compose, Material 3, Room SQLite, Retrofit2, STOMP WebSockets)

---

## 1. Main Idea, Novelty & Core Objectives

### 1.1 Core Concept & Added Value
**CampusLoop** is a map-first mobile social application designed for TU Dublin (Grangegorman) students. Unlike standard messaging apps that require pre-planned group coordination, CampusLoop lowers the friction of real-world campus interaction by answering three immediate questions on a single interactive screen:
* **Who is around?** — See friends currently active on campus who explicitly opted into location sharing.
* **What is happening?** — Discover live open study sessions, coffee breaks, sports, and society events nearby.
* **Can I join right now?** — One-tap joining for open events or verified request/invite flows for private meetups.

### 1.2 Key Objectives & Target Personas
* **Spontaneous Quick Meetups:** Enable any student to publish a short-lived meetup (e.g., *"Coffee in 15 min at Central Quad"*) in under 15 seconds with automatic expiry.
* **Structured Access & Capacity Control:** Support both `OPEN` events (instant join up to `maxParticipants`) and `PRIVATE` invite-only events (host approval workflow).
* **Target Personas:**
  1. *Campus Student* looking for study/coffee company between lectures.
  2. *Event Organizer* managing participant limits and join requests.
  3. *Society/Group Member* coordinating recurring club activities and threaded discussions.

### 1.3 Privacy-by-Design Architecture
Location sharing is strictly decoupled from friendship and event participation:
* **3 Explicit Visibility Modes:** `OFF` / Ghost Mode (default off), `APPROXIMATE_ZONE` (visible only to accepted friends by building zone), and `LIVE_COORDINATES` (live sharing while active).
* **Coarse Campus Zones:** Displays approximate campus building/zone distance (e.g., *"~120m · Library"*) rather than exposing raw street coordinates on public profiles.
* **Graceful GPS Degradation:** If OS location permission is denied, the app remains 100% functional via the `List` mode, Events catalog, and Discussions feed.

---

## 2. System Architecture: The Four-Part Model (`Input → Processing → Storage → Output`)

| Stage | Component | Implementation Details |
| :--- | :--- | :--- |
| **Part I · Data Input** | Sensors & User Actions | **Sensor 1 (GPS Location):** Foreground coordinates via Android `LocationManager` (`CampusLocationManager.kt`).<br>**Sensor 2 (Magnetometer / Compass):** Hardware orientation sensor (`TYPE_ROTATION_VECTOR` in `CampusCompassSensorManager.kt`) computing live azimuth bearing (`0°..360°`).<br>**Interactive Forms:** Quick Meetup & Event creation forms, search queries, category/time filters, threaded comments, and privacy toggles. |
| **Part II · Data Processing** | Domain Logic & State | **Reactive State Holder:** Unidirectional Data Flow (UDF) in `CampusViewModel` exposing immutable `StateFlow` streams.<br>**Event Lifecycle Engine:** Enforces state transitions (`OPEN ↔ FULL → IN_PROGRESS → COMPLETED / CANCELLED`).<br>**Spatial & Privacy Filtering:** Computes Haversine campus-zone distance, compass bearing delta, and filters blocked users. |
| **Part III · Data Storage** | Room SQLite + Cloud Sync | **Local Persistence (Room DB v2):** Offline-first SQLite cache storing 10 normalized entities via `@Dao` interfaces (`UserEntity`, `EventEntity`, `EventParticipantEntity`, `FriendshipEntity`, `GroupEntity`, `GroupMemberEntity`, `LocationShareEntity`, `CampusNotificationEntity`, `AppSettingsEntity`, `PostEntity`, `CommentEntity`).<br>**Remote Backend Contract:** Spring Boot 3.2.5 / Cloudflare Edge (`https://campusloop-api.rohovskyi.dev`) + STOMP 1.2 WebSockets (`/ws`). |
| **Part IV · Data Output** | Compose UI & Feedback | **Isometric Campus Map & List:** Custom Compose `Canvas` map with pulsing user puck, compass HUD badge, friend avatars, and category-coded event pins, paired with an accessible `List` view.<br>**Theming & Localization:** Instant Dark/Light theme switching and live EN/RU bilingual rendering. |

---

## 3. Compliance with Minimum App Requirements (§3 i–viii)

| Requirement | Technical Implementation | Key Classes, Routes & Entities |
| :--- | :--- | :--- |
| **i. Navigation & Data Sharing** | Single-Activity Jetpack Compose architecture using `NavHost` and a 5-tab `CampusBottomBar`. Detail screens pass lightweight entity IDs (`eventId`, `userId`, `groupId`) and observe reactive Room/ViewModel flows. | `CampusLoopApp.kt`, `EventDetails`, `UserProfile`, `GroupDetails`, `Notifications` |
| **ii. Local Database (Room SQLite)** | Relational Room schema caching campus events, participant rosters, friend statuses, student groups, discussion posts/comments, and local preferences (`AppSettingsEntity`). | `CampusDatabase.kt`, `Entities.kt`, `Daos.kt`, `RoomConverters.kt` |
| **iii. Practical Use of Multiple Sensors (2+)** | Combines **(1) GPS Location Provider** (Haversine distance to Grangegorman buildings) with **(2) Rotation Vector / Magnetometer Compass Sensor** (live azimuth heading indicator). | `CampusLocationManager.kt`, `CampusCompassSensorManager.kt` |
| **iv. Continuous Git Version Control** | Structured GitHub repository (`2labor/CampusLoop-Mobile-` and `2labor/CampusLoop-Backend-`) with feature branches and pull requests. | `MVP`, `docs/agentic-workflow`, `main` |
| **v. Dark Mode & Accessibility** | High-contrast Material 3 **Dark Theme** (`#101114` background, `#A9F36B` primary lime) and **Light Theme** (`#F7F8FA` / `#426B22`), `Map / List` toggle, and icon+text chips. | `Color.kt`, `Theme.kt`, `SocialMapScreen.kt` |
| **vi. State Management** | Lifecycle-aware state management via `CampusViewModel` (`AndroidViewModel`) and Kotlin `StateFlow`. | `CampusViewModel.kt`, `CampusRepository.kt` |
| **vii. Notifications, Security & Performance** | In-app notification feed with unread badges, JWT `Authorization: Bearer` interceptor, blocked-user event filtering, and Ghost Mode (`DELETE /api/v1/geo/ghost`). | `NetworkClient.kt`, `AuthSessionManager.kt`, `CampusWebSocketClient.kt` |
| **viii. Multilanguage Support** | Externalized localization in **English** and **Russian** with runtime language switching in Profile & Settings. | `ProfileAndSettingsScreens.kt` |
