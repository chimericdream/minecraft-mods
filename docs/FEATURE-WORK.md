# Feature work: from idea to ready-to-build

How feature ideas are captured, brainstormed, chosen, and refined in this monorepo, for every mod. This
doc stops at **ready to build**. Once an idea is ready, building it follows the normal conventions in
[`docs/agents/`](agents/): branches, tests, and the changelog and release rules.

The four `ideas-*` skills in `.claude/skills/` automate the steps below. This doc is the source of
truth. If a skill and this doc disagree, the doc wins, and the skill should be updated to match.

Rules that apply to every brainstorm, whatever the mod (what never to propose, and what to always
favor), live in [`docs/BRAINSTORMING-RULES.md`](BRAINSTORMING-RULES.md).

## Who decides what

Agents propose; the user decides. In particular, **only the user**:

- votes on shortlists (Yes / Maybe / No),
- approves a mod's identity statement,
- resolves open questions that change what gets built,
- marks an idea **Ready**, and
- drops an idea.

Agents can draft any of these, recommend, and ask, but never fill them in on the user's behalf.

## Where ideas live

Each mod keeps all of its feature ideas in its own `ideas/` folder:

```
<mod>/ideas/
  README.md                     sections: Identity, Active ideas (by theme), Inbox, Archive
  <theme>/<idea-slug>.md        one file per active idea
  archive/<idea-slug>.md        shipped or dropped ideas, kept for the record
  brainstorms/<YYYY-MM-DD>/     one folder per brainstorm session, never edited afterwards
    prompt.md                   the prompt every brainstormer received
    agent1.md … agentN.md       raw answers, verbatim
    combined-ideas.md           merged and de-duplicated
    shortlist.md                ranked and scored, with the user's votes
  brainstorms/<YYYY-MM-DD>-legacy/  a pre-2026-09-29 backlog, verbatim and unvoted
```

`all-hallows-steve/ideas/` is the reference example. New mods get an `ideas/README.md` from the
`scripts/init-mod.sh` template.

- **Ideas for mods that don't exist yet** are filed as GitHub Issues labeled `ideas` (and `enhancement`).
  When a mod is scaffolded, move the issue's content into the new mod's `ideas/README.md` inbox and close
  the issue.
