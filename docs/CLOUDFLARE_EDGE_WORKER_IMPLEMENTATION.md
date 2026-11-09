# CampusLoop Cloudflare Edge Worker & D1 Implementation (`hazed0m`)

> **Live Custom Domain:** [`https://campusloop-api.rohovskyi.dev`](https://campusloop-api.rohovskyi.dev/api/v1/health)  
> **Live Workers.dev URL:** [`https://campusloop-backend.hazekk94.workers.dev`](https://campusloop-backend.hazekk94.workers.dev/api/v1/health)  
> **Account:** `hazekk94@gmail.com` (`05632480b64ab36d4abdf0dc3e82d0b1`)  
> **D1 Database:** `campusloop-db` (`ab6fd3eb-fd74-458f-a3a1-f2f639272fb9`, region `WEUR`)  
> **KV Namespace:** `AUTH_KV` (`304fc37c083c4e58ae1137bcefaac2ac`)

---

## 1. Overview

In addition to the local Kotlin/Spring Boot JVM backend (`CampusLoop-Backend`), the Cloudflare account now hosts a **24/7 always-on Cloudflare Edge Worker + D1 SQLite deployment** (`campusloop-backend`) implementing all **18 REST controllers** (`23b61dc`), **security-audit PR #1 remediations** (`c9a9791`), and a **WebSocket STOMP 1.2 broker (`/ws`)**.

This ensures the Android app (`CampusMates`) works out-of-the-box anywhere in the world against `https://campusloop-api.rohovskyi.dev/` even when local Docker Desktop is turned off.

---

## 2. D1 Relational Tables (`campusloop-db`)

1. `users` — Student accounts, credentials, course, academic year, campus zone, and activity status.
2. `groups_table` — Campus interest groups (`TUD Hackers`, `Roastery Club`, `Campus 5-a-Side`) and join tokens.
3. `group_members` — Group membership roles (`ADMIN`, `MEMBER`).
4. `posts` — Discussion posts with `is_anonymous` author masking (`Anonymous Student`).
5. `comments` — Threaded comments with recursive `parent_id` hierarchy.
6. `events` — Campus events and spontaneous quick meetups.
7. `event_participants` — Event roster and approval status (`APPROVED`, `PENDING`).
8. `friendships` — Mutual student friendships and pending requests.
9. `notifications` — In-app notification queue and unread state.
10. `channels` — 64-bit Snowflake ID (`INTEGER`) channels (`GROUP_TEXT`, `DIRECT`).
11. `channel_members` — Channel membership mapping.
12. `messages` — Real-time channel messages with `parent_message_id` reply references.
13. `geo_locations` — Ephemeral friend coordinates (`longitude`, `latitude`, `updated_at`) for Haversine radius queries and Ghost Mode deletion.
14. `storage_files` — Presigned object storage metadata and payloads for `/api/v1/storage/upload-url`.

---

## 3. Redeploying or Updating the Edge Backend

From `C:\Users\rusla\AndroidStudioProjects\CampusLoop-Backend\cloudflare-edge`:

```powershell
# 1. Apply schema updates to remote D1 database
npx wrangler d1 execute campusloop-db --remote --file=schema.sql

# 2. Deploy Worker to campusloop-api.rohovskyi.dev & workers.dev
npx wrangler deploy
```
