---
name: mc-marketing-screenshots
description: Capture polished 1920x1080 gallery screenshots of a mod's features (CurseForge/Modrinth pages) with no human at the keyboard, using Fabric's client gametest API. Builds a scene per feature, positions the camera, hides or shows the HUD, and saves PNGs to review with Read. Use when the user wants "screenshots for the CurseForge page", feature shots, or a gallery set. For verifying that a rendering change works, use mc-visual-smoke-test instead; this skill is about presentation.
---

# Minecraft marketing screenshots

Built on the same harness as `mc-visual-smoke-test` (read its "preferred" procedure for the base
setup: temp `FabricClientGameTest` class in the mod's `gametest` source set, `fabric-client-gametest`
entrypoint in `fabric/src/gametest/resources/fabric.mod.json`, run with
`./gradlew :<mod>:fabric:runClientGameTest`, ignore the exit-time `Watchdog` crash). This skill adds
what is different when the goal is *good-looking, full-resolution, multi-scene* output.

One run of one test class produces the whole batch (~2.5 minutes), one PNG per scene, under
`<mod>/fabric/build/run/clientGameTest/screenshots/` named `NNNN_<name>.png`. Read each with the Read
tool and judge framing.

## Plan the shot list first

Read the mod's README feature list and pick one scene per *visible* feature, hero shot first. Things
that work well headless: block/item rows, item-frame lineups, worn-armor/head items (third person),
overlays, lit vs. unlit variants, day vs. night. Things that don't (yet): GUIs that need slot
population (use `context.setScreen(...)` and populate manually; untried), anything needing mouse
interaction, and a "lived-in" hero scene (build or load a real world for those).

## Techniques that matter

- **Resolution:** `context.getInput().resizeWindow(1920, 1080)` right after creating the world.
  Without it screenshots are 854x480.
