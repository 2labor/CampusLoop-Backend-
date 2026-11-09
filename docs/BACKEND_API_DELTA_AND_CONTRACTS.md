# CampusLoop Backend (`23b61dc`) — Complete API Delta & DTO Contracts

> **Backend Repository:** `C:\Users\rusla\AndroidStudioProjects\CampusLoop-Backend` (`origin/main` @ commit `23b61dc`)  
> **Framework:** Kotlin 1.9.23 + Spring Boot 3.2.5 + PostgreSQL + Redis (Geo & PubSub) + MinIO S3 + WebSocket STOMP 1.2

---

## 1. Summary of All 18 REST Controllers

| # | Controller | Base Path | Status in Android App (`CampusMates`) |
| :--- | :--- | :--- | :--- |
| 1 | `AuthController` | `/api/v1/auth` | ✅ Integrated |
| 2 | `VerificationController` | `/api/v1/auth/verification` | ✅ Integrated |
| 3 | `PasswordController` | `/api/v1/auth/password` | ✅ Integrated |
| 4 | `UserProfileController` | `/api/v1/profiles` | ✅ Integrated |
| 5 | `UserProfileSearchController` | `/api/v1/users/search` | ✅ Integrated |
| 6 | `GroupController` | `/api/v1/groups` | ✅ Integrated |
| 7 | `GroupMembersController` | `/api/v1/groups/{groupId}/members` | ✅ Integrated |
| 8 | `PostController` | `/api/v1/groups/{groupId}/posts` | ✅ Integrated |
| 9 | `CommentController` | `/api/v1/groups/{groupId}/posts/{postId}/comments` | ✅ Integrated |
| 10 | `EventController` | `/api/v1/events` | ✅ Integrated |
| 11 | `EventParticipantController` | `/api/v1/events` | ✅ Integrated |
| 12 | `FriendshipController` | `/api/v1/friends` | ✅ Integrated |
| 13 | `NotificationController` | `/api/v1/notifications` | ⚠️ Needs path fix (`/raed-all` -> `/read-all`) |
| 14 | `ChannelController` | `/api/v1/channels` | 🆕 Added in `c56e9d1` (To Integrate) |
| 15 | `ChannelMemberController` | `/api/v1/channels` | 🆕 Added in `c56e9d1` (To Integrate) |
| 16 | `MessageController` | `/api/v1/channels/{channelId}/messages` | 🆕 Added in `c56e9d1` (To Integrate) |
| 17 | `GeoLocationController` | `/api/v1/geo` | 🆕 Added in `31d5fa9` (To Integrate) |
| 18 | `StorageController` | `/api/v1/storage` | 🆕 Added in `23b61dc` (To Integrate) |

---

## 2. Exact Kotlin DTOs & Endpoint Contracts for New Controllers

### 2.1 Channels & Channel Members (`ChannelController` & `ChannelMemberController`)

> **Important ID Type Note:** Channels, ChannelMembers, and Messages use 64-bit Snowflake IDs (`Long`), whereas Users and Groups use `UUID` (`String` in JSON).

```kotlin
enum class ChannelType {
    GROUP_TEXT,
    DIRECT
}

data class CreateChannelRequest(
    val groupId: String?, // UUID (nullable for DIRECT channels)
    val name: String?,
    val type: ChannelType
)

data class EditChannelRequest(
    val name: String
)

data class ChannelResponse(
    val id: Long,
    val name: String?,
    val type: ChannelType,
    val groupId: String?,
    val createdAt: String, // ISO-8601 Instant
    val updatedAt: String
)

data class ChannelMemberResponse(
    val id: Long,
    val userId: String, // UUID
    val username: String,
    val channelId: Long,
    val createdAt: String,
    val updatedAt: String
)
```

| Method | Endpoint | Query / Body | Response |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/channels` | Body: `CreateChannelRequest` | `201 Created` `ChannelResponse` |
| `PUT` | `/api/v1/channels/{channelId}` | Body: `EditChannelRequest` | `200 OK` `ChannelResponse` |
| `DELETE` | `/api/v1/channels/{channelId}` | — | `204 No Content` |
| `GET` | `/api/v1/channels/group/{groupId}` | `?page=0&size=20` | `200 OK` `SliceResponse<ChannelResponse>` |
| `GET` | `/api/v1/channels/{channelId}` | — | `200 OK` `ChannelResponse` |
| `GET` | `/api/v1/channels/me` | `?page=0&size=20` | `200 OK` `SliceResponse<ChannelResponse>` |
| `POST` | `/api/v1/channels/direct` | `?userId={targetUserUuid}` | `200 OK` `ChannelResponse` |
| `POST` | `/api/v1/channels/{channelId}/members` | `?userId={targetUserUuid}` | `201 Created` `ChannelMemberResponse` |
| `DELETE` | `/api/v1/channels/{channelId}/members/{memberId}` | — | `204 No Content` |
| `GET` | `/api/v1/channels/{channelId}/members` | `?page=0&size=50` | `200 OK` `SliceResponse<ChannelMemberResponse>` |

---

### 2.2 Channel Messages (`MessageController`)

```kotlin
enum class HistoryDirection {
    AFTER,
    BEFORE
}

