# CampusLoop Backend (`CampusLoop-Backend`)

[![Status](https://img.shields.io/badge/Status-Live%20on%20Edge-brightgreen)](https://campusloop-api.rohovskyi.dev/api/v1/health)
[![Controllers](https://img.shields.io/badge/Controllers-19%20REST-blue)](https://campusloop-api.rohovskyi.dev/debug)
[![STOMP](https://img.shields.io/badge/WebSocket-STOMP%201.2-orange)](https://campusloop-api.rohovskyi.dev/app)
[![Cloudflare](https://img.shields.io/badge/Cloudflare-Worker%20%2B%20D1%20%2B%20KV-F38020)](https://campusloop-api.rohovskyi.dev)

> **Cloudflare Edge Deployment:** [`https://campusloop-api.rohovskyi.dev`](https://campusloop-api.rohovskyi.dev)  
> **Interactive API Tester Web App:** [`https://campusloop-api.rohovskyi.dev/debug`](https://campusloop-api.rohovskyi.dev/debug)  
> **Mobile / Desktop Web App:** [`https://campusloop-api.rohovskyi.dev/app`](https://campusloop-api.rohovskyi.dev/app)  
> **Detailed Instructions & User Guide:** [`docs/DEPLOYED_SYSTEM_INSTRUCTIONS.md`](docs/DEPLOYED_SYSTEM_INSTRUCTIONS.md)  
> **API Debugging Guide:** [`docs/API_DEBUGGING_GUIDE.md`](docs/API_DEBUGGING_GUIDE.md)

---

## 🚀 Quick Start & Live Access Points

| Service / Interface | URL | Purpose |
| :--- | :--- | :--- |
| **Edge REST API** | [`https://campusloop-api.rohovskyi.dev`](https://campusloop-api.rohovskyi.dev/api/v1/health) | 19 REST Controllers on Cloudflare Edge + D1 SQLite |
| **STOMP 1.2 Broker** | `wss://campusloop-api.rohovskyi.dev/ws` | Real-time WebSocket event dispatch & messaging |
| **Interactive API Tester** | [`https://campusloop-api.rohovskyi.dev/debug`](https://campusloop-api.rohovskyi.dev/debug) | Visual GUI console to test all 45+ endpoints with 1-click logins |
| **Campus Web App** | [`https://campusloop-api.rohovskyi.dev/app`](https://campusloop-api.rohovskyi.dev/app) | Responsive student dashboard (meetups, discussions, chat, ghost mode) |
| **Workers.dev Fallback** | [`https://campusloop-backend.hazekk94.workers.dev`](https://campusloop-backend.hazekk94.workers.dev/api/v1/health) | Redundant edge worker mirror |

---

## 📖 How to Use the Frontends

### 1. Interactive API Tester & Debugger (`/debug`)
Open [`https://campusloop-api.rohovskyi.dev/debug`](https://campusloop-api.rohovskyi.dev/debug) or double-click `docs/api-tester.html`:
1. **Select an Environment:** Choose between Cloudflare Edge, `workers.dev`, or Localhost.
2. **1-Click Quick Login:** Click **👨‍💻 Alex**, **🎨 Niamh**, **🤖 Liam**, **🛡️ Lead Admin**, or **🎓 Backend Lead** to automatically generate and attach a Bearer JWT to all outgoing requests.
3. **Execute Any Request:** Expand any of the 45+ endpoint cards, adjust parameters or body JSON, and click **▶ Send Request** to inspect status codes, latency, and formatted responses.
4. **Copy cURL:** Click **📋 Copy cURL** to copy terminal-ready commands with your active authentication token.
5. **Monitor STOMP Traffic:** Open the **⚡ STOMP Console** to view incoming real-time broadcasts on `/topic/events`, `/topic/channels/{id}/messages`, `/user/queue/geo/friends`, and `/topic/stories`.

### 2. Responsive Campus Web App (`/app`)
Open [`https://campusloop-api.rohovskyi.dev/app`](https://campusloop-api.rohovskyi.dev/app):
* **Host Meetups:** Enter a meetup title and click `+ Host Meetup` to broadcast immediately to campus.
* **Anonymous Discussions:** Post updates or questions with the `🕵️ Post Anonymously` toggle enabled.
* **Live Chat Channels:** Switch between `#general-hackers` and `#coffee-runs` for instant messaging.
* **Ghost Mode:** Click `📍 Sharing Location` to toggle `👻 Ghost Mode ON` (removes GPS coordinates from radar).

### 3. Android Mobile App (`CampusMates`)
* Navigate to app settings or `AuthServerDialog.kt`.
* Set Server URL to: `https://campusloop-api.rohovskyi.dev`.
* The app automatically connects via HTTPS REST and WSS STOMP 1.2 with offline-first Room SQLite sync.

---

## 🔑 Test Accounts & Credentials

Default student password for local accounts: **`demo1234`**

| Username | Role / Name | Email | Provider | UUID |
| :--- | :--- | :--- | :--- | :--- |
| `alex_tud` | Alex Mercer (Student) | `alex@tudublin.ie` | `LOCAL` | `00000000-0000-0000-0000-000000000001` |
| `niamh_k` | Niamh Kelly (Designer) | `Niamh.Kelly@tudublin.ie` | `LOCAL` | `00000000-0000-0000-0000-000000000002` |
| `liam_ob` | Liam O'Brien (Robotics) | `liam.obrien@tudublin.ie` | `LOCAL` | `00000000-0000-0000-0000-000000000003` |
| `hazekk94` | Ruslan (Lead Admin) | `hazekk94@gmail.com` | `GOOGLE` | `00000000-0000-0000-0000-000000000011` |
| `c24393471_outlook` | C24393471 (Backend Lead)| `c24393471@mytudublin.ie`| `OUTLOOK` | `00000000-0000-0000-0000-000000000012` |

---

## 🛠️ Redeployment Commands

From `cloudflare-edge/`:
```powershell
# 1. Verify Cloudflare Account
npx wrangler whoami

# 2. Apply Schema Updates to D1
npx wrangler d1 execute campusloop-db --remote --file=schema.sql

# 3. Deploy Edge Worker to rohovskyi.dev & workers.dev
npx wrangler deploy
```

For complete endpoint contracts, curl examples, and WebSocket frames, see [**`docs/DEPLOYED_SYSTEM_INSTRUCTIONS.md`**](docs/DEPLOYED_SYSTEM_INSTRUCTIONS.md).
