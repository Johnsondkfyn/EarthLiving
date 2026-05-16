# Security Notes

## Never Store In GitHub

- root passwords
- SSH private keys
- RCON passwords
- API keys
- Tebex tokens
- Discord bot tokens
- live server logs with player IP addresses
- world backups

## Server Defaults

The Earth Living server should keep:

- whitelist enabled during testing
- online mode enabled
- secure profile enforcement enabled
- RCON disabled unless temporarily needed
- OP list empty during normal operation
- LuckPerms as the normal admin permission system

## Backup Direction

Keep Minecraft backups outside this repository.
Use `/opt/backups/minecraft` on the server and later sync to cloud storage with a backup tool such as `rclone`.
