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
