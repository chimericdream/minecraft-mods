# Test Plan — Cobblicious

**Status: scaffold only — nothing to test yet.** This project is disabled in `settings.gradle` and
contains only Architectury boilerplate (entrypoints, empty mixin config, access widener). No blocks
or assets exist yet. Per its README intro, the plan is *cobbled and mossy variants of natural
stone-type blocks, plus slabs/stairs/walls*. (Note: the README's "Current Features" section is
copy-pasted from Athenaeum and should be fixed.)

## When features land

This will be a block-family content mod, so its test plan should look like a slimmed-down version of
Minekea's (`minekea/TEST_PLAN.md`):

* **Registry/data-completeness GameTests** — for every registered block: item exists, block drops
  itself (or cobbled variant logic, if silk-touch rules apply), stairs/slabs/walls waterlog
  correctly, and recipes (crafting + stonecutter) resolve. These are loop-over-registry tests, not
  per-block hand-written ones.
* **Behavioral GameTests** — anything with mechanics: e.g. if mossy conversion (vine/moss spreading
  or crafting) is implemented, test the conversion path; if cobbled variants come from mining, test
  loot-table behavior with and without Silk Touch.
* **Manual checklist** — visual pass over models/textures for every variant (automated tests can't
  see textures), creative tab organization, stonecutter UI listings.

Conventions: Fabric GameTest classes in
`fabric/src/main/java/com/chimericdream/cobblicious/fabric/test/`, registered under the
`fabric-gametest` entrypoint, structures under
`common/src/main/resources/data/cobblicious/gametest/structure/`. Use ChimericLib's planned
**block-family datagen/testing helpers** (see `chimeric-lib/POTENTIAL_FEATURES.md`) — this mod is
exactly the "declare a base block, generate the family" case, and a shared "assert family is
complete" GameTest helper should be built there, not here.