- **Hide the HUD:** `context.getInput().pressKey(options -> options.keyToggleGui)` (toggle). `Options`
  has no `hideGui` field in 26.2. Press it again to bring the HUD back. Overlays that are HUD layers
  (e.g. a worn pumpkin's camera overlay) only render with the HUD on.
- **Move the camera with the `tp` command, not `snapTo`:**
  `server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "tp @a x y z yaw pitch")`.
  `ServerPlayer.snapTo` moves the player server-side only; the client camera never moves, so every
  scene silently shows the first location. Also set `flying`/`mayfly`, `setNoGravity(true)` and zero the
  delta movement so the creative player doesn't fall.
- **Camera orientation:** yaw 0 = looking south (+z), 180 = north; positive pitch looks down. A block's
  `FACING` is the direction its visible front points, so put the camera on that side of the block
  (camera north of the blocks, blocks `FACING = NORTH`).
- **Space scenes out:** the whole run is one world, so build each scene in its own region (e.g. 60
  blocks apart along X from the spawn position) and `tp` between them. Read the spawn position from
  `server.getPlayerList().getPlayers().getFirst().blockPosition()` instead of hard-coding heights.
- **Time of day:** `time set noon` for color-accuracy shots, `time set midnight` for glow shots,
  `time set 12700` for a sunset sky. Lit blocks barely illuminate bare grass at night; surround them
  with something to catch the light, or use dusk.
- **Third-person:** `context.runOnClient(c -> c.options.setCameraType(CameraType.THIRD_PERSON_FRONT))`
  shows the local player's face; set a head item with `player.setItemSlot(EquipmentSlot.HEAD, stack)`.
  Put lit blocks beside the player so the model isn't a dark silhouette.
- **Item frames:** `new ItemFrame(level, pos, Direction.NORTH)` + `setItem(stack, false)` +
  `level.addFreshEntity(frame)`; `pos` is the air block in front of the wall. Back them with a solid
  wall (deepslate bricks look good) and leave the camera ~8 blocks back so all rows fit.
- **Toasts/chat:** advancement toasts and chat lines appear shortly after a triggering action and fade
  on their own. Take a shot with them (useful for showing advancement names), then
  `context.waitTicks(300)` and take another for a clean version.
- **Settle time:** call `singleplayer.getClientLevel().waitForChunksRender()` then
  `context.waitTicks(40)` before each `takeScreenshot`.
- **Framing rule of thumb:** a row of N one-block objects spaced 2 apart needs the camera about
  `N + 1` blocks back at the default FOV; pitch 8-12 degrees down keeps the sky from dominating.

## Skeleton

```java
// TEMPORARY - DELETE ME
public class ScreenshotClientGameTest implements FabricClientGameTest {
    private static void command(MinecraftServer server, String cmd) {
        server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), cmd);
    }

    private static void camera(MinecraftServer server, double x, double y, double z, float yaw, float pitch) {
        ServerPlayer player = server.getPlayerList().getPlayers().getFirst();
        player.getAbilities().flying = true;
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
        player.setNoGravity(true);
        command(server, String.format("tp @a %f %f %f %f %f", x, y, z, yaw, pitch));
        player.setDeltaMovement(Vec3.ZERO);
    }

    private static void shot(ClientGameTestContext context, TestSingleplayerContext sp, String name) {
        sp.getClientLevel().waitForChunksRender();
        context.waitTicks(40);
        context.takeScreenshot(name);
    }

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext sp = context.worldBuilder().create()) {
            context.getInput().resizeWindow(1920, 1080);
            context.getInput().pressKey(options -> options.keyToggleGui); // hide HUD

            int[] spawn = new int[3];
            sp.getServer().runOnServer(server -> {
                BlockPos p = server.getPlayerList().getPlayers().getFirst().blockPosition();
                spawn[0] = p.getX(); spawn[1] = p.getY(); spawn[2] = p.getZ();
            });
            int sx = spawn[0], sy = spawn[1], sz = spawn[2];

            // Scene 1: build, then camera(...), then shot(...). Repeat per scene, 60 blocks apart.
            sp.getServer().runOnServer(server -> {
                ServerLevel level = server.overworld();
                command(server, "time set noon");
                // level.setBlock(...), block-entity setters, item frames ...
                camera(server, sx + 0.5, sy + 1, sz + 0.5, 0.0F, 10.0F);
            });
            shot(context, sp, "01-name");
        }
    }
}
```

Register it under `"fabric-client-gametest"` in the gametest `fabric.mod.json`, run, then Read each PNG.

## All Hallows Steve recipes (worked on 2026-10-03)

These are the scenes that came out well; reuse the pattern for other mods.

- **Dye color row:** 8 `DECORATED_PUMPKIN` blocks, `x - 7 + i*2`, 6 blocks in front of the camera,
  daytime, same stencil, dye RGBs from vanilla dye colors. Camera 1 block up, pitch 10.
- **Four sides:** 4 pumpkins with all four stencil faces set, `FACING` = N/E/S/W (rotating the block
  rotates the whole stencil set), spaced 2 apart, 4 blocks away, pitch 12.
- **Stencil lineup:** `ModItems.PUMPKIN_STENCIL_ITEMS` (15) in item frames, 5 columns x 3 rows, 1 block
  apart horizontally and 2 vertically, on a 9x6 deepslate-brick wall; camera 8 blocks back, 1 up.
- **Worn pumpkin, third person:** head slot gets a `DECORATED_PUMPKIN` stack with the mod's
  `DYED_COLOR_COMPONENT` and `STENCILS_COMPONENT` set on the stack (all four faces, so whichever
  face is "front" is carved); `time set 12700`; lit pumpkins (`LIT_DECORATED_PUMPKIN`,
  `LIT_DECORATED_PUMPKIN_BLUE`) at +-2 x, `FACING = SOUTH`, facing the camera; `THIRD_PERSON_FRONT`.
- **Worn pumpkin, first person:** same setup, `FIRST_PERSON`, toggle the HUD back on (the overlay is a
  HUD layer). Shot once with advancement toasts, once after `waitTicks(300)`.
- Not captured: carving-station GUI, candle-lit close-up, lit-torch comparison, hero scene. The user
  planned to take those from a pre-made world by hand.

## Clean up (mandatory)

- Delete the temp test class and its `fabric-client-gametest` entry in the gametest `fabric.mod.json`
  (leave other entries alone).
- The screenshots live in `<mod>/fabric/build/run/clientGameTest/screenshots/` (gitignored). Tell the
  user where they are, and don't delete that directory until they've copied out what they want.

## Reviewing results

Read every PNG. Common failures, in the order they bit us: every scene identical (teleport didn't
sync, see `tp` above); sky filling half the frame (increase pitch or move the camera down); edge
objects cropped (move the camera back); bottom row cut off (camera too high or too close); a dark
silhouette (nothing lighting the subject at night); a missing overlay (HUD still hidden). Per the
repo's visual-testing feedback, after two runs that don't settle a *judgment call*, stop and ask the
user rather than iterating alone; mechanical bugs (like the identical-scenes one) are fine to fix and
re-run.
