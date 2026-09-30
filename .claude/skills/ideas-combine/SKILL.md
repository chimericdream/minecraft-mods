---
name: ideas-combine
description: Merge one brainstorm session's raw `agentN.md` answers into a single de-duplicated, categorized `combined-ideas.md` in the same dated folder, with bracketed citations showing which answers suggested each idea and variant. Step 2 of 4 in the ideas pipeline (after ideas-brainstorm, before ideas-shortlist). Use when the user asks to "combine", "merge", or "consolidate" brainstorm results.
---

# Combine a brainstorm session

Step 2 of the ideas pipeline: `ideas-brainstorm` → **ideas-combine** → `ideas-shortlist` →
`ideas-promote`. Reference output:
`all-hallows-steve/ideas/brainstorms/2026-09-29/combined-ideas.md`.

The goal is a **complete, neutral** merge. Ranking and cutting happen in the next step, not here.

## Inputs

- Every `agentN.md` in one `brainstorms/<date>/` folder. Read them all in full.
- `prompt.md` (if present) for the existing-features list and tone constraints.
- `<mod>/ideas/README.md` (if present) for the active ideas.

## Rules

- **Nothing gets dropped.** Every idea from every answer appears somewhere, including odd, off-tone, or
  vanilla-duplicate ones. The shortlist is where they get cut, with a reason.
- **Merge duplicates** into one bullet and cite every answer that suggested it: `[1, 7, 13]`. The number
  of citations is the vote count the shortlist uses, so get it right.
- **Keep distinct takes as variants.** When answers agree on the idea but differ on how it works, list
  the differences as sub-bullets, each with its own citations.
- **Don't editorialize.** No effort estimates, no "good" or "bad" judgments. Neutral one- or
  two-sentence descriptions in plain language.
- If an idea is already an **active idea** in `ideas/README.md`, keep it but mark it
  `(active idea: [name](../../<theme>/<slug>.md))` so ideas-promote can fold new variants into it.
  Mark ideas that match an archived (shipped or dropped) idea as `(archived: [name](../../archive/<slug>.md))`.

## Structure of `combined-ideas.md`

1. `# <Mod> — Combined Feature Ideas`
2. Intro: which files were merged (`agent1.md`–`agentN.md`), what the bracketed numbers mean, a
   one-line summary of the existing features, and a link to `shortlist.md` (written next).
3. `## Contents` with anchor links.
4. Numbered `## N. <Category>` sections. Pick categories to fit the ideas (e.g. core system, lighting,
   crops, food, decoration, creatures, atmosphere, events, exploration, misc). Within each category,
   order ideas by vote count, highest first.
   - Bullet format: `- **Idea name** [1, 4, 9] — description.` then indented variant sub-bullets.
   - Call out the most-suggested ideas in their text ("The single most-suggested idea.").
5. A final `## Tone guidance from the brainstorms` section that collects the tone notes the answers
   volunteered: target feel, what to avoid, what to prefer, and any rules of thumb, with citations.

## Finish

Write the file with the Write tool (LF line endings). Report the idea count and the top few by votes,
and suggest `ideas-shortlist` next. Don't commit unless asked.
