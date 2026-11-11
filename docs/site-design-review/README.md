# Documentation site: baseline audit and designer handoff

Reviewed **November 11, 2026**. This package records the existing site, not a proposed redesign.
Give a fresh agent **`PROMPT.md`** and a checkout of this repository. The prompt is self-contained;
this audit and the screenshots provide supporting evidence, not additional hidden requirements.

## Provenance and scope

- New branch: `site-design/designer-handoff`, created directly from `docs-site`.
- Baseline commit: `e32a0e42b5568a2357c805421a535f3cbca84635`.
- Existing changes to `package.json`, `docs/agents/commands/docs-site.md`, and the untracked
  `scripts/site-review/`, `forwarding-address/`, and `rules-for-thee/` were left untouched.
  The handoff does not depend on those uncommitted files.
- No site implementation, dependency manifest, source Markdown, or mod code was changed.
- Built with `bun run site`: **34 mods, 19 guides / 205 guide pages, 421 documentation pages,
  216 active roadmap ideas**. Those page families plus the three global pages total 629 HTML pages.
- Captured the public build only, using a loopback static server. No GitHub credentials, private
  dashboard data, or local review-desk feedback is included.
- Final captures use Playwright Core 1.65.0 and Chromium; exact browser version, capture times,
  routes, scroll offsets, dimensions, image loading observations, and interaction checks are in
  `manifest.json`. The first browser tool timed out; its preliminary screenshots are not included.

## How the site works today

The site is a small static compiler, **not Next.js, React, or Jekyll**. Bun executes TypeScript;
`marked` renders Markdown, Bun parses YAML, and Node filesystem/path APIs write the output.
There is no separate CMS, application server, or site-specific dependency installation.

| Concern | Source / implementation | Output / responsibility |
| --- | --- | --- |
| Entry point | `scripts/site/index.ts` | Build, optional server and source watcher |
| Assembly | `scripts/site/build.ts` | Clear/rebuild `site/dist`, collect content, compose all pages |
| Catalog metadata | `project-list.json`, `<mod>/gradle.properties`, `<mod>/CHANGELOG.md`; `mods.ts`, `types.ts` | Names, descriptions, versions, flags, optional icons; Git tags / commit dates collected too |
| Shared presentation | `scripts/site/layout.ts` | `THEME_CSS`, `SITE_CSS`, HTML shell, global header, mod navigation, hierarchical guide navigation |
| Catalog / overview / feed | `scripts/site/pages.ts` | `/`, `/mods/<folder>/`, `/changelog/` |
| Repository Markdown | `scripts/site/docs.ts` | README, CHANGELOG, all nested `ideas/**/*.md`; relative links and asset copying |
| In-game books | `scripts/site/guide.ts` | `opus-books/guide/en_us/` Markdown and adjacent `book.yml`; frontmatter, special links, callouts, images |
| Recipes | `scripts/site/recipes/{parse,types,ascii,names}.ts` | Parsed recipe model and pluggable HTML formatter; current renderer uses box-drawing text and ingredient legends |
| Public roadmap | `scripts/site/ideas.ts` | Extract active idea metadata; inline page-specific CSS, JSON payload and vanilla JS for filters / sorting |
| Maintainer dashboard | `scripts/site/local/` | `/local/`, only when `--serve` is used; GitHub issues / PRs and ideas workflow; separate layout |
| Deployment | `.github/workflows/pages.yml` | Build on `main` push or manual dispatch; publish `site/dist` to GitHub Pages |

### Content and URL contracts worth preserving

- The deployment destination is `https://chimericdream.github.io/minecraft-mods`. Internal site
  links are relative, including links from deeply nested pages; do not assume origin-root hosting.
- README becomes `/mods/<folder>/`; CHANGELOG becomes `/mods/<folder>/changelog/`.
- `ideas/README.md` becomes `/mods/<folder>/ideas/`; nested idea Markdown preserves its directory
  structure without `.md`. Brainstorms and archive pages are published too.
- The **roadmap index** is narrower: active `<mod>/ideas/<theme>/*.md`, excluding `brainstorms`
  and `archive`. Publication and roadmap inclusion are different decisions.
- Guide `index.md` maps to its containing directory; numbered filenames/directories survive in
  URLs. Navigation groups nested directories with native `details` elements, opening the current path.
