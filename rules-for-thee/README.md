# Rules for Thee (Fabric/NeoForge)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: optional](https://img.shields.io/badge/client-optional-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Per-player game rules for shared servers. Give a player or a named profile their own value for a game rule that applies to that player._

## Introduction

Game rules apply to everyone. If a server is on hard with `keepInventory` off, all players get that setting. Rules for Thee lets an operator give single players their own value for the rules that apply to *that player*. Everyone else keeps the world setting.

Example: a friends' server is on hard difficulty with `keepInventory` off. Only the kids keep their inventory when they die. Nothing changes for anyone else.

This is a **server-side mod**. There is no config screen. You manage everything with commands, and the mod saves it with the world.

### Minecraft Versions

* 26.2: supported

### Current Features

* **Per-player overrides.** Set a game rule value for one player. That player gets the value. Everyone else gets the world value. An override can be more lenient *or* stricter than the world.
* **Profiles.** A profile is a named set of overrides, such as `kids`. Put a player in a profile (one profile for each player for now). Change the profile once to change all players in it. The overrides of a player have priority over their profile. The profile has priority over the world.
* **Works for offline players.** Select a player by name. The mod stores the override by UUID. You can set up a player who is not online. The player gets a message at the next login.
* **Players get messages.** When the rules of a player change, the player gets a chat message. An online player gets it right away. Other players get it at the next login. `/rulesforthee mine` shows the current overrides of the player next to the world values.
* **Pinned.** Overrides do not follow `/gamerule`. If you change the world value later, the overrides stay the same.

### Supported rules

An override changes only what happens to or for the player it is set on. It never changes anything for other players nearby.

| Rule | Type | What it changes for that player |
|---|---|---|
| `keep_inventory` | on/off | Whether their inventory and XP are kept on death |
| `fall_damage`, `drowning_damage`, `fire_damage`, `freeze_damage` | on/off | Whether they take that kind of damage |
| `show_death_messages` | on/off | Whether *their* death is announced in chat. Also covers the deaths of their tamed animals |
| `show_advancement_messages` | on/off | Whether *their* advancements are announced in chat |
| `spawn_phantoms` | on/off | Whether phantoms spawn for them |
| `immediate_respawn` | on/off | Whether they skip the death screen |
| `reduced_debug_info` | on/off | Whether their F3 screen is reduced |
| `players_nether_portal_default_delay`, `players_nether_portal_creative_delay` | number | How long they stand in a Nether portal before travelling |
| `respawn_radius` | number | How far from the spawn point they can respawn |
| `send_command_feedback` | on/off | Whether they get feedback for the commands they run |
| `pvp` | on/off | Whether they can hurt other players and other players can hurt them. If it is off, neither works. For damage between two players, PVP must be on for both |

You cannot override rules that apply to the whole world (time, weather, mob spawning caps, explosions, and so on) for a single player.

### Commands

`/rulesforthee` (also `/rft`). All commands except `mine` need operator permission, like `/gamerule`.

| Command | What it does |
|---|---|
| `/rulesforthee mine` | Show the rules that differ for you (any player) |
| `/rulesforthee rules` | List the rules that can be overridden |
| `/rulesforthee profile create <name>` / `delete <name>` / `list` / `show <name>` | Manage profiles |
| `/rulesforthee profile edit <name> set <rule> <value>` / `clear <rule>` | Change the rules of a profile |
| `/rulesforthee player <player> show` | Show the profile and effective rules of a player |
| `/rulesforthee player <player> profile set <name>` / `profile clear` | Put a player in a profile, or take them out |
| `/rulesforthee player <player> set <rule> <value>` / `clear <rule>` | Change the overrides set on a player |

Example for the `kids` case:

```
/rulesforthee profile create kids
/rulesforthee profile edit kids set keep_inventory true
/rulesforthee player Junior profile set kids
```

## Notes for Documentation

* **Grave and death-handling mods** that read the world `keepInventory` value themselves do not know about per-player overrides. Rules for Thee changes how vanilla handles death for the player. A mod that replaces this with its own chest or grave can still use the world value. *Your Items Are Safe* is supported. When it is installed, its death handler uses the `keepInventory` value of the player that dies. The support is optional. It does nothing when that mod is not installed.
* A player can be in one profile at a time. Multiple profiles can be added later.
* Not included: per-player difficulty, and rules that apply only to the whole world.

## Issues & Suggestions

Use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report bugs or suggest features.

## Credits

Thanks to Mojang for Minecraft. Thanks also to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
