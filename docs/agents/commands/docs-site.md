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
(`scripts/site/recipes/types.ts`); the current one (`grid.ts`) draws a crafting grid of letter cells with
a legend (`ascii.ts` is the plain-text alternative). To render real item textures, write another
formatter and pass it to `buildGuide`.

All links are relative, so the site works under any base path.

## Design

The look follows [docs/SITE-DESIGN-BRIEF.md](../../SITE-DESIGN-BRIEF.md): a board of flat tiles, Young Serif
over Source Serif 4 (Google Fonts), one crimson accent on a violet ground, light and dark themes that follow
the OS with a remembered manual toggle. Where things live:

- `theme.ts` holds the color tokens (`THEME_CSS`). The local dashboard imports them too, so keep the
  variable names (`--bg`, `--fg`, `--muted`, `--card`, `--line`, `--accent`, `--good`, `--bad`, `--warn`, `--mark`).
- `styles.ts` holds the site stylesheet and the font links; `layout.ts` the page shell, header, footer and sidebar.
- `client.ts` holds the only script (`site.js`): theme toggle, home-page mod finder, sidebar folding and
  entrance reveals. Every effect has a CSS fail-safe and works without script.
- `pages.ts` builds the home board, the mod overview header and the combined changelog.
- The brand mark is copied from `docs/design/logo-placeholder.svg` to `_site/logo.svg` (see `LOGO_SOURCE` in
  `build.ts`); replace that file with the original vector and every page picks it up.
- Home-page data that is not in the Markdown comes from the repo: loaders and tagline from each README,
  the CurseForge link from `curseforge_project_id` in the mod's `gradle.properties`, the Minecraft version
  from the root `gradle.properties`.
- The four featured mods are the `FEATURED` list in `pages.ts`.
