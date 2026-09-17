# ChimericLib (Fabric/NeoForge)

![Version: 6.4.0](https://img.shields.io/badge/version-6.4.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Shared library with common code for chimericdream's mods._

## Introduction

ChimericLib is the core library that the rest of the chimericdream mod suite is built on. It is **not a content
mod** — it adds no blocks, items, or gameplay of its own. Instead it bundles the shared plumbing every mod would
otherwise have to reimplement: registration helpers, data generation, inventory and screen utilities, tag
definitions, and a handful of common blocks/entities.

If you have installed one of the other mods in this suite, you need ChimericLib as a dependency. On its own it does
nothing visible in-game, so **in-game documentation is not applicable** to this mod.

### Minecraft Versions

* 26.2: Supported
* 26.1.2: Bug fixes only
* 1.21.5: Bug fixes only
* 1.21.4: Bug fixes only

### What's Inside

For developers, ChimericLib provides shared code across the Architectury `common` layer, including:

* **Registration** — `ModRegistryHelper` and related helpers for registering blocks, items, and other content
  consistently across Fabric and NeoForge.
* **Blocks** — a `RegisterableBlock` abstraction plus `BlockConfig` and block/item data generators for
  automated model, blockstate, and loot table generation.
* **Inventories** — `ImplementedInventory` and `InventoryUtils` for block entities that hold items.
* **Screens** — reusable single- and double-wide inventory screens and screen handlers.
* **Tags** — common block and item tag definitions shared between mods.
* **Utilities** — helpers for colors, fluids, text, textures, math/direction, tools, configuration, and
  custom player-head game profiles.
* **Entities** — a `SimpleSeatEntity` for sittable blocks (e.g. chairs and stools), plus
  `FallingUpwardBlock`/`FallingUpwardBlockEntity` for blocks that rise instead of fall.
* **Commands** — a small framework (`ChimericCommand`/`ChimericCommands`) for registering commands
  across both loaders without touching Architectury's command event directly. Ships its own first
  command, `/chimericlib blockstate get|set|modify`.
* **Config** — `YaclConfig`/`YaclConfigScreens`, a shared wrapper around YACL's `ConfigClassHandler`
  plus automatic Fabric (Mod Menu) and NeoForge config-screen registration, so a mod no longer
  hand-writes that boilerplate itself. See below.
* **Armor trims** — `TrimMaterialConfig`, `TrimMaterialRegistryHelper`, and `ArmorTrimAtlasProvider`
  automate generating a custom `trim_material` and its `armor_trims`/`items` atlas overrides, so a mod
  only has to hand-author the palette texture and its own material list. On Fabric, chimeric-lib's own
  client init also registers a dynamic item model (`fabric/trims/TrimmedArmorItemModel`) so a custom
  material's icon renders correctly in the inventory too, not just on the worn armor — the Fabric
  equivalent of what NeoForge already does natively. Nothing extra to call for this; it applies
  automatically once a mod's materials are registered.

### Config

`YaclConfig` collapses the hand-written `ConfigClassHandler` + `ModMenuIntegration` + NeoForge
`IConfigScreenFactory` boilerplate every mod used to duplicate into one field and one `init()` call:

```java
public static final YaclConfig<FooConfig> CONFIG = YaclConfig.builder(FooConfig.class, ModInfo.MOD_ID)
    .screen((defaults, config, builder) -> builder.title(...).category(...))
    .build();
// in FooMod.init(): FooConfig.CONFIG.init();
// elsewhere: FooConfig.CONFIG.instance().someField
```

`.screen(...)` is optional — omit it for a config with no config screen. `.onLoad(hook)` runs a
post-load validation/clamping hook, and `.fileName(...)` overrides the default `<modId>.json5`. Config
screens register themselves with Mod Menu (Fabric) and NeoForge automatically; a consuming mod doesn't
need its own `ModMenuIntegration` or `IConfigScreenFactory` registration anymore.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs
you find.

## Credits

Thanks go to the developers of the Fabric and NeoForge mod loaders and the Architectury API, which this library
builds upon.

## License

This library is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
