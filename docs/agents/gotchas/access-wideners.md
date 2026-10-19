# Access wideners

Each mod that needs one has a **canonical, always-committed** `<mod_id>.accesswidener` in
`<mod>/common/src/main/resources/`, wired into Loom via `accessWidenerPath` in **both**
`common/build.gradle` (`file("src/main/resources/<mod_id>.accesswidener")`) and `fabric/build.gradle`
(reads it off the common project, e.g. `accessWidenerPath = project(":minekea:common").loom.accessWidenerPath`).
A mod whose `common/build.gradle` lacks the `loom` block fails with "Cannot query the value of
extension 'loom' property 'accessWidenerPath'".

Loom adds the `"accessWidener"` entry to each fabric `fabric.mod.json`, and the root `build.gradle`
copies the file into every fabric module's resources (`processResources`). Do **not** also commit a
copy under `<mod>/fabric/src/main/resources/` or add the `fabric.mod.json` line by hand — that causes
"Entry ... is a duplicate" in `processResources`. (The old `copy:accesswideners` / `teardown:build`
scripts did exactly this and were removed.)
