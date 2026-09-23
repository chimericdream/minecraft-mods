# Scaffolding

- **New mod**: `scripts/init-mod.sh` (interactive) runs `bun create mod` against the `.bun-create/mod/`
  template, replaces `{{MOD_ID}}`/`{{CLASS_NAME}}`/etc. placeholders, and updates the project list +
  settings.gradle. Produces the standard `common`/`fabric`/`neoforge` layout.
- **New YACL config**: don't hand-write a `ConfigClassHandler`, Mod Menu class, or NeoForge
  `IConfigScreenFactory` registration. Declare one
  `public static final YaclConfig<XConfig> CONFIG = YaclConfig.builder(XConfig.class, MOD_ID).screen(...).build()`
  (chimeric-lib `com.chimericdream.lib.config`) and call `XConfig.CONFIG.init()` from the mod's common
  `init()`; chimeric-lib registers the screen on both loaders. Read values via `CONFIG.instance()`.
  See `chimeric-lib/README.md` → Config, and any migrated mod (e.g. villager-tweaks) as a reference.
- **New block family (minekea)**: follow the existing pattern — a `ModThingGroup` registration class
  (`minekea/common/.../block/**`) + a `ChimericLibBlockDataGenerator` subclass
  (`minekea/fabric/.../block/**DataGenerator.java`) wired into the category aggregator, then run
  datagen. minekea has ~55 such datagen classes as references (e.g. `ArmoireBlockDataGenerator`).
