# CampusLoop Backend — Complete API & Real-Time Debugging Guide

> **Live Production Edge URL:** [`https://campusloop-api.rohovskyi.dev`](https://campusloop-api.rohovskyi.dev)  
> **Fallback Workers.dev URL:** [`https://campusloop-backend.hazekk94.workers.dev`](https://campusloop-backend.hazekk94.workers.dev)  
> **Interactive API Tester Web App:** [`https://campusloop-api.rohovskyi.dev/debug`](https://campusloop-api.rohovskyi.dev/debug)  
> **Mobile / Desktop Web App:** [`https://campusloop-api.rohovskyi.dev/app`](https://campusloop-api.rohovskyi.dev/app)  
> **WebSocket STOMP 1.2 Broker:** `wss://campusloop-api.rohovskyi.dev/ws`  
> **Cloudflare Account:** `hazekk94@gmail.com` (`hazed0m` / Account ID `05632480b64ab36d4abdf0dc3e82d0b1`)  
> **D1 Database:** `campusloop-db` (`ab6fd3eb-fd74-458f-a3a1-f2f639272fb9`) | **KV:** `AUTH_KV` (`304fc37c083c4e58ae1137bcefaac2ac`)  
> **Repository Commit:** `c82e7d4` (Location-Based Stories + MinIO S3 + Redis Geo + STOMP 1.2 + Edge D1)

---

## 1. Quick Debugging Methods

### Method A: Interactive Web Interface (Zero Install)
Open your browser and navigate to either:
1. **Live Edge:** [`https://campusloop-api.rohovskyi.dev/debug`](https://campusloop-api.rohovskyi.dev/debug)
2. **Local Standalone:** Open [`docs/api-tester.html`](api-tester.html) in Chrome, Firefox, or Edge.

**Features of the Web Tester:**
* **1-Click Quick Login:** Switch instantly between student profiles (**Alex Mercer**, **Niamh Kelly**, **Liam O'Brien**) and college admins (**Ruslan / hazekk94**, **C24393471 Backend Lead**).
* **Live STOMP 1.2 Monitor:** Live visual feed of incoming WebSocket frames (`/topic/events`, `/topic/channels/{id}/messages`, `/user/queue/geo/friends`, `/topic/stories`).
* **Interactive Endpoint Cards:** Test all 19 controllers with pre-filled defaults, path variable inputs, and response syntax highlighting.
* **1-Click Copy cURL:** Generates terminal-ready `curl` commands with your active Bearer token.

---

## 2. Test Accounts & Authentication

All default student passwords in the seed database are `demo1234`.

| Username / Login | Full Name | Role | Email | Provider | UUID |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `alex_tud` | Alex Mercer | CS Student (Yr 3) | `alex@tudublin.ie` | `LOCAL` | `00000000-0000-0000-0000-000000000001` |
| `niamh_k` | Niamh Kelly | UX Designer (Yr 3) | `Niamh.Kelly@tudublin.ie` | `LOCAL` | `00000000-0000-0000-0000-000000000002` |
| `liam_ob` | Liam O'Brien | Robotics (Yr 4) | `liam.obrien@tudublin.ie` | `LOCAL` | `00000000-0000-0000-0000-000000000003` |
| `hazekk94` | Ruslan | Lead Admin | `hazekk94@gmail.com` | `GOOGLE` | `00000000-0000-0000-0000-000000000011` |
| `c24393471_outlook` | C24393471 | Backend Lead | `c24393471@mytudublin.ie` | `OUTLOOK` | `00000000-0000-0000-0000-000000000012` |

### Authenticating via cURL
```bash
# 1. Login with password
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"login":"alex_tud","password":"demo1234"}'

# Response:
# {"accessToken":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...","tokenType":"Bearer","userId":"00000000-0000-0000-0000-000000000001"}
```

---

## 3. Complete Endpoint Reference & cURL Commands

### 3.1 Health & Administration
#### `GET /api/v1/health` (or `GET /`)
Checks edge health, controllers count, active college admins, and STOMP endpoint.
```bash
curl -i "https://campusloop-api.rohovskyi.dev/api/v1/health"
```

#### `GET /api/v1/admin/init-college-admins`
Forces re-synchronization of Google & Outlook admin accounts in D1 SQLite.
```bash
curl -i "https://campusloop-api.rohovskyi.dev/api/v1/admin/init-college-admins"
```

---

### 3.2 Authentication & Verification (`/api/v1/auth`)
#### `POST /api/v1/auth/register`
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "student@mytudublin.ie",
    "username": "student_tud",
    "login": "student_tud",
    "password": "Password123!",
    "firstName": "Student",
    "lastName": "Name",
    "bio": "Testing CampusLoop",
    "course": "Computer Science",
    "academicYear": 2,
    "campusZone": "East Quad"
  }'
```

#### `POST /api/v1/auth/oauth` (College Admin OAuth)
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/auth/oauth" \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "GOOGLE",
    "idToken": "mock-token",
    "email": "hazekk94@gmail.com",
    "displayName": "Ruslan"
  }'
```

#### `POST /api/v1/auth/verification/send` & `verify`
```bash
# Send code
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/auth/verification/send" \
  -H "Content-Type: application/json" \
  -d '{"email":"student@mytudublin.ie"}'

# Verify code
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/auth/verification/verify" \
  -H "Content-Type: application/json" \
  -d '{"email":"student@mytudublin.ie","code":"123456"}'
```

---

### 3.3 Student Profiles (`/api/v1/profiles` & `/api/v1/users`)
#### `GET /api/v1/profiles/me`
```bash
curl -H "Authorization: Bearer <TOKEN>" "https://campusloop-api.rohovskyi.dev/api/v1/profiles/me"
```

#### `PUT /api/v1/profiles/me`
```bash
curl -X PUT "https://campusloop-api.rohovskyi.dev/api/v1/profiles/me" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Alex",
    "lastName": "Mercer",
    "bio": "Updated bio via API",
    "course": "Computer Science",
    "academicYear": 3,
    "campusZone": "East Quad",
    "activityStatus": "FREE_FOR_COFFEE",
    "interests": ["CODING", "AI", "COFFEE"]
  }'
```

#### `GET /api/v1/users/search?keyword=Alex&interests=CODING`
```bash
curl -H "Authorization: Bearer <TOKEN>" "https://campusloop-api.rohovskyi.dev/api/v1/users/search?keyword=Alex"
```

---

### 3.4 Groups & Memberships (`/api/v1/groups`)
#### `GET /api/v1/groups?page=0&size=20`
```bash
curl -H "Authorization: Bearer <TOKEN>" "https://campusloop-api.rohovskyi.dev/api/v1/groups"
```

#### `POST /api/v1/groups`
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/groups" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Cloud Native Hackers",
    "description": "Cloud architecture and edge computing at TU Dublin",
    "visibility": "PUBLIC",
    "joinToken": "JOIN-CLOUD-99"
  }'
```

#### `POST /api/v1/groups/{groupId}/join`
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/groups/22222222-2222-2222-2222-222222222222/join" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"joinToken":"JOIN-ROASTERY"}'
```

---

### 3.5 Discussions, Anonymous Masking & Threaded Comments
#### `GET /api/v1/groups/{groupId}/posts`
```bash
curl -H "Authorization: Bearer <TOKEN>" "https://campusloop-api.rohovskyi.dev/api/v1/groups/11111111-1111-1111-1111-111111111111/posts"
```

#### `POST /api/v1/groups/{groupId}/posts` (Supports `isAnonymous`)
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/groups/11111111-1111-1111-1111-111111111111/posts" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Anonymous question about exams",
    "content": "Does anyone have past papers for Advanced Algorithms?",
    "isAnonymous": true
  }'
```

#### `POST /api/v1/posts/{postId}/comments` (Threaded `parentId`)
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/posts/aaaa1111-1111-1111-1111-111111111111/comments" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Check the TU Dublin library archives!",
    "parentId": null
  }'
```

---

### 3.6 Events & Spontaneous Meetups (`/api/v1/events`)
#### `GET /api/v1/events/upcoming`
```bash
curl -H "Authorization: Bearer <TOKEN>" "https://campusloop-api.rohovskyi.dev/api/v1/events/upcoming"
```

#### `POST /api/v1/events` (Broadcasts to `/topic/events`)
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/events" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Flat Whites & Kotlin Review",
    "description": "Coffee sprint in East Quad atrium",
    "category": "COFFEE",
    "campusZone": "East Quad",
    "startTime": "2026-11-10T19:00:00Z",
    "endTime": "2026-11-10T20:30:00Z",
    "maxParticipants": 8
  }'
```

---

### 3.7 Channels & Real-Time Chat (`/api/v1/channels`)
Uses 64-bit Snowflake IDs (`Long` / `INTEGER`) for channels, messages, and members.

#### `GET /api/v1/channels/me`
```bash
curl -H "Authorization: Bearer <TOKEN>" "https://campusloop-api.rohovskyi.dev/api/v1/channels/me"
```

#### `POST /api/v1/channels/direct?userId={targetUserId}`
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/channels/direct?userId=00000000-0000-0000-0000-000000000002" \
  -H "Authorization: Bearer <TOKEN>"
```

#### `POST /api/v1/channels/{channelId}/messages` (Broadcasts STOMP `/topic/channels/{id}/messages`)
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/channels/1001/messages" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Hey everyone, STOMP real-time messaging is live!",
    "parentMessageId": null
  }'
```

#### `GET /api/v1/channels/{channelId}/messages?size=30`
```bash
curl -H "Authorization: Bearer <TOKEN>" "https://campusloop-api.rohovskyi.dev/api/v1/channels/1001/messages"
```

---

### 3.8 Real-Time Geo Location & Ghost Mode (`/api/v1/geo`)
#### `POST /api/v1/geo/location`
Publishes user coordinates to spatial index and broadcasts to `/user/queue/geo/friends`.
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/geo/location" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"latitude": 53.3559, "longitude": -6.2792}'
```

#### `DELETE /api/v1/geo/ghost` (Ghost Mode)
Removes user coordinates from radar and broadcasts `DELETED` action to mutual friends.
```bash
curl -X DELETE "https://campusloop-api.rohovskyi.dev/api/v1/geo/ghost" \
  -H "Authorization: Bearer <TOKEN>"
```

#### `GET /api/v1/geo/friends?radiusMeters=5000`
```bash
curl -H "Authorization: Bearer <TOKEN>" "https://campusloop-api.rohovskyi.dev/api/v1/geo/friends?radiusMeters=5000"
```

---

### 3.9 Location-Based Stories (`/api/v1/stories`) *(New from `c82e7d4`)*
Ephemeral 24-hour location-tagged stories for the Grangegorman spatial map.

#### `POST /api/v1/stories`
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/stories" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "mediaFileKey": "stories/00000000-0000-0000-0000-000000000001/sunset.jpg",
    "latitude": 53.3559,
    "longitude": -6.2792,
    "caption": "Golden hour over East Quad! ☕",
    "privacy": "EVERYONE"
  }'
