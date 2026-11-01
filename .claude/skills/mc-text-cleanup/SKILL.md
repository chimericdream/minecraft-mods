---
name: mc-text-cleanup
description: Clean up prose you just wrote or edited, with a small sub-agent, before you finish. Use AUTOMATICALLY after you change a mod's README, add or edit CHANGELOG entries, or add or edit in-game documentation (Opus guide books, for example `<mod>/common/src/main/resources/assets/<mod>/opus-books/**/*.md`). Removes AI filler, repetition, and inflated words, and enforces ASD-STE100. Do not use for code comments, commit messages, or text you did not change.
---

# Text cleanup for README, CHANGELOG, and in-game docs

Based on the `text-cleanup` skill from `v1truv1us/ai-eng-system`. This version is scoped to this repo:
it cleans only the lines you wrote or changed, and it delegates the edit to a small sub-agent.

The house style is ASD-STE100. The full rule is in
[docs/agents/conventions/code-style.md](../../../docs/agents/conventions/code-style.md). The prompt
below holds the short form, so the sub-agent does not need to read that file.

## When to run

Run this skill once, at the end of the task, after all text edits are done. Run it when you:

- changed a mod's `README.md`,
- wrote or edited a `CHANGELOG.md` entry,
- added or edited an in-game doc page (Opus guide `.md` files and their `book.yml`).

Do not run it again on text that this skill already cleaned. Do not run it on text you did not touch.

## 1. Choose the model

Pick the smallest model that fits. Count only the new or changed text.

| Text to clean                                                          | Model    |
|------------------------------------------------------------------------|----------|
| Up to about 40 lines, one file, plain prose or list items              | `haiku`  |
| About 40–300 lines, or a few files, or a page with widgets/frontmatter | `sonnet` |
| More than that, or the meaning depends on subtle design notes          | `opus`   |

Default to `haiku`. Move up only when the text needs it.

## 2. Find the changed lines

Get the exact scope with `git diff -U0 -- <file>` (or `git diff --cached`). For a new file, the scope is
the whole file. Write down the file path and the changed line ranges. Do not paste the diff or the file
into the prompt.

## 3. Start the sub-agent

Use the `Agent` tool with `subagent_type: "general-purpose"`, the `model` from step 1, and
`run_in_background: false`. Do not fork. Give it only the prompt below, with the placeholders filled in.

```
Edit the prose in <FILE>, lines <RANGES> only. Edit in place. Do not touch other lines.

Rules (ASD-STE100, short form):
- Cut words that give no fact, limit, decision, or needed context.
- Remove: openers and closers ("In this guide…", "Enjoy!"), repeated conclusions and transitions,
  restated headings, marketing words ("seamless", "powerful", "robust", "elevate"), vague claims
  with no source, and filler hedges.
- Write short sentences: 20 words max in an instruction, 25 max in a description. One idea each.
- Use the active voice and the present tense. Use the imperative for instructions.
- Use simple, common words ("use", not "utilize"). One word for one meaning. No idioms, slang, or humor.
- Keep the articles ("the", "a"). Keep contractions to a minimum. No noun string of more than three nouns.
- Do not shorten text if that removes needed precision.

Keep exactly as written: code, inline code, identifiers, item/block/mod/loader/library names, numbers,
version numbers, links and link targets, image paths, YAML frontmatter, widget/directive syntax,
heading levels, list structure, and the CHANGELOG section layout and category names.
Keep all warnings, compatibility limits, and error cases.
Do not add new facts. Do not change the meaning.

Reply with one line: "done" and the number of lines changed. Do not repeat the text.
```

Add one more line to the prompt only for these file types:

- `CHANGELOG.md`: "Each entry stays one bullet. Keep issue and PR references."
- Opus guide page: "The text shows in a Minecraft book. Keep Markdown limited to what the page already uses."

## 4. Check the result

Run `git diff -U0 -- <file>`. Confirm that:

- only the planned lines changed,
- links, code spans, frontmatter, and names are unchanged,
- no fact was lost or added.

If the sub-agent broke a rule, fix the lines yourself. Do not start a second sub-agent for a small fix.
Then end your task. Do not describe the cleanup in your reply beyond one short line.
