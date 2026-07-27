### 26.2 - 4.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.2 and requires Java 25. It is not compatible with 26.1.x or
  1.21.x.
* **The Hardened Shulker Upgrade Smithing Template is gone**, removed in the 26.1.2 release below. If
  you are upgrading from the 1.21.x line you are seeing this for the first time.

#### Changes

* Updated to Minecraft 26.2, Architectury 21.0.4, Fabric API 0.154.2+26.2 and NeoForge
  26.2.0.15-beta.
* No gameplay changes of its own. Everything listed under the 26.1.2 release below is included —
  the two are the same mod built for different Minecraft versions.


### 26.1.2 - 3.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.
* The **Hardened Shulker Upgrade Smithing Template** has been removed for now, along with its recipe,
  its loot-table entries and its texture.

#### Bug Fixes

* The Dye Station now counts its viewers. Its opener counter tested `player.containerMenu instanceof
  ChestMenu`, copy-pasted from the barrel — but the station opens a `DyeStationScreenHandler`, so it
  never counted a single legitimate viewer.
* The Dye Station now issues `stopOpen` when its menu closes. The constructor issued `startOpen` and
  nothing balanced it, so the opener counter never saw a viewer leave.
* **Shift-clicking the crafted shulker box out of the Dye Station now works.** `quickMoveStack` used the
  station's inventory size as the station/player boundary, but the result slot sits after the station's
  slots and is backed by a different container — so the result index fell into the player-slot branch,
  found no legal target, and did nothing at all. The result also now recomputes from the current inputs
  through an explicit callback rather than an off-by-one slot index.
* `DyeStationBlockEntity.setItems` now copies in place instead of `clear()` + `addAll` on a fixed-size
  list, which could throw and let the slot count drift.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* The Dye Station's hand-rolled opener counter was replaced with ChimericLib's `ContainerOpenersCounters`
  factory, which requires the menu class up front so the bug above cannot be reintroduced by copy-paste.
* The NeoForge loot-modifier registry now uses ChimericLib's `LootModifierHelper`.
* Added `DyeStationGameTest` — 5 GameTests covering viewer counting, open/close balance and shift-click
  routing.
