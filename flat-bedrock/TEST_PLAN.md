# Test Plan — Flat Bedrock

Flat Bedrock is a server-side world-generation tweak: a single mixin (`FlatBedrockMixin`) adjusts the
surface rules that place bedrock so the floor of the Overworld/Nether and the Nether roof generate as
one clean layer instead of vanilla's noisy multi-block gradient. There are no items, blocks, or
config. Everything testable is about **what newly generated chunks look like**.

## Test conventions

Follow the Hopper X-Treme pattern for automated tests: test classes in
`fabric/src/main/java/com/chimericdream/flatbedrock/fabric/test/`, registered under the
`fabric-gametest` entrypoint in `fabric/src/main/resources/fabric.mod.json`. GameTests run in a
superflat test world, which **does not exercise normal surface rules** — so the world-gen assertions
below need the "scan real generated chunks" approach described in the automated section, not
structure-based GameTests.

## Manual test plan

Setup: create a **new** default-generation world with the mod installed (Fabric first, then repeat on
NeoForge). This mod only affects newly generated chunks, so never test in a pre-existing world unless
specifically testing the mixed-world case.

1. **Overworld floor**
   * Dig or `/tp` to the bottom of the world in several biomes (plains, ocean, mountains, deep dark).
   * Expect exactly one layer of bedrock at the minimum build height and *no* bedrock in the 4 layers
     above it. `/fill` a 16×5×16 slice with glass just above the floor to visually confirm.
2. **Nether floor and roof**
   * Same check at the bottom of the Nether.
   * At the roof: the top bedrock layer should be a single flat layer with no jagged bedrock below it.
     Confirm the layer sits at the vanilla roof height (build limit expectations for roof-travel
     setups matter to players).
3. **Chunk seams**
   * Fly a few thousand blocks in one direction with an elytra, spot-checking the floor; look for
     stray vanilla-pattern bedrock where chunk generation may have raced mod init.
4. **Mixed old/new worlds**
   * Open a vanilla-generated world with the mod added; verify old chunks keep jagged bedrock, new
     chunks get flat bedrock, and there are no crashes or holes at the boundary between them.
5. **The End (regression)**
   * The End has no bedrock floor; verify end generation (main island, obsidian pillars, gateway
     bedrock blocks) is untouched.
6. **Interaction with other world-gen mods**
   * In the full suite modpack, generate a new world and repeat check 1 — the surface-rule injection
     must survive other mods touching world generation.

## Recommended automated tests

### Chunk-scan GameTests

GameTest structures can't drive real terrain generation, but a GameTest *can* ask the server to
generate chunks in another dimension and inspect them:

* **`overworldFloorIsFlat`** — from a GameTest (or a dedicated test-only command triggered by a
  GameTest), force-load an untouched far-away chunk (e.g. via
  `server.overworld().getChunk(x, z, ChunkStatus.FULL)`), then assert for every column in the chunk:
  `getBlockState(minY) == bedrock` and `getBlockState(minY + 1..minY + 4) != bedrock`.
* **`netherFloorIsFlat`** / **`netherRoofIsFlat`** — same scan in the Nether: exactly one bedrock
  layer at the floor and one at the roof, nothing in between except normal terrain.
* **`endUnaffected`** — generate an End chunk near the main island; assert no bedrock appears
  (other than what vanilla places for gateways, if in range).
* Note for the implementer: these tests are slow (real chunk gen). Use one shared `@BeforeBatch`
  style setup or a single test per dimension, and pick chunk coordinates deterministically from the
  test seed so failures are reproducible.

### Config-free mixin smoke test

* **`mixinApplies`** — assert at test-startup that the surface-rule injection actually applied
  (e.g. the mixin sets a static "applied" flag, or reflectively verify the modified rule source is
  present in the noise settings). This catches silent mixin failures after Minecraft updates, which
  is this mod's primary failure mode.

### Unit-ish test

* If the flattening logic builds a replacement `SurfaceRules.RuleSource`, extract that construction
  into a static method and assert its shape (a single `verticalGradient`/condition at min-Y rather
  than vanilla's randomized gradient). This pins the intent without generating chunks.

## ChimericLib helper opportunities

* **Chunk-scan assertion helper**: `assertColumnLayers(chunk, x, z, Map<Integer, Block>)` and a
  "generate remote chunk and hand it to a consumer" utility. Flat Bedrock is the only current
  world-gen mod, but any future world-gen feature (Minekea's README floats world gen ideas, and the
  jdcrafte scaffold mentions it) would reuse this.
* **Dimension-hopping GameTest fixture**: helpers to run assertions against a non-test dimension
  (`getChunkIn(Level, ...)`), since vanilla GameTest assumes everything happens inside the structure
  bounds.

## Open questions

* Should the flat layer's Y-position or thickness ever be configurable? If yes, tests should be
  parameterized now to make that cheap later.