- **Don't keep ideas anywhere else**: not in a mod's `README.md` or `CHANGELOG.md`, not in new root-level
  lists, and not in a `POTENTIAL_FEATURES.md` (retired; see [Legacy backlogs](#legacy-backlogs)).
- **Build plans are not ideas.** Refactor or migration plans describe *how* to
  build something already decided. They belong to implementation and live wherever that work does.

## The mod's identity statement

`ideas/README.md` opens with one to three sentences saying what the mod is and what tone it keeps, e.g.
"a pumpkin mod with a grounded autumn tone, not a general seasons mod, and not campy or horror". Every
brainstorm prompt includes it, and the shortlist's **Value** score measures fit against it. When a
brainstorm keeps pulling in a direction the statement rules out, that's a question for the user, not a
reason for an agent to change the statement.

## Lifecycle of an idea

| Stage | Where it lives | How it moves on |
|---|---|---|
| **Inbox** | A one-line bullet under `## Inbox` in `ideas/README.md` | Picked up by the next brainstorm or shortlist |
| **Brainstormed** | `brainstorms/<date>/`, merged into `combined-ideas.md` | `ideas-shortlist` ranks it |
| **Shortlisted** | A row in that session's `shortlist.md` | The user votes |
| **Exploring** | `<theme>/<slug>.md`, listed in the README | Refined until the user marks it Ready |
| **Ready** | Same file, `Status: **Ready**` | Implementation starts. This doc's part ends here. |
| **Building** | Same file, `Status: **Building**` plus a link to the branch or plan | Ships or is dropped |
| **Shipped / Dropped** | `archive/<slug>.md` | Done. Kept so the idea isn't brainstormed again from scratch. |

Only Yes and Maybe votes become idea files. No votes stay in the shortlist, which is a record of the
decision.

A quick thought the user wants to note down goes in the **Inbox**. It needs no ceremony, no file, and
no scoring. Small, obvious ideas can skip brainstorming entirely: with the user's OK, an inbox item can
be promoted straight to an idea file.

## Idea files

Every idea file stands alone: someone should understand the idea without opening the shortlist. The
structure, which `ideas-promote` creates:

```markdown
# <Idea name>

> Shortlist #<n> · <Tier> · Effort **<E>** · Value <★> · Votes <v> · My vote **<Yes|Maybe>**
> Status: **Exploring**

## Description
## Decisions
## Brainstorm variants
## Related
## Open questions
```

An idea that didn't come through a shortlist (promoted from the inbox or a legacy backlog) replaces the
shortlist line with `> Source: <where it came from, linked> · Status: **Exploring**`.

- **Description** starts as a verbatim copy of the shortlist entry and then becomes the working spec.
  Edit it freely as the idea firms up, and include an explicit "out of scope" line when that helps.
- **Decisions** records resolved questions as dated one-liners, newest last:
  `- 2026-10-02 — Uses vanilla candles; no new candle item.` A decision is recorded only after the
  user has made it.
- **Brainstorm variants** is reference material, cited back to the session files. Don't rewrite it.
  Later sessions append their own `### From brainstorms/<date>` sub-section.
- **Related** links other idea files, including across themes, with a short reason for each.
- **Open questions** lists what's still undecided. When the user answers one, move the answer to
  Decisions and update the Description.

## Ready to build

An idea is ready when **the user says so**. Before asking, check that:

1. No open question would change what gets built. Questions that can be decided during implementation
   can stay, labeled as such.
2. The Description reads as a spec: what the player sees and does, what's in scope, and what's
   explicitly out of scope.
3. Effort has been re-estimated against the rubric below now that scope is fixed.
4. Dependencies are named: other ideas, chimeric-lib helpers, and loader-specific work.
5. There's a one-line note on how it will be verified: JUnit, GameTest, a visual smoke test, or manual
   steps for the mod's `TEST_PLAN.md`.

Then set `Status: **Ready**` and stop. Starting implementation is a separate request.

## Scoring rubric

Shared by every shortlist and every re-estimate.

- **Effort**, for a two-loader (Fabric + NeoForge) Architectury mod, counting art, datagen and
  rendering, not just code:
  - **S**: a day or two, mostly assets and data, reuses existing code.
  - **M**: a new block or block entity, a new interaction, or a moderate rendering change.
  - **L**: a new entity, custom rendering, worldgen, or a new UI.
  - **XL**: a system that touches many other features.
- **Value**, ★ to ★★★: fit with the mod's identity statement, and how much players would notice it.
- **Votes**: how many independent brainstorm answers suggested it. A signal, not the deciding factor.

Rank by **value relative to effort**, and prefer ideas that deepen what the mod already does over ideas
that spread it sideways.

## Retiring ideas

When an idea ships or is dropped:

1. Set `Status: **Shipped**` (with the version) or `Status: **Dropped**` (with a one-line reason from
   the user).
2. Move the file to `ideas/archive/<slug>.md` and fix the links that pointed at it.
3. Remove it from the README's active list and add a one-line entry under `## Archive`.

Brainstorm prompts list archived ideas so the same ground isn't covered again unless the user asks.

## Skills

| Skill | Does |
|---|---|
| `ideas-brainstorm` | Writes `prompt.md` and collects answers from subagents or pasted from other assistants |
| `ideas-combine` | Merges one session's answers into `combined-ideas.md` |
| `ideas-shortlist` | Scores and ranks the ideas into `shortlist.md`, then waits for the user's votes |
| `ideas-promote` | Turns Yes/Maybe items into idea files, updates the README, and moves ideas between themes |

## Legacy backlogs

Before this process, ideas lived in a `POTENTIAL_FEATURES.md` in each mod (mostly agent-written
brainstorms) and in a root `ideas.md` (the user's own older notes). Both were retired on 2026-09-29:

- Each mod's backlog and its `ideas.md` section were moved **verbatim** into
  `<mod>/ideas/brainstorms/<date>-legacy/` (as `potential-features.md` and `ideas-md.md`), dated by the
  backlog's last commit. Nothing in them has been voted on. When work on a mod starts, run
  `ideas-shortlist` against its legacy folder, the same as a normal session.
- Each mod's `ideas/README.md` has a **draft** identity statement, lifted from its backlog or its
  `mod_description`, waiting for the user's approval. Three mods (blacklight, cobblicious, playgrounds)
  don't have one yet.
- **chimeric-lib is the exception.** Its backlog was already treated as a working list, especially the
  GameTest helper backlog, so its items became idea files straight away. The docs, test plans, and
  source comments that referenced the backlog now point at those files.