```

#### `GET /api/v1/stories/nearby?latitude=53.3559&longitude=-6.2792&radiusMeters=5000`
```bash
curl -H "Authorization: Bearer <TOKEN>" \
  "https://campusloop-api.rohovskyi.dev/api/v1/stories/nearby?latitude=53.3559&longitude=-6.2792&radiusMeters=5000"
```

#### `GET /api/v1/stories/user/{targetUserId}`
```bash
curl -H "Authorization: Bearer <TOKEN>" \
  "https://campusloop-api.rohovskyi.dev/api/v1/stories/user/00000000-0000-0000-0000-000000000002"
```

#### `DELETE /api/v1/stories/{storyId}`
```bash
curl -X DELETE "https://campusloop-api.rohovskyi.dev/api/v1/stories/s1111111-1111-1111-1111-111111111111" \
  -H "Authorization: Bearer <TOKEN>"
```

---

### 3.10 S3 / MinIO Presigned Object Storage (`/api/v1/storage`)
#### Step 1: Request Presigned Upload URL
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/storage/upload-url" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"folderName":"avatars","fileName":"avatar.jpg"}'

# Response:
# {"uploadUrl":"https://campusloop-api.rohovskyi.dev/api/v1/storage/object/avatars%2F...","fileKey":"avatars/..."}
```

