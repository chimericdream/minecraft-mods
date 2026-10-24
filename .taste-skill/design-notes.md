# Chimericdream catalogue redesign

## Subject inventory

- Chimericdream means a creature made from mixed parts, echoed by a collection spanning furniture, practical fixes, and absurd ideas.
- Crimson dragon, twin snowy mountains, crescent moon; the existing replaceable SVG is the brand asset.
- Deep and shadow violet, snow, moon grey, and crimson are the brand's inks.
- 34 mods generated from the repository, with Fabric and NeoForge metadata.
- Minekea has a real in-game book called the Minekea Catalogue.
- Hopper X-Treme upgrades a familiar vanilla block, including upward hoppers.
- Effective Gear gives bonuses to armor, weapons, and tools.
- Camel Nostrils is explicitly weird and spouse-suggested.
- Real pixel artwork is available in icons and resource textures.
- README, guide, changelog, and ideas already form a per-mod set of documents.
- The roadmap exposes 216 active ideas, including votes, effort, and status.
- Everything regenerates with Bun and marked, including recipe diagrams and nested guide navigation.

## Argument and copy deck

1. Hero: this is a browsable collection of Minecraft mods. Proof: real repository count and loader metadata. H1: "Minecraft, with character". Supporting sentence: "34 mods for Fabric and NeoForge, from useful little fixes to furniture, field guides, and wonderfully questionable ideas." Action: "Find a mod".
2. Featured catalogue sheet: the collection spans craft, utility, and silliness. Heading: "Four ways to play". Four entries: Minekea, Hopper X-Treme, Effective Gear, Camel Nostrils. Descriptions summarize their existing READMEs; every entry links to its actual overview and guide.
3. Dense index: visitors can find a mod quickly. Heading: "The collection". Search label: "Find your next mod". Default shows the other 30; searching includes all 34. The existing name, version, description, status, loader, and document links are the complete copy deck for each entry; they remain generated from source.
4. Closing back of the catalogue: follow changes and ideas. Heading: "Still being made". Text: "Follow what has shipped, what is in progress, and the ideas taking shape." Links: Roadmap, Changelog, GitHub. The wordmark closes the page.

No claim supports a founder's letter, so the dealt about section is omitted. No founder biography or proof is invented.

## Direction and idea

Direction: The page is the object, seed 62318, with palette kept.

On this page, chimericdream's mixed collection and the real Minekea Catalogue become a tabbed workshop catalogue, with each mod's documents bound together as a reading folio.

Live thing: search the real 34-mod catalogue by name, description, loader, or status, with a release filter and an accessible result count. Without JavaScript all content and document links are present.

## Dealt adaptations and redraws

- The initial section argument was corrected to comma-separated CLI input. The final sheet has showcase, list, about, footer.
- Hero redraw: Card over a dim room required a nonexistent room photograph and conflicted with the no-stock constraint. Replacement: Tabbed folder stack.
- Type redraw: Anton's sports/nightlife capitals conflicted with the brief's fantasy book/catalogue character. Replacement: STIX Two Text. Use its roman display and reading face; retain Work Sans from the initial deal for compact navigation and controls, giving the distinctive serif/sans pair requested by the brief.
- Showcase alternative: Band of unequal panels can hold four different mods and their existing raster artwork; Output slips along the edge required a full-bleed photograph and six textual specimens.
- List alternative: Two-column menu, photo bullets fits 30 individual mods; the dealt accordion assumed six to nine categories which do not exist.
- Brand violet is retained explicitly under the skill's existing-brand exception.
- Images are exclusively existing project artwork; no stock, generated art, or invented screenshots. Large real raster mod artwork serves as the picture material. No video honestly fits the supplied assets.
- Reading surfaces keep their full content and navigation; they receive typography, spacing, responsive navigation, callout and table treatments rather than scroll spectacle.
- Motion: the featured catalogue's heading holds beside its entries; the tabbed sheets settle on entrance, followed by staggered text and picture arrivals. Controls respond to hover/focus. Reduced motion keeps every final state.

## Before critique

ROUND 0
Seen in one second: a conventional documentation card grid.
Idea (one sentence): none beyond a list of mods.
Live thing: roadmap filters only; no catalogue search.
Layout words by section: intro=paragraph / mods=equal cards.
Scores: design 4.5 · usability 6.5 · creativity 3.5 · content 8 → overall 5.2
Caps applied: inspector failures; weak focal point; small body text.
Top three problems (most visible first), each with the screenshot it shows in and the fix:
1. desktop-fold.jpg: all 34 mods have equal visual weight; feature the requested four with unequal geometry and list the rest densely.
2. mobile-fold.jpg: long generic intro and no direct finding tool; shorten the introduction and add a real catalogue search.
3. desktop-page-01.jpg: tiny grey descriptions and repeated outlined boxes; increase reading sizes, use full ink, separate by tone and space.

Baseline inspector: 6 FAIL, 8 WARN. Baseline source lint: 9 FAIL, 14 WARN. Baseline screenshots are in before/ and the complete five-page, three-width, two-theme matrix is in before/matrix/.
