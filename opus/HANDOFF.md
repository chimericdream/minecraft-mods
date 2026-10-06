# Opus — handoff notes

Written at the end of the first overnight session (2026-10-06), when the sandbox could not reach the Fabric /
Architectury / NeoForge Maven hosts, and updated after the first real build on the laptop. Delete this file once
the items below are done.

## State of things

| Layer | Status |
|---|---|
| `core/` (frontmatter, Markdown → document model, book loading, links, tags, search, layout engine, recipe/widget specs, `ValidateBook`) | **Written and tested.** 78 JUnit tests pass under Gradle (`./gradlew :opus:fabric:test`) and earlier under plain `javac`/JUnit. |
| `client/`, `item/`, `component/`, `OpusMod`, loader client entry points | **Compiles** for common, Fabric and NeoForge. The first build found only three errors (`net.minecraft.Util` is now `net.minecraft.util.Util`; `Minecraft#setScreen` is `setScreenAndShow`), so the other GUI API guesses were right. **Not yet run in game.** |
| Gradle wiring (`common/fabric/neoforge build.gradle`, `gradle.properties`) | **Verified** for `shadowJar` on both loaders: commonmark and SnakeYAML are bundled and fully relocated to `com.chimericdream.opus.shadow.*` (no unrelocated `org/commonmark` or `org/yaml`, no `module-info`), and the bytecode references the relocated packages. NeoForge's *dev runtime* handling (`forgeRuntimeLibrary`) is untested until a NeoForge `runClient`. |
| In-game behaviour | **Never seen.** |

## First steps

1. ~~`./gradlew :opus:common:compileJava`~~ Done. `:opus:fabric:test`, `:opus:neoforge:compileJava` and both `shadowJar` tasks also pass.
2. `./gradlew :opus:fabric:runClient` — go through `TEST_PLAN.md`. Likely trouble spots, since these are the parts the compiler cannot judge: the book screen's drawing happens in `extractBackground` on purpose (see its Javadoc), so check that the search box and buttons draw on top of the book; scissoring and scaled heading text; the `opus:book` item model and name.
3. NeoForge dev run (`:opus:neoforge:runClient`): the libraries use `forgeRuntimeLibrary(implementation(...))` in `neoforge/build.gradle` (pattern from the root build for the quiltmc parsers). If NeoForge cannot see commonmark/SnakeYAML in dev, that is where to look.
4. Check the resulting jars load on a dedicated server (nothing client-only is touched at startup except `OpusClient`, reached only from client entry points).

## Running the core tests without Gradle

They only need JDK 21+, commonmark 0.30.0 (core, gfm-tables, gfm-strikethrough, task-list-items), SnakeYAML 2.7 and
JUnit 5 on the classpath: compile `common/src/main/java/com/chimericdream/opus/core` and
`fabric/src/test/java`, then run the JUnit console launcher. `ValidateBookTest` finds the guide through the
relative path `common/src/main/resources/assets/opus/books/guide`, so run from `opus/`.

## Decisions taken for you (reconsider freely)

These were assumed approved under "assume I agree with the recommended approach". None breaks another mod.

- **Resource-pack content** (`assets/<ns>/books/<book>/<lang>/…`), client-side; the mod is "client required, server optional".
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
