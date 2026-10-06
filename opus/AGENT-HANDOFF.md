# Opus — agent handoff

For a coding agent picking this up cold. The human-oriented status is in [`HANDOFF.md`](HANDOFF.md); read that
first, then this. Delete both when the first-run checklist is done.

## The user and how they want to work

- Wants a Patchouli replacement: a book is a **folder of static files** (no Java, no JSON), written in
  **Markdown** with YAML frontmatter. Scrolling single column, not Patchouli's fixed two-page spread. The
  reference they like is the Markdown Manual mod (TIS-3D book screenshot: icon tabs down the left, centred item
  image, link tooltip showing the target file name).
- Prefers purely client-side where possible, but **functionality wins**: server code is fine when it earns its place.
- Asked for minimal sycophancy. Uses they/them if you must refer to them.
- For design calls in the overnight session, they said to assume they agree with the recommended approach unless
  it would force a *breaking* change in another mod. Don't carry that blanket approval into new sessions unless
  they repeat it. The assumptions made are listed in `HANDOFF.md` under "Decisions taken for you".
- Did not ask for a pull request. Don't open one unprompted. Work is on `claude/loving-wozniak-l8d29n`.
- Follows `docs/FEATURE-WORK.md`: agents *propose*, only the user votes, approves the mod identity statement,
  resolves questions that change scope, marks ideas **Ready**, or drops them. Everything in `ideas/` is
  `Exploring`, and the identity statement in `ideas/README.md` is still marked draft. Keep it that way.
  Check ideas against `docs/BRAINSTORMING-RULES.md` (A3 accessibility is the relevant one here).

## Repo conventions that matter here

Start at `AGENTS.md` (documentation map). The parts you will actually hit:

- **Commits**: run `git status` first and commit by explicit pathspec; the repo often has unrelated staged
  work (`docs/agents/releases/git.md`). End messages with the attribution trailers the session gives you.
- **Changelog / version**: any player-visible change adds an entry under `### Unreleased changes` in
  `opus/CHANGELOG.md` and triggers the `mod_version` check (`docs/agents/releases/versioning.md`). Currently
  `1.0.0-beta.0`. Player-facing tone: short, non-technical.
- **Scaffolding** came from `scripts/init-mod.sh` (interactive; feed it with `printf`). It edits
  `project-list.json` and `settings.gradle`.
- **Tests** live in the `fabric` subproject's `src/test` (`docs/TESTING.md`), JUnit 5. Core tests need no
  Minecraft bootstrap.
- MC **26.2** names, Mojang mappings: `net.minecraft.resources.Identifier`, `GuiGraphicsExtractor`
  (`extractBackground`, `extractContent`, `extractRenderState`), `KeyEvent`, `MouseButtonEvent`. See
  `docs/MC-26.2-NOTES.md` before guessing at any API.
- Skills worth using: `mc-source-decompile` (read real 26.2 source instead of guessing),
  `mc-visual-smoke-test` (screenshot a GUI/rendering change headlessly), `ideas-*` (feature pipeline).

## Architecture in one screen

```
opus/common/src/main/java/com/chimericdream/opus/
  core/                    NO Minecraft imports. Keep it that way: it is what makes everything testable.
    Diagnostic(s)          error/warning with file + line; loading never throws for content problems
    frontmatter/           Frontmatter (the --- block + body + body start line), YamlSupport (SnakeYAML, SafeConstructor)
    model/                 Document / Block / Inline / Style: Opus's own AST (renderer never sees commonmark)
    markdown/              MarkdownParser: commonmark -> model; callouts, widget fences, item icons, heading anchors
    widget/                RecipeSpec, WidgetSpecs (item/entity), Ids, WidgetTypes (the known fence labels)
    book/                  BookSource (+MapSource/DirectorySource/layered), BookLoader, Book, BookNode,
                           LinkTarget, SearchIndex, IconRef, BookMeta (book.yml)
    layout/                LayoutEngine (Document + width -> positioned Elements), Layout, Element,
                           TextMetrics (renderer supplies), WidgetSizer, LayoutConfig
    tools/ValidateBook     CLI used by tests and (eventually) CI
  client/, item/, component/, OpusMod   Minecraft-facing; NEVER COMPILED (see below)
```

