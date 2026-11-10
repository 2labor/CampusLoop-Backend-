# CampusLoop Backend Specification

Version 1.0  
Date 2026-11-01  
Scope Android Kotlin Jetpack Compose MVP

## 1. Purpose and status legend

This specification defines the backend contract and implementation plan for CampusLoop, aligned with the supplied CampusLoop product and UI/UX design document and the Android Studio project found at `C:\Users\rusla\AndroidStudioProjects\CampusMates`.

The document distinguishes three kinds of statements:

- **Existing implementation** describes behavior observed in `CampusLoop-Backend--main`.
- **Recommendation** describes the target behavior required for a secure, privacy-preserving MVP.
- **Unresolved decision** identifies a product or operational choice that must be confirmed before implementation.

## 2. Executive summary

The backend is a Spring Boot 3.2.5 Kotlin service using Java 21, Spring Data JPA, PostgreSQL, Redis, Spring Security, JWT, email delivery, and STOMP WebSocket infrastructure. It already contains controllers, services, repositories, entities, DTOs, password reset and email verification building blocks, notifications, friend requests, groups, events, and participant workflows.

The Android project is still a Compose starter application. It currently contains one `MainActivity`, a greeting screen, generated theme/resources, and template tests. There is no API client, navigation graph, Room database, location permission flow, map screen, authentication UI, or feature implementation. The backend therefore cannot yet be considered integrated with the mobile MVP.

The backend is not release-ready without remediation. The most important blockers are:

1. enforce authorization at the service/query boundary for private events, groups, profiles, participants, and location data;
2. remove default database credentials and unauthenticated infrastructure exposure;
3. implement privacy-aware location sharing as a first-class capability;
4. re-enable account verification and add rate limiting and anti-enumeration behavior;
5. correct persistence constraints and make event capacity updates transactional and race-safe;
6. define one versioned API contract and add integration/security tests before connecting the Android client.

## 3. Source review and current implementation

### 3.1 Reviewed sources

- Product and UI/UX design document: `Campus Social Project DesDoc.pdf`, 24 pages.
- Backend archive: `CampusLoop-Backend--main.zip`.
- Android Studio project: `C:\Users\rusla\AndroidStudioProjects\CampusMates`.

### 3.2 Current backend stack

| Area | Existing implementation | Target MVP recommendation |
|---|---|---|
| Runtime | Spring Boot 3.2.5, Kotlin 1.9.23, Java 21 | Keep the stack; upgrade on a planned cadence |
| HTTP | Spring MVC REST controllers | Versioned JSON REST API under `/api/v1` |
| Persistence | Spring Data JPA and PostgreSQL driver | PostgreSQL with explicit migrations and constraints |
| Cache and transient tokens | Redis | Redis for short-lived tokens, rate limits, pub/sub, and optional cache |
| Authentication | BCrypt passwords and HMAC JWT | Short-lived access JWT plus refresh-token rotation and revocation |
| Email | Spring Mail and HTML templates | Verification and reset flows with generic responses and rate limits |
| Realtime | STOMP over WebSocket, simple broker, Redis pub/sub attempt | REST-first sync for MVP; authenticated scoped realtime after REST is stable |
| API documentation | None observed | OpenAPI generated from controllers and contract tests |
| Database migrations | None observed | Flyway or Liquibase from the first deployable build |
| Observability | No Actuator or structured audit trail observed | Health, metrics, traces, security events, and privacy-safe logs |

### 3.3 Current package architecture

The backend is a layered monolith:

`controller` -> `service` -> `repository` -> JPA entities

Cross-cutting pieces are in `config`, `security`, `infra.websocket`, and `domain.mapper`. This is a reasonable starting point for a two-person MVP. The recommended next step is a feature-oriented modular monolith inside the same deployable application, separating `auth`, `profile`, `events`, `friends`, `groups`, `location`, `notifications`, and `sync` while retaining shared database and security infrastructure.

### 3.4 Existing endpoints

The following routes are present in the source. They are not a complete or approved public contract until authorization, validation, status codes, and error handling are standardized.

#### Authentication and account

