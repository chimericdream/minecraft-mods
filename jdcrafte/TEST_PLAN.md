# Test Plan — JDCrafte

**Status: scaffold only — nothing to test yet.** This project is disabled in `settings.gradle` and
contains only Architectury boilerplate. The README is a farm-themed idea list (feeding trough,
crates, rustic furniture, farm carts/minecarts, irrigation, crops, professions) with nothing
implemented.

## When features land

Expected shape based on the idea list — plan accordingly:

* **Feeding trough** — GameTests: animals within range path to the trough and consume feed; breeding
  triggers when two fed adults are in range; trough inventory depletes correctly; hopper interaction
  (can troughs be auto-filled?). Reuse the container-assertion helpers from Hopper X-Treme's tests.
* **Crates / storage** — mirror Minekea's crate coverage (see `minekea/TEST_PLAN.md`): capacity,
  hopper in/out, contents preserved on break if applicable, screen opens.
* **Furniture / decorations** — registry-completeness loop tests (item, loot table, recipe, model
  reference exists) plus seat-entity tests if sittable (ChimericLib `SimpleSeatEntity`).
* **Carts/minecart variants** — entity GameTests: cart follows rails, harvests/interacts with the
  block it passes over, inventory behavior; these are the most complex and deserve their own
  structures per rail scenario.
* **Crops** — growth-stage GameTests using `randomTick` forcing or bonemeal, drop tables at each
  stage, farmland trampling rules.
* **Manual checklist** — visual/model pass, sounds, creative tab, NeoForge parity.

Conventions: Fabric GameTest classes in
`fabric/src/main/java/com/chimericdream/jdcrafte/fabric/test/`, registered under the
`fabric-gametest` entrypoint, structures under
`common/src/main/resources/data/jdcrafte/gametest/structure/`. Animal-and-crop fixtures ("spawn N
cows in pen", "advance crop to stage X") are prime candidates for ChimericLib's GameTest helper
collection rather than living here.
