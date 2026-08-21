### 26.2 - 6.1.0

#### New Features

* Added four advancements: **Big Gulp** (dry a region with a 16-sponj wall), **Spill Response Team**
  (absorb a total of 100,000 blocks of water), **Dry Heat** (dry a wet sponj in the nether), and
  **Space Heater** (dry a wet lava sponj in the end).


### 26.2 - 6.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.2 and requires Java 25. It is not compatible with 26.1.x or
  1.21.x.

#### Changes

* Updated to Minecraft 26.2, Architectury 21.0.4, Fabric API 0.154.2+26.2 and NeoForge
  26.2.0.15-beta.
* No gameplay changes of its own. Everything listed under the 26.1.2 release below is included —
  the two are the same mod built for different Minecraft versions.


### 26.1.2 - 5.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* **The connected-sponj limit is now explicit.** A sponj's clear radius (`6 + 3*(n-1)`) and block budget
  (`64 * n`) both scale with the number of connected sponjes, so an unbounded wall would clear tens of
  thousands of liquid blocks in a single tick. That was previously bounded by an obscure quirk in the
  distance check; the count is now capped at 16 directly, with the reasoning recorded next to the
  constant. Small builds behave as before; very large sponj walls are bounded predictably instead of
  accidentally.
* The four sponge blocks were ~95% identical and now share two base classes — `AbstractSponjBlock` for the
  dry absorb/flood-fill and `AbstractWetSponjBlock` for the dry-out and drip particles. Behavior is
  unchanged.
* Sponj's local `BlockUtils` was moved into ChimericLib and is now shared.
* Added `SponjAbsorptionRangeGameTest` — 4 GameTests that pin the absorption bound.
