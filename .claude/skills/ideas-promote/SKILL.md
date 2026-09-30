---
name: ideas-promote
description: After the user has voted on a brainstorm `shortlist.md`, promote every Yes/Maybe item into its own standalone idea file under `<mod>/ideas/<theme>/`, link the shortlist rows to those files, and update the active-ideas listing in `<mod>/ideas/README.md`. Also handles moving an idea between theme folders. Step 4 of 4 in the ideas pipeline. Use when the user asks to "promote", "create files for", or "break out" shortlisted ideas, or to regroup/move idea files.
---

# Promote voted ideas

Step 4 of the ideas pipeline: `ideas-brainstorm` → `ideas-combine` → `ideas-shortlist` →
**ideas-promote**. Reference output: the idea files and `README.md` in `all-hallows-steve/ideas/`.

Idea files are where each idea is iterated on before it's built, so they start as a faithful copy of
what the shortlist says, not a rewrite.

## 1. Pick the items

Every row of the shortlist table whose **My Vote** is **Yes** or **Maybe**, and no other rows. If any
rows are blank, ask before treating them as No.

## 2. Group into theme folders

- Reuse the existing theme folders under `ideas/` (e.g. `carving/`, `crops/`, `harvest-crafts/`,
  `farm-creatures/`, `folklore/`). Create a new one only when nothing fits.
- Never put idea files at the root of `ideas/`, and never inside `brainstorms/`.
- Say where borderline items went. The user may regroup them. (Turnip lanterns started in `folklore/`
  and moved to `harvest-crafts/`.)
- File names are short kebab-case slugs of the idea name.

## 3. Write each idea file

```markdown
# <Idea name>

> Shortlist #<n> · <Tier> · Effort **<E>** · Value <★> · Votes <v> · My vote **<Yes|Maybe>**

## Description

<the shortlist's section for this item, copied verbatim>

## Brainstorm variants

From [combined-ideas § <k>](../brainstorms/<date>/combined-ideas.md#<anchor>). Bracketed numbers are
the `brainstorms/<date>/agentN.md` files that suggested each variant.

- <the matching bullets and sub-bullets from combined-ideas.md, with their citations>
- <related ideas that were cut or voted No, labeled as such, e.g. "(Shortlist #24, voted **No**.)">

## Related

- [<Other idea>](../<theme>/<slug>.md) — <one-phrase reason>

## Open questions

_None yet._
```

If the idea file **already exists** from an earlier session, leave its Description and any notes the
user has added. Append this session's variants under a new
`### From brainstorms/<date>` sub-heading in **Brainstorm variants**, with its own citation line.

## 4. Update links and the README

- In `shortlist.md`, link each promoted row's Feature cell to its file (`../../<theme>/<slug>.md`), and
  add a line above the table saying linked rows have their own idea files.
- `<mod>/ideas/README.md` lists **all active ideas**, not any particular shortlist. It has a short intro
  (points to `brainstorms/` for raw sessions), then one `## <Theme>` section per folder with one entry
  per idea: `- [Name](<theme>/<slug>.md) — <one-sentence summary>`. Add new ideas and keep it in sync
  with the folders.

## Moving an idea between themes

Move the file, then fix every link that points to it (grep for the slug across `ideas/`: the README,
shortlist rows in every session, and the Related sections of other idea files). Also fix the moved
file's own relative links, then check that every link resolves.

## Finish

Write files with the Write tool (LF line endings). Report what was created and where. Commit only when
asked, and then stage only the ideas folder: `git commit -- <mod>/ideas`.
