# Standalone prompt: redesign Chimeric Mods documentation

You are a senior product designer and frontend engineer. Design and implement a distinctive,
professional documentation website for **Chimeric Mods**, a collection of Minecraft mods by
chimericdream. This is a real redesign, not just an audit, a generic landing-page mockup, or a palette
swap. You have no access to an earlier conversation. Everything essential is specified here.

## Outcome

Make the site feel intentionally designed for people discovering mods and looking up how they work.
Combine the character of this Minecraft mod collection with the clarity and navigability of excellent
technical documentation. Improve information hierarchy, content discovery, long-form reading, mobile
wayfinding, and the roadmap. Deliver working code using the real repository content, then verify it in
a browser and provide representative before/after screenshots and a concise design rationale.

Do not stop after proposing a direction. Make reasonable decisions and implement the selected design.
Do not start another agent or rely on other threads, account access, private tools, or external memory.

## Repository and starting point

- Repository: `chimericdream/minecraft-mods`; the local folder may be named `chimeric-mods`.
- Baseline branch: `docs-site`, commit `e32a0e42b5568a2357c805421a535f3cbca84635`.
- The baseline evidence was recorded November 11, 2026. Recheck facts against the checkout you receive.
- Work in your supplied isolated checkout / branch. If you must create a branch, use
  `site-design/<descriptive-name>` from `docs-site` only after checking repository state. Do not reset,
  stash, delete, or overwrite someone else's changes. Do not commit or push unless separately asked.
- Read root `AGENTS.md` and applicable nested instructions before changing files.
- Optional evidence package: `docs/site-design-review/README.md`, `manifest.json`, and 14 PNGs in
  `docs/site-design-review/screenshots/`. If they are supplied, inspect them. They show the baseline,
  not a visual direction you must imitate. If absent, the architecture and findings below are sufficient
  to start; run and inspect the baseline yourself.

## Users and jobs

1. A Minecraft player arrives without knowing the collection: understand what is here and find a mod
   relevant to their interests without scanning 34 indistinguishable entries.
2. A player knows the mod: get to its guide and a specific feature / recipe quickly.
3. A returning player wants changes: distinguish released functionality, unreleased work and speculative
   ideas, with clear paths to the relevant mod and full changelog.
4. An interested community member wants the roadmap: browse ideas and understand status and context
   without needing to understand the maintainer's entire scoring workflow.
5. The maintainer needs low-friction publishing: existing Markdown and metadata remain the source of truth.

Prioritize the first two jobs. The site should be welcoming without becoming a marketing funnel that
pushes actual documentation below a giant hero. Technical reference and planning transparency matter.

## Current implementation: use this map

The site is a **Bun + TypeScript static generator with `marked` and vanilla browser JavaScript**, not
Next.js or React. There is no CMS or runtime backend. Work with this architecture by default; a new
framework, component ecosystem, service, or complicated build pipeline needs a concrete benefit beyond
developer preference. Do not add infrastructure just to reproduce layouts achievable with HTML/CSS.

- `package.json`: root `site` command. `bun run site` builds ignored output into `site/dist/`.
- `scripts/site/index.ts`: CLI, optional `--serve`, watcher. `bun run site --serve --port 4000`
  rebuilds on source Markdown / YAML / PNG / properties changes; generator changes need a restart.
- `scripts/site/build.ts`: collects content, builds guides and documentation, writes global pages,
  CSS and `.nojekyll`. A public build does not call the local-only GitHub dashboard.
- `scripts/site/mods.ts`, `types.ts`: catalog from `project-list.json`, per-mod `gradle.properties`,
  changelog status, optional `common/src/main/resources/assets/<mod-id>/icon.png`, Git tags / dates.
- `scripts/site/layout.ts`: theme tokens, all shared CSS, HTML shell, header, mod navigation,
  nested guide navigation and guide page composition. Best starting point for a coherent system.
- `scripts/site/pages.ts`: home catalog, mod overview, combined changelog.
- `scripts/site/docs.ts`: README / CHANGELOG / recursive `ideas/` discovery, Markdown rendering,
  asset copying, link rewriting, ideas navigation and generic document shell.
- `scripts/site/guide.ts`: frontmatter and Opus book Markdown rendering; guide discovery under
  `<mod>/common/src/main/resources/assets/<id>/opus-books/guide/en_us/` with adjacent `book.yml`.
- `scripts/site/recipes/`: parsed recipe model, names, ASCII renderer, and `RecipeFormatter`
  interface. Presentation can be replaced without discarding the recipe parser.
