---
name: ideas-identity
description: Write or refine one mod's identity statement (the opening section of `<mod>/ideas/README.md`) by interviewing the user about their original vision, drafting in chat, and writing it only once approved — including boundary notes for addon, sibling, or overlapping mods. Step 0 of the ideas pipeline, before ideas-brainstorm. Use when the user asks to "refine/approve/write an identity statement", when a mod's statement is still marked Draft, or when starting a brand-new mod idea.
---

# Identity statement

Step 0 of the ideas pipeline: **ideas-identity** → `ideas-brainstorm` → `ideas-combine` →
`ideas-shortlist` → `ideas-promote`. Reference results: `archaeology-tweaks/ideas/README.md` (addon
boundary), `athenaeum/ideas/README.md` (sibling mod), `but-what-about/ideas/README.md` (migration plan).

What an identity statement is, and why every brainstorm and shortlist leans on it, is defined in
[`docs/FEATURE-WORK.md`](../../../docs/FEATURE-WORK.md) ("The mod's identity statement"). That doc wins
if this skill disagrees with it. **The user approves statements.** You gather, ask, and draft; you never
write one as final without the user's go-ahead.

Work one mod at a time. Drafts are often older than the mod's real ambitions, so the interview matters
more than the drafting.

## 1. Gather evidence

Read, don't guess:

- `<mod>/ideas/README.md`: the current statement, or the "Draft, needs approval" text and where it came
  from (a `mod_description`, a legacy backlog, or nothing).
- `<mod>/README.md` and `gradle.properties` (`mod_description`): what the mod does today. The README can be
  stale or a copy of another mod's text; say so if it is.
- `<mod>/ideas/brainstorms/*legacy*/`: `ideas-md.md` is the user's own older notes, and
  `potential-features.md` is mostly agent-written. Note where they disagree. Lines such as "everything
  below stays inside that lane" in the agent-written file are not the user's decisions.
- Sibling mods that might overlap (other `ideas/README.md` identity sections).

## 2. Interview about the vision

Lead with what you found, in a few lines, then ask. Shipped features are a floor; the user's original
vision is often broader. Use `AskUserQuestion` for choices, and keep it to two or three questions per
round:

- **Scope**: what the mod covers, and where it stops.
- **Tone**: relative to vanilla, for example vanilla-friendly and additive, or ambitious.
- **Neighbors**: does part of the vision belong in another mod, an addon, or a sibling? Does it overlap an
  existing mod?
- **Constraints**: for example a modpack that ships the mod (it must keep working in existing worlds), or
  "server-side only".

Open-ended answers are fine and often better than the options. Follow what they actually say, including
requests to hold off or change direction.

## 3. Draft in chat

Show the statement (one to four sentences) and, when needed, a boundary note. Say what you are unsure
about and what the draft rules out, and ask for approval or edits. Repeat until the user approves.

- Wording: a bolded lead sentence, then scope and tone. State what the mod is *not* only when that
  steers brainstorms. Don't copy "every idea below"-style lines from a legacy backlog.
- A **boundary note** goes in a `### ...` subsection under Identity when the mod has an addon, a sibling,
  or an overlap. Use "this mod owns / the other owns", and list open questions as "Not decided". Add
  the reciprocal note to the other mod's README when it already has an approved statement.
- Moving blocks between mods is a breaking change: point to
  [`docs/BLOCK-MIGRATION.md`](../../../docs/BLOCK-MIGRATION.md) and state the consequence, which is that
  the mod giving up the block gains a required dependency on the one receiving it.

## 4. Write it

On approval:

1. Replace the draft in `<mod>/ideas/README.md` (everything between `## Identity` and
   `## Active ideas`), removing the "**Draft, needs approval.**" line. Write LF line endings. Where an
   "Active ideas" paragraph refers to the old statement, leave it.
2. Commit just that file (and any reciprocal note) using a pathspec, for example
   `git commit -m "docs(<mod>): approve identity statement" -- <paths>`. Check `git status` first.
3. If the discussion created a **mod that doesn't exist yet** (an addon, a sibling), open a GitHub
   issue labeled `ideas` and `enhancement`, as `docs/FEATURE-WORK.md` says, with why it exists, the
   scope, and open questions. Confirm before filing. The issue title style is
   `New mod idea: <Name>`.
4. **Migration-era tracker (delete this step when done).** While the checklist issue "Approve each mod's
   ideas identity statement" is open, tick that mod's entry in its body (edit the line with the new
   statement, via `gh issue edit --body-file`). When every entry is ticked, remove this step.

Don't push unless asked.

## New mods

For a mod with no `ideas/` folder yet, run the same interview and draft in chat. If the user wants to
keep it as a seed, file it as a GitHub issue (`New mod idea: <Name>`). Once the mod's folder exists, put
the approved statement in its `ideas/README.md`, following `docs/agents/conventions.md`'s scaffolding steps.

## Gotchas

- `git diff` may fail with a missing external diff wrapper on this machine. Use `git --no-pager diff
  --no-ext-diff` or `git log --stat`.
- Text files must be LF. Write them with the Write tool or Python, not a long multi-file heredoc.
- Don't touch the mod's own `README.md` as part of this, unless the user asks. A stale one is worth
  mentioning.
