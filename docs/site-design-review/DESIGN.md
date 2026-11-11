# Chimeric Mods field guide

A workshop catalog would give the collection a strong sense of place but would put more emphasis on
the individual projects than on finding an answer. A technical reference desk would serve repeat
visitors well with a denser index. The field guide gives discovery and documentation a shared
visual language. Warm paper, forest-green accents, serif titles, and real mod icons give the site an
identity while leaving the Minecraft imagery and the content to carry each page.

## Design and implementation

- **Catalog** puts search and guide availability above the grid. Search matches real names, project
  folders, and descriptions. Cards use the shipped icons where available and initials elsewhere,
  with direct links to guides, overviews, changelogs, and ideas.
- **Reading** uses a narrower article column, a sticky section index on wide screens, and native
  disclosure controls on smaller screens. Guide links follow book order. On the 390 px Framed
  Planks capture, the article starts at y=237, compared with y=697 in the baseline.
- **Recipes** show the station, slots, ingredient key, and output together. Text gives the row and
  column for shaped recipes and the slot names for smithing. The same parser supplies the visual
  layout and text. The existing ASCII formatter is still available through the formatter interface.
- **Roadmap** gives each idea a visible title, summary, and status. Keyboard-operable filters and
  sorting controls preserve the baseline URL hash format. Every card expands to show its planning
  metadata, including the maintainer's recorded vote. The same content fits desktop and mobile.
- **Release notes and overviews** present unreleased changes and latest releases in distinct
  sections. Overviews show the project version and render README badge labels as text. Linked
  dependency badges keep their links. Minekea's README label still reports 10.3.0-beta.0 while
  its project metadata reports 10.3.1. The repository content retains both source values.
- **Theme** defaults to the OS setting. Light and Dark overrides persist across pages and load
  before the stylesheet. Shared tokens cover surfaces, text, borders, status colors, and focus.

`layout.ts`, `styles.ts`, and `client.ts` implement the shared shell, reading layout, and browser
controls. `pages.ts`, `docs.ts`, and `ideas.ts` render the catalog, changelog, Markdown pages,
and roadmap. `build.ts` selects the HTML recipe formatter in `recipes/html.ts`. The build reads
the existing Markdown and project metadata and writes the same public route families.

## Before and after

The [baseline captures](screenshots/) and the [redesign captures](redesign/) share filenames for
the 14 comparison views. The redesign includes an additional 768 px tablet table and a 390 px
roadmap with advanced filters open. [The capture manifest](redesign/manifest.json) records the
route, viewport, scroll position, and article position for every screenshot.

| View | Baseline | Redesign |
| --- | --- | --- |
| Catalog | [Desktop](screenshots/01-home-desktop.png) | [Desktop](redesign/01-home-desktop.png) |
| Guide | [390 px](screenshots/12-guide-mobile.png) | [390 px](redesign/12-guide-mobile.png) |
| Roadmap | [390 px](screenshots/13-roadmap-mobile.png) | [390 px](redesign/13-roadmap-mobile.png) |
| Opus recipes | [Desktop](screenshots/05-guide-recipes-desktop.png) | [Desktop](redesign/05-guide-recipes-desktop.png) |

## Validation

```powershell
bun install --frozen-lockfile
bun run site
python -m http.server 4180 --bind 127.0.0.1 --directory site/dist
```

For the screenshots and interaction checks, run the following with Playwright Core and Chromium
available. Set `SITE_PLAYWRIGHT` to the absolute path to Playwright Core when it is installed
outside the repo. Set `SITE_CHROMIUM` to the browser executable when using an existing Chromium
binary. The script serves the generated files through Playwright request routing at root and
`/minecraft-mods/` and writes its captures to `redesign/`.

```powershell
node docs/site-design-review/capture-redesign.cjs
bun run lint scripts/site/build.ts scripts/site/client.ts scripts/site/docs.ts scripts/site/ideas.ts scripts/site/layout.ts scripts/site/pages.ts scripts/site/recipes/html.ts scripts/site/styles.ts docs/agents/commands/docs-site.md docs/site-design-review/capture-redesign.cjs docs/site-design-review/DESIGN.md
git diff --check
```

The public build generated 34 mods, 19 guides with 205 pages, 421 Markdown documentation pages,
and 216 active ideas. The only build warnings were the six already recorded in the baseline:
two unsupported Opus cross-book references and four unresolved repository Markdown links.

Chromium verified catalog search, guide filtering and navigation, the skip link, theme
persistence, keyboard-operated mobile navigation and roadmap filters, both title sort directions,
hash restoration, reset, zero results, and root navigation with JavaScript disabled. It loaded
the sampled guide image and heading links with the Pages base path, reported no page errors or
missing local responses, and found no horizontal document overflow in the captured views. The
public output included `.nojekyll` and excluded `/local/`. A separate static check resolved all
19,652 local link and asset references across the 629 generated HTML pages under the Pages prefix.

The sandbox restricted the preview's loopback socket, so the browser checks used Playwright
request routing. External image hosts were blocked during capture. Bun's installer also hit a
temporary-directory access error in this worktree, so the build used the existing cached copy of
the locked `marked` package. Validation covers Chromium viewport sizes and selected keyboard paths.
