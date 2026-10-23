# Getting more eyes on the mods — brainstorm

_Drafted 2026-10-15. Goal: **more people using and enjoying the mods.** Not money, not Patreon, not
ads. The only metric that really matters here is "someone out there is playing with a thing I built," so
everything below is ranked by (joy per hour of effort), not by reach alone._

> Caveat: web research for this session was thin (search results were mostly generic), so community-rule
> specifics (subreddit self-promo rules, Modrinth/CurseForge ranking behaviour) are from general knowledge
> and **must be checked on each site's rules page before posting.** Everything about *your* repo is verified.

## 0. The honest diagnosis

Looking at the repo, the mods themselves are in good shape; **discoverability is the gap.** Concretely:

1. **CurseForge only.** No README or metadata links to a Modrinth page. A large share of Fabric/NeoForge
   players (and nearly all modpack tooling like Prism/Modrinth App users) never look at CurseForge.
2. **Manual releases.** Publishing is a manual step after tagging, so updates reach the site late or
   inconsistently. Fresh uploads and "updated recently" are the main free exposure both sites give you. See
   [CURSEFORGE-PUBLISHING-PLAN.md](../docs/history/CURSEFORGE-PUBLISHING-PLAN.md).
3. **Broken/placeholder metadata.** Of the `fabric.mod.json` files, 16 still say
   `"sources": "https://github.com/FabricMC/fabric-example-mod"` and 19 say `"homepage": "https://fabricmc.net/"`;
   most of the rest point at long-dead per-mod repos (`chimericdream/sponj`, `…/hopper-extreme`, etc.).
   That's what Mod Menu shows players, and what a modpack author clicks to find you. NeoForge's
   `neoforge.mods.toml` has `#logoFile` commented out everywhere. **This is the highest-value fix in the
   whole document and it's pure find-and-replace.**
4. **READMEs are written for a repo, not a storefront.** Typically 0–3 images; no GIFs. A mod page is judged
   in about 5 seconds by its first image and its first sentence.
5. **No "face".** There's no single place (site, Discord, social account) tying the ~30 mods together as a
   *family*, even though they genuinely are one (shared lib, shared in-game guide system, shared style).

The good news: **MC 26.x is early in its life.** Many popular 1.21 mods haven't ported yet. Being among the
first working, polished options in a niche on 26.2 is a real, time-limited advantage (Stack It Up is already
using exactly this).

## 1. Quick wins (an evening or two, high payoff)

