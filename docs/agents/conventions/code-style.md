# Conventions

- **Line endings**: LF everywhere, enforced by the root `.gitattributes` (`* text=auto eol=lf`).
  `bun run lint` (also the git pre-commit hook and a Claude post-edit hook) additionally rejects CRLF,
  non-UTF-8 bytes, a BOM, misplaced access wideners, a malformed `mod_version`, and a changelog that
  doesn't start with `### Unreleased changes`. Enable the hook once per clone:
  `git config core.hooksPath .githooks`.
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
- **Writing files programmatically**: LF is required (`core.autocrlf` is `false`, so git stores exactly what
  you write). Python on Windows translates `\n` to `\r\n` by default and the `csv` module defaults to
  `\r\n`: pass `newline="\n"` to `open(...)` and `lineterminator="\n"` to `csv.writer` / `DictWriter`, and
  always `encoding="utf-8"`. Verify at the byte level (`b.count(b"\r\n") == 0`).
- **Don't write files with Windows PowerShell 5.1** (`Set-Content`, `Out-File`, `>`): it defaults to the ANSI
  codepage and has written Windows-1252 bytes (lone `0x97` em dashes) into `.md` files, which later fail
  UTF-8 edits. Use the Write/Edit tools, or Python with explicit `encoding="utf-8"` and `newline="\n"`.
  PowerShell is fine for read-only tasks.
- **Follow an existing naming convention over personal habit.** The maintainer defaults to hyphenated names
  (`{stencil}-candlelit-{count}.png`) but wants strong repo conventions followed instead (e.g. All Hallows
  Steve overlay textures use underscores: `creeper_lit_blue.png`). When asked to name a file or asset, check
  its neighbors, point out any mismatch, and default to the existing style without asking twice.
