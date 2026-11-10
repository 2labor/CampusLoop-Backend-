# CampusLoop Security Audit

Version 1.0  
Date 2026-11-01  
Assessment type Static repository review

## 1. Scope and limitations

This audit covers the extracted `CampusLoop-Backend--main` repository and the Android project at `C:\Users\rusla\AndroidStudioProjects\CampusMates`, with the product requirements taken from the supplied CampusLoop design document.

No installed skill explicitly named `security-audit` or containing security-audit instructions was available in the configured skill roots. The review therefore applies a repository-grounded OWASP-style assessment covering authentication, authorization, sensitive data exposure, input validation, secrets and configuration, availability, dependency/runtime hardening, and privacy.

The assessment is static. It did not include an authenticated runtime penetration test, production configuration review, database inspection, cloud IAM review, or third-party service review. The repository's Maven wrapper could not run because the archive contains `wrapper/maven-wrapper.properties` but the wrapper expects `.mvn/wrapper/maven-wrapper.properties`; no Maven executable was available in the environment. The repository also contains no application configuration file, so startup and integration behavior could not be verified end to end.

Severity is prioritized as:

- **P0 Critical**: do not expose or integrate until fixed.
- **P1 High**: fix before MVP release; meaningful confidentiality, integrity, or abuse risk.
- **P2 Medium**: fix before production or before enabling the affected feature.
- **P3 Low**: hardening, quality, or defense-in-depth.

## 2. Executive conclusion

The backend has a useful foundation, but authorization and privacy controls do not yet match the product's core requirements. In particular, the current implementation exposes broad profile/event/group data to authenticated users, returns exact event coordinates, and provides no authorization check for WebSocket subscriptions. The local deployment file also publishes PostgreSQL and Redis with a default password and no Redis password. Verification enforcement, rate limiting, token revocation, and security regression coverage are incomplete.

The highest-risk abuse case is an authenticated user enumerating other users, private event details, participant lists, and private groups, then subscribing to realtime destinations belonging to other users or resources. That is an authorization design issue, not a UI issue.

## 3. Findings summary

| ID | Severity | Finding | Status |
|---|---|---|---|
| SEC-01 | P0 | Development infrastructure exposes default PostgreSQL credentials and unauthenticated Redis | Open |
| SEC-02 | P0 | Object-level authorization is missing or incomplete for profiles, events, participants, groups, and private resources | Open |
| SEC-03 | P0 | WebSocket origins are wildcarded and subscriptions are not authorization-scoped | Open |
| SEC-04 | P1 | Account verification is disabled and university-domain validation is bypassable by suffix matching | Open |
| SEC-05 | P1 | Password-reset and verification flows enable account enumeration and lack rate limiting | Open |
| SEC-06 | P1 | Request validation is inconsistently applied; geospatial, time, text, and pagination limits are missing | Open |
| SEC-07 | P1 | JWT lifecycle lacks revocation, rotation, audience/issuer checks, and authorities | Open |
| SEC-08 | P1 | Event participant uniqueness and capacity enforcement are unsafe | Open |
| SEC-09 | P1 | Group membership authorization and persistence rules contain access-control and integrity defects | Open |
| SEC-10 | P1 | Realtime Redis channel wiring does not match its listener patterns and has no replay contract | Open |
| SEC-11 | P1 | Exact location and broad profile data are returned without a privacy projection | Open |
| SEC-12 | P2 | Dependency, container, TLS, logging, and operational hardening are incomplete | Open |
| SEC-13 | P2 | Security and authorization test coverage is insufficient | Open |

## 4. Detailed findings

### SEC-01 P0 Default credentials and public infrastructure ports

**Evidence**

- `docker-compose.yml:4-14` sets `POSTGRES_USER: user` and `POSTGRES_PASSWORD: password`.
- `docker-compose.yml:14` publishes PostgreSQL on `5435:5432`.
- `docker-compose.yml:17-19` runs `redis:latest` with no password or TLS configuration.
- `docker-compose.yml:18` publishes Redis on `6379:6379`.

