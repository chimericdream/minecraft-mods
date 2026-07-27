### 26.2 - 4.0.1

#### Bug Fixes

* Fixed an item duplication exploit in the Hopper Item Filter's editor: setting a filter entry could,
  through a specific sequence of clicks, be swapped back out for a full, spendable copy of the item at
  no cost. The filter's slots now only ever hold "ghost" copies of an item to represent the filter
  entry — setting one never consumes the item you clicked in, and clearing one never hands anything
  back, so there's no longer any way to net-gain real items through the filter editor.


### 26.2 - 4.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.2 and requires Java 25. It is not compatible with 26.1.x or
  1.21.x.
* **The separate "Filtered" hopper blocks are deprecated**, as of the 26.1.2 release below. If you are
  upgrading from the 1.21.x line you are seeing this for the first time: filtering is now built into
  every upgraded hopper, the filtered variants' recipes are gone, and any you still have convert to
  their filter-capable equivalent when placed. See the 3.0.0 entry for the details.

#### Changes

* Updated to Minecraft 26.2, Architectury 21.0.4, Fabric API 0.154.2+26.2 and NeoForge
  26.2.0.15-beta.
* No gameplay changes of its own. Everything listed under the 26.1.2 release below is included —
  the two are the same mod built for different Minecraft versions.


### 26.1.2 - 3.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.
* **The separate "Filtered" hopper blocks are deprecated.** Filtering is now built into every upgraded
  hopper, hupper and multi-hopper, so the filtered variants no longer have a reason to exist. Their
  crafting recipes have been removed. Any deprecated filtered hopper you still have will automatically
  convert to its filter-capable equivalent when placed, preserving its contents, custom name, cooldown
  and filter settings. Do not craft new ones.

#### Bug Fixes

* Fixed the filtered-hopper menu geometry, which disagreed between client and server. The filter slot is
  the last container slot, but the server block entity hides it from the `Container` API while the client
  builds the menu over a dummy container that does not. Two consequences: an ordinary item placed in the
  last storage slot was treated as the filter and the GUI rejected every insert, and shift-click routing
  desynced because the hopper/player boundary was 6 server-side and 7 client-side.
* Fixed a `ClassCastException` when a foreign hopper — a hopper minecart, for example — pulled from an
  X-Treme hopper. `canExtract` cast its argument straight to this mod's block entity, but the public
  entry point accepts any vanilla `Hopper`. Foreign hoppers now fall back to vanilla pull-from-above
  semantics.
* `isFull()` no longer counts the filter slot as storage, so a hopper with a filter set is no longer
  reported as one item fuller than it is.
* A non-filtered multi-hupper no longer refuses to pull items. It was the only variant gating extraction
  on `isFilter == (slot == 5)` — dead code from an older filter design — so it would only pull the filter
  item, and only out of a source's sixth slot.
* The multi-hopper round-robin cursor now survives a chunk unload/reload. It was memory-only, so after a
  reload a hopper repeated the side it had just handed off to instead of continuing the rotation.
* Fixed the Hopper Item Filter's crafting recipe.
* Hopper menus now issue `stopOpen` when closed, so viewer counts stay balanced.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* The six block-entity classes — roughly 3,700 lines of ~80% identical copies of a vanilla-hopper fork —
  were collapsed into a shared base-class hierarchy, a net reduction of about 3,250 lines. Behavior is
  unchanged; the hupper and multi-hupper `UP` pull geometry is preserved verbatim. The deferred
  block/screen-handler collapse is documented in `REFACTOR-3.1-PLAN.md`.
* The Wrench is now a thin subclass of ChimericLib's `AbstractWrenchItem`. Its recipe, tooltip and
  behavior are unchanged.
* Simplified `HopperItemFilterItem.use` and `FilterSlot.mayPlace`, which carried redundant nested
  client-side checks.
* Test coverage is now 20 GameTests, including new tests for foreign-hopper extraction, filter-slot
  geometry, and multi-hopper cursor persistence.
