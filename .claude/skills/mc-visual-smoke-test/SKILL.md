---
name: mc-visual-smoke-test
description: Automated in-game VISUAL verification for this Minecraft mod repo (MC 26.2, Architectury) with no human at the keyboard — create a world, build a scene, take a screenshot, and read the PNG back as an image. Use when a change affects RENDERING (block/entity/BER models, block colors, GUI-in-world, textures) and "it compiles" or a headless GameTest is not enough proof. NOT for logic-only changes (use JUnit/GameTest instead).
---

# Minecraft headless visual smoke test

Compiling — and even a headless GameTest — does not prove that something *renders* correctly. This
skill drives the real client with no user input to build a scene and capture a screenshot, then reads
the PNG back with the Read tool to visually confirm the result.

**Preferred approach: Fabric's `fabric-client-gametest-api-v1`.** It runs in its own isolated run
directory (`<mod>/fabric/build/run/clientGameTest/`), never touches the developer's own dev-run
`options.txt`/saves, defaults to a clean flat world, and — critically — neutralizes the game window's
focus/iconify handling so it never grabs real OS focus or captures the mouse the way a normal launched
client does. Every fabric subproject in this repo already has this enabled
(`enableClientGameTests = true` in the root `build.gradle`'s `fabricApi.configureTests` block), so no
project setup is needed — just write the temporary test class. **Prefer this over the older manual
`runClient` + tick-hook approach below**, which drives a real, focusable game window and both requires
and later must revert `options.txt` (`pauseOnLostFocus`), and previously caused real mouse-focus loss
for the user while it ran.

This is inherently **temporary throwaway code**. The single most important rule: **remove the test
class and its `fabric-client-gametest` entrypoint entry when you are done.**

## Procedure (preferred: `fabric-client-gametest-api-v1`)

### 1. Add a temporary client gametest class

Create a temp class in the mod's existing `gametest` source set (the same one server-side `@GameTest`
classes already live in, e.g. `log-all-the-things/fabric/src/gametest/java/.../fabric/test/`), clearly
marked `// TEMPORARY - DELETE ME`, implementing `FabricClientGameTest`:

```java
package com.chimericdream.<mod>.fabric.test;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

public class SmokeTestClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
            singleplayer.getServer().runOnServer(server -> {
                // Build the scene here: server.overworld().setBlock(pos, state, flags), block-entity
                // mutation, entity.teleportTo(...) — same as the manual approach's "server thread" step.
            });

            singleplayer.getClientLevel().waitForChunksRender(); // reliable, not a fixed tick guess
            context.waitTicks(20); // let lighting settle

            context.takeScreenshot("smoke-test"); // returns the Path directly
        }
    }
}
```

### 2. Register the entrypoint

Add it to the `fabric-client-gametest` entrypoint list in the mod's test-only
`fabric/src/gametest/resources/fabric.mod.json` (alongside any existing `fabric-gametest` server-side
tests — same file, same test-only mod, different entrypoint key):

```json
"entrypoints": {
    "fabric-gametest": [ ... ],
    "fabric-client-gametest": [
        "com.chimericdream.<mod>.fabric.test.SmokeTestClientGameTest"
    ]
}
```

### 3. Build the scene / position the camera

Inside `singleplayer.getServer().runOnServer(server -> ...)`, mutate the world exactly as in the manual
approach (`level.setBlock`, block-entity setters, `entity.teleportTo(...)` to position the camera).
Note directional blocks: a block's `FACING` is the direction the *placer looked*, so the visible front
faces the **opposite** way — put the camera on the opposite side of `FACING` to see the front/contents.
The world defaults to flat (grass over dirt/stone, normal sea-level-ish height) with a fixed seed — no
need to guess terrain height or query heightmaps the way a raw `createFreshLevel` call requires.

### 4. Screenshot

`context.takeScreenshot("name")` returns the `Path` directly — no need to hunt for it under
`run/screenshots/`. It lands under `<mod>/fabric/build/run/clientGameTest/screenshots/`. Read it with
the Read tool (it renders as an image). The runner closes the game automatically once the test method
returns — no manual `mc.stop()` needed.

### 5. Clean up

- Delete the temp test class.
- Remove its entry from the `fabric-client-gametest` entrypoint list in `fabric.mod.json` (leave any
  other `fabric-gametest`/`fabric-client-gametest` entries untouched).
- Delete `<mod>/fabric/build/run/clientGameTest/` (isolated build output, safe to remove wholesale —
  it's never the developer's own dev-run directory).

### Run it

```
./gradlew :<mod>:fabric:runClientGameTest
```

## Procedure (fallback: manual `runClient` + tick hook)

Only reach for this if a scenario genuinely doesn't fit the client-gametest API (e.g. something that
needs the *real* `Minecraft.getInstance()` launched via the normal dev run config for some reason).
Expect it to visibly steal window focus and mouse capture while it runs — confirmed by the user after
repeated real-world use — so warn them before running it, and prefer fixing the constraint instead of
defaulting to this path.

