---
name: ideas-shortlist
description: Turn one brainstorm session's `combined-ideas.md` into a ranked, scored `shortlist.md` (effort, value, votes, tiered by value-for-effort, with a cut list and an empty "My Vote" column), then hand it to the user to vote Yes / Maybe / No. Step 3 of 4 in the ideas pipeline (after ideas-combine, before ideas-promote). Use when the user asks to "shortlist", "rank", "prioritize", or "curate" brainstormed ideas.
---

# Shortlist a brainstorm session

Step 3 of the ideas pipeline: `ideas-brainstorm` → `ideas-combine` → **ideas-shortlist** →
`ideas-promote`. Reference output: `all-hallows-steve/ideas/brainstorms/2026-09-29/shortlist.md`.

Unlike the combine step, this one is **opinionated**. Make real calls and explain them.

## Inputs

- `combined-ideas.md` in the session folder (vote counts, variants, tone guidance).
- The mod's existing code and features. Skim enough to judge what reuses existing systems. Reuse is the
  biggest effort discount.
- `<mod>/ideas/README.md`. Active ideas are **not re-ranked**. List them once under "Already active"
  with links, and mention any new variants the session found for them.

## Scoring

- **Effort**, for a two-loader (Fabric + NeoForge) Architectury mod, counting art, datagen and
  rendering, not just code:
  - **S**: a day or two, mostly assets and data, reuses existing code.
  - **M**: a new block or block entity, a new interaction, or a moderate rendering change.
  - **L**: a new entity, custom rendering, worldgen, or a new UI.
  - **XL**: a system that touches many other features.
- **Value**, ★ to ★★★: fit with the mod's identity and tone, and how much players would notice it.
- **Votes**: citation count from `combined-ideas.md`. A signal, not the deciding factor.

Rank by **value relative to effort**. Cheap, high-value ideas come first even when bigger ones are more
exciting. Favor ideas that deepen what the mod already does over ideas that spread it sideways.

## Structure of `shortlist.md`

1. `# <Mod> — Feature Shortlist`, then an intro linking `combined-ideas.md` and the cut section.
2. `## How items were scored`, with the definitions above and one paragraph on the ranking philosophy.
3. `## Ranked list`: one table with columns `# | Feature | Effort | Value | Votes | My Vote`, split by
   bold tier rows (**Tier 1 — Quick wins**, **Tier 2 — Solid mid-size features**, **Tier 3 — Big
   bets**). Aim for roughly 20–26 items. Leave **My Vote** empty.
4. One `### N. Name — Effort · Value` section per item, grouped under tier headings. Give each item 2–5
   sentences saying what it is, why it earns its place, and **a scoped-down recommendation** (which
   variant to pick and what to skip). Cross-reference related items by number (`#16`).
5. `## Honorable mentions`: near-misses worth doing if a related feature gets built.
6. `## What was cut and why`: every remaining idea from `combined-ideas.md`, grouped by reason, e.g.
   scope creep (belongs in a different mod), already in vanilla, hard to build for the payoff,
   off-tone or mechanically odd, too vague to act on.
7. `## Suggested first arc`: one short paragraph naming a coherent first set of items, and what follows.

## Finish

Write the file with the Write tool (LF line endings). Then ask the user to fill in **My Vote** with
**Yes**, **Maybe**, or **No** for each row. The user owns the votes. Never fill them in or guess them.
Once they've voted, `ideas-promote` is next. Don't commit unless asked.
