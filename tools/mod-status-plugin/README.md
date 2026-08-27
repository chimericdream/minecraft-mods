# Chimeric Mod Status — IntelliJ plugin

Shows each mod's version in the Project View, next to the folder name, in the same muted style the
IDE uses for `node_modules`' "library root" label. Mods with pending changelog entries get an amber
dot beside the version and a matching badge on the folder icon.

```
▸ 📁 chimeric-lib     6.4.0
▸ 📁 hopper-xtreme    4.1.0-beta.0 ●
▸ 📁 minekea          10.1.0
▸ 📁 sponj●           6.1.0 ●
▸ 📁 docs
▸ 📁 node_modules  library root
```

Same source of truth as `bun run status` (`scripts/mod-status.ts`):

- **version** — `mod_version` from the folder's `gradle.properties`.
- **unreleased** — non-blank content between `### Unreleased changes` and the next `### ` heading in
  the folder's `CHANGELOG.md`.

A folder is treated as a mod when its `gradle.properties` declares **both** `mod_id` and
`mod_version`. That is what distinguishes the mod roots from the repo root and from each mod's
`neoforge/` subproject, which also ships a `gradle.properties` but only sets `loom.platform`. Mods
commented out of `settings.gradle` are labelled too — they are just not bold, because they are not
Gradle modules.

Editing either file re-reads it and refreshes the tree; no restart or re-open needed.

## Build

This is a **standalone Gradle build**. It is deliberately not part of the monorepo's
`settings.gradle`, so `./gradlew build` at the repo root never sees it.

```sh
cd tools/mod-status-plugin
./gradlew buildPlugin     # -> build/distributions/mod-status-plugin-<version>.zip
```

Install the zip with **Settings → Plugins → ⚙ → Install Plugin from Disk…**, then restart.

To try changes without touching your real IDE, boot a throwaway one with the plugin preinstalled:

```sh
./gradlew runIde -PsandboxProject=/path/to/some/project
```

### gradle.properties knobs

| Property | Purpose |
| --- | --- |
| `localIdePath` | Builds against the IDE already installed on this machine instead of downloading a ~2 GB SDK. Point it at the install directory (the one containing `build.txt`). Comment it out to download IntelliJ IDEA Ultimate 2026.2.1 instead. |
| `pluginVersion` | Version stamped into `plugin.xml` and the zip filename. |

The `-Djavax.net.ssl.trustStoreType=WINDOWS-ROOT` in `org.gradle.jvmargs` is a workaround for Avast's
HTTPS scanning on this machine — it re-signs traffic with a root CA that Windows trusts but the JDK's
`cacerts` does not, so without it every dependency download dies with `PKIX path validation failed`.
Drop the flag on a machine without TLS-inspecting antivirus.

## How it works

| File | Role |
| --- | --- |
| `ModStatusNodeDecorator` | The `com.intellij.projectViewNodeDecorator` extension. Appends the version and marker, and badges the icon. |
| `ModStatusReader` | Parses `gradle.properties` / `CHANGELOG.md`. No platform state, so the rules live in one readable place. |
| `ModStatusService` | Project-level cache. The tree asks for decorations far more often than the files change. |
| `ModStatusFileListener` | Invalidates the cache and refreshes the Project View when either file is touched. |
| `UnreleasedBadgeIcon` | The amber dot, drawn rather than shipped as an SVG so it scales with DPI and needs no light/dark pair. |

Two platform details shaped the design:

- **The version is a coloured text fragment, not a location string.** `setLocationString` is the API
  that produces "library root", but `NodeRenderer` renders location strings with a hard-coded
  `GRAYED_ATTRIBUTES`, which would leave no way to make the unreleased marker stand out.
  `PsiDirectoryNode` already builds content-root rows out of fragments (that is where the bold comes
  from), so appending to them preserves it; for plain directories the decorator seeds the fragment
  list from the presentable text first, reproducing the attributes `NodeRenderer` would have applied.
- **The badge is layered onto the folder icon, not placed beside it.** A tree row is a
  `SimpleColoredComponent` with exactly one icon, always leftmost — there is no "right icon" API. A
  `RowIcon` would widen mod rows and knock them out of alignment with the rest of the tree, so
  `LayeredIcon` puts the dot in the icon's bottom-right corner instead.
