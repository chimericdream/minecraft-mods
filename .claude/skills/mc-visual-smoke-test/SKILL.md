---
name: mc-visual-smoke-test
description: Automated in-game VISUAL verification for this Minecraft mod repo (MC 26.2, Architectury) with no human at the keyboard — create a world, build a scene, take a screenshot, and read the PNG back as an image. Use when a change affects RENDERING (block/entity/BER models, block colors, GUI-in-world, textures) and "it compiles" or a headless GameTest is not enough proof. NOT for logic-only changes (use JUnit/GameTest instead).
---

# Minecraft headless visual smoke test

Compiling — and even a headless GameTest — does not prove that something *renders* correctly. This
skill drives the real client with no user input: boot to the title screen, open/create a world, build
a scene on the server thread, screenshot the framebuffer, quit, and read the PNG back with the Read
tool to visually confirm the result.

This is inherently **temporary throwaway code**. The single most important rule: **remove the hook
when you are done.** The only trace a past run left behind was an untracked
`minekea/fabric/run/screenshots/armoire-smoke-test.png` — don't leave debug code in the tree.

## Procedure

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
  **ignore it.** It's a known non-daemon thread leak in a shared dependency, not your code or a real
  failure. The game did fully close. See `docs/MC-26.2-NOTES.md`.
- `--args="--quickPlaySingleplayer ..."` does **not** reach the game through Loom's `runClient` — don't
  bother; open the world from the tick hook instead.
- Run via the loader's client run task (e.g. `./gradlew :minekea:fabric:runClient`).
