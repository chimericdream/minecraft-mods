# Opus — handoff notes

Written at the end of the first overnight session (2026-10-06) so work can continue on a machine that can
reach the Fabric / Architectury / NeoForge Maven hosts. Delete this file once the items below are done.

## State of things

| Layer | Status |
|---|---|
| `core/` (frontmatter, Markdown → document model, book loading, links, tags, search, layout engine, recipe/widget specs, `ValidateBook`) | **Written and tested.** 78 JUnit tests passed, compiled with plain `javac` (`--release 21 -Xlint:all`, no warnings) and run with the JUnit console launcher against commonmark 0.30.0 and SnakeYAML 2.7. |
| `client/`, `item/`, `component/`, `OpusMod`, loader client entry points | **Never compiled.** The sandbox could not reach `maven.fabricmc.net`, `maven.architectury.dev` or `maven.neoforged.net` (blocked by the network policy), so Minecraft was unavailable. Written from the patterns in other mods (see below), then syntax-checked only (javac with the missing-class errors filtered out: no syntax errors, no misuse of the core API). |
| Gradle wiring (`common/fabric/neoforge build.gradle`, `gradle.properties`) | **Never run.** Follows the root `build.gradle` patterns. |
| In-game behaviour | **Never seen.** |

Everything in `core/` is free of Minecraft types, so it should compile unchanged under
Gradle's `release = 25`.

## First steps

1. `./gradlew :opus:common:compileJava` and fix what breaks. Expected trouble spots, most likely first:
   - `client/screen/BookRenderer.java`, `BookWidgets.java`: `GuiGraphicsExtractor` signatures: `text(font, comp, x, y, color, boolean dropShadow)`, `item(stack, x, y)`, `itemDecorations(font, stack, x, y)`, `setTooltipForNextFrame(font, Component, x, y)`, `pose()` as a `Matrix3x2fStack` with `pushMatrix/translate/scale/popMatrix`, `enableScissor/disableScissor`, `fill`. Reference for what is known to exist: better-target-dummies `MobPickerScreen` (`text(font, comp, x, y, color)`), all-hallows-steve `CarvingStationScreen` (`pose()`, `item`), minekea `BlockPainterScreen` (`blit`, `extractBackground`).
   - `client/screen/BookScreen.java`: `mouseScrolled(double, double, double, double)`, `MouseButtonEvent#x()/y()`, `KeyEvent#key()`, `Button.builder(...).bounds(...).build()`, `EditBox.SEARCH_HINT_STYLE`, `ConfirmLinkScreen.confirmLinkNow(Screen, String)`, `Font#plainSubstrByWidth`, `Button#active`. Drawing is done in `extractBackground` on purpose (see its Javadoc).
   - `client/book/BookRepository.java`: `LanguageManager#getSelected()` (returns the language code?).
   - `client/screen/ItemLookup.java`: `BuiltInRegistries.ITEM.getValue(Identifier)`, `Identifier.tryParse`.
   - `client/OpusClient.java`: Architectury `ClientTickEvent.CLIENT_POST`.
   - `item/OpusBookItem.java`: copied from hopper-xtreme's `HopperItemFilterItem`; likely fine.
2. `./gradlew :opus:fabric:test` — the 78 tests should pass under Gradle too.
3. `./gradlew :opus:fabric:runClient` — go through `TEST_PLAN.md`.
4. NeoForge dev run: the libraries use `forgeRuntimeLibrary(implementation(...))` in `neoforge/build.gradle` (pattern from the root build for the quiltmc parsers). If NeoForge cannot see commonmark/SnakeYAML in dev, that is where to look. Also confirm `./gradlew :opus:fabric:shadowJar` relocates (`com.chimericdream.opus.shadow.*`).
5. Check the resulting jars load on a dedicated server (nothing client-only is touched at startup except `OpusClient`, reached only from client entry points).

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
