# ChimericLib — Ideas

Feature ideas for this mod, from first thought to ready to build. The process is described in
[`docs/FEATURE-WORK.md`](../../docs/FEATURE-WORK.md). These files were promoted directly from the legacy
backlog in [`brainstorms/2026-08-27-legacy/`](brainstorms/2026-08-27-legacy/) without a shortlist vote,
because other docs and test plans already treat that backlog as a working list.

## Identity

**Shared plumbing for the chimericdream suite.** Systems, helpers, and building blocks that more than one of
these mods would otherwise each reinvent: registration, datagen, config, commands, inventories, testing, and a
few shared blocks and entities. Much of it is extracted from patterns that already exist in individual mods. It
is built for this suite, so other mods may depend on it but the API is shaped and versioned for these mods. It
adds nothing a player would install it for, and what it does add exists to be used by other mods.

## Active ideas

### Suite patterns

- [Block migration framework](suite-patterns/block-migration-framework.md) — A general "deprecated block
  converts itself on placement or load" system, so any mod can rename or merge blocks without
  DataFixerUpper.
- [Shared Wrench API](suite-patterns/shared-wrench-api.md) — One wrench capability and default item that
  works across Minekea, Hopper X-Treme, and future mods.
- [Item Filter API](suite-patterns/item-filter-api.md) — Hopper X-Treme's include/exclude filter logic and
  UI as a reusable component.
- [Villager profession helper](suite-patterns/villager-profession-helper.md) — Miniblock Merchants'
  convert-register-trade flow as a declarative builder.

### Datagen and registration

- [Block family generator](datagen/block-family-generator.md) — Declare a base block once and generate its
  stairs, slabs, walls, and variants with all their data.
- [Creative tab builder](datagen/creative-tab-builder.md) — Ordering, icons, and per-mod tab conventions in
  one helper.
- [Tag-first behavior registry](datagen/tag-first-behavior-registry.md) — Register behavior against tags so
  datapacks can opt blocks into suite mechanics.
- [Patchouli book datagen](datagen/patchouli-book-datagen.md) — Generate in-game book structure from
  registered content so docs stay in sync.

### Infrastructure

- [Config sync layer](infrastructure/config-sync-layer.md) — Sync server-authoritative YACL config values
  to clients on join, with a consistent override indicator.
- [Networking wrapper](infrastructure/networking-wrapper.md) — A thin, versioned packet abstraction so
  common code never touches loader-specific channels.
- [Component/NBT compatibility helpers](infrastructure/component-nbt-compat-helpers.md) — Read old NBT and
  new data components through one API.

### Testing

- [GameTest harness helpers](testing/gametest-harness-helpers.md) — The suite-wide backlog of shared
  GameTest fixtures identified by the per-mod test plans, with their consumers. Partly built.

### Dev tools

- [Registry dump command](dev-tools/registry-dump-command.md) — `/chimericlib dump` writes a registry
  report to disk for debugging cross-mod registration.
- [Debug overlay hooks](dev-tools/debug-overlay-hooks.md) — An opt-in F3-style section where suite mods
  can publish debug lines.

### Conveniences

- [SimpleSeatEntity polish](conveniences/simple-seat-entity-polish.md) — Seat height offsets, safe
  dismounting, and multi-seat blocks.
- [Sound and particle helpers](conveniences/sound-particle-helpers.md) — One-liners for playing sounds and
  particles at a block on both sides.
- [Block-entity interval and area helpers](conveniences/block-entity-interval-and-area-helpers.md) — An
  every-N-ticks ticker, an area entity scan, and a feed-an-animal helper; first consumer is the JD Crafte
  feeding trough.

## Inbox

- Add more helpers for Fabric datagen.
- Add carpets to the `c:shears_mineable` tag.

## Archive

_Empty._
