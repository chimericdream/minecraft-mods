# Opus — handoff notes

Written at the end of the first overnight session (2026-10-06), when the sandbox could not reach the Fabric /
Architectury / NeoForge Maven hosts, and updated after the first real build on the laptop. Delete this file once
the items below are done.

## State of things

| Layer | Status |
|---|---|
| `core/` (frontmatter, Markdown → document model, book loading, links, tags, search, layout engine, recipe panels/specs, `ValidateBook`) | **Written and tested.** 89 JUnit tests pass under Gradle (`./gradlew :opus:fabric:test`). |
| `client/`, `item/`, `component/`, `OpusMod`, loader client entry points | **Compiles** for common, Fabric and NeoForge, and the **book screen has been seen running on Fabric** through a temporary headless client game test (see below). |
| Gradle wiring (`common/fabric/neoforge build.gradle`, `gradle.properties`) | **Verified** for `shadowJar` on both loaders: commonmark and SnakeYAML are bundled and fully relocated to `com.chimericdream.opus.shadow.*` (no unrelocated `org/commonmark` or `org/yaml`, no `module-info`), and the bytecode references the relocated packages. NeoForge's *dev runtime* handling (`forgeRuntimeLibrary`) is untested until a NeoForge `runClient`. |
| In-game behaviour | **Fabric, headless:** at 1920x1080 / GUI scale 2 and 1280x720 / GUI scale 3 the guide opens and navigates; text column cap, clickable breadcrumbs, Back/`<`/`>`, sidebar and search (including the clear button and right-click clear), link and item tooltips, all recipe panels, item widgets and tables were checked from screenshots. **Not yet checked:** NeoForge, a dedicated server, the item and F7 key in a real world, live window resize, and a person actually reading it. |

## What the headless check was

A temporary `FabricClientGameTest` (deleted afterwards, as `mc-visual-smoke-test` requires) created a world, resized the
window, called `OpusClient.openBook("opus:guide", page)`, and used `getInput()` (`scroll`, `setCursorPos`,
`pressMouse`, `typeChars`, `pressKey`) plus `takeScreenshot`. It needs `opus/fabric/src/gametest/java/...` and a
`gametest/resources/fabric.mod.json` with a `fabric-client-gametest` entrypoint, and runs with
`./gradlew :opus:fabric:runClientGameTest`; the exit-time `Watchdog` crash is the known harmless one. Cursor
positions are window pixels, so at GUI scale 2 a logical coordinate is doubled.

## First steps

1. ~~`./gradlew :opus:common:compileJava`~~ Done, along with tests, `:opus:neoforge:compileJava` and both `shadowJar` tasks.
2. Walk `TEST_PLAN.md` by hand once on Fabric (`./gradlew :opus:fabric:runClient`), especially the in-world parts the headless check skipped: the `opus:book` item and F7.
3. NeoForge dev run (`:opus:neoforge:runClient`): the libraries use `forgeRuntimeLibrary(implementation(...))` in `neoforge/build.gradle` (pattern from the root build for the quiltmc parsers). If NeoForge cannot see commonmark/SnakeYAML in dev, that is where to look.
4. Check the resulting jars load on a dedicated server (nothing client-only is touched at startup except `OpusClient`, reached only from client entry points).

## Running the core tests without Gradle

They only need JDK 21+, commonmark 0.30.0 (core, gfm-tables, gfm-strikethrough, task-list-items), SnakeYAML 2.7 and
JUnit 5 on the classpath: compile `common/src/main/java/com/chimericdream/opus/core` and
`fabric/src/test/java`, then run the JUnit console launcher. `ValidateBookTest` finds the guide through the
relative path `common/src/main/resources/assets/opus/opus-books/guide`, so run from `opus/`.

## Decisions taken for you (reconsider freely)

These were assumed approved under "assume I agree with the recommended approach". None breaks another mod.

- **Resource-pack content** (`assets/<ns>/opus-books/<book>/<lang>/…`), client-side; the mod is "client required, server optional".
- **Scrolling single column + sidebar table of contents**, not paged spreads.
- **Own document model** between commonmark and layout/rendering, so the parser library can change.
- **Custom syntax**: widgets are fenced blocks (` ```recipe `, ` ```item `, ` ```entity `) with a YAML body; inline icons are `![](item:ns:id)`; links use `item:`/`book:` schemes; callouts are `> [!NOTE]`. No `:::` containers yet.
- **Recipes**: inline definitions first (works anywhere); by-id lookup is parsed but not resolved (needs a server sync — see ideas).
- **Ids**: numeric filename prefixes (`01-`) only order pages and are not part of the id; `index.md` is the chapter page; `_`/`.` prefixed files are ignored.
- **A leading `# Heading`** becomes the page title (and is not drawn twice).
- **Unknown frontmatter keys warn**; `x-` prefixed keys are free for authors.
- **Bundled libraries are relocated** (`com.chimericdream.opus.shadow.*`).
- **`mod_version` is `1.0.0-beta.0`** (like other unreleased new mods in the repo) and the changelog already lists the planned first-release features; both are only true once the in-game layer works.
- **Mod identity** in `ideas/README.md` is a draft awaiting your approval; the `ideas/` files are all `Exploring` and nothing is voted.
- **Book item**: one generic `opus:book` item with an `opus:book_id` component; no per-book items.
- **No new networking yet**; nothing server-side beyond the item and component.

## What is deliberately not done

- Recipe by id (server sync), live entity rendering, multiblock previews, tags/bookmarks UI, images, advancement gating (stub returns "unlocked"), accessibility settings, skins. Each is a file in `ideas/`.
- The Markdown Manual screenshot you sent (icon tabs down the left, a centred item image, a hover tooltip with the target file name) is not reproduced exactly: the sidebar is a text table of contents with search, and the link tooltip does show the target's file name. Chapter-icon tabs would be a skin/layout option.
- A Gradle task for `ValidateBook` (see `ideas/authoring/build-time-validation.md`).

## Repo bookkeeping

- Work is on `claude/loving-wozniak-l8d29n`, which started at the same commit as `origin/main`.
- `project-list.json` / `settings.gradle` were updated by `scripts/init-mod.sh` (it also re-sorted `blacklight` in the JSON, which is the script's normal output).
