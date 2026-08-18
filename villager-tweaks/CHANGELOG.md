### Unreleased changes

#### New Features

* Added a **Cap max discount** trading tweak: an optional cap on how far reputation-driven trade
  discounts (mainly from curing zombie villagers repeatedly) can reduce a trade's price, expressed as
  a percentage of the original price. *(Default: off; default cap 99% when enabled.)*
* Added two advancements: **Bag and Tag**, for bagging a villager who's reached max trading level, and
  **Pied Piper**, for luring 8 villagers to you at the same time.

### 26.2 - 6.1.0

#### Changes

* Added a **Villager Growth** config section: an override for how long (in ticks) baby villagers take
  to grow up, and an option to display the remaining grow-up time — the breeder-adjacent sibling of
  the existing zombie-conversion cure-time override/display options. The override applies to babies
  from both spawn eggs/natural spawns and villager breeding (bred babies use a separate vanilla code
  path that doesn't go through the same age-setting hook).

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

#### Bug Fixes

* **Global reputation now actually works.** Reputation events were written under the shared global key,
  but the read path returned early whenever `enableBadReputation` was set — which is its default — so the
  mod wrote reputation to one key and read it back from another, and global reputation silently did
  nothing. The two settings are now independent: `enableGlobalReputation` decides *which* reputation to
  read (shared vs. per-player), and `enableBadReputation` decides *which gossip types* count. With both
  tweaks off, vanilla behavior is unchanged.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* Added `GlobalReputationGameTest` — 4 GameTests covering the four config combinations.