**Impact**

Anyone able to reach the host ports may read or modify all application data, inject or delete transient tokens, publish fake realtime events, or destroy the database. Even if intended for local development, copying this compose file to a shared host creates an immediate compromise path.

**Remediation**

- Remove fixed credentials from version-controlled compose files.
- Inject strong local secrets through an ignored `.env` file or secret manager.
- Bind development ports to `127.0.0.1` unless remote access is intentional.
- Require Redis authentication and disable public exposure; use TLS in shared environments.
- Pin image versions and add a private network for the application, PostgreSQL, and Redis.
- Rotate credentials if this file has ever been used outside a private local machine.

### SEC-02 P0 Missing object-level authorization and private-resource filtering

**Evidence**

- `controller/UserProfileController.kt:33-38` returns any profile by `userId` without checking the caller's relationship or discovery policy.
- `controller/EventController.kt:73-79` returns an event without an authenticated principal or visibility check.
- `service/impl/EventServiceImpl.kt:142-146` loads an event directly by ID with no access policy.
- `controller/EventParticipantController.kt:28-35` lists participants without an authenticated principal or event-access check.
- `controller/GroupController.kt:43-50` loads a group while ignoring the authenticated `userId`.
- `service/impl/GroupServiceImpl.kt:55-59` returns any group directly by ID.
- `controller/GroupController.kt:53-80` lists and searches groups without a caller scope.

**Impact**

An authenticated user can enumerate profiles, event details, participant lists, and private groups by changing UUIDs. This violates the product requirement that private event details and location-related data be restricted to invited or approved users.

**Remediation**

- Define a resource authorization policy for every read and write.
- Pass caller identity into service methods for all resource reads.
- Replace direct `findById` reads with visibility-aware queries or policy checks.
- Return `404` for hidden resources where hiding existence is appropriate.
- Add regression tests for caller A accessing caller B's private event, group, participant list, and profile.

### SEC-03 P0 Wildcard WebSocket origin and unrestricted subscriptions

**Evidence**

- `config/WebSocketConfig.kt:23-26` calls `.setAllowedOriginPatterns("*")`.
- `infra/websocket/JwtChannelInterceptor.kt:18-34` authenticates only the `CONNECT` command.
- No subscription interceptor or destination authorization is present.
- Realtime publishers use user/resource-specific destinations such as `/topic/users/{id}/friends` and `/topic/events/{id}/participants`.

**Impact**

Any origin may attempt a WebSocket connection, and any authenticated client that knows or guesses a destination can subscribe to another user's relationship events, private event participant events, or other scoped data. Authentication at connect time does not authorize every destination.

**Remediation**

- Replace `*` with the exact mobile/backend origins needed for deployment.
- Authorize every `SUBSCRIBE` destination using the authenticated principal and resource policy.
- Reject client-supplied user IDs that do not equal the authenticated user where the destination is user-private.
- Use short-lived access tokens, connection expiry, and a safe reconnect flow.
- Add WebSocket tests for unauthorized subscriptions and cross-user event topics.

### SEC-04 P1 Verification enforcement is disabled and domain validation is weak

**Evidence**

- `service/impl/AuthFacade.kt:48` comments out the verification email send.
- `service/impl/AuthFacade.kt:61-63` comments out the verified-account check during login.
- `service/impl/AuthFacade.kt:33-35` uses `email.endsWith(universityDomain, ignoreCase = true)`.

**Impact**

Unverified accounts can log in and use authenticated functionality. Suffix matching without a domain boundary can accept lookalike domains that merely end with the configured string.

**Remediation**

- Normalize the email and parse the domain portion.
- Accept only an exact configured domain or an explicitly configured subdomain boundary.
- Send verification as part of registration or require an explicit resend step.
- Reject unverified login or issue a restricted token with no social/event privileges, according to product decision.
- Store `verifiedAt` rather than only a mutable boolean.

