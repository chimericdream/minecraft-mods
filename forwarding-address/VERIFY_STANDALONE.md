# Forwarding Address 1.0: standalone verification

Run this against the built jars in a normal launcher instance (Prism, the vanilla launcher, etc.), not the dev
environment. It covers what the automated tests could not: NeoForge, a dedicated server, and the
removed-mod flow. Each step says what to do and what you should see. Stop at the first failure and note the step.

Delete this file once the release is cut (it is not committed).

## 0. Setup

Build the jars: `./gradlew :forwarding-address:fabric:build :forwarding-address:neoforge:build`
(run from the repo root). The jars are:

* `forwarding-address/fabric/build/libs/forwardingaddress-fabric-26.2-1.0.0-beta.0.jar`
* `forwarding-address/neoforge/build/libs/forwardingaddress-neoforge-26.2-1.0.0-beta.0.jar`

(Use the plain jar, not `-raw` or `-sources`. The version in the name changes once you set 1.0.0.)

Make four instances, Minecraft 26.2, Java 25:

| Instance | Loader | Mods |
| --- | --- | --- |
| `fa-fabric` | Fabric | Fabric API, Architectury API, chimeric-lib, Forwarding Address, **Mod A** |
| `fa-neoforge` | NeoForge | Architectury API, chimeric-lib, Forwarding Address, **Mod A** |
| `fa-server` | Fabric dedicated server | same as `fa-fabric` |
| `fa-client-b` | Fabric | same as `fa-fabric`, used for the mismatch check |

**Mod A** is any mod with a few plain blocks (a stairs-like block with `facing`/`half`, a block with a boolean
such as `waterlogged` or `lit`). A mod from this repo works. You will remove it later. Pick two of its blocks:
call them `modA:one` and `modA:two`. Also pick a block entity block from it if it has one (`modA:chest`).
**Mod B** is a mod that has the target blocks. A vanilla block works as a target if you have no second mod
(for example `minecraft:birch_stairs`); the steps below use vanilla targets.

Launch each instance once so the config folder exists, then close it.

Check: the log (`logs/latest.log`) has a line from Forwarding Address with no errors and a `stub config`
fingerprint (`stub config 00000000` or similar when there are no stubs).

## 1. Smoke test, both loaders

Do this on `fa-fabric`, then repeat on `fa-neoforge`.

1. Create a creative world. Place `modA:one` and `modA:two`, a few of each, including rotated ones.
   Put some in a chest, hold one, and drop one on the ground. Put one in an item frame.
2. Save and quit. Close the game.
3. Create `config/forwardingaddress/migrations/test.json`:

   ```json
   {
     "blocks": [
       { "from": "modA:one", "to": "minecraft:birch_stairs" },
       { "from": "modA:two", "to": "minecraft:stone" }
     ]
   }
   ```

   (Replace `modA` with the real namespace and use real IDs.)
4. Start the game and load the world.

Expected:

* [ ] Placed blocks turn into the targets within a few seconds, with `facing` and `half` kept on the stairs.
* [ ] Chest contents, the held item, the dropped item and the framed item are all converted.
* [ ] The log says it converted blocks and does not print errors.
* [ ] Quit and reload: nothing converts a second time and nothing reverts.

## 2. Bad config does not break the game

On either loader:

1. Add `config/forwardingaddress/migrations/broken.json` containing `{not json`.
2. Add a second file `partly.json` with one good entry and one entry whose `from` is `NOT AN ID`.
3. Edit `config/forwardingaddress/settings.json` so `maxConversionsPerTick` is `-5`, then `999999`.
4. Start the game.

Expected:

* [ ] The game starts and the world loads.
* [ ] The log names `broken.json` as unreadable and names the bad entry in `partly.json`; the good entry still works.
* [ ] The settings value is clamped to a sane range, not applied raw.
* [ ] Delete the two bad files and restore settings afterwards.

## 3. The dump command

On `fa-fabric` with Mod A still installed and `modA` blocks registered:

1. In a world with cheats, run `/forwardingaddress dump modA`.
2. Open `config/forwardingaddress/dumps/modA.json`.

Expected:

* [ ] The chat message says how many blocks were written and where.
* [ ] Every `modA` block is listed with its properties and every value, and each `to` is blank.
* [ ] A block entity block has an `unsupported` note and no `stub` section.
* [ ] `/forwardingaddress dump nosuchmod` fails with "No blocks are registered".
* [ ] `/forwardingaddress dump Bad Name!` is refused as an invalid namespace.
* [ ] Starting the game with the dump still in `dumps/` changes nothing (the folder is never loaded).
* [ ] In a world with cheats off, or on the server as a non-operator, the command is not available or is refused.

