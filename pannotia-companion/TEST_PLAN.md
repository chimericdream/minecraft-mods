# Test Plan — Pannotia Companion

**Status: scaffold only — nothing to test yet.** This project is disabled in `settings.gradle` and
contains only Architectury boilerplate (entrypoints, empty mixin config, access widener). The README
is empty. The name suggests a companion/helper mod for a "Pannotia" world, server, or modpack —
likely glue features (recipes, tags, world tweaks) rather than standalone content.

## When features land

Companion/glue mods are mostly data: recipes, tags, loot-table injections, and small behavioral
tweaks. The high-value automated tests are **data-resolution tests**, which are cheap to write:

* **Recipe tests** — for every added/changed recipe, assert it resolves via the server
  `RecipeManager` and produces the expected output (no world interaction needed).
* **Tag membership tests** — assert each modified tag contains/excludes the intended entries after
  datapack load.
* **Loot injection tests** — build the loot table's `LootParams` and roll it N times with a seeded
  random, asserting injected entries appear (see the loot-sampling approach in
  `athenaeum/TEST_PLAN.md` — same pattern, and a shared ChimericLib helper should serve both).
* **Behavioral GameTests** — only for actual mechanics, following the Hopper X-Treme conventions:
  classes in `fabric/src/main/java/com/chimericdream/pannotiacompanion/fabric/test/`, registered
  under the `fabric-gametest` entrypoint, structures under
  `common/src/main/resources/data/pannotiacompanion/gametest/structure/`.
* **Manual checklist** — whatever the companion integrates with (other mods? a specific modpack?)
  needs an integration smoke pass in that environment, since GameTests here run without the partner
  content.
