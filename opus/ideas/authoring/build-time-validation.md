# Validate books in the build

> Source: initial design discussion, 2026-10-06 · Effort **S** · Value ★★
> Status: **Exploring**

## Description

Run `ValidateBook` from Gradle (a `validateBooks` task) and a Bun script so CI fails on broken links, bad frontmatter and invalid widgets. Wire it into mods that ship an Opus book (starting with the bundled guide).

**Out of scope:** checking that item/entity ids exist in the game's registries (needs Minecraft; see the in-game reload report instead).

**Dependencies:** `ValidateBook` (done and tested), a `JavaExec` task in the mod build, and a `package.json` script.
**Verification:** The existing `ValidateBookTest` covers the tool; the build wiring needs one real Gradle run.

## Decisions

- 2026-10-06: Gradle half done. `:opus:common:validateBooks` runs on `check`; the shipped jars declare `Main-Class` so authors run `java -jar opus.jar <path>`. Still open: a Bun script / CI job.

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Should a failing book fail `./gradlew build` or only the CI job? (Recommend: only warnings never fail, errors fail the build.)