Flow in game: `OpusReloadListener` (client resources) -> `BookRepository.reload` -> for each
`assets/<ns>/books/<name>/book.yml`: `ResourceBookSource` (language folder layered over the default language) ->
`BookLoader.load` -> `Book`. `BookScreen` lays a page out with `LayoutEngine` + `MinecraftTextMetrics` and
`BookRenderer`/`BookWidgets` draw the resulting `Element`s.

### Invariants worth protecting

- **Layout and renderer must agree.** `WidgetSizer.DEFAULT` reserves the space; `BookWidgets` must draw inside
  exactly that size (18px slots, 28px arrow gutter). Change both together.
- **Layout is renderer-independent**: text width only comes from `TextMetrics`. Don't call Minecraft from `core/`.
- Layout output coordinates are relative to the content area's top-left; the renderer adds origin and scroll.
  Backgrounds (callout, code, table header) are inserted *before* their text in the element list on purpose.
- **Ids**: path with `.md`, numeric ordering prefixes (`01-`, regex `^\d+[-_.](?=.)`) and `index` removed. Link
  resolution tries the literal path, then the stripped id, then aliases. The root node's id is `""`.
- A leading `# H1` becomes the page title and is removed from the body (when there is no frontmatter title, or
  it equals it).
- Content problems become `Diagnostics`; invalid widgets carry `error` and render as an error line. Never throw
  for authoring mistakes.
- Frontmatter keys are a closed set (`BookLoader.KNOWN_KEYS`); unknown keys warn unless prefixed `x-`.
- Fenced block labels in `WidgetTypes` are widgets; every other label is a code block. A new widget needs:
  a label in `WidgetTypes`, a spec parser + `WidgetSpecs.validate` case, a `WidgetSizer` size, a `BookWidgets`
  draw case, a parser test, a layout test, and a section in the bundled guide.
- The bundled guide (`assets/opus/books/guide`) must stay at 0 errors / 0 warnings (`ValidateBookTest`). It
  currently documents some things that are only planned (the `since` badge, locked-page behaviour, image
  support is noted as unsupported); keep it honest as features land or fail.

## What is and isn't verified

Verified: all of `core/` (78 tests) via plain `javac`/JUnit. **Not** verified: every file under `client/`,
`item/`, `component/`, `OpusMod`, both loader client entry points, the Gradle edits, and any in-game behaviour.
The previous session had no access to the Fabric/Architectury/NeoForge Maven hosts. The per-file `UNVERIFIED`
Javadoc names the exact guesses. API facts that *are* confirmed (copied from working code in this repo):

| Fact | Where it is used in the repo |
|---|---|
| `ReloadListenerRegistry.register(PackType, listener, Identifier)`, `ResourceManagerReloadListener`, `manager.listResources(String, Predicate<Identifier>)` -> `Map<Identifier, Resource>` | `athenaeum/.../AthenaeumReloadListener.java` |
| `KeyMapping(String, InputConstants.Type, int, KeyMapping.Category.register(Identifier))` + `KeyMappingRegistry.register` | `minekea/.../client/Keybindings.java` |
| `Item.Properties().stacksTo(1).arch$tab(...).useItemDescriptionPrefix().setId(REGISTRY_HELPER.makeItemRegistryKey(id))`; `use(Level, Player, InteractionHand)`; `InteractionResult.SUCCESS.heldItemTransformedTo(stack)` | `hopper-xtreme/.../HopperItemFilterItem.java` |
| `REGISTRY_HELPER.CUSTOM_COMPONENTS.register(id, () -> DataComponentType.<T>builder().persistent(CODEC).build())` | `hopper-xtreme/.../HopperXtremeComponentTypes.java` |
| `Screen` subclass: `init()`, `this.font`, `addRenderableWidget`, `EditBox` + `setHint(...EditBox.SEARCH_HINT_STYLE)`, `setResponder`, `KeyEvent#isSelection`, `MouseButtonEvent`, `graphics.text(font, Component, x, y, color)` | `better-target-dummies/.../MobPickerScreen.java` |
| `extractBackground(GuiGraphicsExtractor, mouseX, mouseY, delta)`, `context.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, u, v, w, h, texW, texH)`, `blitSprite(...)`, `guiGraphics.pose()` as a `Matrix3x2f`-compatible stack, `GuiGraphicsExtractor#item` | `minekea/.../BlockPainterScreen.java`, `all-hallows-steve/.../CarvingStationScreen.java`, `sneaky-tweaks/.../CampfireSneaking.java` |
| Client entry points: Fabric `ClientModInitializer`; NeoForge `@EventBusSubscriber(value = Dist.CLIENT)` + `FMLClientSetupEvent` | `hopper-xtreme/{fabric,neoforge}/.../client/` |
| Networking: `NetworkManager.registerReceiver(c2s()/s2c(), ID, CODEC, handler)` once from common init | `all-hallows-steve/.../PumpkinFaceNetworking.java` |
| NeoForge dev needs plain jars via `forgeRuntimeLibrary(runtimeOnly(...))` | root `build.gradle` |

