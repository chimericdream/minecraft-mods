# Conventions

- **Enforced mechanically — don't re-check by hand.** LF line endings, valid UTF-8 (no BOM, no stray
  Windows-1252 bytes), access wideners only under `<mod>/common/src/main/resources/`, `mod_version`
  format, and the changelog's leading `### Unreleased changes` are checked by `bun run lint` (git
  pre-commit hook, Claude post-edit hook, CI). Claude pre-tool hooks (`scripts/hooks/pre-tool-guard.ts`)
  block `python3`, file writes from Windows PowerShell 5.1, and bare multi-mod `git commit`s. Enable the
  git hook once per clone: `git config core.hooksPath .githooks`.
- **demo-world (minekea)**: `minekea/demo-world/` is a deterministic showcase generator. Generated
  files (`demo_build.mcfunction`, manifests) are produced by `generate_layout.py` /
  `extract_jar_contents.py` — **never hand-edit them**; regenerate. See `minekea/demo-world/README.md`.
- **Container block-entity inventories**: pick the abstraction by capability, don't mix arbitrarily.
  - A block entity that should support **loot tables** (chest/barrel-like) extends
    `RandomizableContainerBlockEntity` and honors `trySaveLootTable`/`tryLoadLootTable` in its
    save/load. Don't also implement `ImplementedInventory` on these — its defaults are dead weight and
    it's easy to bypass the loot-table plumbing.
  - `ImplementedInventory` (chimeric-lib) is for inventories that are **not** loot-table-capable —
    typically item-backed or purpose-built containers (glass jar, shelf, display case, the hopper
    filter item). Back them with a `NonNullList.withSize(...)`.
  - `setItems` must preserve the fixed slot count: copy in place
    (`for i: items.set(i, incoming.get(i))`), never `clear()+addAll` on a fixed-size `NonNullList`
    (that can throw or let the size drift). Same fixed-size rule as `ImplementedInventory.clearContent`.
- **Writing files programmatically**: when scripting file output, pass `encoding="utf-8"` and
  `newline="
"` to Python's `open(...)` (and `lineterminator="
"` to `csv.writer`); Python on Windows
  otherwise writes CRLF. The lint rejects the result; this is how to avoid producing it.
- **Follow an existing naming convention over personal habit.** The maintainer defaults to hyphenated names
  (`{stencil}-candlelit-{count}.png`) but wants strong repo conventions followed instead (e.g. All Hallows
  Steve overlay textures use underscores: `creeper_lit_blue.png`). When asked to name a file or asset, check
  its neighbors, point out any mismatch, and default to the existing style without asking twice.
