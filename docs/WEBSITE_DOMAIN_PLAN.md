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

Future option: GitHub Pages can still be used later if we want static hosting outside the server, but the current production website is server-hosted.

## Future improvements

- Add trailer or hero video
- Add screenshots from EarthOS and the Earth map
- Add feature pages for EarthOS, transport, mining and nightlife
- Add development blog/news posts
- Add Discord call-to-action
- Expand multilingual website content beyond the current English/Danish landing page

## Safety note

Do not commit account credentials, DNS tokens, passwords, SSH keys, recovery codes, private server IPs, or private player/admin data.
