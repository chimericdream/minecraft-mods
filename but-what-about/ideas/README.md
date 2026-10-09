# But What About...? — Ideas

Feature ideas for this mod, from first thought to ready to build. The process is described in
[`docs/FEATURE-WORK.md`](../../docs/FEATURE-WORK.md).

## Identity

> **Finishing Mojang's block sets.** When Mojang adds a block but leaves out its family (the stairs,
> slabs, walls, and cracked, chiseled, or mossy variants its siblings already have), this mod fills in
> the gap. Scope is strictly vanilla completion: every block should be something players would
> reasonably expect Mojang to have shipped, made from existing vanilla materials and textures. New
> materials and invented variants don't belong here.

### Boundary with Minekea

Minekea overlaps with this mod. Anything that finishes a vanilla block family belongs here, so some
of Minekea's blocks will move over time. Minekea keeps furniture and its own original blocks.

Moving a block between mods is a breaking change, so it follows
[`docs/BLOCK-MIGRATION.md`](../../docs/BLOCK-MIGRATION.md):

1. **This mod adds the block first**, under its own ID, with the same shape, textures, and recipe.
2. **Minekea keeps the old block and item registered indefinitely**, so old worlds and inventories
   still load. It drops them from creative tabs and stops generating their recipes.
3. **Minekea converts placed blocks on their first server tick**, swapping each one for its
   counterpart here via `level.setBlock`. Stairs, slabs, and walls have no block entity, so the
   block-entity trap in the doc doesn't apply, but any block that does have one needs the
   snapshot/clear/swap/restore steps.
4. **Minekea therefore gains a required dependency on this mod**, since the conversion target must
   be registered. Check that before moving anything.
5. **A GameTest covers each move**: the placed block converts, with the same facing, waterlogging, and
   so on.

## Active ideas

No active ideas yet. The legacy backlog is in [`brainstorms/2026-08-13-legacy/`](brainstorms/2026-08-13-legacy/),
waiting to be shortlisted and voted on (`ideas-shortlist`) when work on this mod starts.

## Inbox

_Empty._

## Archive

_Empty._