data class CreateMessageRequest(
    val content: String,
    val parentMessageId: Long? = null
)

data class EditMessageRequest(
    val content: String
)

data class ChatMessageResponse(
    val id: Long,
    val content: String,
    val senderName: String,
    val channelId: Long,
    val parentContent: String?,
    val createdAt: String,
    val updatedAt: String
)

data class LastMessageResponse(
    val id: Long,
    val content: String,
    val senderName: String,
    val createdAt: String
)
```

| Method | Endpoint | Query / Body | Response |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/channels/{channelId}/messages` | Body: `CreateMessageRequest` | `201 Created` `ChatMessageResponse` |
| `PUT` | `/api/v1/channels/{channelId}/messages/{messageId}` | Body: `EditMessageRequest` | `200 OK` `ChatMessageResponse` |
| `DELETE` | `/api/v1/channels/{channelId}/messages/{messageId}` | — | `204 No Content` |
| `GET` | `/api/v1/channels/{channelId}/messages/{messageId}` | — | `200 OK` `ChatMessageResponse` |
| `GET` | `/api/v1/channels/{channelId}/messages/last` | — | `200 OK` `LastMessageResponse` (`404` if none) |
| `GET` | `/api/v1/channels/{channelId}/messages` | `?cursorId={long?}&direction={BEFORE\|AFTER}&size=30` | `200 OK` `SliceResponse<LastMessageResponse>` |

---

### 2.3 Real-Time Geo Location & Ghost Mode (`GeoLocationController`)

```kotlin
data class UpdateLocationRequest(
    val longitude: Double,
    val latitude: Double
)

data class UserLocationDto(
    val userId: String, // UUID
    val longitude: Double,
    val latitude: Double,
    val updatedAt: String // ISO-8601 Instant
)
```

| Method | Endpoint | Query / Body | Response |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/geo/location` | Body: `UpdateLocationRequest` | `200 OK` (Empty body; broadcasts via STOMP to friends) |
| `DELETE` | `/api/v1/geo/ghost` | — | `204 No Content` (Removes from Redis Geo & notifies friends) |
| `GET` | `/api/v1/geo/friends` | `?radiusMeters=5000.0` | `200 OK` `List<UserLocationDto>` |

---

### 2.4 Presigned Object Storage (`StorageController`)

```kotlin
data class CreateUploadUrlRequest(
    val folderName: String,
    val fileName: String
)

data class DeleteFileRequest(
    val fileKey: String
)

data class PresignedUrlResponse(
    val uploadUrl: String,
    val fileKey: String
)
```

| Method | Endpoint | Body | Response |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/storage/upload-url` | `CreateUploadUrlRequest` | `200 OK` `PresignedUrlResponse` |
| `DELETE` | `/api/v1/storage` | `DeleteFileRequest` | `204 No Content` (`403` if `fileKey` does not contain caller's `userId`) |

---

## 3. Complete WebSocket STOMP 1.2 Destination Matrix

All STOMP messages use the `RealtimeMessage<T>` JSON envelope:
```json
{
  "action": "CREATED | UPDATED | DELETED",
  "entityId": "1234567890",
  "payload": { ... },
  "timestamp": "2026-11-09T19:45:00Z"
}
```

| STOMP Destination | Scope | Triggered By | Payload Type (`T`) |
| :--- | :--- | :--- | :--- |
| `/topic/events` | Broadcast | `EventService` create/update/cancel | `EventResponse` |
| `/topic/channels/{channelId}/messages` | Channel Subscribers | `MessageDispatchService` create/edit/delete | `ChatMessageResponse` (`null` on `DELETED`) |
| `/user/queue/notifications` | Per-User Queue | `NotificationService` | `NotificationResponse` |
| `/user/queue/geo/friends` | Per-User Queue | `GeoLocationServiceImpl` update / ghost mode | `UserLocationDto` (`null` on `DELETED`) |
| `/topic/users/{userId}/friends` | Per-User Topic | `FriendshipService` request/accept/remove | `FriendshipResponse` |