| Method | Path | Existing behavior |
|---|---|---|
| POST | `/api/v1/auth/register` | Creates a local account and returns account data |
| POST | `/api/v1/auth/login` | Checks login or email and returns one JWT |
| POST | `/api/v1/auth/verification/send` | Sends a verification code |
| POST | `/api/v1/auth/verification/verify` | Consumes the code and marks an account verified |
| POST | `/api/v1/auth/password/forgot` | Sends a password reset code |
| POST | `/api/v1/auth/password/reset` | Resets the password using email and code |
| POST | `/api/v1/auth/password/change-password` | Changes the authenticated user's password |

#### Events and participants

| Method | Path | Existing behavior |
|---|---|---|
| POST | `/api/v1/events` | Creates an event and adds the creator as approved participant |
| PUT | `/api/v1/events/{eventId}` | Updates an upcoming event if the caller is creator |
| DELETE | `/api/v1/events/{eventId}/cancel` | Cancels an event if the caller is creator |
| PUT | `/api/v1/events/{eventId}/complete` | Completes an event if the caller is creator |
| GET | `/api/v1/events/{eventId}` | Returns an event by ID |
| GET | `/api/v1/events/upcoming` | Returns upcoming events, optionally filtered by category |
| GET | `/api/v1/events/my/created` | Returns events created by the caller |
| GET | `/api/v1/events/{eventId}/participants` | Returns participant records |
| GET | `/api/v1/events/my-participants` | Returns the caller's participant records |
| POST | `/api/v1/events/{eventId}/participants` | Joins open events or creates a pending record for closed events |
| DELETE | `/api/v1/events/{eventId}/participants/me` | Leaves an event unless caller is creator |
| PUT | `/api/v1/events/{eventId}/participants/{targetUserId}?status=...` | Creator changes a participant status |

#### Friends

| Method | Path | Existing behavior |
|---|---|---|
| POST | `/api/v1/friends/requests/{targetUserId}` | Sends a friend request or accepts a reverse pending connection |
| PUT | `/api/v1/friends/requests/{requesterId}/accept` | Accepts a pending request |
| DELETE | `/api/v1/friends/requests/{requesterId}/reject` | Deletes a pending request |
| DELETE | `/api/v1/friends/{targetUserId}` | Removes an accepted connection |
| POST | `/api/v1/friends/block/{targetUserId}` | Deletes an existing connection and writes a block record |
| DELETE | `/api/v1/friends/block/{targetUserId}` | Removes a caller-owned block record |
| GET | `/api/v1/friends` | Lists accepted connections |
| GET | `/api/v1/friends/requests/incoming` | Lists incoming requests |
| GET | `/api/v1/friends/requests/incoming/count` | Returns incoming request count |

#### Profiles, users, groups, and notifications

| Area | Existing paths |
|---|---|
| Profiles | `GET /api/v1/profiles/me`, `GET /api/v1/profiles/{userId}`, `PUT /api/v1/profiles/me` |
| User search | `GET /api/v1/users/search?query=...` |
| Groups | `POST /api/v1/groups`, `GET /api/v1/groups/{groupId}`, `GET /api/v1/groups`, `GET /api/v1/groups/search`, `PUT /api/v1/groups/{groupId}`, `DELETE /api/v1/groups/{groupId}` |
| Group membership | `POST /api/v1/groups/{groupId}/join`, `DELETE /api/v1/groups/{groupId}/leave`, `GET /api/v1/groups/{groupId}/members`, role/status mutation routes |
| Notifications | list, unread count, mark-one-read, mark-all-read, and delete routes under `/api/v1/notifications` |

## 4. Alignment with the Android MVP

### 4.1 Current Android state

The project `CampusMates` has:

- one `:app` module;
- Kotlin and Jetpack Compose enabled;
- Material 3, Compose UI, lifecycle runtime, and test dependencies;
- no networking, serialization, Room, location, map, WebSocket, navigation, or dependency-injection library;
- only generated starter UI in `MainActivity.kt`;
- only generated example unit and instrumentation tests;
- no location permissions in `AndroidManifest.xml`.

### 4.2 Required mobile-to-backend boundary

