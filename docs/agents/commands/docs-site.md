# Docs site

The public site at `https://chimericdream.github.io/minecraft-mods` is generated from files already in
the repo by `scripts/site/` (Bun + `marked`, no framework). Nothing is copied by hand: edit the
README, CHANGELOG, `ideas/` or in-game guide and rebuild.

- `bun run site` builds static HTML into `site/dist/` (gitignored).
- `bun run site --serve` builds, serves at `http://localhost:4000/` (`--port N`, `--open`), and rebuilds
  when a `.md`, `.yml`, `.png` or `gradle.properties` file changes. Generator code changes need a restart.
  It also writes the **local-only** dashboard at `/local/` (open GitHub issues and PRs via `gh`, ideas
  pipeline status; `--refresh` bypasses the 5-minute GitHub cache). `/local/` is never built without
  `--serve`, so it is never deployed.
- `.github/workflows/pages.yml` deploys `site/dist` on every push to `main`
  (repo Settings > Pages > Source must be "GitHub Actions").

## What gets published

Per mod: README (overview page), CHANGELOG, everything under `ideas/` (brainstorms included), and the
in-game guide at `<mod>/common/src/main/resources/assets/<id>/opus-books/guide/en_us/`. `TEST_PLAN.md`,
`docs/` and the design notes are not published. Site-wide: the roadmap table (`/ideas/`) and the combined
changelog (`/changelog/`).

## Guides and recipes

`scripts/site/guide.ts` renders the Opus Markdown dialect: frontmatter, `> [!TIP]` callouts,
`texture:` images (copied from the owning mod's resources), `item:`/`entity:` references (shown as text)
and ```` ```recipe ```` blocks. Recipes go through a `RecipeFormatter`
(`scripts/site/recipes/types.ts`); the current one (`grid.ts`) draws an HTML crafting grid of lettered
slots with a legend. To render real item textures, write another formatter and pass it to `buildGuide`.

## Design

`scripts/site/layout.ts` holds the shell, the colour tokens (`THEME_CSS`, shared with `/local/`) and all
the CSS; `site.js` (also in `layout.ts`) is the only script: the theme switch and the home page's
catalogue filter. Light and dark follow the OS, and the theme button cycles auto/light/dark and
remembers the choice. Fonts are Young Serif (display) and Source Serif 4 (text) from Google Fonts.
Each mod's contact-sheet frames are up to eight of its own item (or block) textures, picked by
`scripts/site/sheet.ts`; mods without textures show their icon. The brand mark is copied from
`docs/design/logo-placeholder.svg` (replace that file to swap it). The four mods featured on the home
page are the `FEATURED` list in `scripts/site/pages.ts`. Mod pages link to CurseForge through
`curseforge_project_id` in each mod's `gradle.properties`.

All links are relative, so the site works under any base path.