#### Step 2: Binary PUT directly to `uploadUrl`
```bash
curl -X PUT "https://campusloop-api.rohovskyi.dev/api/v1/storage/object/<FILE_KEY>" \
  -H "Content-Type: image/jpeg" \
  --data-binary "@test_image.jpg"
```

#### Step 3: GET Object by Key
```bash
curl -i "https://campusloop-api.rohovskyi.dev/api/v1/storage/object/<FILE_KEY>"
```

---

### 3.11 AI Recommendation Engine & Promos (`/api/v1/ai` & `/api/v1/promos`)
#### `GET /api/v1/ai/suggestions?zone=East%20Quad&activity=CODING`
```bash
curl "https://campusloop-api.rohovskyi.dev/api/v1/ai/suggestions?zone=East%20Quad&activity=CODING"
```

#### `GET /api/v1/promos`
```bash
curl "https://campusloop-api.rohovskyi.dev/api/v1/promos"
```

#### `POST /api/v1/admin/ai/config` (Admin Only)
```bash
curl -X POST "https://campusloop-api.rohovskyi.dev/api/v1/admin/ai/config" \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "campusVibe": "Hackathon Sprint Mode",
    "popularityMultiplier": 1.75,
    "promoActive": true,
    "prompt": "Promote coffee runs and collaborative coding."
  }'
```

