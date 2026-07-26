# Test Plan — Athenaeum

Athenaeum generates custom written books from datapack definitions and injects them into structure
loot (stronghold libraries, woodland mansions). Key classes: `AthenaeumReloadListener` (loads book
definitions from datapacks), `BookRegistry`/`AthenaeumRegistries`, `AthenaeumBook` (the data model),
`GetRandomBookFunction` + `AthenaeumLootFunctionTypes` (the loot function that turns a placeholder
into a random book), `AthenaeumLootTables` (injection targets), and `AthenaeumConfig`. Client is
optional — this is a server-driven data mod, which makes it unusually automatable.

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/athenaeum/fabric/test/`, registered under the
`fabric-gametest` entrypoint in `fabric.mod.json`. Most tests here don't need structures at all —
they exercise the reload listener, registry, and loot function directly against the running test
server. Test-only book datapacks should live in the fabric test source set's resources so tests
control their own inputs.

## Manual test plan

Setup: new world with the mod; locate (or `/place`) a stronghold library and woodland mansion.
Fabric full pass; NeoForge smoke pass (loot injection is loader-specific on NeoForge — see its
`LootModifier` classes in other suite mods; verify which mechanism Athenaeum uses there).

1. **Books appear in loot** — open stronghold library chests: expect mod-generated written books
   alongside vanilla loot. Same for woodland mansion chests. Check several chests — frequency should
   feel like loot, not guarantee.
2. **Book integrity** — open a generated book: title, author, and pages render; no raw JSON, no
   truncated pages beyond vanilla's page limits; the book is a real `written_book` (signed), not
   writable.
3. **Datapack-driven definitions** — add a custom datapack defining a new book; `/reload`; verify
   the new book can appear (and that `/reload` doesn't duplicate registry entries — reload listeners
   are a classic double-registration source).
4. **Malformed datapack handling** — ship a book JSON with a missing field / bad syntax; `/reload`;
   the game must log a sane error and skip the entry, not crash or poison the whole registry.
5. **Config** — exercise whatever `AthenaeumConfig` exposes (via Mod Menu on Fabric); confirm
   persistence and effect.
6. **Localization/formatting** — books using formatting codes or long titles render correctly in
   the lectern and in hand.

## Recommended automated tests

### GameTests / server tests — registry & reload

* **`booksLoadFromDatapack`** — with a test datapack defining 2 known books, assert after server
  start that `BookRegistry` contains exactly those entries with correct title/author/page content.
* **`reloadIsIdempotent`** — trigger a resource reload (`server.reloadResources(...)`) twice; assert
  registry size unchanged and no duplicates.
* **`malformedBookSkipped`** — test datapack includes one valid and one invalid book JSON; assert
  the valid one loads, the invalid one is absent, and (if the reload listener exposes it) an error
  was recorded — the test must pass *because* of graceful degradation, not luck.

### GameTests — loot function

* **`getRandomBookFunctionProducesSignedBook`** — build `LootParams` for a chest context, run
  `GetRandomBookFunction` on a dummy stack with a seeded random, and assert: result is
  `minecraft:written_book`, has title/author/pages components populated from a registered book.
* **`lootFunctionWithEmptyRegistry`** — clear/empty registry (no datapack): function must degrade
  gracefully (defined fallback item or unmodified stack — pin the intended contract).
* **`randomnessCoversRegistry`** — with 3 registered books and a seeded loop of ~200 rolls, assert
  every book appears at least once (catches off-by-one selection bugs).

### GameTests — loot table injection

* **`strongholdLibraryTableContainsBookPool`** — resolve
  `minecraft:chests/stronghold_library` from the server's reloadable registries and assert the
  injected pool/entry is present (structural assertion). Same for
  `minecraft:chests/woodland_mansion`.
* **`chestRollProducesBook`** — place a chest in a GameTest structure, set its loot table to the
  stronghold library table with a fixed seed chosen (by trial during test authoring) to yield a
  book, open-trigger it via a mock player, and assert a written book is present. Marked
  seed-brittle: prefer the structural assertion above as the primary guard; keep exactly one
  end-to-end roll test.
* **NeoForge parity note** — if injection uses a different mechanism on NeoForge (global loot
  modifiers), these two tests must eventually run there too; until NeoForge test wiring exists,
  cover it in the manual pass.

### Unit tests

* **`AthenaeumBook` codec/parse round-trip** — serialize a book definition to JSON, parse it back,
  assert equality; include optional-field defaulting. If parsing is codec-based this is nearly free
  and catches format drift immediately.
* **Page-splitting / length rules** — if `AthenaeumBook` or the reload listener enforces vanilla
  page/character limits, table-test the boundaries (empty pages, max page count, over-long page).

## ChimericLib helper opportunities

* **Loot-table test kit** — `assertLootTableContains(server, tableId, entryPredicate)` and
  `rollLootTable(server, tableId, seed, rolls)` returning stacks. Needed here, in Miniblock
  Merchants (conversion-item drops), Archaeology Tweaks (brushing loot), and Shulker Stuff
  (enchantment availability in loot).
* **Test-datapack fixture** — a documented convention + helper for shipping datapack fixtures inside
  the fabric test source set and asserting on reload results; every data-driven suite mod benefits.
* **Reload-idempotence harness** — "reload twice, assert registry stable" as a one-liner; any mod
  with a reload listener should run it.

## Open questions

* What is the intended fallback when the book registry is empty (vanilla book? air? skip pool)?
  Needed for `lootFunctionWithEmptyRegistry`.
* README "Planned Features" lists config options controlling which books generate — when that lands,
  extend the config tests to cover allow/deny filtering.