## Running things in a restricted sandbox

- `./gradlew` was not executable: use `sh ./gradlew`. Gradle also needs the plugin/Maven hosts; check them first:
  `curl -s -o /dev/null -w '%{http_code}\n' https://maven.fabricmc.net/` (and architectury / neoforged). Maven
  Central and the Gradle plugin portal worked; the three loader hosts and curseforge.com / forgecdn.net did not.
  Don't try to bypass the proxy; see `read_documentation` topic `environment.network`.
- That sandbox's JDK was 21 and the project needs 25, so a Gradle build there would also need a JDK 25 toolchain.
- Maven Central returned HTTP 429 when jars were fetched back-to-back: sleep a few seconds between retries.
- Core tests without Gradle, from `opus/` (jars: commonmark 0.30.0 core + gfm-tables + gfm-strikethrough +
  task-list-items, snakeyaml 2.7, junit-platform-console-standalone):

```sh
CP=$(ls libs/*.jar | grep -v junit | tr '\n' ':'); JU=libs/junit-platform-console-standalone-*.jar
javac --release 21 -Xlint:all -cp "$CP" -d out $(find common/src/main/java/com/chimericdream/opus/core -name '*.java')
javac --release 21 -cp "$CP:$JU:out" -d out $(find fabric/src/test/java -name '*.java')
java -jar $JU execute --class-path "out:$CP" --scan-class-path out
```

- A syntax-only check of the Minecraft-facing files (no Minecraft jars) is possible: compile them against `out`
  and discard `cannot find symbol` / `does not exist` / `does not override` errors; anything left is a real
  syntax problem. It cannot catch wrong Minecraft signatures.

## Suggested order of work

1. Get `:opus:common:compileJava` green (see `HANDOFF.md` for the likely trouble spots), then `:opus:fabric:test`.
2. `runClient`, walk `TEST_PLAN.md`, fix what breaks. Use `mc-visual-smoke-test` to capture the book screen for
   review; check against the Markdown Manual look the user described.
3. NeoForge dev run + shadow jar relocation check; dedicated-server startup check.
4. Once it opens: advancement gating (`BookScreen#advancementDone` is a stub), then live window resize
   (`BookScreen` re-lays out in `init`; confirm it survives `resize`), then recipe by id (server sync) and the
   entity widget; the rest are in `ideas/`.
5. Remove `UNVERIFIED` headers as each file is confirmed; update `CHANGELOG.md` accurately; delete both
   handoff files and the `planning-docs.md` entry that points to them.

## Known rough edges (not bugs found by tests, just things to look at)

- `LayoutEngine` merges adjacent same-style words into one run using summed widths; with a proportional font the
  summed width can differ by a pixel from the measured width of the joined string. Harmless but visible in hit
  boxes. If it matters, measure the joined text instead.
- Tag ingredients (`#minecraft:planks`) draw a barrier item until `ItemLookup` resolves tags.
- `Inline.Image` (non-icon images) renders as a muted `[alt]` text placeholder.
- Entity icons in text show `?`.
- `BookScreen` builds its table of contents once in `init`; locked/unlocked changes won't refresh it yet.
- Block icons use the item registry with the block's id, which is right for nearly every block but not for ones
  without an item form.
- No config screen yet. The scaffold's metadata still declares YACL as a required dependency
  (`fabric.mod.json`, `neoforge.mods.toml`); either add a config (chimeric-lib `YaclConfig`, see
  `docs/agents/conventions/scaffolding.md`) or drop the dependency.
