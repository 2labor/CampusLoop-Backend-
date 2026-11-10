# CampusLoop Product Description

## Header

CampusLoop is a social map for campus life. It helps students see what is happening nearby, find friends who have chosen to share their presence, and join or create a meetup without a long planning process.

**Baseline date:** Tuesday 27 October 2026  
**Platform:** Android  
**UI stack:** Kotlin, Jetpack Compose, Material 3  
**MVP focus:** map, events, friends, groups, profile, and location privacy

## Product promise

CampusLoop makes the campus feel live and discoverable. The product is designed around three questions:

1. Who is around?
2. What is happening?
3. Can I join?

The app should feel like a modern social product rather than an administrative university tool. The map is the center of the experience, while detail pages keep the map visually light and make actions clear.

## Feature app reference page

### Social map

The map combines event markers, opted-in friend markers, the user's current position when permission is granted, and a list alternative for accessibility and reliable browsing. Map content is always filtered by campus and privacy policy.

### Events

Users can browse upcoming events, open an event detail page, create a quick meetup, join open events, request access to private events, leave events, and view participant status. Organizers can update, cancel, complete, and approve or reject participants.

### Friends

Users can search the campus community, send and accept friend requests, remove connections, block users, and see friends who have opted into location sharing.

### Groups

Users can create public or private groups, view group details, request access, join public groups, and see active members. Group administration belongs to the creator and approved moderators.

### Profile and privacy

The profile contains name, avatar, short biography, course or academic year, campus, and interests. Privacy settings separate Android device permission from CampusLoop sharing mode.

### Notifications

The notification surface covers friend requests, event applications, approvals, invitations, and event state changes. Notifications are durable and can be recovered after the app is offline.

## Application shell

### Shared header

The header is a reusable Compose component. It contains the CampusLoop identity, selected campus, and page-specific discovery or settings actions.

| Page type | Header content |
|---|---|
| Map | Campus selector, search, filters |
| Feed | Page title, search, filters |
| Detail | Back action, concise page title, overflow action when permitted |
| Form | Back action, page title, save or publish action |
| Profile | Avatar or title, settings action |

The header must remain readable in both themes, support large text, and expose content descriptions for icon buttons.

### Shared footer

The footer is the bottom navigation bar used by the main application shell:

| Destination | Purpose | MVP status |
|---|---|---|
| Map | Friends, events, and nearby campus activity | Implement |
| Events | Browse and manage events | Implement |
| Create | Start an event or group | Implement |
| Chats | Future messages and discussions | Prepared, no dead actions |
| Profile | Profile, friends, and settings | Implement |

The footer should preserve navigation state, expose selected state through semantics, and avoid presenting a non-functional chat action as if it were available.

## MVP page set

### Page 1 Map

**Purpose:** Give the user an immediate view of campus activity.

**Primary content:**

- campus map;
- friend markers for accepted friends with permitted sharing;
- event markers for events visible to the caller;
- current position only after Android permission and app sharing consent;
- map/list switch;
- friend/event/all filters;
- create meetup button.

**States:** loading, permission not granted, location unavailable, no friends nearby, no events nearby, network unavailable, normal map.

**Privacy rule:** Do not show an exact person's location unless an explicit server-side policy allows it. Joining an event does not turn on location sharing.

### Page 2 Events Feed

**Purpose:** Provide a reliable list of discoverable upcoming events.

**Card content:** category, title, place, time, participant availability, access type, and organizer summary.

**Filters:** category, time, nearby, open events, and invited events.

**States:** loading, empty result, no search matches, full event, expired event, access denied, pending request, success, and operation failure.

### Page 3 Event Details

**Purpose:** Explain an event and make the next action obvious.

**Content:** title, description, category, time, place, organizer, event access, participant summary, capacity, and current user status.

**Actions:** join, request access, leave, edit, cancel, complete, approve, reject, and view participants according to role.

**Rules:** private event details and participant lists are server-authorized. The Android client must not rely on hiding buttons as an access control.

### Page 4 Create Event

**Purpose:** Let a user create a quick meetup in a few steps.

**Fields:** title, short description, category, open/private access, date/time, campus place, optional map point, and maximum participants.

**Validation:** title length, future time, valid campus location, positive participant limit, and end time after start time when an end time is used.

### Page 5 Friends

**Purpose:** Manage trusted connections and discover people.

**Sections:** friend list, incoming requests, search results, and relationship actions.

**Rules:** block status filters profile search, friend lists, location projections, event visibility, and notifications.

### Page 6 Groups

**Purpose:** Give recurring interests a shared space.

**Content:** group name, description, category, privacy, creator, member count, and membership state.

**States:** no groups, loading members, invitation expired, request pending, access denied, active member, and banned member.

### Page 7 Profile and Privacy Settings

**Purpose:** Let users manage identity, preferences, and location visibility.

**Profile:** avatar, full name, username, bio, course, academic year, campus, and interests.

**Location modes:** `Hidden`, `Friends`, and `During Event`.

**Important distinction:** Android device permission decides whether the device may provide a coordinate. CampusLoop sharing mode decides whether the backend may publish a privacy-filtered location projection.

## MVP user journeys

### Find friends nearby

Open Map -> grant or decline location permission -> view permitted friend markers -> open Friend Preview -> view profile or relationship action -> hide location from Privacy Settings when needed.

### Create a spontaneous meetup

Open Create -> choose Quick Meetup -> enter title -> choose place -> set time, access, and participant limit -> publish -> event appears in feed and eligible map projections.

### Join an open event

Open Event Details -> review organizer, place, time, and capacity -> join -> receive confirmed participant state -> see event in My Events.

### Request access to a private event

Open an invitation or permitted private event -> request access -> wait for organizer decision -> receive approved or rejected status -> only approved users receive private participant details.

## States and accessibility

Every page must specify loading, success, empty, error, and offline behavior. Errors should explain what happened and give a next action. Important map content must have a list alternative. Avatars, map markers, icon buttons, status chips, and navigation items need Compose semantics and TalkBack descriptions. Status must not be communicated by color alone.

## Localization and themes

The MVP supports English and Russian. UI strings belong in Android resources, with date/time formatting based on the device locale. The interface supports light and dark themes, scalable text, and the supplied CampusLoop palette as a starting point.

## Technical page contract

Pages navigate by stable IDs, not serialized domain objects:

```text
Map
Events
EventDetails(eventId)
CreateEvent
Friends
UserProfile(userId)
Groups
GroupDetails(groupId)
Profile
PrivacySettings
```

Each page obtains or observes its data through a ViewModel. Room is a local cache and offline source; the backend remains authoritative for event access, participant status, friendship state, group membership, and location visibility.

## Implementation sequence

1. Foundation: navigation, theme, shared header/footer, state models, Room, and test scaffolding.
2. Map and events: event feed, details, create event, join/leave, participant state, and map/list shell.
3. Friends and groups: profiles, search, relationships, groups, and membership.
4. Privacy and integration: location modes, backend integration, offline behavior, and error handling.
5. Polish: accessibility, localization, dark/light themes, real-device testing, and demo readiness.

## Footer

CampusLoop Mobile - Product and MVP page reference  
Baseline date: Tuesday 27 October 2026  
Repository owner: 2labor