### SEC-05 P1 Account enumeration and missing abuse controls

**Evidence**

- `service/impl/AccountPasswordServiceImpl.kt:27-30` throws a distinct error when an email does not exist.
- `service/impl/AccountVerificationServiceImpl.kt:22-28` also returns distinct account-existence errors.
- Verification and reset codes are generated with `Random.nextInt` in `AccountVerificationServiceImpl.kt:30` and `AccountPasswordServiceImpl.kt:31`.
- No controller or Redis rate limit is present for login, verification, resend, or password reset.

**Impact**

Attackers can enumerate registered campus users, brute-force six-digit codes, abuse email delivery, and perform password spraying or denial-of-service against accounts.

**Remediation**

- Return the same `202` response for existing and non-existing reset/verification addresses.
- Rate-limit by IP, email, account, and device fingerprint where appropriate.
- Track attempts and lock or delay after repeated failures.
- Generate codes with a cryptographically secure RNG, store only a hash, use a short TTL, and consume atomically.
- Use generic login failures and audit suspicious activity.

### SEC-06 P1 Inconsistent validation and missing request/resource limits

**Evidence**

- `controller/AuthController.kt:23-31` does not use `@Valid` on registration or login requests.
- `domain/request/CreateEventRequest.kt` has no validation annotations for title, coordinates, time, or capacity.
- `controller/GroupController.kt:33-38,83-90` accepts group requests without `@Valid`.
- `controller/UserProfileController.kt:41-47` accepts profile updates without `@Valid`.
- Search and list endpoints accept free-form queries and Spring `Pageable` without a visible maximum page size.

**Impact**

Oversized input, invalid coordinates, past events, huge page requests, and unbounded search can cause data-quality failures or resource exhaustion. Validation gaps also increase the chance of stored abusive content.

**Remediation**

- Add `@Valid` consistently and use explicit field constraints.
- Validate domain invariants in services, including campus boundary and state transitions.
- Configure a hard page-size maximum and maximum query length.
- Add request-body and response-size limits at the reverse proxy and application layer.
- Normalize and bound user-generated text; add content moderation/reporting before broad launch.

### SEC-07 P1 Weak JWT session lifecycle

**Evidence**

- `security/JwtCore.kt:22-33` signs a token containing only the subject, issue time, and expiry.
- `security/JwtCore.kt:16-20` derives an HMAC key from a configured string with no visible key rotation or validation policy.
- `service/impl/AuthFacade.kt:65` issues a JWT directly after password validation.
- Password change/reset services update the password but do not revoke existing JWTs.
- `JwtTokenFilter.kt:23-29` authenticates only a UUID subject and assigns no authorities.

**Impact**

A stolen access token remains usable until expiry, including after password reset or change. There is no issuer/audience separation, session inventory, or privilege claim model. A weak or misconfigured secret could invalidate the trust boundary for the whole application.

**Remediation**

- Use short-lived access tokens and rotating, revocable refresh tokens.
- Add `iss`, `aud`, `jti`, and a key identifier; validate them strictly.
- Maintain session/revocation state for refresh tokens and emergency access-token invalidation.
- Rotate signing keys through a secret manager or JWKS.
- Load and validate secrets at startup; fail closed if weak or absent.
- Add explicit authorities only when needed and enforce them consistently.

### SEC-08 P1 Event participant integrity and capacity race

**Evidence**

- `domain/entity/EventParticipant.kt:21-24` declares uniqueness over `event_id, account_id`.
- The same entity maps the user relationship with `@JoinColumn(name = "user_id")` at lines 34-36.
- `service/impl/EventParticipantServiceImpl.kt:63-68` counts approved participants, then saves at lines 77-83 without a lock or atomic capacity update.

**Impact**

The unique constraint references a column that does not match the mapped join column and may fail schema creation or fail to prevent duplicate users. Concurrent joins can exceed `maxParticipants`, which violates the product's participant-limit rule.

