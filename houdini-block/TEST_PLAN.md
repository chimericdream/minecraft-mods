# Test Plan — Houdini Block

Houdini Block adds one block whose entire feature is *suppressing neighbor block updates* when it is
placed, broken, or replaced. `HoudiniWorldMixinLogic.preventBlockUpdates(previousState, newState,
newBlock)` decides suppression from three blockstate properties (`PREVENT_ON_PLACE`,
`PREVENT_ON_BREAK`, `REPLACE_BLOCK`), which together encode the four player-facing modes (prevent on
break / prevent on place / prevent all / replace block). Fabric and NeoForge hook this via separate
`HoudiniWorldMixin` implementations, so **both loaders need behavioral coverage** — this mod is a
prime candidate for divergence between platforms.

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/houdiniblock/fabric/test/`, registered under the
`fabric-gametest` entrypoint in `fabric.mod.json`, with `.snbt` structures under
`common/src/main/resources/data/houdiniblock/gametest/structure/`. Because suppression is exactly the
kind of thing redstone components observe, every automated test below uses an **update detector**: an
observer facing the mutated position wired to a redstone lamp (or a comparator-latched circuit), so
"an update happened" becomes an assertable block state.

## Manual test plan

Setup: creative world, `/give` the Houdini Block, plus an observer, sand, water bucket, and a basic
redstone lamp circuit. Repeat the full pass on Fabric and NeoForge.

1. **Mode switching & tooltip**
   * Cycle through the four modes (however the item exposes it — verify the mechanism itself) and
     confirm the item tooltip names the active mode each time.
2. **Prevent on break**
   * Place the block with an observer watching it; placing SHOULD fire the observer.
   * Break it; the observer must NOT fire. Repeat with sand stacked on top (sand must not fall) and
     against water (water must not flow into the gap immediately).
3. **Prevent on place**
   * Inverse of the above: placing fires nothing; breaking fires updates normally.
4. **Prevent all**
   * Neither placing nor breaking fires the observer, drops sand, or lets water flow.
5. **Replace block mode**
   * Place a Houdini block in replace mode into a spot inside a powered redstone line / under
     floating sand; swap it in and out; nothing around it should re-evaluate.
6. **Suppression tricks regression**
   * Classic checks: place against a BUD-powered piston, next to a rail on a slope, and adjacent to
     an active hopper — none should react when suppression applies.
7. **Non-suppressed edges**
   * Breaking a *neighboring* block next to a Houdini block must still update normally — the mod
     should only filter updates originating from the Houdini block position itself.
   * Explosions, pistons pushing the block, and fluid replacing it: document and verify what happens
     (the mixin only special-cases air/liquid transitions; anything surprising here should become a
     bug or a documented limitation).
8. **Survival drops**
   * Break the block in survival with/without suppression; it must still drop itself and keep its
     mode (check whether mode is stored on the item after re-picking it up — if not, that's a
     finding to report).

## Recommended automated tests

### GameTests — one per mode × mutation

Shared structure: a 3×3×3 platform with an **observer at (2,1,1) facing (1,1,1)** and a redstone lamp
the observer powers. Helper `assertObserverFired(context, bool)` reads the lamp's `lit` state after
2 ticks.

* **`placeFiresUpdates_preventOnBreakMode`** — set block at (1,1,1) to Houdini with
  `PREVENT_ON_BREAK=true` only; assert lamp lit (placing is not suppressed in this mode).
* **`breakSuppressed_preventOnBreakMode`** — pre-place the block, reset lamp, `context.destroyBlock`;
  assert lamp stays off.
* **`placeSuppressed_preventOnPlaceMode`** / **`breakFiresUpdates_preventOnPlaceMode`** — inverse pair.
* **`allSuppressed_preventAllMode`** — both mutations, lamp never lights.
* **`replaceMode_swapInsideCircuit`** — structure with a powered redstone wire crossing (1,1,1);
  `setBlock` Houdini (replace mode) into the wire's position and back; assert the rest of the wire
  keeps its power value (read `RedstoneWireBlock.POWER` on a neighboring wire before/after).
* **Physics-based double-checks** (updates observers can miss):
  * **`sandDoesNotFall`** — sand block on top of the Houdini block; break the Houdini block in a
    suppressing mode; after 5 ticks assert the sand is still a block (not a falling entity / not
    dropped) — mirrors Hopper X-Treme's "assert no item entities" trick
    (`assertItemEntityCountIs`).
  * **`waterDoesNotFlow`** — water source adjacent; break with suppression; assert the vacated pos
    is still air after 10 ticks.
* **Negative control** — same structures with a plain stone block instead of Houdini, asserting the
  lamp DOES light / sand DOES fall. Keeps the detector honest if game behavior shifts.

### Unit tests

`HoudiniWorldMixinLogic.preventBlockUpdates` is nearly pure — it reads two `BlockState`s and a
`Block`. With registries bootstrapped (or by refactoring the property reads behind a small
interface), table-test all 4 modes × {place over air, place over water, break to air, break to
water, replace with stone} → expected boolean. The current logic has subtle asymmetries (e.g.
liquid handling, `REPLACE_BLOCK` checked on both old and new state) that deserve pinned expectations.

### Cross-loader parity

The NeoForge path uses its own mixin + event handler. Until NeoForge GameTest wiring exists in this
repo, add a **manual parity checklist** run before each release (steps 2–5 above on NeoForge). If
NeoForge gametests are added later, the same test classes should be shared from `common`.

## ChimericLib helper opportunities

* **Block-update detector fixture** — "build observer+lamp watching pos X" + `assertNeighborUpdated`
  / `assertNoNeighborUpdate` assertions. Reusable by Hopper X-Treme (its redstone wire mixin),
  Minekea (wrench toggling beam connections shouldn't cause update storms), and any future
  update-suppression feature.
* **`assertBlockStateProperty(helper, pos, property, expected)`** convenience — every mode test does
  this repeatedly.
* **Negative-control structure conventions** — a documented pattern for "same structure, vanilla
  block, inverted assertion" so detector-based tests stay self-validating.

## Open questions

* Is the mode stored on the placed blockstate only, or also on the dropped item? Survival round-trip
  behavior (manual test 8) determines whether an extra GameTest for drop persistence is needed.
