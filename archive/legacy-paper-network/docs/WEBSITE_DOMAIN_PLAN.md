# Website and Domain Plan

## Priority
High priority.

## Recommended domain

`earthliving.earth`

This is the recommended public domain for EarthLiving because it matches the server name, the Earth-map concept, and the long-term world-simulation vision.

## Current status

Status as of 2026-05-20: `earthliving.earth` has been purchased, DNS is configured, HTTPS is active, and the public website is live from the EarthLiving server through Nginx.

Implemented:

- `earthliving.earth` points to `159.195.149.253`.
- `www.earthliving.earth` points to `earthliving.earth`.
- HTTPS is enabled with Let's Encrypt.
- The website is deployed under `/var/www/earthliving-site` on the server.
- The live site includes EarthLiving branding, animated hero title, fixed centered logo background, desktop/mobile preview toggle, automatic mobile layout, and English/Danish language toggle.
- The roadmap numbers are stored in `docs/data/roadmap-status.json`.
- The website now has public private-test access cards for Discord onboarding status, live BlueMap access, and current server status.
- The website has a visual progress area for EarthOS, BlueMap and server screenshots/media as approved captures become available.
- A small devlog/news section is live for foundation updates.
- The website shows a technical note that production hosting is Nginx on the EarthLiving server while GitHub remains source of truth for website files.

## Purpose
The website should become the public information and marketing hub for EarthLiving. It should explain the project, show progress, help attract future players and testers, and make the server look serious before public launch.

## Recommended domain structure

| Address | Purpose |
|---|---|
| earthliving.earth | Main website |
| play.earthliving.earth | Minecraft server address later |
| map.earthliving.earth | BlueMap / live map later |
| status.earthliving.earth | Server status later |
| wiki.earthliving.earth | Guides and documentation later |
| discord.earthliving.earth | Discord landing page or redirect later |

## Website should include

- Player-facing EarthLiving overview
- EarthOS explanation
- Main systems overview
- Transport, mining, economy, tourism, nightlife and company features
- Coming soon / launch status
- Roadmap link
- GitHub/project link
- Discord link later
- Screenshots and videos later
- SEO-friendly descriptions
- Development updates

## Hosting direction

Current hosting uses Nginx on the EarthLiving server. GitHub remains the source of truth for website files under `docs/`, and the live Nginx site is deployed from those files.

## Deployment notes

Current live deployment path:

```text
/var/www/earthliving-site
```

Current Nginx site config:

```text
/etc/nginx/sites-available/earthliving-site.conf
```

## Automatic deployment

GitHub Actions workflow:

```text
.github/workflows/deploy-website.yml
```

When repository secrets are configured, pushes to `main` that change `docs/**` will automatically deploy the website to the server.

Required GitHub repository secrets:

```text
EARTHLIVING_SSH_HOST
EARTHLIVING_SSH_USER
EARTHLIVING_SSH_KEY
```

Do not commit the real SSH key or secret values to the repository.

Future option: GitHub Pages can still be used later if we want static hosting outside the server, but the current production website is server-hosted.

## Future improvements

- Add trailer or hero video
- Replace visual placeholders with approved screenshots/video from EarthOS, BlueMap and the server
- Add feature pages for EarthOS, transport, mining and nightlife
- Move live map to `map.earthliving.earth` when the permanent subdomain is ready
- Move server status to `status.earthliving.earth` or connect a real status API later
- Replace Discord onboarding placeholder with the final invite/landing flow
- Expand multilingual website content beyond the current English/Danish landing page

## Safety note

Do not commit account credentials, DNS tokens, passwords, SSH keys, recovery codes, private server IPs, or private player/admin data.
