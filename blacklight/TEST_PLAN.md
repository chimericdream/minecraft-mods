# Test Plan — Blacklight

**Status: scaffold only — nothing to test yet.** This project is disabled in `settings.gradle` and
contains only the Architectury boilerplate (mod entrypoints, empty mixin config, access widener).
There are no blocks, items, mixins with logic, or assets, and the README is still the Fabric
template.

## When features land

Write the real plan alongside the first feature, following the suite conventions established in
`hopper-xtreme/TEST_PLAN.md`:

* **Manual test plan** — a creative-world checklist per player-facing feature, run on both Fabric
  and NeoForge.
* **Automated tests** — Fabric GameTest classes in
  `fabric/src/main/java/com/chimericdream/blacklight/fabric/test/`, registered under the
  `fabric-gametest` entrypoint in `fabric.mod.json`, with `.snbt` structures under
  `common/src/main/resources/data/blacklight/gametest/structure/`.
* Use ChimericLib's GameTest helpers (see `chimeric-lib/POTENTIAL_FEATURES.md`) for common fixtures
  before writing bespoke ones.

If the concept involves light/rendering (the name suggests invisible-light or glow mechanics), plan
for the split early: light *levels* are server-testable via GameTest (`Level.getBrightness` /
light-engine queries), while visual glow effects will need manual client verification.

## Smoke tests worth adding even now

* The mod loads on both loaders without errors when re-enabled in `settings.gradle` (build-level
  check; the shared `./gradlew build` covers compilation but not runtime init).
