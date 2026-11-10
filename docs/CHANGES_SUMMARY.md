# CampusLoop Mobile — Complete MVP Implementation & Change Log

> **Repository:** `2labor/CampusLoop-Mobile-`  
> **Active Branch:** `MVP`  
> **Pull Requests:** [PR #1 (MVP Release)](https://github.com/2labor/CampusLoop-Mobile-/pull/1) · [PR #2 (AI Agentic Workflow & Docs)](https://github.com/2labor/CampusLoop-Mobile-/pull/2)  
> **Date:** November 2026  
> **Status:** Production-Ready MVP + Live Cloudflare Edge Backend (`https://campusloop-api.rohovskyi.dev`)

---

## 1. Initial State & Problem Analysis

Prior to this work, the project had visual Jetpack Compose screens, but **no working functionality**:
1. **Zero Persistence:** No local SQLite database or Room implementation existed. All data was kept in volatile in-memory StateFlow objects. Every app process restart wiped all newly created meetups, joined events, community groups, profile updates, and friendship changes.
2. **Mock Sensors:** GPS location and compass orientation were purely hardcoded static values. No `LocationManager` or `SensorManager` was integrated.
3. **No OS Runtime Permission Requests:** While `ACCESS_FINE_LOCATION` was declared in `AndroidManifest.xml`, the app never requested runtime permissions from the Android OS. Tapping "Grant GPS" merely flipped an in-memory boolean flag.
4. **Startup Crash:** Attempting to run the app caused `NoSuchMethodException: CampusViewModel.<init> [class android.app.Application]` because Android's `AndroidViewModelFactory` uses Java reflection, which requires `@JvmOverloads` when Kotlin constructors have default parameters.
5. **Event State Machine Bugs:** 
   - Substring parsing of time presets resulted in unclosed parentheses (e.g. `(15:00`).
   - Leaving an event when it was `IN_PROGRESS` or `COMPLETED` erroneously reverted its status to `OPEN`.
   - Full events blocked pending waitlist requests for private meetups.
6. **Safety & Privacy Leak:** Blocked students' organized events were not filtered from the map or event feed.
7. **Navigation & Backstack Flaws:** Submitting the `CreateEvent` form preserved the dirty form on the backstack instead of popping it.
8. **Static Notifications:** Tapping an unread notification did not mark it as read, and user actions produced zero notifications.
9. **Zero Test Coverage:** Only a dummy template test existed.

---

## 2. Complete Inventory of Changes

### A. Local SQLite Database Layer (Room & KSP)
* **`app/src/main/java/com/example/campusmates/data/local/converters/RoomConverters.kt`**:
  TypeConverters for all 11 domain enums (`AppLanguage`, `EventCategory`, `EventVisibility`, `EventStatus`, `ParticipantStatus`, `ActivityStatus`, `LocationSharingMode`, `MascotSkin`, `FriendshipState`, `GroupVisibility`, `GroupMembershipState`) and list serialization.
* **`app/src/main/java/com/example/campusmates/data/local/entity/Entities.kt`**:
  Normalized SQLite tables matching the Milestone 1 + Backend v2 architecture specification (`UserEntity`, `EventEntity`, `EventParticipantEntity`, `FriendshipEntity`, `GroupEntity`, `GroupMemberEntity`, `LocationShareEntity`, `CampusNotificationEntity`, `AppSettingsEntity`, `PostEntity`, `CommentEntity`).
* **`app/src/main/java/com/example/campusmates/data/local/dao/Daos.kt`**:
  Reactive Room DAOs (`UserDao`, `EventDao`, `EventParticipantDao`, `GroupDao`, `NotificationDao`, `SettingsDao`, `PostDao`, `CommentDao`) providing `Flow` queries and suspend mutation methods.
* **`app/src/main/java/com/example/campusmates/data/local/CampusDatabase.kt`**:
  RoomDatabase singleton (v2) with destructive migration fallback and asynchronous seed pre-population on first install.
* **`app/src/main/java/com/example/campusmates/data/repository/CampusRepository.kt`**:
  Single source of truth repository exposing domain models, atomic transactions, durable notification generation, and remote REST + STOMP synchronization.

### B. Hardware Sensor & Android OS Integration
* **`app/src/main/java/com/example/campusmates/sensor/CampusLocationManager.kt`**:
  Native Android `LocationManager` integration querying `GPS_PROVIDER` and `NETWORK_PROVIDER`. Includes mathematical Haversine formula calculation computing live meter distances to campus zones.
* **`app/src/main/java/com/example/campusmates/sensor/CampusCompassSensorManager.kt`**:
  Hardware `SensorManager` integration listening to `Sensor.TYPE_ROTATION_VECTOR` (with accelerometer + magnetic fallback) to calculate live device azimuth angles (`0°..360°`).
* **`app/src/main/java/com/example/campusmates/ui/screens/SocialMapScreen.kt`** & **`ProfileAndSettingsScreens.kt`**:
  Added Compose `rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions())` to trigger native Android OS location permission dialogs.

### C. Backend & Cloudflare Edge Integration (All 18 Controllers + STOMP 1.2)
* **Live Hosted Cloudflare Edge (`hazed0m` / `hazekk94@gmail.com`):**
  - Custom Domain: `https://campusloop-api.rohovskyi.dev` (`wss://campusloop-api.rohovskyi.dev/ws`)
  - Workers.dev URL: `https://campusloop-backend.hazekk94.workers.dev`
  - Cloudflare D1 Database: `campusloop-db` (`ab6fd3eb-fd74-458f-a3a1-f2f639272fb9`)
* **Android Network Layer (`data/remote/`):**
  - `AuthSessionManager.kt`: Defaults to `https://campusloop-api.rohovskyi.dev/` with persistent JWT session state.
  - `NetworkClient.kt` & `CampusApiInterfaces.kt`: Full Retrofit interfaces for `AuthApi`, `PostApi`, `CommentApi`, `GroupApi`, `FriendshipApi`, `UserProfileApi`, `EventApi`, `NotificationApi`, `ChannelApi`, `MessageApi`, `GeoApi`, and `StorageApi`.
  - `CampusWebSocketClient.kt`: Native OkHttp STOMP 1.2 protocol engine subscribing to `/topic/events`, `/topic/system/announcements`, `/user/queue/notifications`, `/user/queue/geo/friends`, and `/topic/users/{userId}/friends`.
