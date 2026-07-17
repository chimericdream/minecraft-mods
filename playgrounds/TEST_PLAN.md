# Test Plan — Playgrounds

**Status: scaffold only — and probably exempt.** This project is disabled in `settings.gradle`,
contains only Architectury boilerplate, and its name suggests it is a **development sandbox** for
experiments rather than a shippable mod. The README is empty.

## Recommendation

Don't invest in a test plan for a sandbox. Instead, two suggestions:

1. **Use this project as the home for testing experiments.** When building ChimericLib's planned
   GameTest harness helpers (fixtures, inventory diffing, update detectors — see
   `chimeric-lib/POTENTIAL_FEATURES.md` and `chimeric-lib/TEST_PLAN.md`), Playgrounds is the natural
   place to prototype a helper against a throwaway block before promoting it into the library.
2. **If Playgrounds ever graduates into a real mod**, write its plan at that point following the
   suite conventions: manual checklist per feature (Fabric + NeoForge), Fabric GameTest classes in
   `fabric/src/main/java/com/chimericdream/playgrounds/fabric/test/` registered under the
   `fabric-gametest` entrypoint, structures under
   `common/src/main/resources/data/playgrounds/gametest/structure/`.
