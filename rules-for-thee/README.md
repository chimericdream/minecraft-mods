# Rules for Thee (Fabric/NeoForge)

![Version: 1.0.0-beta.0](https://img.shields.io/badge/version-1.0.0--beta.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: optional](https://img.shields.io/badge/client-optional-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Per-player game rules for shared servers: give individual players or named profiles their own value for a game rule wherever the rule is about that player._

## Introduction

Game rules are all-or-nothing: a server is on hard with `keepInventory` off, and that applies to everyone. Rules for Thee lets an operator give individual players their own value for the rules that are about *that player*, while everyone else keeps the world's setting.

The motivating case: a friends' server on hard difficulty with `keepInventory` off, where only the kids keep their inventory when they die. Nobody else's game changes.

This is a **server-side mod**. Vanilla clients can join a server that has it, and there is no config screen: everything is managed with commands and saved with the world.

### Minecraft Versions

* 26.2: supported

### Current Features

* **Per-player overrides.** Set a game rule's value for one player. They get it; everyone else gets the world's value. An override can be more lenient *or* stricter than the world.
* **Profiles.** A profile is a named set of overrides, such as `kids`. Put a player in a profile (one profile per player for now) and change the profile once to change everyone in it. A player's own overrides beat their profile's, and their profile beats the world.
* **Works for offline players.** Target a player by name; the override is stored by UUID, so you can set up a player who isn't online and they are told the next time they log in.
* **Players are told.** When a player's rules change they get a chat message right away if they are online, or at their next login. `/rulesforthee mine` shows their current overrides next to the world's values.
* **Pinned.** Overrides don't follow `/gamerule`. Changing the world's value later leaves them as they are.

### Supported rules

An override only changes what happens to or for the player it's set on, never other players nearby.

| Rule | Type | What it changes for that player |
|---|---|---|
| `keep_inventory` | on/off | Whether their inventory and XP are kept on death |
| `fall_damage`, `drowning_damage`, `fire_damage`, `freeze_damage` | on/off | Whether they take that kind of damage |
| `show_death_messages` | on/off | Whether *their* death is announced in chat. Also covers their tamed animals' deaths |
| `show_advancement_messages` | on/off | Whether *their* advancements are announced in chat |
| `spawn_phantoms` | on/off | Whether phantoms spawn for them |
| `immediate_respawn` | on/off | Whether they skip the death screen |
| `reduced_debug_info` | on/off | Whether their F3 screen is reduced |
| `players_nether_portal_default_delay`, `players_nether_portal_creative_delay` | number | How long they stand in a Nether portal before travelling |
| `respawn_radius` | number | How far from the spawn point they can respawn |
| `send_command_feedback` | on/off | Whether they are sent feedback for commands they run |

Rules that are about the whole world (time, weather, mob spawning caps, explosions, and so on) can't be overridden per player.

### Commands

`/rulesforthee` (also `/rft`). Everything except `mine` needs operator permission, like `/gamerule`.

| Command | What it does |
|---|---|
| `/rulesforthee mine` | Show the rules that differ for you (any player) |
| `/rulesforthee rules` | List the rules that can be overridden |
| `/rulesforthee profile create <name>` / `delete <name>` / `list` / `show <name>` | Manage profiles |
| `/rulesforthee profile edit <name> set <rule> <value>` / `clear <rule>` | Change a profile's rules |
| `/rulesforthee player <player> show` | Show a player's profile and effective rules |
| `/rulesforthee player <player> profile set <name>` / `profile clear` | Put a player in a profile, or take them out |
| `/rulesforthee player <player> set <rule> <value>` / `clear <rule>` | Change a player's own overrides |

Example, for the motivating case:

```
/rulesforthee profile create kids
/rulesforthee profile edit kids set keep_inventory true
/rulesforthee player Junior profile set kids
```

## Notes for Documentation

* Server-side only; a vanilla client can join. Messages from the mod are plain text for that reason.
* **Grave and death-handling mods** that read the world's `keepInventory` value themselves don't know about per-player overrides yet. Rules for Thee changes how vanilla handles death for the player, but a mod that replaces that with its own chest or grave may still act on the world's value. *Your Items Are Safe* is supported: when it's installed, Rules for Thee makes its death handler use the dying player's `keepInventory` value. It is optional and does nothing when that mod is absent.
* A player can be in one profile at a time. Multiple profiles are a possible later addition.
* Not covered: per-player difficulty, and rules that only make sense for the whole world.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