The mobile app should use the backend through a repository layer. Compose screens must not call HTTP directly. Recommended flow:

`Composable` -> `ViewModel` -> `UseCase` or feature repository -> `RemoteDataSource` and `LocalDataSource` -> API / Room

Use entity IDs in navigation, as required by the product document. The server response must be reloaded by ID after navigation rather than serializing whole objects into route arguments.

### 4.3 Android MVP feature order

1. Authentication and session storage.
2. Events feed and event details.
3. Create, join, leave, approve, and complete event flows.
4. Map/list read model using campus event locations and opt-in friend locations.
5. Profiles and friend requests.
6. Groups and group membership.
7. Notifications and optional realtime updates.

Room should cache server-owned records and expose `Flow`/`StateFlow`. The server remains authoritative for permissions, event capacity, membership, and location visibility.

## 5. Target architecture

### 5.1 Recommended deployment shape

Use a single stateless backend service for the MVP, one PostgreSQL database, and one Redis instance. Put the service behind HTTPS termination and a reverse proxy or managed load balancer. Keep PostgreSQL and Redis on private network segments; only the API should be reachable from the public internet.

### 5.2 Feature modules

Recommended logical modules:

- `auth`: registration, login, refresh, logout, verification, password reset;
- `users`: account, profile, public profile projection, search;
- `events`: lifecycle, access policy, participant state machine;
- `friends`: requests, accepted relationships, block enforcement;
- `groups`: group lifecycle, membership, roles, invitations;
- `location`: sharing preferences, ephemeral location updates, map read model;
- `notifications`: durable in-app notifications and delivery;
- `sync`: cursors, change feed, idempotency, outbox;
- `platform`: error handling, validation, security, audit, observability.

### 5.3 Transactions and ownership

All write use cases must execute in a transaction. The service layer, not the controller, owns authorization decisions. Repositories should expose purpose-built queries that include the caller's visibility scope rather than loading an object and relying on the UI to hide it.

## 6. Domain model

### 6.1 Account and user profile

`Account` stores authentication identity and credential state. `UserProfile` stores user-facing profile content. The API must never serialize password hashes or internal account fields.

Target fields:

- `accountId`, `userId`, `email`, `login`, `provider`, `verifiedAt`, `status`;
- `username`, `fullName`, `avatarUrl`, `bio`, `course`, `academicYear`, `campusId`, `interests`;
- `createdAt`, `updatedAt`, `deletedAt`.

### 6.2 Campus

Add a `campuses` table before multi-campus behavior is introduced. An event and a user profile should reference a stable `campusId`, not a free-text campus name. Store campus boundary or approved venue data separately so event coordinates can be validated against the campus.

### 6.3 Event

Target event fields:

- `eventId`, `campusId`, `organizerId`;
- `title`, `description`, `category`, `eventType`;
- `startAt`, `endAt`, `status`;
- `placeName`, `latitude`, `longitude`, `locationPrecision`;
- `visibility`, `maxParticipants`;
- `createdAt`, `updatedAt`, `cancelledAt`, `completedAt`.

