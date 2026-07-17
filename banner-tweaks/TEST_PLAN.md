# Test Plan — Banner Tweaks

Banner Tweaks raises the banner pattern-layer cap from vanilla's 6 to a configurable 1–32 (default
12). The moving parts: `BannerPatternsComponentMixin` (component-level cap),
`LoomMenuMixin`/`LoomScreenHandlerMixin` (applying patterns past 6 in the loom),
`BannerBlockEntityRendererMixin` + render-state mixins (drawing tall stacks),
`MapStateMixin` (banner markers on maps), `BannerTweaksConfig` (YACL,
`config/bannertweaks.json5`), and a **login-time server→client sync** of the layer limit
(`ModPackets.BANNER_LAYER_LIMIT`, per-loader `LoginMixin` + `ServerNetworking`). The sync is the
most fragile piece and deserves the most attention.

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/bannertweaks/fabric/test/`, registered under the
`fabric-gametest` entrypoint, structures under
`common/src/main/resources/data/bannertweaks/gametest/structure/`. Loom logic and the component cap
are server-side and GameTest-able; renderer mixins are client-only and stay manual (or move to
`fabric-client-gametest-api-v1` later). Config-sensitive tests must set and restore
`BannerTweaksConfig` values programmatically.

## Manual test plan

Setup: creative world; banners, dyes, loom, cartography table, maps. Fabric full pass, NeoForge
smoke pass.

1. **Loom past vanilla cap** — apply patterns one at a time in the loom up to the default limit
   (12): the loom must keep accepting patterns at 7–12 and refuse the 13th (UI shouldn't show
   pattern options / apply button once at cap — verify the exact refusal UX).
2. **Config bounds** — set max layers to 1: loom accepts a single pattern only. Set to 32: verify
   you can genuinely stack 32. Confirm changes persist in `bannertweaks.json5` and take effect
   (note whether relog/world reload is required).
3. **Rendering** — a 12+ layer banner renders all layers, in order, on: placed banner, wall banner,
   held item, item frame, and shield (apply banner to shield!). Look for z-fighting or missing top
   layers — the render mixins exist precisely because vanilla truncates.
4. **Banner on map** — mark a 12-layer banner on a map (use banner on map or right-click banner with
   map in hand per current vanilla flow); the map marker should carry the banner's name/color
   without corruption (`MapStateMixin`).
5. **Copying & crafting** — copy an over-6-layer banner onto a blank banner in the crafting grid
   (vanilla copy mechanic): all layers must survive. Also test cauldron washing (removes top layer)
   on a 12-layer banner.
6. **Server sync** — dedicated server with limit 20; client with local config 12 joins: the loom on
   that client must allow 20 layers (server value wins). Then join a *vanilla* server with the mod
   only on the client: loom must fall back to vanilla behavior without crashing (or the mod's
   defined fallback — pin it).
7. **Mixed clients** — un-modded client on modded server: over-limit banners should still be
   *visible* (vanilla clients render what they're given — verify how >6-layer component data reaches
   them) and must not kick the client with a component-validation error. This is the highest-risk
   compatibility case.
8. **Persistence** — place a 12-layer banner, reload the world: layers intact. Break it: dropped
   item retains all layers.

## Recommended automated tests

### GameTests — component & loom logic

* **`bannerComponentAcceptsUpToConfiguredLayers`** — build a `BannerPatternLayers` (or place a
  banner block and write its component) with 12 layers; assert all 12 survive a set/get round-trip
  on the block entity, and survive `saveAdditional`/`loadAdditional` (write NBT, reload BE).
* **`bannerComponentRespectsCap`** — with config max = 8, attempt to construct/apply 10 layers via
  the loom path; assert the result has exactly 8 (or the apply is refused — pin the mixin's actual
  contract).
* **`loomAppliesSeventhPattern`** — instantiate the loom menu server-side
  (`new LoomMenu(...)` with a `SimpleContainer`), load a 6-layer banner + dye, select a pattern,
  and assert the output slot yields a 7-layer banner. This exercises `LoomMenuMixin` without a
  client. Repeat at `config.max` and `config.max + 1` (expect refusal).
* **`configChangeTakesEffectWithoutRestart`** — set limit 6 → assert refusal at 7; set limit 12 in
  the same test → assert acceptance; guards against the mixin caching the limit at class-load.

### GameTests — map markers

* **`mapMarkerSurvivesManyLayers`** — create a `MapItemSavedData`, add a banner marker pointing at a
  12-layer banner block, serialize/deserialize the map state, assert marker intact and named
  correctly.

### Sync tests

* **`loginPacketCarriesConfiguredLimit`** — unit-level: encode the `BANNER_LAYER_LIMIT` payload with
  limit 20, decode, assert 20. Trivial but pins the wire format on both loaders.
* Full login-flow testing (client receives and applies the limit) requires a client gametest or the
  manual pass (test 6). Document the manual dependency in the test class javadoc.

### Unit tests

* If the cap logic is a pure function (clamp/validate layer list against limit), extract and
  table-test: 0, limit−1, limit, limit+1, 32, and vanilla-6 interactions.

## ChimericLib helper opportunities

* **Config override fixture** — snapshot/mutate/restore YACL config inside a test (shared need with
  Beacon & Conduit Tweaks, Villager Tweaks, Shulker Stuff).
* **Menu/screen-handler test harness** — "open menu M server-side with container contents C, click
  slot/select recipe, assert output slot" — needed here (loom), Shulker Stuff (dye station), Hopper
  X-Treme (filter screens), Minekea (crates). This is probably the single highest-leverage helper
  in the whole suite.
* **Networking round-trip helper** — encode/decode assertion for custom payloads, so every mod with
  a packet (Banner Tweaks, Minekea's `network` package) pins its wire format the same way.
* **BE NBT round-trip helper** — `assertSurvivesReload(blockEntity)` writing then re-reading NBT;
  needed by nearly every block-entity mod in the suite.

## Open questions

* Vanilla-client-on-modded-server: is the >6-layer component data safe for unmodded clients, or
  does the design assume all clients have the mod? Determines how aggressive sync tests must be.
* What is the intended loom UX at cap (hide patterns vs. block output)? Pin before writing the
  refusal assertions.
