# Custom recipe helpers

> Source: Opus `TomeCombineRecipe` (shipped) and Hopper X-Treme's [filter copy and paste](../../../hopper-xtreme/ideas/filtering/filter-copy-and-paste.md) (grid-copy recipe, planned), 2026-10-14 · Status: **Exploring**

## Description

Make registering a custom (special) crafting recipe type a one-liner in any suite mod. Vanilla's data-driven
recipes match by item type only, so anything that has to look inside a stack (a configured vs. blank filter,
a book's contents) needs a `CustomRecipe` subclass. Today each mod hand-writes the same plumbing.

**What exists today.** Opus' `TomeCombineRecipe` is the reference: a `DeferredRegister<RecipeSerializer<?>>`,
a `RegistrySupplier<RecipeSerializer<...>>` built from `MapCodec.unit(...)` and `StreamCodec.unit(...)`, an
`init()` called from the mod's common init, and a hand-written `data/<mod>/recipe/<name>.json` containing only
`{"type": "<mod>:<name>"}`. It is entirely common code. Architectury's `DeferredRegister` covers both loaders,
so there is no loader-specific work. (An earlier note that custom recipes need per-loader code was wrong.)

**What a helper could cover, roughly most to least worth doing:**

1. **Parameterless serializer registration.** A single call that takes a mod id, a recipe id, and a
   constructor, and returns the `RegistrySupplier` with the unit codecs and `DeferredRegister` set up.
2. **Datagen for the recipe JSON.** Emit the `{"type": ...}` file so it isn't hand-written, on both Fabric
   and NeoForge datagen.
3. **Matching helpers.** Count the non-empty stacks in a `CraftingInput` and match them against predicates,
   e.g. "exactly one stack matching A and exactly one matching B, nothing else". Opus' loop and the filter
   copy recipe would both use it.
4. **"Keep the original" support.** Copy-style recipes (book cloning) return the source in the remaining
   items instead of consuming it. A small base class could wrap that.
5. **Recipes with parameters.** Serializers that carry fields (an ingredient, a count) need a real `MapCodec`
   and `StreamCodec`. A thin helper may not be worth it. See the open questions.

**Out of scope:** replacing vanilla data-driven recipes, or a recipe-builder DSL for ordinary shaped and
shapeless recipes.

## Decisions

- 2026-10-14 — Captured as a lib idea because more than one mod is expected to want custom recipe types.
  Nothing else decided yet.

## Related

- [GameTest harness helpers](../testing/gametest-harness-helpers.md) — recipes should be testable with a
  shared helper that assembles a `CraftingInput` and asserts the match and result.
- [Component/NBT compatibility helpers](../infrastructure/component-nbt-compat-helpers.md) — recipes like
  these read and copy item components.
- [Filter copy and paste](../../../hopper-xtreme/ideas/filtering/filter-copy-and-paste.md) — the next
  consumer, with a copy-style recipe (item 4). Opus is the existing consumer.

## Open questions

- How much can really be abstracted beyond item 1? Is the datagen piece (item 2) worth it for a one-line
  JSON file?
- Should recipes with parameters (item 5) be supported, or is the helper only for parameterless recipes?
- How does a copy-style recipe keep its original in 26.2? Check the current `CustomRecipe` remaining-items
  API before designing item 4.
- Where does it live in chimeric-lib's package layout?
