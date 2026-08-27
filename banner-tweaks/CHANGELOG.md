### Unreleased changes


### 26.2 - 5.1.0

#### New Features

* Banner tooltips now show a layer count (e.g. "5/12 layers").


### 26.2 - 5.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.2 and requires Java 25. It is not compatible with 26.1.x or
  1.21.x.

#### Changes

* Updated to Minecraft 26.2, Architectury 21.0.4, Fabric API 0.154.2+26.2 and NeoForge
  26.2.0.15-beta.
* No gameplay changes of its own. Everything listed under the 26.1.2 release below is included —
  the two are the same mod built for different Minecraft versions.


### 26.1.2 - 4.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* Documented why `MapStateMixin` keeps its two `@Overwrite` methods rather than converting to
  `@Inject`/`@ModifyArg`. The MC-144406 fix (banner markers off the edge of a map being dropped instead
  of clamped to the border) is distributed through both methods with no stable injection point, so they
  stay reimplemented wholesale — at the cost of conflicting with any other mod that overwrites
  `MapItemSavedData#addDecoration` or `#toggleBanner`. Comments only; no behavior change.
* Added `README.md`, `TEST_PLAN.md` and `POTENTIAL_FEATURES.md`.
