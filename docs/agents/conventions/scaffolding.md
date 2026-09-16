# Scaffolding

- **New mod**: `scripts/init-mod.sh` (interactive) runs `bun create mod` against the `.bun-create/mod/`
  template, replaces `{{MOD_ID}}`/`{{CLASS_NAME}}`/etc. placeholders, and updates the project list +
  settings.gradle. Produces the standard `common`/`fabric`/`neoforge` layout.
- **New block family (minekea)**: follow the existing pattern — a `ModThingGroup` registration class
  (`minekea/common/.../block/**`) + a `ChimericLibBlockDataGenerator` subclass
  (`minekea/fabric/.../block/**DataGenerator.java`) wired into the category aggregator, then run
  datagen. minekea has ~55 such datagen classes as references (e.g. `ArmoireBlockDataGenerator`).
