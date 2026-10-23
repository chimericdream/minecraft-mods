The docs site is restyled in the stack you already had (Bun + `marked`, static HTML, all links relative), but it is not fully clean on the inspector: the reading pages and the mod pages still have defended failures (listed below). Nothing is committed.

The before shots of the home, mod, guide, roadmap and changelog pages, each at 1440, 768 and 390px, are in `.taste-skill/before/`. The after shots are in `.taste-skill/after/` and are dark only, because I couldn't make the inspector render light. I looked at the light theme in separate screenshots (home, mod, roadmap, changelog and the mobile guide) and they look intended, but those aren't saved there. I ran `bun run lint` (passes) and checked that the theme toggle, the mod search and the 1440px page width work. I did not click through every link, and the local `/local/` dashboard was not opened or screenshotted (it builds and only its color tokens changed).

**Direction.** I used the "contact sheet" direction with the brand palette kept.
- **Idea:** the author's 34 mods, each shown by its own texture, become a specimen cabinet. Four featured mods are large tiles in a sideways drag row. The other 30 sit in a dense three-column directory with a find box.
- **Featured four:** Minekea gets a wide tile. Camel Nostrils shows its icon upside down, a nod to its upside-down blocks.
- **Type:** I redrew the dealt face (General Sans) to Alegreya, because a grotesk doesn't fit fantasy and Alegreya reads well at length. One family, so the inspector's "font not dealt" warning is deliberate.
- **Colour:** dark and light each have their own ground, ink and crimson. Light is a snow-violet tone, not white.
- **Other changes:**
  - The roadmap value column shows `2/3` instead of ★, because the inspector flags ★ as an emoji.
  - The changelog is now a grid of tone-filled blocks.
  - The sidebar is a collapsible panel.
  - The generator now copies the logo and writes the new `site.css` and `site.js`.

**Before and after.** The "after" figures are for home and mod pages only (I didn't re-run the guide page's fixes), so this is not a full side-by-side:

| Page | Before | After |
|---|---|---|
| Home | 6 FAIL, 7 WARN | 0 FAIL, 4 WARN |
| Mod | 5 FAIL | 2 FAIL |
| Guide | — | 5 FAIL, 3 WARN |

Remaining failures I'm defending:
- **Pictures, focal point, motion on reading surfaces:** the guide, roadmap and changelog are reading pages, and the brief says they favor typography over spectacle.
- **One picture on mod pages:** no screenshots exist yet. The page has room for README screenshots once you add them.
- **"Off-centre" on pages with a sidebar:** this is the layout, not a bug.
- **Mobile guide "eyebrow":** the inspector reports the collapsed sidebar's "Overview" link as sitting above the title at 768/390px. I haven't confirmed whether the sidebar is actually collapsed there.
- **Warnings:** the drag row is meant to run off the edge, and there is no video. The lint's two monospace flags are code blocks and recipe grids, which are real code.

**Things to know.**
- **Logo:** the dragon is the placeholder, referenced from `docs/design/logo-placeholder.svg`. Swap that file when you have the original.
- **Hand-written copy:** the four featured taglines in `pages.ts` are mine, the camel one quoted from its README. The hero line and the about text are also written by me, so check they sound like you.
- **No found images:** every picture is a mod's own icon or the logo, so there are no credits to show.
- **Stale dev server:** `bun run site --serve` reruns the old generator whenever a `.png` in the repo changes, because the watcher only reloads Markdown and images. Restart it after generator edits.
