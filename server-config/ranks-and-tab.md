# EarthLiving Ranks and TAB

Updated: 2026-05-29

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

## EarthLiving VS3.5 placeholders

EarthLivingCore `0.9.3` registers a PlaceholderAPI expansion when PlaceholderAPI is installed.

Use these in TAB as normal PlaceholderAPI placeholders:

```text
%earthliving_current_country%
%earthliving_border_access%
%earthliving_required_visa%
%earthliving_passport_status%
%earthliving_verified_status%
%earthliving_current_server%
```

Safe fallback values:

```text
Unknown country
Access unknown
No visa required
No passport data
Unverified
Main
```

Recommended TAB examples are stored in:

```text
server-config/tab/earthliving-placeholders.yml
```

Recommended header:

```yaml
header:
  - "<#8FE388>&m                                                </#49D5FF>"
  - "<#8FE388>&lEarth</#49D5FF><#49D5FF>&lLiving</#8FE388>"
  - "&7%earthliving_current_server% &8| &f%online%&7 online &8| &f%server_tps_1_colored% &7TPS"
  - "&7Country: &f%earthliving_current_country% &8| &7Access: &f%earthliving_border_access%"
```

Recommended footer:

```yaml
footer:
  - "&7Passport: &f%earthliving_passport_status% &8| &7Visa: &f%earthliving_required_visa%"
  - "&7Discord: &f%earthliving_verified_status% &8| &7Rank: &f%luckperms-primary-group%"
```

Recommended player format:

```yaml
customtabname: "%player%"
tabsuffix: " &8| &f%earthliving_current_country% &8| &a%earthliving_verified_status%"
tagsuffix: " &8[%earthliving_border_access%]"
```

Recommended group/rank layout remains LuckPerms-first:

```yaml
sorting-types:
  - "GROUPS:owner,developer,admin,moderator,staff,builder,supporter,citizen,default"
  - "PLACEHOLDER_A_TO_Z:%earthliving_current_country%"
  - "PLACEHOLDER_A_TO_Z:%player%"
```
