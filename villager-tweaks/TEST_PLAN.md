# Test Plan — Villager Tweaks

Villager Tweaks bundles configurable villager QoL changes: the **Bagged Villager** item
(`BaggedVillagerItem` — bundle + shift-right-click stores a villager with full NBT; using the item
re-spawns the villager and returns a plain bundle), trading tweaks (`VTTradeOfferMixin`: max-trade
override, demand modifier toggle), reputation tweaks (`VTVillagerEntityMixin`: global reputation,
negative-reputation toggle), zombie-conversion tweaks (`VTZombieEntityMixin`,
`VTZombieVillagerEntityMixin`: always-convert, fixed cure time, cure-time display), and the lure
feature (follow players holding emerald blocks/ore — see `ModTags`). Everything is driven by
`VillagerTweaksConfig` (YACL). There is also a `WorkstationCheckerItem` in the code that the README
doesn't mention — verify its status and either document or exclude it.

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/villagertweaks/fabric/test/`, registered under the
`fabric-gametest` entrypoint, structures under
`common/src/main/resources/data/villagertweaks/gametest/structure/`. Villager tests need entities,
not structures: spawn villagers programmatically with controlled profession/trades so assertions are
deterministic. All config-gated features must set config in-test and restore afterward.

## Manual test plan

Setup: creative world; villagers (some employed, a baby), bundle, emerald blocks, zombie, weakness
potions + golden apples. Fabric full pass, NeoForge smoke pass.

1. **Bagging** — shift-right-click an employed villager with a bundle: villager despawns, you hold a
   Bagged Villager item. Check the item keeps the villager's custom name (named with a name tag
   beforehand).
2. **Un-bagging on a block** — use the item on the top face of a block with 2 air blocks above:
   villager reappears with same profession, level, trades, and name; the item becomes a plain
   bundle. Try invalid targets: side/bottom faces, a spot with only 1 air block above — placement
   must fail and the item must remain a Bagged Villager (no villager loss!).
3. **Un-bagging in place** — right-click air while on the ground: villager spawns at your feet.
   While airborne (elytra/jumping): nothing happens (the code PASSes when not on ground).
4. **Bagging edge cases** — baby villager, zombie villager, wandering trader, and a villager
   currently trading with another player (multiplayer): define and verify which are baggable.
   A bundle *with items inside* used on a villager: does bagging eat the contents? (The un-bag
   returns `Items.BUNDLE.getDefaultInstance()` — contents would be lost; likely needs a guard.)
   Bagged Villager item destroyed (lava/despawn): villager is gone — acceptable? Document.
5. **Max trades override** — enable, set a value (e.g. 2): each offer locks after 2 uses; set −1:
   trade an offer 30+ times without lockout, and confirm restocking still works for other offers.
6. **Demand modifier** — with the toggle off, spam-trade one offer and verify prices do NOT inflate;
   with it on (default), vanilla inflation returns.
7. **Global reputation** — enable; player A heals reputation (cure a zombie villager), player B
   checks prices on the same villager: B should see A's discount. Requires two accounts/LAN.
8. **Negative reputation off** — hit a villager; verify no gossip penalty (prices/iron golem
   aggression unchanged).
9. **Zombie conversion** — "always convert": on Easy difficulty, let a zombie kill a villager —
   must convert 100% (vanilla Easy is 0%). Fixed cure time: cure a zombie villager with the
   override enabled and time the conversion (set 200 ticks for a fast check). Cure-time display:
   verify where/how remaining time renders.
10. **Lure** — enable; hold an emerald block: nearby villagers path toward you (like animals to
    food); swap to emerald ore and deepslate emerald ore; confirm villagers stop following when you
    swap away.
11. **Config screen** — all options appear in the Trading / Zombie Conversion / Misc sections and
    persist.

## Recommended automated tests

### GameTests — Bagged Villager (highest value: data-loss risks)

* **`bagAndUnbagPreservesVillagerData`** — spawn a villager; set profession, level 3, custom trades,
  custom name; simulate the bagging interaction (call the interaction handler or construct the item
  with the villager's NBT the way the bagging path does); then invoke
  `BaggedVillagerItem#useOn` on a platform block via mock player; assert: a villager exists with
  identical profession/level/offers/name, and the held item is now a plain bundle.
* **`unbagFailsOnInvalidPlacement`** — use the item against a side face and under a 1-air-gap
  ceiling; assert FAIL result, no villager spawned, **item unchanged** (regression against villager
  loss).
* **`unbagAirborneIsNoop`** — mock player off the ground; assert PASS and no spawn.
* **`babyAndZombieVillagerRules`** — pin whichever bagging eligibility rules you decide in manual
  test 4.

### GameTests — trading

* **`maxTradesOverrideLocksAtN`** — spawn villager with one offer, config override = 2; call
  `offer.increaseUses()` via the trade path (or simulate trades through the merchant menu) twice;
  assert `isOutOfStock`; third attempt refused.
* **`maxTradesMinusOneNeverLocks`** — override = −1; 50 uses; assert never out of stock.
* **`demandModifierOff`** — trade repeatedly, then restock; assert `demand` stays 0 / price
  unchanged (compare `offer.getCostA()` before/after).

### GameTests — reputation & zombies

* **`globalReputationShared`** — two mock players; apply +rep gossip from player A (villager
  gossip API); assert price discount visible in offers generated for player B only when the config
  is on.
* **`negativeReputationToggle`** — damage villager by mock player with toggle off; assert gossip
  contains no MINOR_NEGATIVE entry.
* **`zombieAlwaysConverts`** — set difficulty context appropriately; zombie kills villager (spawn
  both, set villager health to 1, make zombie attack); assert a zombie villager entity exists and
  no villager death drop. Run with config off as negative control (this one is randomness-tainted
  on Normal; force Easy where vanilla chance is 0 for a clean signal).
* **`fixedCureTime`** — override cure time to 100 ticks; start a cure (apply weakness + golden apple
  via code or set the converting state directly); assert conversion completes within 100+slack
  ticks and NOT before ~100.

### GameTests — lure

* **`villagerFollowsEmeraldHolder`** — mock player holding emerald block 8 blocks from a villager;
  after ~100 ticks assert the villager moved measurably closer; negative control with a stick.
  (Pathfinding tests are flaky — use a corridor structure to constrain movement and generous
  distance thresholds.)

### Unit tests

* None of substance; the logic is entity-coupled. Effort belongs in GameTests.

## ChimericLib helper opportunities

* **Villager fixture builder** — `spawnVillager(helper, pos).profession(X).level(3).offers(...)
  .named("Bob")` returning the entity; needed by every villager test here and throughout Miniblock
  Merchants. This is the single most shareable fixture between the two mods.
* **Mock-player interaction helpers** — shift-right-click an entity with item X; use item on block
  face F — shared with Artificial Heart, Archaeology Tweaks, Minekea.
* **Config override fixture** — as noted in the Banner Tweaks and Beacon & Conduit plans; this mod
  has the most config-gated branches in the suite, so it benefits most.
* **Gossip/reputation assertion helpers** — small wrappers over the gossip container API for
  readable assertions.

## Open questions

* `WorkstationCheckerItem` exists in code but not in the README — live feature, WIP, or dead code?
* Bagging a bundle-with-contents: intended to refuse, or acceptable to destroy contents? Test 4
  needs the answer; my recommendation is refuse unless empty.
* Which entity types are baggable (baby villagers, zombie villagers, wandering traders)?