---

## 4. WebSocket STOMP 1.2 Protocol Debugging

Endpoint: `wss://campusloop-api.rohovskyi.dev/ws`

### Frame Handshake

#### 1. Client `CONNECT` Frame
```
CONNECT
accept-version:1.2
Authorization:Bearer <TOKEN>

^@
```
*(Where `^@` is the null byte `\0`)*

#### 2. Server `CONNECTED` Response Frame
```
CONNECTED
version:1.2
server:CampusLoop-Edge-Broker/1.2

^@
```

#### 3. Client `SUBSCRIBE` Frames
```
SUBSCRIBE
id:sub-events
destination:/topic/events

^@
```
```
SUBSCRIBE
id:sub-chat-1001
destination:/topic/channels/1001/messages

^@
```
```
SUBSCRIBE
id:sub-geo
destination:/user/queue/geo/friends

^@
```
```
SUBSCRIBE
id:sub-stories
destination:/topic/stories

^@
```

#### 4. Server Broadcast `MESSAGE` Envelope
All real-time events conform to `RealtimeMessage<T>`:
```
MESSAGE
destination:/topic/channels/1001/messages
content-type:application/json
subscription:sub-chat-1001

{"action":"CREATED","entityId":3002,"payload":{"id":3002,"channelId":1001,"senderName":"alex_tud","content":"Hello world!","createdAt":"2026-11-10T17:50:00Z"},"timestamp":"2026-11-10T17:50:00Z"}
^@
```

---

## 5. Troubleshooting Checklist

| Symptom | Cause | Solution |
| :--- | :--- | :--- |
| **`401 Unauthorized`** | Missing or expired JWT token | Call `POST /api/v1/auth/login` to obtain a fresh 7-day token or use Quick Login in [`/debug`](https://campusloop-api.rohovskyi.dev/debug). |
| **`403 Forbidden` on `DELETE /api/v1/storage`** | `fileKey` does not contain the caller's UUID | Storage delete enforces ownership matching `fileKey.includes(userId)`. |
| **`403 Forbidden` on `DELETE /api/v1/stories/{id}`** | Story belongs to another student | Delete is restricted to the story author (`user_id == authUser.sub`). |
| **No friends showing in `GET /api/v1/geo/friends`** | Target user is in Ghost Mode or mutual friendship is not `ACCEPTED` | Mutual accepted friendship + `POST /api/v1/geo/location` required to appear on radar. |
| **CORS errors in browser** | Request missing `OPTIONS` handling | Cloudflare Edge responds with `204 No Content` and wildcard CORS headers on `OPTIONS`. Verify request URL is `https://campusloop-api.rohovskyi.dev`. |
| **WebSocket handshake fails** | Missing `Upgrade: websocket` header or HTTP port | Ensure connection protocol is `wss://` on port 443 (Edge) or `ws://localhost:8080/ws` (Spring Boot). |

---

## 6. Edge Redeployment Commands

From `C:\Users\rusla\AndroidStudioProjects\CampusLoop-Backend\cloudflare-edge`:

```powershell
# 1. Verify Cloudflare Account
npx wrangler whoami

# 2. Check D1 SQLite Tables
npx wrangler d1 execute campusloop-db --remote --command="SELECT name FROM sqlite_master WHERE type='table';"

# 3. Deploy Edge Worker to Custom Domain (campusloop-api.rohovskyi.dev) & workers.dev
npx wrangler deploy
```