## 4. The removed-mod flow (the main check)

On `fa-fabric`. Use a fresh world so earlier tests don't interfere.

1. With Mod A installed and **no** Forwarding Address config, build: several `modA:one` blocks in different
   rotations and `half` values, a few `modA:two`, and a chest holding `modA` items. Drop one `modA` item and put
   one in an item frame. Also put a `modA` item in your own inventory and armor/offhand if it has such items.
   Save and quit.
2. Run `/forwardingaddress dump modA` once before you remove the mod, as a pack maker would. (Do this in step 1's
   world before quitting.) Copy `dumps/modA.json` to `migrations/modA.json`.
3. In that file fill in `to` for the entries you want (`modA:one` -> `minecraft:birch_stairs`,
   `modA:two` -> `minecraft:stone`). Delete the `chest` entry if it was flagged unsupported. Leave one entry's
   `to` blank.
4. Quit. **Remove Mod A's jar from `mods/`.**
5. Start the game and load the world.

Expected:

* [ ] The world loads without a "missing registry entries" or similar error screen.
* [ ] The log shows the stub registered for each entry with a `to`, and a `stub config` fingerprint that is not zeros. Note it.
* [ ] The blocks you migrated convert to the targets, state kept (`facing`, `half`, boolean properties).
* [ ] The entry with a blank `to` is not registered and is ignored with a log line.
* [ ] Inventory, chest contents, the item entity and the framed item convert.
* [ ] An unconverted stub (use `/setblock ~ ~ ~ modA:one` before it converts, or place one while the queue is
      busy) shows the missing-model look, has "Retired block" in its name, breaks by hand and drops an item that converts.
* [ ] Quit and reload: still fine, nothing converts twice.
* [ ] Put Mod A's jar **back** and start again: no duplicate-registration crash (the stub is skipped because the mod owns the namespace) and the real blocks are available.
* [ ] Remove the jar again and delete the JSON stub entry (keep the migration only, no `stub` section). Load the world. Because the migration only renames (no mappers), the world should still load and convert (this is the saved-data rewrite).

## 5. NeoForge

Repeat step 4 on `fa-neoforge`. This is the main unverified piece: NeoForge has only been compiled for stubs.

* [ ] A stub under a namespace that no installed mod owns registers and the world loads.
* [ ] Blocks convert; dropped items and item frames convert after a reload (this uses `EntityJoinLevelEvent`).
* [ ] No errors in the log about registering under a foreign namespace.
* [ ] Putting Mod A back does not crash.

## 6. Dedicated server and client mismatch

On `fa-server` plus `fa-client-b` (Fabric).

1. Give the server the stub file from step 4 and start it. Note the `stub config` fingerprint in its log.
2. Give `fa-client-b` the **same** file. Join. Expected: join succeeds; the client log shows the same fingerprint.
   - [ ] Join works and stubs and conversion behave as in step 4.
3. Change one entry in the client's file, or delete it. Join again.
   - [ ] The join is refused with a registry mismatch that names the stub IDs, and the two logs show different fingerprints.
4. Run `/forwardingaddress dump modA` from the server console and as a non-operator player.
   - [ ] The console works and writes to the server's `config/forwardingaddress/dumps/`; a non-operator is refused.

## 7. Large backlog

On either loader, in a world where thousands of blocks need converting (fill a large area with `/fill` of a
`modA` block, a few hundred thousand, then migrate it):

* [ ] The game stays responsive; conversion happens over time, a bounded number per tick.
* [ ] After the backlog drains, the log reports the total and nothing is left unconverted.
* [ ] Lowering `maxConversionsPerTick` in `settings.json` and restarting slows the drain.

## 8. Duplicate guard

* [ ] Add a stub entry whose `from` uses `minecraft:` as the namespace: it is refused and logged, the game starts.
* [ ] Add a stub for an ID that another installed non-vanilla mod already registers: startup stops with a message
      naming the ID and the file. (Remove the entry afterwards.)

## 9. Hopper X-Treme

If you are migrating Hopper X-Treme onto this mod: install both, load a world made before the migration, and
confirm the hoppers convert with their filter slot and inventory intact. Note any gap.

## When you are done

Tick the matching boxes in `TEST_PLAN.md`, delete this file, then run `mc-cut-release`.
Anything that failed: note the step number, the loader, and the relevant lines of `logs/latest.log`.
