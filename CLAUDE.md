# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Local development (H2 in-memory DB, no Docker)
```bash
mvn spring-boot:run
```
Access at http://localhost:8080. H2 console at http://localhost:8080/h2-console.

### Run tests
```bash
mvn test
mvn test -Dtest=DistributeTest          # single test class
mvn test -Dtest=DistributeTest#shouldDistributePatrolsEven  # single method
```
Tests use the `test` profile with an H2 in-memory DB (`application-test.properties`).

### Docker development (MySQL + Traefik + SSL)
```bash
./start.sh dev        # sets up certs, starts Docker Compose with dev profile
```
Access at https://gokapp.local (requires `/etc/hosts` entry and self-signed cert).

### Build container image
```bash
mvn jib:build          # build and push directly to ghcr.io (no Docker daemon needed)
mvn jib:dockerBuild    # build to local Docker daemon instead
```
Produces `ghcr.io/erydberg/gokapp:<version>` (Alpine JRE, multi-platform arm64+amd64).

### Production
```bash
./start.sh prod       # or: cp .env.prod .env && docker compose -f compose.yaml -f compose.prod.yaml up -d
```

## Architecture

### Stack
Spring Boot 3.5.9 · Java 21 · Thymeleaf (server-side rendering) · Spring Data JPA · Spring Security · Lombok · H2 (dev/test) · MySQL (docker/prod) · ZXing (QR codes) · Traefik (reverse proxy + TLS).

### Domain model
The app manages scout (or similar) orienteering competitions.

- **Track** — a competition category/age group (e.g. "spårare", "upptäckare"). Patrols belong to one track.
- **Station** — a checkpoint. Has `minScore`/`maxScore` and `minStyleScore`/`maxStyleScore`. A station can apply to all tracks (`allTracks = true`) or only to a specific subset (`ManyToMany` with `Track`). Has an optional `waypoint` flag (waypoints don't count toward max-score tiebreakers). A station can be assigned to a `User` so that non-admin users only see their own station.
- **Patrol** — a competing team. Belongs to a `Track`, has a `startStation` (assigned by the distribute feature), contact details, and a set of `Score`s. Ranking is computed by `Patrol.compareTo`: total score → total score points → count of stations where score = max − x (tiebreaker cascade).
- **Score** — one patrol's result at one station (`scorePoint` + `stylePoint`). Unique constraint on `(station_id, patrol_id)`.
- **Config** — singleton app config (competition name, QR enabled, public result visibility).
- **RegistrationConfig** — controls public patrol self-registration: open/close dates, max patrols, info text, confirmation message.

### Package structure
Each domain concept has its own package under `se.scouttavling.gokapp`:
`configuration`, `distribute`, `patrol`, `print`, `score`, `security`, `start`, `startfinish`, `station`, `track`, `viewresults`.
Each package follows the pattern: Entity → Repository → Service → Controller (and DTOs/mappers where needed).

### Profiles
| Profile | DB | Seed data |
|---|---|---|
| `dev` (default) | H2 in-memory | `DevDataInitializer` seeds tracks, stations, patrols, users |
| `docker` | MySQL (env vars) | — |
| `prod` | MySQL (env vars) | — |
| `test` | H2 in-memory | — |

`AdminUserInitializer` runs on every startup (order 1) and creates an admin user if none exists.

### Security
Three roles: `ROLE_ADMIN`, `ROLE_USER`, `ROLE_STARTFINISH`.
- `/admin/**` requires ADMIN.
- `/startfinish/**` requires STARTFINISH or ADMIN.
- `/public/register`, `/`, `/login`, `/css/**`, `/js/**` are public.
- Non-admin users with `ROLE_USER` are restricted to scoring only their assigned station; admins see all stations.

### Distribute feature
`Distribute` (plain class, no Spring) round-robins patrols across stations. Two strategies exposed via `/admin/distribute`: distribute all patrols together, or distribute per-track (so each track starts fresh at station 0, avoiding imbalance when track sizes differ greatly).

### Views
Thymeleaf templates in `src/main/resources/templates/`. Shared layout fragments are in `fragments.html`. The `config` object (competition name, etc.) is injected into every controller via a `@ModelAttribute` method.
