# Test Plan — Hang From Slabs

**Status: scaffold only — nothing to test yet.** This project is disabled in `settings.gradle` and
contains only Architectury boilerplate (entrypoints, empty mixin config, access widener). The README
is empty; the name implies letting hangable blocks (lanterns, chains, bells, etc.) attach to the
underside of top slabs / slab-like surfaces where vanilla refuses placement.

## When features land

The likely implementation is a placement-rule mixin, which is very GameTest-friendly:

* **Placement GameTests** — for each supported hangable block (lantern, soul lantern, chain, bell,
  hanging sign, amethyst cluster...): structure with a top slab, `helper.placeAt`/`setBlock` the
  hangable block beneath it, assert placement succeeds and the correct `hanging`/`facing` state is
  set. Mirror negative cases: bottom slabs should still reject hanging placement (or whatever rule
  is chosen).
* **Support-removal GameTests** — break the slab; assert the hanging block pops and drops itself
  (use `assertItemEntityCountIs`, as in Hopper X-Treme's tests).
* **Vanilla regression** — hanging from full blocks must still work; placement on walls/floors
  unchanged.
* **Manual checklist** — visual seating of each hangable model under a slab (no z-fighting/floating
  gaps), plus a NeoForge parity pass.

Conventions: Fabric GameTest classes in
`fabric/src/main/java/com/chimericdream/hangfromslabs/fabric/test/`, registered under the
`fabric-gametest` entrypoint, structures under
`common/src/main/resources/data/hangfromslabs/gametest/structure/`. A ChimericLib
**"placement matrix" helper** (try placing block X against face F of block Y, assert
success/failure) would keep these tests to a few lines each — flag it in
`chimeric-lib/POTENTIAL_FEATURES.md` when implementation starts.
