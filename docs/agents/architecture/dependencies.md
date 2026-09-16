# chimeric-lib is an in-build project dependency (no publish loop)

Most mods depend on chimeric-lib, wired as an in-build **`project()` dependency** rather than a
published Maven coordinate. A published-coordinate fallback is used only when chimeric-lib is not part
of the build (e.g. an external consumer outside this repo).

Consumer mods and chimeric-lib's own `test`/`gametest` source sets all resolve chimeric-lib as an
in-build **`project()` dependency**, so editing chimeric-lib source recompiles directly into whatever
you build or test — **no `bun run publish:lib` needed** during development. `mavenLocal()` has been
removed from the resolution repositories, so a stale published jar cannot shadow your source. The
wiring (why it depends on both `:common` and the platform project, the settings.gradle hoist +
`evaluationDependsOnChildren()` that orders configuration) is documented in
`../../../DEPENDENCY-PLAN.md`.

`bun run publish:lib` is now **release-only**: it publishes chimeric-lib for *external* consumers, not
for the edit→build loop in this repo.
