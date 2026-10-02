# Test Plan — All Hallows Steve

## Automated coverage

GameTests live in `fabric/src/gametest/java/.../fabric/test/` (an isolated source set, never shipped) and
are listed under `fabric-gametest` in `fabric/src/gametest/resources/fabric.mod.json`. Run them with
`./gradlew :all-hallows-steve:fabric:runGameTest`, and run `bun run verify:gametests` after adding a test
class.

* `PlayerPumpkinGameTest` — torches and shears on decorated and vanilla pumpkins.
* `DispenserPumpkinGameTest` — the same, driven by dispensers, including candles.
* `CandlePumpkinGameTest` — candle-lit pumpkins: adding candles, snuffing, relighting, shears, light
  levels, overlay suffixes, drops, and saving and loading.
* `PumpkinContentsGameTest` — the contents message for every pumpkin state, and that inspecting changes
  nothing.

Mock players have no network connection, so `PumpkinContents.messageSink` is swapped for a recorder in
tests (`PumpkinMessageRecorder`). What really appears above the hotbar needs the manual pass below.

## Manual test plan

Setup: creative world, a few decorated pumpkins (carved and dyed in the Pumpkin Carving Station), every
kind of torch, several colors of candle, flint and steel, shears, and a dispenser. Repeat on Fabric and
NeoForge.

1. **Candle-lit pumpkins**
   * Sneak and use a candle on a hollow pumpkin: it lights with one candle. Repeat to four candles; a
     fifth does nothing to the pumpkin.
   * Light levels are 3, 6, 9, 12 (check with F3 or a light-level mod). The glow overlay changes with each
     candle.
   * Empty hand (not sneaking) snuffs it; the overlay goes back to the plain unlit one and the light
     goes out. Flint and steel relights it. Adding a candle to a snuffed pumpkin leaves it snuffed.
   * Shears turn it back into a plain pumpkin and drop each candle in the color it went in with; dye and
     carving stay. Breaking the pumpkin drops the pumpkin plus the candles.
   * A torch can't be added to a candle pumpkin, and a candle can't be added to a torch pumpkin.
   * Dispenser: a candle adds one, flint and steel relights, shears remove, and a full pumpkin makes the
     dispenser fail rather than drop the candle.
2. **Pumpkin contents message**
   * Sneak with an empty hand on a hollow pumpkin: "Empty". On each torch pumpkin: the torch's name. On a
     candle pumpkin: "N candle(s) (lit)" or "(snuffed)". Nothing about the pumpkin changes.
   * The same message appears after you add a candle, snuff, relight, light with a torch, or shear.
   * A dispenser doing any of those shows no message.
3. **Wearable decorated pumpkins** — _to be written when that idea is built._
