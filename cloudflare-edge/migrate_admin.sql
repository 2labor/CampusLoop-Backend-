-- Migration: Update users table to allow same email across different auth providers (UNIQUE(email, provider))
CREATE TABLE IF NOT EXISTS users_new (
    id TEXT PRIMARY KEY,
    email TEXT NOT NULL,
    username TEXT NOT NULL,
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
    updated_at TEXT NOT NULL,
    UNIQUE(email, provider)
);

INSERT OR IGNORE INTO users_new SELECT * FROM users;
DROP TABLE IF EXISTS users;
ALTER TABLE users_new RENAME TO users;

-- 1. Create Admin Account 1: Google Auth on c24741821@mytudublin.ie
INSERT OR REPLACE INTO users (
    id, email, username, login, password_hash, provider, is_verified,
    first_name, last_name, bio, course, academic_year, campus_zone, activity_status, interests, created_at, updated_at
) VALUES (
    '00000000-0000-0000-0000-000000000011',
    'c24741821@mytudublin.ie',
    'c24741821_google',
    'c24741821_google',
    'demo1234',
    'GOOGLE',
    1,
    'Admin (Google)',
    'TU Dublin',
    'TU Dublin Administrator via Google Workspace (c24741821@mytudublin.ie)',
    'TU Dublin Computer Science Admin',
    4,
    'East Quad',
    'AVAILABLE',
    '["ADMIN","CAMPUS_LEAD","GOOGLE_WORKSPACE"]',
    '2026-11-09T20:00:00Z',
    '2026-11-09T20:00:00Z'
);

-- 2. Create Admin Account 2: Outlook / Microsoft Auth on c24741821@mytudublin.ie
INSERT OR REPLACE INTO users (
    id, email, username, login, password_hash, provider, is_verified,
    first_name, last_name, bio, course, academic_year, campus_zone, activity_status, interests, created_at, updated_at
) VALUES (
    '00000000-0000-0000-0000-000000000012',
    'c24741821@mytudublin.ie',
    'c24741821_outlook',
    'c24741821_outlook',
    'demo1234',
    'OUTLOOK',
    1,
    'Admin (Outlook)',
    'TU Dublin',
    'TU Dublin Administrator via Microsoft 365 / Outlook (c24741821@mytudublin.ie)',
    'TU Dublin Computer Science Admin',
    4,
    'Central Quad',
    'AVAILABLE',
    '["ADMIN","CAMPUS_LEAD","OUTLOOK_365"]',
    '2026-11-09T20:00:00Z',
    '2026-11-09T20:00:00Z'
);

-- Assign both Admin accounts to all campus groups as ADMIN
INSERT OR REPLACE INTO group_members (id, group_id, user_id, role, created_at)
VALUES
('gm-adm-g-1', '11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000011', 'ADMIN', '2026-11-09T20:00:00Z'),
('gm-adm-g-2', '22222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000011', 'ADMIN', '2026-11-09T20:00:00Z'),
('gm-adm-o-1', '11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000012', 'ADMIN', '2026-11-09T20:00:00Z'),
('gm-adm-o-2', '33333333-3333-3333-3333-333333333333', '00000000-0000-0000-0000-000000000012', 'ADMIN', '2026-11-09T20:00:00Z');

-- Assign both Admin accounts to real-time chat channels
INSERT OR REPLACE INTO channel_members (id, channel_id, user_id, username, created_at, updated_at)
VALUES
(2011, 1001, '00000000-0000-0000-0000-000000000011', 'c24741821_google', '2026-11-09T20:00:00Z', '2026-11-09T20:00:00Z'),
(2012, 1001, '00000000-0000-0000-0000-000000000012', 'c24741821_outlook', '2026-11-09T20:00:00Z', '2026-11-09T20:00:00Z');