1. **Fix the metadata** (see #3 above) in one sweep: `homepage` → the mod's CurseForge/Modrinth page,
   `sources`/`issues` → `https://github.com/chimericdream/minecraft-mods` (+ the mod's subfolder),
   `NeoForge logoFile` → the same icon the Fabric build uses. A tiny check in `bun run status` or CI could
   fail the build on `fabric-example-mod` so it never regresses. _(I can do this.)_
2. **Publish on Modrinth too** (same jars, same changelog). Follow on to the pipeline plan; it's "free".
   Cross-link the two pages in each description ("Also on Modrinth/CurseForge").
3. **Page anatomy that works** (both sites):
   - **First line = the hook**, ≤ 15 words, in player language: "Hoppers that go *up*, pull from the sides, and
     filter." Not "Upgrade your hoppers!".
   - **First image = the money shot**, not a recipe grid: a GIF or a clean 16:9 screenshot of the best moment.
   - 3–6 gallery images with **captions**; one GIF per headline feature.
   - Feature bullets, then "Works with" (Fabric & NeoForge, client+server, 26.2), then dependencies in plain
     words ("needs ChimericLib — one click"), then the in-game guide (Opus) mention.
   - Use **every relevant category/tag** the sites allow; they power browse pages and search.
4. **Use the tooling you already built.** The `mc-marketing-screenshots` skill can produce polished 1920×1080
   gallery shots headlessly. Run it per mod and keep the PNGs in `<mod>/docs/gallery/` (committed), so pages
   can be refreshed after any visual change. Add a **GIF mode** (capture N frames → `ffmpeg` palette GIF/WebM)
   — animated hoppers, swinging playground equipment, sponj soaking up an ocean are *made* for GIFs.
5. **Short "release notes" posts that sell**, not just list: lead with the one thing a player will care about
   ("Filters can now be copied in a crafting grid!") above the changelog.
6. **Pin the right file/version**: make sure "latest" on each page is the 26.2 release, and mark old MC versions
   so players don't download the wrong one and leave a 1-star "crashes".

## 2. Where to post (and how not to be annoying)

Rule of thumb: **show, don't pitch.** A 10-second clip of the thing working beats any paragraph. Post when
you have something to *show* (new feature, new mod, a cool build), not on a schedule.

| Venue | What fits | Notes |
| --- | --- | --- |
| **Modrinth** (project, gallery, "versions" feed) | Everything | Its search ranks by recency/follows/downloads; early follows matter. Join their Discord's showcase/author channels |
| **CurseForge** | Everything | Keep "Updated" fresh; fill all tags; respond to comments |
| **r/feedthebeast, r/fabricmc, r/NeoForge, r/Minecraft, r/MinecraftMods, r/minecraftbuilds** (builders: Minekea, Houdini Block), r/redstone & **r/technicalminecraft** (Hopper X-Treme, Better Portal Linking, Flat Bedrock) | Short clip + one-line description + link in comments | **Check each sub's self-promo rules.** Some require a flair or limit to one mod-post per N days. r/Minecraft rewards visuals/humour (Camel Nostrils, Creeper-in-a-box from Toy Box) |
| **Mod-loader Discords** (Fabric, NeoForged, Modrinth, Architectury) | `#mod-showcase`-type channels; answer questions; help others | Being useful in `#dev` chat is the best long-term marketing for the *library* mods (ChimericLib, Opus) |
| **Minecraft Forum / Planet Minecraft** | PMC is still a lively builder community: Minekea/Houdini Block showcase builds, map-style posts | Cross-post with a build, not a mod ad |
| **Bluesky / Mastodon / (X)** | Short clips with hashtags `#minecraft #minecraftmods #fabricmc #neoforge` | The modded-MC crowd lives on Bluesky/Mastodon more than it used to; low effort if clips already exist |
| **YouTube Shorts / TikTok** | 15–30 s "this mod adds X" clips | The best *discovery* channel for casual players. One reusable clip → Shorts + TikTok + Reddit + Bluesky |
| **Hacker News / Lobsters** | Only the *engineering* angle (e.g., "one Architectury monorepo, 30 mods, an agent doing releases") | Not for the mods; for the dev story. Optional |
| **Modpack authors/servers** | Direct, polite note: "Here's a mod you may like; permission to include it is already granted by MIT license" | Modpack inclusion is the single biggest driver of downloads in this ecosystem. Make it easy: clear license, both loaders, dependencies minimal |
| **Mod-list sites** (Modpack Index, CurseForge "Related", Planet Minecraft, 9minecraft-style aggregators) | Automatic once posted | Some aggregators re-host jars — accept it, but keep links in the description canonical |

## 3. Ideas by hook (the mods have different audiences)

| Mod | Who would love it | Hook / content idea |
| --- | --- | --- |
| **Hopper X-Treme** (strongest "wow") | Redstone/tech players, sorting-system builders, server admins | Side-by-side GIF: vanilla hopper clock vs. X-Treme; "hoppers that push **up**" is a one-liner nobody has seen; 30 s demo of a compact sorter; filter-copy-in-crafting-grid clip |
| **Stack It Up** | Anyone who used AllStackable on older versions | Title/description should contain "AllStackable" (searchable lineage). Post in places asking "is there AllStackable for 26.x?" — search Reddit/Discord for exactly that |
| **Next Update Now** | Players impatient for the next MC drop; YouTubers covering snapshots | Timing play: publish/refresh around each snapshot/release; short "see the next update content today" clips; pitch to snapshot-coverage creators |
| **Minekea** (biggest content volume) | Builders, decorators | A gallery of builds **using** it (you already have a tiled 126×126 demo world!). Planet Minecraft + r/minecraftbuilds posts; "one build, four blocks you didn't know you needed" mini-series |
| **Sponj / Log All The Things / Houdini Block / Hang From Slabs** | Builders, survival players, tech players | Tiny single-feature clips; each is a perfect 10-second Short |
| **Camel Nostrils / Toy Box / Artificial Heart** | Meme-ier audience, decor fans | Humour ships on its own. The camel nostrils upside-down bed deserves its own "why does this exist" post. Toy Box's creeper-in-a-box: jump-scare GIF |
| **Better Portal Linking / Flat Bedrock / Enchantment Numbers Fix / Effective Gear** | Technical & speedrunner-adjacent, QoL seekers | Bundle as "tiny vanilla-friendly fixes" collection; post Better Portal Linking in nether-travel/tech subs with a before/after of unwanted portal links |
| **Villager Tweaks / Miniblock Merchants / Sneaky Tweaks / Shulker Stuff / Banner Tweaks / Archaeology Tweaks / Beacon & Conduit Tweaks** | "Vanilla+" players, modpack authors who want light QoL mods | "Vanilla-friendly" is the brand: pitch as a **Vanilla+ QoL family** with a single landing page and a pre-made "starter set" modpack (see §4) |
| **Athenaeum / Opus** | Pack makers, server owners, other mod devs | Opus is a real devtool: *"Write your in-game guide in Markdown, ship it as a datapack/mod."* Pitch to the modding community (Fabric/NeoForge Discords, dev blog post, an example repo). Athenaeum shows it off |
| **ChimericLib** | Other mod devs | Small dev blog / README "quick start"; Modrinth/CurseForge tags for library. Don't push to players |
| **Blacklight / Playgrounds / Cobblicious** (planned) | — | **Build in public**: post concept art/early clips when each starts. People follow a thing they watched grow; also gives you design feedback cheaply |

## 4. "Family" ideas (the multiplier)

1. **Landing page** (GitHub Pages from this repo, or a Modrinth *organization* page): grid of all mods with
   hero image, one-liner, loader badges, download links. Modrinth has Organizations; CurseForge has a
   "Projects by author" page — also fill out the author profile (banner, bio, links).
2. **Starter modpacks** — "Chimeric Vanilla+" (QoL set) and "Chimeric Builder" (Minekea + Houdini Block + Hang From Slabs +
   Artificial Heart …) on Modrinth (`.mrpack`) and CurseForge. A modpack page is *another search surface* that
   links to every mod. `scripts/create-modpacks.ts` already builds folders of jars — extending it to emit an
   `.mrpack` index is small.
3. **Consistent branding**: one icon style, one banner template, one badge style across pages so the family
   is recognisable (the README badges already are). A header image template generated from a script keeps it
   effortless.
4. **In-game cross-promotion with taste**: each mod's Opus guide could end with a "More from this author"
   page (opt-out in config). Mod Menu already lists everything installed; make sure the descriptions + icons
   there are good (see metadata fix).
5. **Translations**: a handful of lang files (es, pt_br, ru, zh_cn, de, fr) open entire markets. Translation is
   exactly what a model can do well in bulk; ask for community review via a GitHub issue template
   ("improve my translation"). Pages in those languages get found by people searching natively.

## 5. Content calendar (a gentle, sustainable one)

Don't try to do everything. A rhythm that fits "build things, share when it's fun":

- **Every release:** screenshot/GIF, 2-sentence hook, changelog → CurseForge + Modrinth (automated) → *one* social
  clip (manual, 10 min).
- **Monthly:** one "spotlight" — pick the mod with the best story, make one polished clip, post it in 2–3 venues.
- **Quarterly:** refresh gallery images (the screenshot skill makes this nearly free), re-check pages are tagged
  for the newest MC version, check for popular unported mods that overlap yours.
- **On each Minecraft drop:** the port-day window. Be early with the ones that have demand; post a "now on 26.x"
  note.

## 6. Community & relationships (the slow, durable stuff)

- **Respond to every comment/issue within a day or two**, even just "thanks, looking." Maintainer
  responsiveness is a hidden ranking factor in human trust — people recommend mods whose author answers.
- **Make GitHub issue templates** (bug / suggestion) and a pinned "what's coming" issue or Discussions board:
  public roadmap = followers. The `ideas/` folders are already a roadmap; a "vote on what's next" Discussion
  using the shortlist items is nearly free and creates participation.
- **A small Discord** (or just GitHub Discussions + a Modrinth/CF links page) so players can show builds and
  ask questions. Only worth it if it's low-maintenance; Discussions is the lighter option.
- **Look for the "I used X" moments**: search `site:reddit.com "hopper xtreme"`, Modrinth reviews, YouTube
  descriptions for your mod names; reply with thanks. That's the thing you said gives you joy — and it
  compounds, because people feel seen.
- **Collaborate**: ping creators of adjacent mods ("our mods work together; want a cross-link?"). Cross-links
  between friendly mods are genuine, durable traffic.
- **Be a good citizen in the ecosystem**: file/answer issues in Architectury/YACL/Fabric/NeoForge repos; people
  click through to the profile of useful contributors.

## 7. Measurement without being a chart-watcher

You don't need analytics to enjoy this, but a little signal tells you what to repeat:

- Weekly glance at CurseForge/Modrinth download + follower counts per mod → a tiny `bun run stats` script that
  appends to a CSV (Modrinth has a public API; CurseForge needs the API key you'll have anyway for publishing).
  The existing `dashboard/` could show it.
- Note which post preceded each bump; do more of that.
- Track "mentions found" as a joy metric: a plain `MENTIONS.md` with links to anyone's video/post/pack that uses
  your mods. Great for morale and for outreach thank-yous.

## 8. Things to avoid

- **Don't spam** the same post to ten subs on one day. Space them; respect flair/rules.
- **Don't trade downloads for ads/monetization** — you said no; the same applies to link shorteners, ad-wall
  "download" sites. Keep direct links.
- **Don't over-promise on planned mods** (Blacklight/Playgrounds/Cobblicious pages say "planned"): fine for
  build-in-public posts, but don't publish a page until the first working version exists.
- **Don't let page ≠ reality.** The fastest way to lose trust is a "26.2" page whose jar crashes on a server. The
  existing GameTest + visual smoke test work is a marketing asset: mention "tested on Fabric and NeoForge" where true.

## 9. Concrete next actions (ordered)

1. Metadata sweep: `fabric.mod.json` homepage/sources/issues + NeoForge `logoFile` for all 33 mods (+ lint).
2. Create Modrinth account/organization + pages for the 5 most visual mods (Hopper X-Treme, Minekea, Sponj,
   Stack It Up, Better Portal Linking). Wire into the publishing pipeline.
3. Extend `mc-marketing-screenshots` with a short GIF/WebM capture mode; produce one hero GIF per released mod.
4. Rewrite the first two lines of each released mod's description in player language (I can draft all of them
   from the READMEs for your edit pass).
5. Stack It Up + Next Update Now: targeted "looking for this" searches and helpful replies.
6. Starter modpack: "Chimeric Vanilla+" (`.mrpack` + CurseForge pack).
7. One landing page (GitHub Pages) + pinned Discussions roadmap seeded from `ideas/`.
8. Pick one weekend for a "launch week" of the Minekea gallery: demo-world flythrough video + PMC post +
   r/minecraftbuilds post.

## 10. What I can do autonomously for you (just say go)

- Metadata sweep + lint (items 1 above).
- Draft hook lines, short descriptions, tag lists, and gallery shot lists per mod.
- Generate gallery PNGs with the headless screenshot skill (and the GIF mode once added).
- Write the landing page, `.mrpack` generator, and `bun run stats` script.
- Translate lang files for review.
- Keep a `docs/marketing/<mod>.md` page-copy file per mod so page text lives in the repo and the publishing
  pipeline can push it to both sites.
