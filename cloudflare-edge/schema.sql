-- CampusLoop D1 Schema (v23b61dc + security-audit PR #1)
CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    username TEXT UNIQUE NOT NULL,
    login TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    provider TEXT NOT NULL DEFAULT 'LOCAL',
    is_verified INTEGER NOT NULL DEFAULT 1,
    first_name TEXT NOT NULL DEFAULT '',
    last_name TEXT NOT NULL DEFAULT '',
    bio TEXT NOT NULL DEFAULT 'Exploring CampusLoop on Grangegorman!',
    course TEXT NOT NULL DEFAULT 'Computer Science',
    academic_year INTEGER NOT NULL DEFAULT 3,
    campus_zone TEXT NOT NULL DEFAULT 'East Quad',
    activity_status TEXT NOT NULL DEFAULT 'FREE_FOR_COFFEE',
    interests TEXT NOT NULL DEFAULT '["CODING","COFFEE","TECH"]',
    avatar_key TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS groups_table (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    visibility TEXT NOT NULL DEFAULT 'PUBLIC',
    is_verified INTEGER NOT NULL DEFAULT 1,
    join_token TEXT UNIQUE NOT NULL,
    creator_id TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS group_members (
    id TEXT PRIMARY KEY,
    group_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    role TEXT NOT NULL DEFAULT 'MEMBER',
    created_at TEXT NOT NULL,
    UNIQUE(group_id, user_id)
);

CREATE TABLE IF NOT EXISTS posts (
    id TEXT PRIMARY KEY,
    group_id TEXT NOT NULL,
    author_id TEXT NOT NULL,
    author_name TEXT NOT NULL,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    is_anonymous INTEGER NOT NULL DEFAULT 0,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
    id TEXT PRIMARY KEY,
    post_id TEXT NOT NULL,
    group_id TEXT NOT NULL,
    author_id TEXT NOT NULL,
    author_name TEXT NOT NULL,
    content TEXT NOT NULL,
    parent_id TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    category TEXT NOT NULL DEFAULT 'COFFEE',
    visibility TEXT NOT NULL DEFAULT 'PUBLIC',
    status TEXT NOT NULL DEFAULT 'OPEN',
    campus_zone TEXT NOT NULL DEFAULT 'Central Quad',
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,
    max_participants INTEGER NOT NULL DEFAULT 8,
    organizer_id TEXT NOT NULL,
    organizer_name TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS event_participants (
    id TEXT PRIMARY KEY,
    event_id TEXT NOT NULL,
    user_id TEXT NOT NULL,
    username TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'APPROVED',
    created_at TEXT NOT NULL,
    UNIQUE(event_id, user_id)
);

CREATE TABLE IF NOT EXISTS friendships (
    id TEXT PRIMARY KEY,
    initializer_id TEXT NOT NULL,
    target_id TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'PENDING',
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    UNIQUE(initializer_id, target_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    type TEXT NOT NULL DEFAULT 'SYSTEM',
    reference_id TEXT,
    is_read INTEGER NOT NULL DEFAULT 0,
    created_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS channels (
    id INTEGER PRIMARY KEY,
    name TEXT,
    type TEXT NOT NULL DEFAULT 'GROUP_TEXT',
    group_id TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS channel_members (
    id INTEGER PRIMARY KEY,
    channel_id INTEGER NOT NULL,
    user_id TEXT NOT NULL,
    username TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL,
    UNIQUE(channel_id, user_id)
);

CREATE TABLE IF NOT EXISTS messages (
    id INTEGER PRIMARY KEY,
    channel_id INTEGER NOT NULL,
    sender_id TEXT NOT NULL,
    sender_name TEXT NOT NULL,
    content TEXT NOT NULL,
    parent_message_id INTEGER,
    parent_content TEXT,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS geo_locations (
    user_id TEXT PRIMARY KEY,
    longitude REAL NOT NULL,
    latitude REAL NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS storage_files (
    file_key TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    content_type TEXT NOT NULL DEFAULT 'application/octet-stream',
    data_base64 TEXT,
    created_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS stories (
    id TEXT PRIMARY KEY,
    media_file_key TEXT NOT NULL,
    caption TEXT,
    user_id TEXT NOT NULL,
    privacy_settings TEXT NOT NULL DEFAULT 'EVERYONE',
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    expires_at TEXT NOT NULL,
    created_at TEXT NOT NULL,
    updated_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS attachments (
    id TEXT PRIMARY KEY,
    file_size INTEGER NOT NULL,
    original_name TEXT NOT NULL,
    file_key TEXT NOT NULL,
    file_type TEXT NOT NULL,
    mime_type TEXT NOT NULL,
    message_id INTEGER,
    post_id TEXT,
    created_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS shareable_tokens (
    token TEXT PRIMARY KEY,
    type TEXT NOT NULL,
    target_entity_id TEXT NOT NULL,
    creator_id TEXT NOT NULL,
    expires_at TEXT NOT NULL,
    max_uses INTEGER,
    uses_count INTEGER NOT NULL DEFAULT 0,
    created_at TEXT NOT NULL
);

-- Seed initial data (Users, Groups, Posts, Comments, Events, Channels, Messages)
INSERT OR IGNORE INTO users (id, email, username, login, password_hash, first_name, last_name, bio, course, academic_year, campus_zone, activity_status, created_at, updated_at)
VALUES
('00000000-0000-0000-0000-000000000001', 'alex@tudublin.ie', 'alex_tud', 'alex_tud', 'demo1234', 'Alex', 'Mercer', 'Full-stack dev & espresso enthusiast at East Quad.', 'Computer Science', 3, 'East Quad', 'FREE_FOR_COFFEE', '2026-11-01T10:00:00Z', '2026-11-09T19:00:00Z'),
('00000000-0000-0000-0000-000000000002', 'Niamh.Kelly@tudublin.ie', 'niamh_k', 'niamh_k', 'demo1234', 'Niamh', 'Kelly', 'UI/UX Designer & Hackathon lead.', 'Creative Digital Media', 3, 'Central Quad', 'STUDYING', '2026-11-01T10:05:00Z', '2026-11-09T19:00:00Z'),
('00000000-0000-0000-0000-000000000003', 'liam.obrien@tudublin.ie', 'liam_ob', 'liam_ob', 'demo1234', 'Liam', 'O''Brien', 'Robotics & embedded Rust tinkerer.', 'Robotics Engineering', 4, 'Lower House', 'HEADING_TO_EVENT', '2026-11-01T10:10:00Z', '2026-11-09T19:00:00Z');

INSERT OR IGNORE INTO groups_table (id, name, description, visibility, is_verified, join_token, creator_id, created_at, updated_at)
VALUES
('11111111-1111-1111-1111-111111111111', 'TUD Hackers', 'Software engineering, hackathons, and cloud architecture at Grangegorman.', 'PUBLIC', 1, 'JOIN-TUD-HACKERS', '00000000-0000-0000-0000-000000000001', '2026-11-01T10:00:00Z', '2026-11-09T19:00:00Z'),
('22222222-2222-2222-2222-222222222222', 'Roastery Club', 'Specialty pour-over coffee runs and study breaks around Grangegorman.', 'PUBLIC', 1, 'JOIN-ROASTERY', '00000000-0000-0000-0000-000000000002', '2026-11-01T10:00:00Z', '2026-11-09T19:00:00Z'),
('33333333-3333-3333-3333-333333333333', 'Campus 5-a-Side', 'Evening football and social runs on the Grangegorman Astro pitches.', 'PUBLIC', 1, 'JOIN-SPORTS', '00000000-0000-0000-0000-000000000003', '2026-11-01T10:00:00Z', '2026-11-09T19:00:00Z');

INSERT OR IGNORE INTO posts (id, group_id, author_id, author_name, title, content, is_anonymous, created_at, updated_at)
VALUES
('aaaa1111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000002', 'Niamh Kelly', 'Cloudflare Edge + Spring Boot Integration Live!', 'Our CampusLoop backend is now live on hazed0m Cloudflare Edge with full REST + STOMP WebSocket support!', 0, '2026-11-09T18:00:00Z', '2026-11-09T18:00:00Z'),
('aaaa2222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000001', 'Anonymous Student', 'Best quiet study sockets in Central Quad?', 'Anyone know if the 3rd floor atrium tables have working USB-C outlets today?', 1, '2026-11-09T18:30:00Z', '2026-11-09T18:30:00Z');

INSERT OR IGNORE INTO comments (id, post_id, group_id, author_id, author_name, content, parent_id, created_at, updated_at)
VALUES
('cccc1111-1111-1111-1111-111111111111', 'aaaa1111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000003', 'Liam O''Brien', 'Tested the STOMP WebSocket connection from the Android emulator—latency is under 30ms!', NULL, '2026-11-09T18:15:00Z', '2026-11-09T18:15:00Z'),
('cccc2222-2222-2222-2222-222222222222', 'aaaa1111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000002', 'Niamh Kelly', 'Awesome! Threaded replies and anonymous posts are syncing cleanly too.', 'cccc1111-1111-1111-1111-111111111111', '2026-11-09T18:20:00Z', '2026-11-09T18:20:00Z');

INSERT OR IGNORE INTO events (id, title, description, category, visibility, status, campus_zone, start_time, end_time, max_participants, organizer_id, organizer_name, created_at, updated_at)
VALUES
('eeee1111-1111-1111-1111-111111111111', 'Kotlin & Coffee Sprint @ East Quad', 'Grabbing flat whites and reviewing the new CampusLoop real-time channels & geo radar!', 'COFFEE', 'PUBLIC', 'OPEN', 'East Quad', '2026-11-10T14:00:00Z', '2026-11-10T15:30:00Z', 8, '00000000-0000-0000-0000-000000000002', 'Niamh Kelly', '2026-11-09T18:00:00Z', '2026-11-09T18:00:00Z'),
('eeee2222-2222-2222-2222-222222222222', 'Evening 5-a-Side Football', 'Friendly match on the Grangegorman Astro pitch. All skill levels welcome!', 'SPORTS', 'PUBLIC', 'OPEN', 'Sports Pitch', '2026-11-10T18:00:00Z', '2026-11-10T19:30:00Z', 10, '00000000-0000-0000-0000-000000000003', 'Liam O''Brien', '2026-11-09T18:10:00Z', '2026-11-09T18:10:00Z');

INSERT OR IGNORE INTO channels (id, name, type, group_id, created_at, updated_at)
VALUES
(1001, 'general-hackers', 'GROUP_TEXT', '11111111-1111-1111-1111-111111111111', '2026-11-09T18:00:00Z', '2026-11-09T18:00:00Z'),
(1002, 'coffee-runs', 'GROUP_TEXT', '22222222-2222-2222-2222-222222222222', '2026-11-09T18:00:00Z', '2026-11-09T18:00:00Z');

INSERT OR IGNORE INTO channel_members (id, channel_id, user_id, username, created_at, updated_at)
VALUES
(2001, 1001, '00000000-0000-0000-0000-000000000001', 'alex_tud', '2026-11-09T18:00:00Z', '2026-11-09T18:00:00Z'),
(2002, 1001, '00000000-0000-0000-0000-000000000002', 'niamh_k', '2026-11-09T18:00:00Z', '2026-11-09T18:00:00Z');

INSERT OR IGNORE INTO messages (id, channel_id, sender_id, sender_name, content, parent_message_id, parent_content, created_at, updated_at)
VALUES
(3001, 1001, '00000000-0000-0000-0000-000000000002', 'niamh_k', 'Welcome to #general-hackers on CampusLoop Cloudflare Edge!', NULL, NULL, '2026-11-09T18:30:00Z', '2026-11-09T18:30:00Z');

INSERT OR IGNORE INTO geo_locations (user_id, longitude, latitude, updated_at)
VALUES
('00000000-0000-0000-0000-000000000002', -6.2785, 53.3562, '2026-11-09T19:00:00Z'),
('00000000-0000-0000-0000-000000000003', -6.2801, 53.3548, '2026-11-09T19:00:00Z');

INSERT OR IGNORE INTO stories (id, media_file_key, caption, user_id, privacy_settings, latitude, longitude, expires_at, created_at, updated_at)
VALUES
('s1111111-1111-1111-1111-111111111111', 'stories/00000000-0000-0000-0000-000000000002/sample_story.jpg', 'Coding sprint at East Quad atrium!', '00000000-0000-0000-0000-000000000002', 'EVERYONE', 53.3559, -6.2792, '2026-11-12T20:00:00Z', '2026-11-10T17:00:00Z', '2026-11-10T17:00:00Z');