**Remediation**

- Change the unique constraint to `(event_id, user_id)` and verify it in a migration test.
- Use a database transaction with a row lock on the event, an atomic counter guarded by a check, or a serializable/optimistic version strategy.
- Treat duplicate join as `409`, not a generic exception.
- Add concurrent join tests for open and private events.

### SEC-09 P1 Group membership authorization and integrity defects

**Evidence**

- `domain/entity/GroupMember.kt:20-22` has no unique constraint for `(group_id, user_id)`.
- `service/impl/GroupMemberServiceImpl.kt:164-168` checks `repository.findByIdOrNull(operatorId)`, treating a user ID as a group-membership row ID rather than querying by group and user.
- `service/impl/GroupMemberServiceImpl.kt:74-105` receives `targetId` but looks up membership using the operator's user at lines 84-85.
- `controller/GroupMembersController.kt:59-78` exposes role and status mutations whose service logic must be independently enforced.

**Impact**

Private group member-list access can fail open or fail closed unpredictably. Duplicate membership rows can distort counts and role decisions. The add-member path may mutate the wrong membership or not add the requested target.

**Remediation**

- Add a unique `(group_id, user_id)` constraint.
- Load membership by `(groupId, operatorId)` for authorization and `(groupId, targetId)` for the target record.
- Enforce active membership and role hierarchy in one transaction.
- Prevent demoting/removing the last admin without a transfer path.
- Add private-group read/write regression tests.

### SEC-10 P1 Realtime channel mismatch and no replay contract

**Evidence**

- `infra/websocket/RedisWebSocketBroadcaster.kt:12-18` publishes to `ws:broadcast:` and `ws:sendToUser:` channels.
- `config/RedisConfig.kt:67-68` subscribes to `ws:broadcast:*` and `ws:user:*`; the user-message pattern does not match `ws:sendToUser:`.
- `RedisWebSocketReceiver.kt:9-12` removes `ws:broadcast` but leaves the separating colon, which can produce an invalid destination.
- The publisher contains no durable sequence or replay token.

**Impact**

Direct notifications may not be delivered across instances, and broadcast destinations may be malformed. Reconnecting mobile clients can miss state changes, so the realtime channel cannot be the source of truth for event or privacy state.

**Remediation**

- Align channel names and add an integration test with two application instances.
- Publish after database commit through an outbox.
- Add sequence IDs and replay from REST/change feed after reconnect.
- Keep all authorization checks independent of the channel implementation.

### SEC-11 P1 Exact location and broad profile data lack privacy projection

**Evidence**

- `domain/dto/EventResponse.kt:11-12` includes raw `latitude` and `longitude`.
- `domain/mapper/impl/EventMapperImpl.kt:50-52` always maps exact coordinates.
- `domain/dto/UserProfileResponse.kt` includes full name, bio, course, academic year, campus, interests, and avatar for every profile response.
- The design brief requires location sharing modes and explicitly prohibits exact location exposure without consent.

**Impact**

Even if event-level access is later fixed, a single broad DTO makes accidental privacy leakage likely. Exact coordinates can reveal a person's presence, routine, or meeting location.

**Remediation**

- Create separate public, friend, organizer, and owner projections.
- Make location sharing a server-side policy with `HIDDEN`, `FRIENDS`, and `DURING_EVENT` modes.
- Return coarse location or venue ID by default, with an accuracy radius and timestamp.
- Expire and delete ephemeral location records; do not store a location history in the MVP.

### SEC-12 P2 Incomplete runtime and supply-chain hardening

**Evidence**

- `docker-compose.yml:6,17` uses `postgres:latest` and `redis:latest`.
- No dependency scanning, SBOM, TLS configuration, container user, health check, or network isolation is present in the archive.
- `pom.xml` uses a manually pinned JJWT version but has no visible dependency governance or update automation.
- No application properties or deployment configuration is present in `src/main/resources`.