Recommended event status: `DRAFT`, `PUBLISHED`, `FULL`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`, `EXPIRED`. `FULL` should be derived or maintained transactionally; the client must not infer it from a stale count.

### 6.4 Event participant

Use one row per `(eventId, userId)` with a unique constraint. Recommended statuses are `INVITED`, `PENDING`, `APPROVED`, `REJECTED`, `LEFT`, and `REMOVED`. The organizer is an approved participant or an explicit organizer relation, but not both accidentally.

### 6.5 Friendship and block

Store a canonical ordered pair `(lowerUserId, higherUserId)` or enforce symmetric lookup through a database constraint. Store `PENDING`, `ACCEPTED`, and `BLOCKED` in a relationship record, and record who initiated the request. Every read and write must apply block filtering.

### 6.6 Group and membership

A group has a creator, visibility, and lifecycle. Group membership has a unique `(groupId, userId)` key, `PENDING`/`ACTIVE`/`BANNED` state, and `ADMIN`/`MODERATOR`/`MEMBER` role. Private group details and member lists require active membership or a privileged operator.

### 6.7 Location share

Add a first-class location share record per user:

- `userId`;
- `mode`: `HIDDEN`, `FRIENDS`, `DURING_EVENT`;
- `lastLatitude`, `lastLongitude`, `accuracyMeters`, `capturedAt`, `expiresAt`;
- `visibleToEventId` when mode is `DURING_EVENT`;
- `enabledAt`, `updatedAt`.

The record is ephemeral by design. Do not retain a historical location trail for the MVP. If historical analytics are later required, collect a separate consent decision and use coarse aggregation.

### 6.8 Notification and sync records

Notifications are durable, user-scoped records. Add an `outbox_events` table for reliable publication after a database commit, and a monotonically increasing `changeSequence` or server cursor for incremental mobile sync.

## 7. Database schema and constraints

The target schema should include at least:

| Table | Key constraints and indexes |
|---|---|
| `accounts` | unique normalized email, unique normalized login, provider, verification and status fields |
| `users` | stable UUID primary key, normalized username uniqueness if usernames are public |
| `user_profiles` | one-to-one user key, bounded text lengths |
| `user_interests` | composite primary key `(user_id, interest)` |
| `campuses` | unique campus slug and active flag |
| `events` | organizer/campus foreign keys, status/category/time indexes, coordinate range checks |
| `event_participants` | unique `(event_id, user_id)`, event/status and user/status indexes |
| `friendships` | canonical pair uniqueness, target/status index, block index |
| `groups` | creator/campus foreign keys, visibility and name search support |
| `group_members` | unique `(group_id, user_id)`, group/status and user/status indexes |
| `location_shares` | one current row per user, expiry index, no location history in MVP |
| `notifications` | receiver/created index and receiver/unread index |
| `outbox_events` | sequence, aggregate, status, created index, idempotency key |

The current source contains a critical mapping defect: `EventParticipant` declares a unique constraint over `event_id` and `account_id`, while its relationship join column is named `user_id`. This must be corrected before schema generation or migration.

Use Flyway or Liquibase migrations. Do not rely on Hibernate auto-DDL in production. Add a migration test that starts a clean PostgreSQL schema and verifies constraints.

## 8. API contract

### 8.1 General rules

- Base path: `/api/v1`.
- Content type: `application/json`.
- IDs: UUID strings.
- Times: ISO-8601 UTC instants, for example `2026-11-01T19:30:00Z`.
- Pagination: cursor pagination for feeds and change feeds; bounded page size for simple lists.
- Every mutating request should accept an `Idempotency-Key` where duplicate submission is possible.
- Never return exact user coordinates in a public profile or broad event feed unless the caller's visibility scope allows it.

### 8.2 Authentication contract

Recommended endpoints:

| Method | Path | Response |
|---|---|---|
| POST | `/auth/register` | `201` account summary; verification required |
| POST | `/auth/login` | `200` access token and refresh token metadata |
| POST | `/auth/refresh` | `200` rotated access and refresh tokens |
| POST | `/auth/logout` | `204`; revokes the refresh token family |
| POST | `/auth/verification/send` | `202` generic message |
| POST | `/auth/verification/verify` | `204` or `200` verified account summary |
| POST | `/auth/password/forgot` | `202` generic message regardless of account existence |
| POST | `/auth/password/reset` | `204`; revokes existing sessions |
| POST | `/auth/password/change` | `204`; revokes other sessions |

The existing single `TokenResponse(token)` should be replaced by a documented token envelope. Access tokens should be short-lived, and refresh tokens should be opaque, hashed at rest, rotated, and revocable.

### 8.3 Event contract

Recommended routes:

- `GET /events?campusId=&from=&to=&category=&visibility=&cursor=`: return only events visible to the caller.
- `POST /events`: create a published or draft event with validated campus location and time.
- `GET /events/{eventId}`: return a visibility-filtered event projection.
- `PATCH /events/{eventId}`: organizer-only update with optimistic version check.
- `POST /events/{eventId}/cancel`, `POST /events/{eventId}/complete`: organizer-only transitions.
- `POST /events/{eventId}/participants`: join or request access according to event policy.
- `DELETE /events/{eventId}/participants/me`: leave.
- `GET /events/{eventId}/participants`: organizer and approved participants only, unless product decision says otherwise.
- `PATCH /events/{eventId}/participants/{userId}`: organizer-only approve/reject/remove.
- `POST /events/{eventId}/invites`: organizer-only invitation creation.

Create request validation must require a nonblank bounded title, allowed category and type, future `startAt`, optional future `endAt` after `startAt`, valid latitude and longitude, a campus boundary check, and a positive maximum participant count when present.

### 8.4 Friends and profiles

- `GET /users/search?q=&cursor=` returns a minimal discoverable profile projection, subject to block and discovery settings.
- `GET /profiles/me` and `PATCH /profiles/me` manage the caller's profile.
- `GET /profiles/{userId}` returns a policy-filtered public/friend profile.
- `POST /friends/requests`, `POST /friends/requests/{id}/accept`, `POST /friends/requests/{id}/reject`, `DELETE /friends/{userId}`, `POST /blocks/{userId}`, and `DELETE /blocks/{userId}` implement explicit relationship actions.
- `GET /friends`, `GET /friends/requests/incoming`, and `GET /friends/requests/outgoing` are caller-scoped.

The server must verify that the accepting or rejecting user is the request target, not merely one of the two people in an undirected lookup.

### 8.5 Groups

- `GET /groups` and `GET /groups/search` return public groups and groups the caller may discover.
- `POST /groups` creates a group and its admin membership atomically.
- `GET /groups/{groupId}` returns public summary or member-only details for private groups.
- `POST /groups/{groupId}/join` creates an active or pending membership according to visibility.
- `POST /groups/{groupId}/invites` creates an invitation for a target user.
- `PATCH /groups/{groupId}/members/{userId}` changes role or status after an admin/moderator check.
- `GET /groups/{groupId}/members` returns active members only unless caller is privileged.

### 8.6 Location and map

Recommended MVP routes:

- `GET /map?campusId=&bounds=&include=events,friends`: returns events plus location-sharing friend projections visible to the caller.
- `PUT /me/location-sharing`: sets `HIDDEN`, `FRIENDS`, or `DURING_EVENT` and an expiry.
- `POST /me/location`: submits an ephemeral coordinate only when device permission and sharing mode allow it.
- `DELETE /me/location`: immediately removes the current location.

The map response should return `displayLatitude`, `displayLongitude`, `accuracyRadiusMeters`, `capturedAt`, and a `stale` flag. For friends, use campus-place or coarse-cell display by default; exact coordinates require an explicit product decision and a narrowly scoped relationship.

### 8.7 Standard error envelope

Use one error shape:

```json
{
  "code": "EVENT_FULL",
  "message": "This event has reached its participant limit.",
  "fieldErrors": [],
  "requestId": "7d9c...",
  "retryable": false
}
```

Recommended status mapping:

- `400` malformed request or invalid state transition;
- `401` missing or invalid authentication;
- `403` authenticated but not permitted;
- `404` resource not found or intentionally hidden;
- `409` conflict such as duplicate membership or stale version;
- `422` valid JSON that fails domain validation;
- `429` rate limit;
- `500` unexpected server failure without internal details.

Do not expose stack traces, SQL, email existence, token values, or internal IDs in error messages.

## 9. Authentication and authorization

### 9.1 Authentication

1. Register with normalized email/login and a strong password policy.
2. Require verification before social discovery or event creation, unless an explicit product decision permits a limited unverified state.
3. Issue a short-lived access JWT containing `sub`, `iat`, `exp`, `iss`, `aud`, and a unique `jti`.
4. Store refresh-token hashes with device/session metadata and rotate on use.
5. Revoke the session family on password reset, password change, logout, or suspected refresh-token reuse.
6. Keep JWT secrets in a secret manager and rotate signing keys with a key identifier.

### 9.2 Authorization rules

| Resource | Read | Write |
|---|---|---|
| Own profile | owner | owner |
| Other profile | public projection, or richer projection for accepted friends | none |
| Open event | authenticated users in same campus/allowed audience | organizer |
| Private event | organizer, invitees, approved/requesting participants according to policy | organizer; participant can manage own request |
| Event participant list | organizer and approved participants | organizer status changes; user can leave |
| Public group | authenticated users | admin/moderator by action |
| Private group | active members and privileged operators | admin/moderator by action |
| Friend requests | involved users only | involved users only |
| Location share | user and policy-approved viewers | owner only |
| Notifications | receiver only | receiver read/delete; server creates |

Enforce these rules in service methods and repository queries. UI hiding is not a security control.

## 10. Location privacy design

The product brief makes privacy by design a core principle. The backend must implement it explicitly:

- sharing is `HIDDEN` by default;
- device permission is separate from app sharing preference;
- joining an event does not automatically enable sharing;
- `FRIENDS` exposes only accepted, unblocked friends;
- `DURING_EVENT` exposes only the event scope and only during the configured window;
- location records expire automatically and are deleted on disable;
- no location history is retained in MVP;
- exact coordinates are never included in public profiles, broad search, or unrestricted event responses;
- every map response is filtered by the caller's relationship and campus;
- stale location must be marked or omitted, never presented as current;
- abuse controls and a future block/report feature are required before broad rollout.

## 11. Validation and business rules

Apply Bean Validation at the controller boundary and repeat critical invariants in the domain service. Add explicit maximum lengths for all text and an allowlist for enum values.

Minimum rules:

- email normalized to lowercase and trimmed;
- university-domain validation checks a domain boundary, not only a suffix;
- passwords are never logged or returned;
- `title` 1-120 characters; `description` 0-2000; names and bios bounded;
- coordinates must be within valid geographic ranges and inside the selected campus boundary;
- `startAt` must be in the future at creation; `endAt` must be after `startAt`;
- `maxParticipants` must be null or between 1 and a configured safe maximum;
- search query length must be bounded and rate-limited;
- pagination size must have a server-side maximum;
- every state transition must be explicit and idempotent;
- event join must lock or atomically update capacity and enforce the unique participant key;
- private access must be checked on every read, join, participant-list, and realtime path.

## 12. Realtime and synchronization strategy

### 12.1 MVP recommendation

Use REST plus Room as the primary sync path. On app start and foreground resume, fetch a cursor-based change feed for the user's visible events, friends, groups, notifications, and location projections. Use `updatedAt` plus a server cursor or outbox sequence. Handle `401` by refreshing once, then clear the session. Handle conflicts by refetching the affected entity.

This gives the Android MVP deterministic offline/cache behavior without making WebSocket delivery a prerequisite for correctness.

### 12.2 Later WebSocket strategy

If realtime is needed for the map or participant list:

- authenticate the STOMP `CONNECT` frame;
- authorize every `SUBSCRIBE` destination against the caller and resource;
- use opaque destination IDs only after authorization;
- emit outbox-backed events with event ID, sequence, action, resource ID, and a minimal payload;
- support reconnect with `lastSequence` and REST replay;
- never trust a client-supplied destination or user ID;
- constrain origins to the mobile/backend deployment origins;
- use Redis streams or a durable broker if replay and multi-instance delivery are required.

The current simple broker and Redis pub/sub attempt do not provide a durable replay contract.

## 13. Deployment and configuration

Required configuration groups:

- database URL, username, password, SSL mode, pool limits;
- Redis URL, authentication, TLS, timeouts;
- JWT signing key or key set, issuer, audience, access and refresh TTLs;
- allowed university domains;
- email host, port, sender, credentials, and template URLs;
- rate limits and maximum page sizes;
- campus identifiers and geographic boundaries;
- log redaction and audit settings.

Deployment requirements:

- pin PostgreSQL and Redis image versions; do not use `latest`;
- bind local development databases to loopback or a private network;
- never commit real secrets; use environment injection or a secret manager;
- enable TLS for API, database, Redis, and email transport where supported;
- run migrations as a controlled release step;
- configure backups, restore tests, health checks, and resource limits;
- add dependency scanning, container scanning, secret scanning, and SBOM generation;
- expose readiness only after database and Redis dependencies are healthy.

## 14. Testing strategy

### 14.1 Backend tests

- unit tests for state machines, authorization policies, location projection, validation, and token services;
- repository tests against PostgreSQL for constraints and visibility queries;
- service tests for organizer/member/admin boundaries;
- MockMvc or WebTestClient contract tests for every endpoint;
- integration tests for registration, verification, reset, events, friends, groups, notifications, and map privacy;
- concurrency test for event capacity and duplicate join;
- WebSocket tests for connect, authorized subscription, unauthorized subscription, and reconnect replay;
- security regression tests for IDOR/BOLA, private resource access, block enforcement, rate limits, and token revocation;
- migration test from an empty database and from the previous version.

### 14.2 Android tests

- repository tests with fake and recorded API responses;
- Room migration and cache tests;
- ViewModel tests for loading, empty, offline, unauthorized, conflict, and retry states;
- Compose UI tests for map/list, event creation, private-event access, permission denial, and accessibility semantics;
- instrumentation tests for real-device location permission and lifecycle behavior.

### 14.3 Release gates

Do not release the MVP until:

- all P0/P1 security findings are closed or formally accepted;
- private event and group authorization tests pass;
- database migrations and capacity constraints are verified;
- Android can register/login, load events, create/join events, and handle no location/no network;
- error responses and loading/empty states match the product document;
- logs contain no passwords, tokens, exact location trails, or raw reset codes.

## 15. Phased implementation plan

### Phase 0 Foundation and hardening

Add configuration profiles, migrations, consistent errors, validation, rate limiting, structured logging, access-token/refresh-token design, and security tests. Fix the event participant constraint and group membership query. Remove default exposed infrastructure credentials.

### Phase 1 Authentication and profiles

Implement register, verify, login, refresh, logout, password reset, session revocation, profile read/update, and user search with privacy-filtered projections. Add the Android API client, secure token storage, navigation, and auth screens.

### Phase 2 Events

Implement event creation, map/list event feed, detail, join/leave, private requests/invitations, participant moderation, lifecycle transitions, and capacity-safe transactions. Add Room cache and Compose event screens.

### Phase 3 Friends and groups

Implement friend workflows, block enforcement, public/private groups, membership approvals, roles, and group member visibility. Add profile, friend, and group screens.

### Phase 4 Location privacy and notifications

Implement location sharing preferences, ephemeral location updates, map projections, notification inbox, and push-provider integration if required. Test permission denial, stale data, and revocation.

### Phase 5 Realtime and polish

Add authenticated scoped realtime only if REST sync is stable. Add outbox/replay, accessibility, English/Russian localization, dark/light themes, observability dashboards, release checks, and production deployment automation.

## 16. Unresolved decisions

The following decisions are not fully specified by the supplied materials:

1. Exact campus identity, boundary, venue catalog, and map provider.
2. Whether event coordinates are exact, venue-based, or always coarse for viewers.
3. Whether closed-event requests are allowed without an invite, or invite-only strictly means invite-only.
4. Whether university email verification is mandatory for all users in the MVP.
5. Whether chats are excluded from the MVP navigation or represented as a disabled/future section.
6. Whether the app needs push notifications in the MVP or only in-app notifications.
7. Required retention period for accounts, notifications, audit records, and deleted profiles.
8. Whether groups are campus-specific and whether cross-campus discovery is allowed.
9. Production hosting, domain, TLS termination, email provider, and operational owner.
10. Whether realtime map updates are a release requirement or a post-MVP enhancement.

## 17. Definition of backend done for the Android MVP

The backend is ready when the Android app can, using documented contracts and an authenticated session:

1. register, verify, login, refresh, logout, and recover a password;
2. load a privacy-filtered campus event map/list;
3. create, edit, cancel, complete, join, leave, and moderate events safely;
4. create a private event whose details and participants are inaccessible to unauthorized users;
5. search profiles and send, accept, reject, remove, and block friend relationships;
6. create and join groups with private membership controls;
7. select and revoke location-sharing mode without retaining a location history;
8. receive durable notifications and recover from offline/reconnect states;
9. obtain consistent errors, empty states, and retry guidance;
10. pass migration, authorization, privacy, concurrency, and integration tests.