- Headings receive generated, deduplicated IDs. Markdown links to published files become local site
  links; existing non-published files link to GitHub; unresolved links become text plus build warnings.
- `texture:` images copy real mod resources into the guide output. `item:`, `block:`, and `entity:`
  references are text, not live Minecraft renders. Cross-book references have documented fallbacks.
- Published content does **not** include mod `TEST_PLAN.md`, mod `docs/`, or repository design notes.
  This audit under repository `docs/` is not itself published by the generator.
- `.nojekyll` preserves underscore-prefixed asset folders on Pages. `site/dist/` and `site/.cache/`
  are generated / ignored, not the place to implement a design.

### Client behavior

Only the roadmap has substantial public-page scripting. It exposes mod and theme selects, search,
effort/value/maintainer-vote/tier/status filter pills, twelve sortable columns, counts, Reset, and
`/` to focus search. State persists in the URL hash and restores on reload. This is a read-only
view of recorded votes, **not a public voting application**. There is no catalog search, global
documentation search, manual theme toggle, previous/next article navigation, or generated on-page TOC.

## Visual and interaction findings

These are design judgments grounded in the captures and source, not a complete accessibility audit.

### Strengths to retain

- Real content is already unified across mods without duplicating authoring work.
- A consistent header and per-mod navigation make the basic information architecture understandable.
- The catalog handles missing guides / icons and release-state badges rather than assuming uniform mods.
  This baseline has 25 real catalog icons and 9 initial-based fallbacks.
- Nested guides and ideas, source links, readable callouts, code blocks and semantic articles exist.
- Light and dark palettes follow the system preference; visible content works without a framework.
- The roadmap is functional, with useful deep-linkable filtering rather than decorative controls.

### Highest-value opportunities

1. **Catalog discovery, not just an alphabetical inventory.** The landing page introduces how files
   are published, then immediately shows 34 similarly weighted cards. It lacks a strong player-facing
   explanation, search, or task-oriented discovery. Existing icons provide character, but the site
   itself has little identity. See captures 01, 11 and 14.
2. **Reading hierarchy and wayfinding.** Most articles use an 892px-wide column at 1440px viewport;
   the aggregate changelog uses 1152px. There is no narrower prose measure, on-page TOC, next/previous
   path, sticky navigation or footer. The 13,100px kitchen-sink page leaves the sidebar's space empty
   when scrolled down because its links are not sticky. See 02-08 and 10.
3. **Mobile navigation is stacked ahead of the task.** At 390px, the expanded guide sidebar occupies
   581px, and the article begins at document y=697. The first viewport is mostly navigation rather
   than the requested answer. The header wraps GitHub onto a second row. Home cards themselves fit
   reasonably well. See 11-12; these captures show no whole-document horizontal overflow.
4. **Roadmap information priority.** The desktop table has twelve columns of equal visual emphasis;
   maintainer bookkeeping competes with title, mod, summary and status. At 390px, its internal
   horizontal scroller initially shows only Mod and Theme; the idea title is offscreen. The entire
   mobile page is 17,406px high. Retain the power filters, but give mobile a deliberately readable
   representation. See 09 and 13.
5. **Control accessibility and state clarity.** Source inspection and DOM capture found 19 filter
   pills implemented as clickable `span` elements without `tabindex`; sortable table headers are
   click-only `th` elements without button semantics or `aria-sort`. Native controls are a better
   foundation. Global navigation lacks an active-page indication. These are concrete concerns,
   not a claim that every contrast, focus, screen-reader or keyboard path was tested.
6. **Documentation is not the same as README presentation.** The Minekea overview repeats its
   description and a badge wall before substantive content; the generated version is 10.3.1 while
   the README shield still says 10.3.0-beta.0. This is source drift, not something to disguise with
   styling or silently rewrite in a design task. See 02.
7. **Purposeful specialized components.** The letter-keyed crafting diagrams work, but feel more
   like terminal output than a designed reference; real screenshots demonstrate the appeal of
   Minecraft-native imagery. Recipes need readable results and ingredients, including fallbacks,
   not invented item renders. Idea metadata is currently one dense blockquote. The changelog is a
   long text stream dominated by its first entry. See 04-06, 08 and 10.