**Impact**

Unpinned infrastructure can change unexpectedly, vulnerable dependencies may go unnoticed, and operational misconfiguration can expose credentials or traffic. The archive is not self-contained enough to reproduce a secure deployment.

**Remediation**

- Pin image digests or tested versions.
- Add Dependabot/Renovate, OWASP Dependency-Check or equivalent, container scanning, and SBOM generation.
- Add HTTPS, secure headers, health/readiness checks, resource limits, and private network configuration.
- Document required configuration and fail fast when secrets or database settings are missing.

### SEC-13 P2 Insufficient security regression coverage

**Evidence**

- The only backend test is `CampusloopApplicationTests.kt`, a context-load test.
- The Android project contains only generated example tests.
- No tests were observed for private resource authorization, rate limits, token revocation, location privacy, concurrent event joins, or WebSocket subscriptions.

**Impact**

The most consequential controls are unverified and can regress silently during feature work.

**Remediation**

- Add repository, service, controller, migration, concurrency, and WebSocket tests described in the backend specification.
- Make security tests mandatory in CI and block release on P0/P1 regressions.
- Add a small seed dataset with users A/B, a private event, a private group, blocked relationship, and location-sharing cases.

## 5. Priority remediation plan

### Before any shared deployment

1. Remove default DB/Redis exposure and rotate any reused credentials.
2. Add authorization checks for profiles, events, participants, groups, and all private reads.
3. Lock down WebSocket origins and authorize subscriptions, or disable WebSocket until scoped correctly.
4. Reconcile schema constraints and add migrations.

### Before Android MVP integration

1. Define and publish the versioned REST contract and standard errors.
2. Add `@Valid`, bounded inputs, page-size caps, and domain validation.
3. Implement verification enforcement, generic reset responses, rate limits, and token revocation.
4. Implement location-sharing preferences and privacy projections.
5. Add endpoint and authorization regression tests.

### Before production release

1. Add refresh-token rotation, secret/key rotation, TLS, security headers, dependency/container scanning, and SBOM.
2. Add outbox/replay or make REST sync authoritative for all realtime state.
3. Add audit logging for authentication, access-policy changes, blocks, invitations, and moderation actions.
4. Test backups, migrations, incident response, and account/data deletion.

## 6. Evidence index

The most relevant repository evidence is:

- `docker-compose.yml`
- `src/main/kotlin/com/example/project/campusloop/config/SecurityConfig.kt`
- `src/main/kotlin/com/example/project/campusloop/config/WebSocketConfig.kt`
- `src/main/kotlin/com/example/project/campusloop/security/JwtCore.kt`
- `src/main/kotlin/com/example/project/campusloop/security/JwtTokenFilter.kt`
- `src/main/kotlin/com/example/project/campusloop/controller/UserProfileController.kt`
- `src/main/kotlin/com/example/project/campusloop/controller/EventController.kt`
- `src/main/kotlin/com/example/project/campusloop/controller/EventParticipantController.kt`
- `src/main/kotlin/com/example/project/campusloop/controller/GroupController.kt`
- `src/main/kotlin/com/example/project/campusloop/service/impl/EventServiceImpl.kt`
- `src/main/kotlin/com/example/project/campusloop/service/impl/EventParticipantServiceImpl.kt`
- `src/main/kotlin/com/example/project/campusloop/service/impl/GroupMemberServiceImpl.kt`
- `src/main/kotlin/com/example/project/campusloop/service/impl/AuthFacade.kt`
- `src/main/kotlin/com/example/project/campusloop/service/impl/AccountPasswordServiceImpl.kt`
- `src/main/kotlin/com/example/project/campusloop/domain/entity/EventParticipant.kt`
- `src/main/kotlin/com/example/project/campusloop/domain/mapper/impl/EventMapperImpl.kt`
- `C:\Users\rusla\AndroidStudioProjects\CampusMates\app\src\main\java\com\example\campusmates\MainActivity.kt`
