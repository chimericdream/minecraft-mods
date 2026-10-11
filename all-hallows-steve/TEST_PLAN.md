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
* `AdvancementGameTest` — the advancement tab: parents, hidden flags, the experience reward, and that each
  one is awarded by the event it describes.
* `PumpkinContentsGameTest` — the contents message for every pumpkin state, and that inspecting changes
  nothing.
* `CiderPressGameTest` — the cider press: a timed pressing's outputs, fuel charges, what stops it (too few
  apples, no bottle, no fuel, a full output, pulling the apples out), the hopper slot rules, and pomace's
  tags, composting, and healing of injured livestock.

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
3. **Wearable decorated pumpkins**
   * Put a decorated pumpkin on your head from the inventory. Right-clicking it places it instead. A lit
     pumpkin (torch or candle) can't go in the helmet slot.
   * In first person, the screen is covered in the pumpkin's color with the stencil's shape cut out to see
     through. With no stencil on the front face the screen is fully covered. Switch to third person and
     the overlay goes away.
   * Bind "Turn Worn Pumpkin" (Controls, All Hallows Steve) and press it: the front face goes north, east,
     south, west. The overlay and the pumpkin on your head both follow. With no pumpkin on, it does nothing.
   * Log out and in: the chosen face is remembered. With a second player watching, they see your pumpkin
     turn, and see the right face when they join after you.
   * The pumpkin item itself never changes: two identical pumpkins still stack after you turned one.
   * Endermen: looking at one while wearing a pumpkin does not anger it. A creaking you are looking at
     keeps moving, as with a vanilla carved pumpkin. An armor stand shows the north face.
   * Config screen (Mod Menu, or the config file): the opacity slider goes from 10% to 100% and an
     uncarved pumpkin becomes see-through at the low end.
4. **Advancements** (open the advancement screen with L; use `/advancement revoke @s everything` to reset)
   * Pick up a pumpkin: the "All Hallows Steve" tab appears with *It's Pumpkin Season*. Craft a Pumpkin
     Carving Station: *Gourd Workshop*.
   * Take a dyed pumpkin from the station: *Not Just Orange*. Take one with a stencil carved in:
     *First Cut Is the Deepest*. (Both at once awards both.)
   * Wear a decorated pumpkin with a stencil on any face: *Pumpkin Head*. Press the turn key: *Better Side*.
   * Wear a decorated pumpkin with nothing carved on any face: *Hey! Who turned out the lights?* It stays
     out of the tab until earned. A lit pumpkin can't be worn, so it awards nothing.
   * Light a pumpkin with a soul, copper or redstone torch, or add a candle to a hollow one: *Lit
     Different*. A regular torch does not; neither does a dispenser doing it.
   * Pick up the Heart, Jigsaw, Spawner, or Structure Block stencil (`/give`): *Rare Cut*.
   * Pick up all 15 stencils (`/give`): the hidden *A Face for Every Occasion* toast, and 100 experience
     points. Missing even Blank leaves it unearned.
   * Check the toast and chat message for each, on both Fabric and NeoForge (the trigger is registered
     separately on each).
5. **Cider press** (give yourself apples, glass bottles, charcoal, coal, and blaze powder)
   * Craft the press from an iron ingot, two sticks, a barrel, and three planks. Place it: the spout is on
     the face toward you, and it turns with the placement direction.
   * Right-click it: the GUI shows a fruit slot, a bottle slot, a fuel slot with a small bar under it, a
     cider slot and a pomace slot. Only apples, bottles, and fuel go into their slots; the two outputs take
     nothing. Shift-clicking sends each item to the right slot.
   * Put in 3 apples, a bottle, and charcoal: the fuel bar fills and the arrow fills over about 10
     seconds, then one cider and one pomace appear. Blaze powder fills the bar fully (20 pressings),
     charcoal and coal 8. With fewer than 3 apples, no bottle, or no fuel, nothing starts.
   * Drink the cider: it restores hunger like a snack, has no effect, and leaves an empty glass bottle.
   * A hopper above feeds apples, bottles, and fuel; a hopper below or beside takes the cider and
     pomace. A hopper beside it never pulls out the apples or the fuel. A comparator reads the contents.
   * Break the press with items inside: everything drops. It mines faster with an axe.
   * Pomace: throw it in a composter (it adds a layer sometimes); feed it to a pig to breed it. Hurt a
     pig, cow, sheep, or horse (fall damage works) and feed it pomace: it heals a heart and the pomace is
     used up, in creative too without using it up. A healthy cow ignores it. A wolf never takes it.
   * Check the textures look right, and that the arrow and fuel bar fill cleanly over their outlines. Repeat on Fabric and NeoForge (the
     NeoForge composter value comes from a data map).