- `scripts/site/ideas.ts`: roadmap metadata extraction and page-specific inline CSS / JSON / JS.
- `scripts/site/local/`: separate maintainer-only dashboard with GitHub issues, PRs and ideas pipeline.
  It shares theme tokens but has its own layout. Do not expose or make the public site depend on it.
- `.github/workflows/pages.yml`: publish `site/dist` to GitHub Pages after `main` pushes or manual runs.

Baseline public build: **34 mods, 19 guides with 205 pages, 421 documentation pages, 216 active ideas**.
These are a snapshot, not constants to hardcode. Mods vary: some have no guide, some have only a README,
some have large nested books, some have long names or missing icons. The catalog has 25 real icons and
9 initial-based fallbacks. Handle content diversity as part of the design, not as an edge case to hide.

## Content, routes, and behavior you must preserve

- Deployment is static GitHub Pages at `https://chimericdream.github.io/minecraft-mods`. Support both
  root preview hosting and that nested base path. All routes/assets must work on direct deep links.
- Keep existing URLs and generated heading anchors wherever possible. If a change is unavoidable,
  provide a static-compatible migration, not a server-only redirect assumption.
- Global pages: `/` catalog, `/ideas/` roadmap, `/changelog/` aggregate feed.
- Mod overview: `/mods/<folder>/`, generated from `<mod>/README.md` and metadata.
- Full per-mod changelog: `/mods/<folder>/changelog/`, generated from `CHANGELOG.md`.
- Ideas hub and detail: `/mods/<folder>/ideas/` and nested paths from `ideas/**/*.md`.
  README files map to directory indexes. Brainstorms and archives are intentionally public and must
  remain reachable, even though they are excluded from the active roadmap index.
- Guides: `/mods/<folder>/guide/` and nested directory URLs; `index.md` becomes its directory index.
  Numbered names such as `01-building-blocks/09-framed-planks/` are part of existing URLs.
- Preserve frontmatter titles, heading anchors, nested guide/ideas navigation, cross-document links,
  GitHub source links, Markdown tables/code/lists, and real image assets.
- Support Opus `> [!TIP]` / other callouts, `texture:` images and width hints, item/block/entity text
  references, and recipe blocks. Existing cross-book links and unavailable imagery have fallbacks;
  do not imply live 3D renders or automatically resolved items that the build cannot supply.
- Recipes include shaped and shapeless crafting, cooking, stonecutting and smithing. Preserve
  ingredient alternatives, counts and output names; visual improvements must retain text equivalents.
- Roadmap: search, mod/theme filters, effort/value/maintainer-vote/tier/status filtering, all existing
  metadata, sorting, result count, Reset, and URL-hash shareability / restoration. You may reorganize
  controls and de-emphasize fields, but do not delete the power-user information. Recorded votes are
  **read-only maintainer metadata**, not a functioning community-vote UI.
- Combined changelog currently has separate unreleased and latest-release sections; do not imply
  these entries form a dated chronological feed without actual date data.
- Continue generating from the existing Markdown and properties; do not create parallel hand-copied
  content or author dozens of hardcoded pages. Do not change mod behavior, Gradle files, or feature
  planning decisions just to make the website look more polished.
- Do not accidentally publish `/local/`, caches, design review notes, credentials, `TEST_PLAN.md`, or
  repository/mod internal docs. Keep `.nojekyll` and the GitHub Pages deployment working.

## Baseline findings: solve problems, not just styling

The current design has system-ui typography, blue links, neutral bordered cards, a 1200px layout,
260px sidebar, a 1600px roadmap layout, and automatic light/dark colors. Its straightforward generation
pipeline and consistent links are strengths. The opportunities are substantive:

- Home is an alphabetical grid without catalog search or strong player-oriented hierarchy. Its
  introduction explains publishing mechanics more than what a player can discover.
- Desktop article measure is usually 892px; the aggregate changelog is 1152px. Long pages lack a
  table of contents, next/previous guidance and persistent wayfinding.
- At 390px, a representative guide's expanded sidebar occupies 581px and its article begins at
  document y=697. Mobile should prioritize the current answer and offer navigation on demand.
- The twelve-column roadmap hides the actual idea title offscreen on mobile; only Mod and Theme
  are initially visible. Deliberately design the compact view instead of simply shrinking the table.
- Roadmap filter pills are click-only `span`s, and sorting uses click-only `th`s. Replace with
  semantic keyboard-operable controls, exposed state, focus treatment and meaningful feedback.
