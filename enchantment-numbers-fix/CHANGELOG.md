### 26.2 - 4.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.2 and requires Java 25. It is not compatible with 26.1.x or
  1.21.x.

#### Changes

* Updated to Minecraft 26.2, Architectury 21.0.4, Fabric API 0.154.2+26.2 and NeoForge
  26.2.0.15-beta.
* No gameplay changes of its own. Everything listed under the 26.1.2 release below is included —
  the two are the same mod built for different Minecraft versions.


### 26.1.2 - 3.0.0

#### BREAKING CHANGES

* This release targets Minecraft 26.1.2 and requires Java 25. It is not compatible with 1.21.x.

#### Bug Fixes

* Enchantment levels outside 1–3999 no longer crash. Classic Roman numerals have no representation for
  zero or negatives — which another mod's `/enchant` command can produce — and the lookup returned `null`
  for those, which was unboxed straight to `int`. Out-of-range levels now fall back to the Arabic value.

#### Changes

* Updated to Minecraft 26.1.2 / Architectury 20.0.7, built against official Mojang mappings.
* **Much better compatibility with other enchantment-tooltip mods.** The mixin used `@Overwrite` on
  `Enchantment.getFullname` — a verbatim copy of vanilla with one line changed — which clashes with *any*
  other mod touching that method. It now redirects only the single call that builds the level suffix, so
  vanilla keeps ownership of the description text, the curse/gray styling, and the decision of whether to
  append a suffix at all.
* Added `README.md`, `TEST_PLAN.md` and `POTENTIAL_FEATURES.md`.
