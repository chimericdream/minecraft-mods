# ChimericLib (Fabric/NeoForge)

![Version: 6.0.0-alpha.0](https://img.shields.io/badge/version-6.0.0--alpha.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Shared library with common code for chimericdream's mods._

## Introduction

ChimericLib is the core library that the rest of the chimericdream mod suite is built on. It is **not a content
mod** — it adds no blocks, items, or gameplay of its own. Instead it bundles the shared plumbing every mod would
otherwise have to reimplement: registration helpers, data generation, inventory and screen utilities, tag
definitions, and a handful of common blocks/entities.

If you have installed one of the other mods in this suite, you need ChimericLib as a dependency. On its own it does
nothing visible in-game, so **in-game documentation is not applicable** to this mod.

### Minecraft Versions

* 26.2: supported

### What's Inside

For developers, ChimericLib provides shared code across the Architectury `common` layer, including:

* **Registration** — `ModRegistryHelper` and related helpers for registering blocks, items, and other content
  consistently across Fabric and NeoForge.
* **Blocks** — a `RegisterableBlock` abstraction plus `BlockConfig` and block/item data generators for
  automated model, blockstate, and loot table generation.
* **Inventories** — `ImplementedInventory` and `InventoryUtils` for block entities that hold items.
* **Screens** — reusable single- and double-wide inventory screens and screen handlers.
* **Tags** — common block and item tag definitions shared between mods.
* **Utilities** — helpers for colors, fluids, text, textures, math/direction, tools, and configuration.
* **Entities** — a `SimpleSeatEntity` for sittable blocks (e.g. chairs and stools).

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/chimericlib-mc/issues) to report any bugs
you find.

## Credits

Thanks go to the developers of the Fabric and NeoForge mod loaders and the Architectury API, which this library
builds upon.

## License

This library is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
