# EarthLiving Ranks and TAB

Updated: 2026-05-27

## Main server status

LuckPerms ranks have been created on Main and TAB has been installed.

Active plugin:

- `TAB 6.0.2`
- Source: Modrinth `tab-was-taken`
- Server jar: `plugins/TAB-v6.0.2.jar`

## LuckPerms groups

Current rank ladder:

```text
owner
developer
admin
moderator
staff
builder
supporter
citizen
default
```

`default` is the public visitor rank.

`TheKing189` is assigned to `owner`.

## Prefixes

```text
owner      &4[Owner]
developer  &3[Dev]
admin      &c[Admin]
moderator  &6[Mod]
staff      &e[Staff]
builder    &b[Builder]
supporter  &d[Supporter]
citizen    &a[Citizen]
default    &7[Visitor]
```

TAB uses LuckPerms prefixes through:

```text
%luckperms-prefix%
%luckperms-suffix%
```

## Config Source

Server snapshots are stored in:

```text
server-config/luckperms/groups/
server-config/luckperms/users/theking189.yml
server-config/tab/config.yml
server-config/tab/groups.yml
```

## Notes

- TAB header/footer has EarthLiving branding, online count, TPS, player rank and ping.
- TAB sorting follows the LuckPerms rank ladder.
- DiscordSRV detects LuckPerms after restart.
- Before public launch, decide which ranks should sync to Discord roles.