- Overviews repeat some README headings/descriptions and badge walls. One observed source mismatch:
  Minekea metadata is 10.3.1 while its README badge says 10.3.0-beta.0. Report content drift honestly;
  do not silently invent metadata or rewrite upstream docs to hide it.
- Recipe diagrams, idea metadata, version/status badges, callouts and release entries deserve a
  cohesive set of purpose-built components. Use actual mod images where helpful, not fake screenshots.

These observations are not a prescribed visual style. You are free to reorganize the shell, typography,
navigation, catalog, responsive layouts and visual language while preserving the content contracts.

## Design expectations

Briefly consider two or three meaningfully different art directions, pick one, and explain why it fits
this collection. Keep this exercise short; spend most effort executing a consistent design across real
pages. Make a recognizable system rather than unrelated beautiful screens.

Use deliberate typography, spacing, reading measure, surface hierarchy, mod identity, responsive
behavior, and restrained motion. Minecraft references should feel grounded in actual assets and the
collection's personality, not a blanket pixel font or generic game-store UI. Avoid generic dashboard
chrome, decorative gradients or giant empty hero sections that do not serve the documentation.
No specific palette, typeface or layout is mandated.

Must visibly address: catalog discovery (a working mod search is a strong default), clear global/mod/
page navigation, readable documentation, useful mobile navigation, a readable mobile roadmap, clear
release-versus-idea status, and coherent light/dark appearances. Preserve a good system-default theme;
if you add a manual choice, make it functional, persistent, and initialized without a distracting flash.
If you add global docs search, it must index real content and navigate to real results; otherwise do not
show a pretend search box. Any curated categories must have a maintainable source, not invented facts.

Provide semantic landmarks, logical headings, skip navigation, visible focus, labels, keyboard-operable
menus/filters/sorting, sufficient contrast, accessible status text, and reduced-motion behavior.
Tables and code may scroll inside their own containers; the document itself must not overflow on
mobile. Preserve native links so essential docs remain reachable without client JavaScript.

Do not fabricate download URLs, compatibility guarantees, screenshots, endorsements, dates, counts,
voting actions or mod features. Existing source text can remain imperfect; distinguish content cleanup
from design implementation. Do not add unavailable font/image services or an account requirement.

## Workflow and acceptance

1. Inspect instructions, current code, several source documents and real assets. Build/run the baseline
   and inspect representative pages before changing them. Use supplied captures if available.
2. Record your chosen direction and a compact component/token plan, then implement shared templates
   so the redesign applies to the full corpus, not just showcased pages. Keep changes scoped to the site.
3. Run `bun run site`. Generated output is disposable; edit generators/styles/assets, not `site/dist`.
   A public-only preview can use `python -m http.server 4180 --bind 127.0.0.1 --directory site/dist`
   after building, avoiding any GitHub API dependency. The normal `--serve` command also builds the
   maintainer dashboard and may need GitHub CLI access; do not make public-site work wait on that.
4. Use Playwright or equivalent to inspect and capture the result at approximately 1440px desktop,
   768px tablet, and 390px mobile. You need representative coverage, not every permutation. Include:
   home; Minekea overview and a nested guide; an image-rich All Hallows Steve guide; Opus's
   `/mods/opus/guide/99-appendices/kitchen-sink/` for callouts/recipes/tables; an ideas hub and detail;
   the roadmap; the combined changelog; and at least one meaningful dark-mode view. Inspect scrolled
   content as well as the first viewport.
5. Verify navigation and direct deep links, images and heading anchors, a non-root Pages base path,
   catalog search if added, roadmap filtering / actual sort order / reset / zero results / hash restore,
   keyboard behavior, mobile menus and overflow. Test theme persistence if you add a theme switch.
   Check browser errors. Keep `/local/` absent from a normal public build. Run targeted repo lint and
   any relevant existing tests. No Minecraft/Gradle build is necessary for a website-only change.
6. Save your screenshots, summarize design decisions and changed files, give exact run/validation
   commands and results, and honestly identify remaining issues. Do not claim a full accessibility or
   cross-browser audit from a few screenshots.

Known baseline build warnings: two intentionally unsupported Opus cross-book references and four
unresolved Markdown links (All Hallows Steve archive, Better Target Dummies idea, Sneaky Tweaks README
and brainstorm). Record existing warnings separately from regressions. Do not spend the redesign on
unrelated source cleanup.

Success means a visitor can find a mod, open the right guide, read a specific answer comfortably on a
phone, distinguish current functionality from plans, and browse the roadmap without fighting its
layout. The maintainer should still publish by editing the same files and running the same simple build.
