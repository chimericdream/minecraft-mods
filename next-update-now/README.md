# Next Update Now (Fabric/NeoForge)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Get content from the next update on the current version!_

## Introduction

### Minecraft Versions

* 26.2: Supported (26.3 content only)

### What does this mod do?

This mod brings early access to upcoming Minecraft content — pulled from snapshots and/or the latest release — to players who haven't upgraded yet (or can't). If you're stuck on an older version for modpack, server, or compatibility reasons, this mod lets you try out new blocks and other "easy" additions from the next version without leaving your current one.

### How does version support work?

The mod always targets the **current stable version**, while pulling content from the **in-progress snapshot cycle**. For example:

* During the 26.3 snapshot cycle, the mod targets **26.2** and adds early access to 26.3 content.
* Once 26.3 officially releases, the mod gets **one final 26.2 release** to wrap things up.
* Development then pauses until the **26.4 snapshot cycle** begins — at which point the mod shifts to targeting **26.3** and pulling in 26.4 content.

In short: the mod always lives one version behind the snapshot it's pulling from.

### Will you keep supporting older versions once a new cycle starts?

No. Once a snapshot cycle ends and the mod moves on to the next one, the previous version's build is considered final. There are no ongoing updates or backports to versions the mod has already moved past.

### Is this a "backport everything" mod?

No. There are already other mods dedicated to fully backporting new content to old versions — this isn't one of them. This mod's goal is early access to *some* new content, not full parity with the upcoming release.

### What content can I expect to get?

At minimum, new **blocks** and other "easy" additions land every cycle. Beyond that:

* **Entities, mobs, and world generation** are added *only* when they can be implemented without significant effort.
* Some features from a given snapshot cycle may not be included at all, if they'd require substantial work to replicate on the older version.

There's no guarantee of full feature parity with any given snapshot or release.

### Why not just wait for the official update?

This mod is for players who are staying on an older version deliberately or by necessity — server operators, modpack maintainers, people waiting on other mod compatibility, etc. It's meant to bridge the gap, not replace upgrading.

### Does this work with Forge, Fabric, or NeoForge?

It's built for **Java Edition**, targeting **Fabric/NeoForge**.

### Is this compatible with other mods?

Compatibility isn't guaranteed with every mod, especially ones that touch the same blocks, entities, or world generation systems this mod modifies. Report conflicts through the issue tracker.

### What happens if a snapshot feature changes before the full release?

Since the mod tracks in-progress snapshots, there's a chance early-access content may not perfectly match the final released version. The mod will be updated to match the final release as part of that cycle's last update for the older version.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
