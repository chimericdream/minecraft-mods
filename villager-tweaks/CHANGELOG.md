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
