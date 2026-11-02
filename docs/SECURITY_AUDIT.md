# CampusLoop Backend Security Audit & Remediation Report

**Branch:** `security-audit`  
**Date:** 2026-11-02  
**Target Repository:** `2labor/CampusLoop-Backend-`

---

## 1. Executive Summary & Requirements Evaluation

This security audit evaluates the CampusLoop Spring Boot 3.2.5 / Kotlin backend against the product's core privacy and security requirements:
- **University-restricted onboarding** (verified campus email domain boundary enforcement).
- **Object-level authorization (BOLA/IDOR protection)** across private groups, closed events, friend requests, and blocked user profiles.
- **Realtime WebSocket isolation** (preventing cross-user STOMP topic/queue eavesdropping and wildcard origin attacks).
- **Infrastructure & secret hardening** (removing hardcoded credentials and public port bindings).
- **Database & concurrency integrity** (preventing capacity race conditions and schema constraint mismatches).

---

## 2. Remediated Findings in This PR

| ID | Severity | Finding | Remediation Implemented |
|---|---|---|---|
| **SEC-01** | **P0 Critical** | `docker-compose.yml` exposed hardcoded credentials (`user`/`password`), unauthenticated Redis, and `:latest` images on `0.0.0.0`. | Pinned `postgres:16-alpine` and `redis:7.2-alpine`, enforced mandatory env variables (`POSTGRES_USER`, `POSTGRES_PASSWORD`, `REDIS_PASSWORD`), enabled Redis `--requirepass`, bound ports to `127.0.0.1`, isolated services in `campusloop_internal` network, and added `.env.example`. |
| **SEC-02** | **P0 Critical** | Missing object-level authorization across private groups, closed event participant lists, friend requests, and blocked profiles; `SecurityConfig` had a malformed `"api/v**/auth/**"` matcher that left `/api/v1/auth/password/change-password` under `permitAll`. | Scoped `SecurityConfig` `permitAll()` strictly to public auth endpoints; enforced active-member checks on private group reads (`GroupServiceImpl`) and excluded private groups from public listings/search (`GroupRepository`); restricted closed-event participant lists to organizers and approved participants (`EventParticipantServiceImpl`); blocked profile viewing across `BLOCKED` relationships (`UserProfileServiceImpl`); and enforced target-only acceptance/rejection on friend requests (`FriendshipServiceImpl`). |
| **SEC-03** | **P0 Critical** | WebSocket endpoint `/ws` allowed wildcard `*` origins and `JwtChannelInterceptor` only authenticated `CONNECT` without authorizing `SUBSCRIBE` destinations. | Replaced wildcard origins with configurable `campusloop.websocket.allowed-origins` (`WebSocketConfig.kt`) and added `StompCommand.SUBSCRIBE` authorization in `JwtChannelInterceptor.kt` to block cross-user subscriptions to `/topic/users/{userId}/**` and `/user/{userId}/**`. |
| **SEC-04** | **P1 High** | Account email verification was commented out in `AuthFacade.kt` and university domain check used `email.endsWith(...)`, allowing lookalike suffix domains (e.g., `attacker-tudublin.ie`). | Implemented strict email domain boundary parsing (`AuthFacade.isAllowedUniversityEmail`) accepting only exact domain or subdomain matches, normalized email/login inputs, and re-enabled verification enforcement (configurable via `campusloop.auth.require-verification`). |
| **SEC-05** | **P1 High** | Password reset and verification flows threw distinct account-existence errors (enabling user enumeration) and used non-cryptographic `kotlin.random.Random`. | Switched OTP generation to `java.security.SecureRandom` and updated `forgotPassword` and `sendVerificationCode` to return silently when an email is unknown or already verified. |
| **SEC-06** | **P1 High** | Missing `@Valid` annotations in controllers and missing Bean Validation constraints on event, group, and profile request DTOs. | Added `@Valid` across `AuthController`, `GroupController`, and `UserProfileController`, and added `@NotBlank`, `@Size`, `@Future`, `@DecimalMin`/`@DecimalMax`, and `@Min`/`@Max` constraints to request DTOs. |
| **SEC-07** | **P1 High** | `JwtCore` accepted arbitrary-length secrets and issued tokens without `jti` or `iss` validation. | Enforced a minimum 256-bit (32-byte) secret key check at startup and added `jti` and `iss` (`campusloop.jwt.issuer`) claims with strict parser verification. |
| **SEC-08** | **P1 High** | `EventParticipant` unique constraint referenced non-existent `account_id` instead of `user_id`, and `joinToEvent` / `updateParticipant` had a check-then-act race on `maxParticipants`. | Fixed `@UniqueConstraint(columnNames = ["event_id", "user_id"])` on `EventParticipant` and added `@Lock(LockModeType.PESSIMISTIC_WRITE)` (`EventRepository.findByIdWithLock`) during participant join and approval. |
| **SEC-09** | **P1 High** | `GroupMember` lacked a unique `(group_id, user_id)` constraint; `GroupMemberServiceImpl.getGroupMembers` passed `operatorId` (a `User` ID) to `repository.findByIdOrNull`; `addMember` looked up `operatorId` instead of `targetId` without role validation; `GroupMembersController` passed `(groupId, userId)` in reversed order. | Added unique `(group_id, user_id)` constraint on `GroupMember`; fixed `getGroupMembers` to query by `(group, operatorUser)`; fixed `addMember` to require moderator/admin privileges and add `targetUser`; fixed swapped arguments in `GroupMembersController.joinGroup` and `leaveGroup`. |
| **SEC-10** | **P1 High** | `RedisWebSocketBroadcaster` published user messages to `ws:sendToUser:*` while `RedisConfig` listened on `ws:user:*`, and `RedisWebSocketReceiver` stripped `ws:broadcast` without the trailing colon. | Aligned Redis pub/sub user channel to `ws:user:{userId}:{destination}` and fixed broadcast prefix removal to `ws:broadcast:`. |
| **BUILD-01** | **P2 Medium** | Maven wrapper failed because `maven-wrapper.properties` was placed in `wrapper/` instead of `.mvn/wrapper/`. | Copied `maven-wrapper.properties` to `.mvn/wrapper/maven-wrapper.properties` so `./mvnw` and `./mvnw.cmd` execute cleanly. |

---

## 3. Next Steps Roadmap (Phase 2 Hardening)

1. **Refresh Token Rotation & Session Revocation:** Introduce opaque refresh tokens stored in Redis/PostgreSQL and revoke active sessions upon password reset or change.
2. **Rate Limiting:** Add Redis-backed rate limiting (e.g., Bucket4j) on `/api/v1/auth/**` endpoints by IP and normalized email.
3. **Database Migrations:** Introduce Flyway migrations (`src/main/resources/db/migration`) to manage schema evolution deterministically.
4. **Location Privacy Projections:** Implement coarse/ephemeral location sharing modes (`HIDDEN`, `FRIENDS`, `DURING_EVENT`) before exposing map endpoints to the Android client.
