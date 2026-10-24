# Docs site

The public site at `https://chimericdream.github.io/minecraft-mods` is generated from files already in
the repo by `scripts/site/` (Bun + `marked`, no framework). Nothing is copied by hand: edit the
README, CHANGELOG, `ideas/` or in-game guide and rebuild.

- `bun run site` builds static HTML into `site/dist/` (gitignored).
- `bun run site --serve` builds, serves at `http://localhost:4000/` (`--port N`, `--open`), and rebuilds
  when a `.md`, `.yml`, `.png` or `gradle.properties` file changes. Generator code changes need a restart.
  Add `--no-watch` for a fixed preview during screenshot capture; use `--port N` to isolate worktrees.
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
(`scripts/site/recipes/types.ts`); the current one (`ascii.ts`) draws a box-drawing crafting grid with a
legend. To render real item textures, write another formatter and pass it to `buildGuide`.

All links are relative, so the site works under any base path.