### 1. Add a temporary client-tick hook

Create a temp class under the relevant mod's client package, e.g.
`minekea/common/.../client/SmokeTest.java`, clearly marked `// TEMPORARY - DELETE ME`. Register an
Architectury `ClientTickEvent.CLIENT_POST` listener and init it from the mod's
`...Client.onInitializeClient()` (or the common client init). Drive everything off a small state
machine keyed on a tick counter so each phase runs once.

### 2. Prep the run directory (do this before launching)

In the target loader's run dir (e.g. `minekea/fabric/run/options.txt`), set:

```
pauseOnLostFocus:false
```

The launched window is never focused; with the default `true`, the integrated server pauses and your
scene never builds. **Record the original value and restore it afterward.**

### 3. Open a world (at the title screen)

MC 26.x has no `mc.screen` field — gate on `mc.gui.screen() instanceof TitleScreen`.

`WorldPresets::createTestWorldDimensions` is **not** a small/void world — in practice it generated a
normal-bounds Overworld (-64 to 319) with real terrain, and `Heightmap.Types.WORLD_SURFACE` queries
against it returned wildly inconsistent, unusable values (seen: 233–332 across runs on presumably-air
columns). Don't rely on it for placement height. A fixed, comfortably-high absolute Y (e.g. 250, clear
of all normal terrain, well under the 319 ceiling) is more reliable than any heightmap-relative
calculation here.

Fresh flat creative world:
```java
mc.createWorldOpenFlows().createFreshLevel(
    "smoke-test",
    new LevelSettings("smoke-test", GameType.CREATIVE,
        LevelSettings.DifficultySettings.DEFAULT, true, WorldDataConfiguration.DEFAULT),
    WorldOptions.testWorldWithRandomSeed(),
    WorldPresets::createTestWorldDimensions,
    mc.gui.screen());
```
Or open the existing dev save (back it up first): `mc.createWorldOpenFlows().openWorld("New World", onFail)`.

### 4. Build the scene on the server thread

Get the integrated server (`mc.getSingleplayerServer()`) and run mutations inside `server.execute(...)`
so they land on the server thread and bypass permission checks:
- `level.setBlock(pos, state, flags)`, block-entity mutation, `entity.teleportTo(...)`.
- Vanilla commands:
  `server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(), "time set noon")`.

Position the **camera** deliberately (teleport the player). Note directional blocks: e.g. a block's
`FACING` is the direction the *placer looked*, so the visible front faces the **opposite** way — put
the camera on the opposite side of `FACING` to see the front/contents.

**Creative-mode players fall unless given flight**, and a long, uncontrolled fall in an unfamiliar test
world can end with a void-safety teleport back to spawn well before your screenshot tick, silently
moving the camera out from under you (server-side `position()` will still report where you *told* it to
teleport to — the divergence only shows up client-side). After teleporting, pin the player in place:
```java
player.getAbilities().flying = true;
player.getAbilities().mayfly = true;
player.onUpdateAbilities();
player.setNoGravity(true);
player.setDeltaMovement(Vec3.ZERO);
```

For aiming the camera at a specific point rather than guessing yaw/pitch by hand, compute it the same
way `GameTestPlayers.lookAt` does (see `chimeric-lib`'s test fixtures) rather than hand-picking degrees:
```java
double xd = target.x - from.x, yd = target.y - from.y, zd = target.z - from.z;
double horizontalDistance = Math.sqrt(xd * xd + zd * zd);
float pitch = (float) (-(Math.atan2(yd, horizontalDistance) * 180.0 / Math.PI));
float yaw = (float) (Math.atan2(zd, xd) * 180.0 / Math.PI) - 90.0F;
```
(Positive pitch looks **down**, negative looks **up** — the opposite of the intuitive guess.)

### 5. Screenshot, then quit

Wait ~200 in-world ticks for chunks/lighting to settle, then:
```java
Screenshot.grab(mc.gameDirectory, "smoke-test.png", mc.gameRenderer.mainRenderTarget(), 1, cb);
```
~40 ticks later call `mc.stop()`. The PNG lands in `<loader>/run/screenshots/`. Read it with the Read
tool (it renders as an image).

### 6. Clean up

- Delete the temp class and its init call.
- Restore `options.txt` (`pauseOnLostFocus`).
- Delete the screenshot PNG (it is untracked debug output).

## Notes / gotchas

- On exit you will likely see a `java.lang.Error: Watchdog (Client shutdown from post-main)` crash —
  **ignore it, for either procedure above.** It's a known non-daemon thread leak in a shared
  dependency, not your code or a real failure. The game did fully close. See `docs/MC-26.2-NOTES.md`.
- `--args="--quickPlaySingleplayer ..."` does **not** reach the game through Loom's `runClient` — don't
  bother; open the world from the tick hook instead (manual procedure only).
- Run via the loader's client run task: `./gradlew :<mod>:fabric:runClientGameTest` (preferred) or
  `./gradlew :<mod>:fabric:runClient` (manual fallback).
