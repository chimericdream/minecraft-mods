### Unreleased changes

#### New Features

* Added **Trade preview**: a villager's trades for levels it hasn't reached yet now appear greyed out below its
  current trades, and can't be selected. Hover one to see which level unlocks it. A villager's future trades are now
  decided up front, so they will differ from vanilla. Villagers you've already traded with get their remaining levels
  filled in the next time you open their trades. With this on, a villager now levels up while you're still trading and
  the new trades unlock on screen, without closing and reopening it. *(Default: on.)*

### 26.2 - 6.3.1

#### Bug Fixes

* Fixed the Fabric version failing to load its access widener, which could cause a crash or a black screen on startup. Only the 26.2 release was affected.

### 26.2 - 6.3.0

#### New Features

* Added the Villager Tweaks Guide, an in-game book covering the Bagged Villager and every setting. Craft one from a book and an emerald, or use `/opus give villagertweaks:guide`. Install the optional Opus mod (1.2.0 or newer) to get it.

#### Changes

* The config screen is now provided through Chimeric Lib, which must be updated to 6.6.0 or newer. The
  settings themselves are unchanged.

### 26.2 - 6.2.0

#### New Features

* Added a **Cap max discount** trading tweak: an optional cap on how far reputation-driven trade
  discounts (mainly from curing zombie villagers repeatedly) can reduce a trade's price, expressed as
  a percentage of the original price. *(Default: off; default cap 99% when enabled.)*
* Added two advancements: **Bag and Tag**, for bagging a villager who's reached max trading level, and
  **Pied Piper**, for luring 8 villagers to you at the same time.
* Added a **Leash nitwits** toggle: leads can be attached to nitwit villagers. *(Default: off.)*

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