The current visual vocabulary is system-ui body text at 16px / 1.6, a pale gray page and white panels,
blue links, 1px borders, 6-8px radii, a 260px sidebar, 1200px main layout / 1600px roadmap layout,
and a single 760px sidebar-stacking breakpoint. Dark mode swaps color tokens. This is a coherent
utility baseline; a professional redesign should improve hierarchy and task flow, not merely recolor it.

## Screenshot inventory

All PNGs are viewport captures: desktop **1440 x 1000**, mobile **390 x 844**, device scale factor 1.
Mobile means viewport resizing, not iOS/Android or touch emulation. Captures 04 and 05 deliberately
scroll to anchors; the others start at the top. File numbers match `manifest.json`.

| File under `screenshots/` | Representative surface |
| --- | --- |
| `01-home-desktop.png` | Catalog and primary navigation, light |
| `02-mod-overview-desktop.png` | Minekea metadata, actions and README |
| `03-guide-index-desktop.png` | Guide landing page, nested navigation and callout |
| `04-guide-callouts-desktop.png` | Opus kitchen-sink examples and rendered callouts |
| `05-guide-recipes-desktop.png` | Recipe source examples and rendered crafting diagrams |
| `06-guide-images-desktop.png` | All Hallows Steve guide: recipe and real Minecraft imagery |
| `07-ideas-index-desktop.png` | Ideas hub, identity and thematic navigation |
| `08-idea-detail-desktop.png` | First roadmap idea: metadata, decisions, related links, open questions |
| `09-roadmap-desktop.png` | Full filter system and dense table |
| `10-changelog-desktop.png` | Aggregate unreleased / released feed |
| `11-home-mobile.png` | Wrapped global header and single-column cards |
| `12-guide-mobile.png` | Expanded current section preceding article content |
| `13-roadmap-mobile.png` | Wrapped filters and horizontally scrolling table |
| `14-home-dark-desktop.png` | System dark palette |

Per-mod changelogs use the generic document shell already sampled by ideas pages. We intentionally
did not capture every mod, archive, theme, filter permutation or viewport. The local maintainer
dashboard is mapped in code but not visually audited. This is not a full cross-browser test.

## Validation and known baseline limitations

The capture script verified 216 initial roadmap rows, a Minekea filter yielding 19 rows, a zero-result
search, hash-state restoration after reload, Reset, title sorting in both directions (including actual
row order), and `/` focus. No JavaScript page errors were observed on these paths.
The empty state currently reports `0 of 216 ideas` and otherwise leaves an empty table.

An additional filesystem check resolved 17,046 static internal link occurrences across all 629 HTML
pages under a synthetic `/minecraft-mods/` URL prefix, with no missing targets or base-path escapes.
This excludes links inside scripts, remote URLs and anchor existence; it is not a live browser crawl
of every route. A normal public build contains no `local/index.html`. Targeted repository lint passed
for this package's Markdown, capture runner and manifest, and `git diff --check` passed.

The public build succeeded with six existing warnings: two cross-book references rendered as text
in Opus and four unresolved Markdown links in All Hallows Steve, Better Target Dummies, Sneaky Tweaks
README and a Sneaky Tweaks brainstorm. These are baseline content limitations, not introduced here.
Image `loaded: false` in an offscreen part of a long page can simply mean a lazy image was not fetched;
the manifest is observational and does not label those images broken. External README badge services
are live dependencies, so pixel-identical reruns are not guaranteed.

### Reproduce captures

1. Install normal repo dependencies if needed, then build: `bun install`, `bun run site`.
2. In another terminal, serve only the public output:
   `python -m http.server 4180 --bind 127.0.0.1 --directory site/dist`.
3. With Playwright Core and its Chromium browser available, run
   `node docs/site-design-review/capture.cjs --playwright <absolute-path-to-playwright-core>`.
   If `playwright-core` is installed in the repo, omit the option. An existing installation was used
   for this audit; no dependency or lockfile changes were made. The runner also accepts `--base-url`
   and `--output`; `--output` names a screenshot directory and writes its sibling `manifest.json`.
4. Stop the temporary server when done. The runner closes its browser automatically.

The runner targets the **baseline DOM**, including its roadmap selectors. Update its selectors if
reusing it after a redesign. For a before/after comparison, preserve these captures and use a separate
output directory. The screenshots and prompt are regular repository files; carry this directory
along when handing work to another checkout. A branch alone does not transfer uncommitted files.
