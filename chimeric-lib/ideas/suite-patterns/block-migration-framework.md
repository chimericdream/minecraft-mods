# Block migration framework

> Source: legacy backlog, [§ Extract & generalize existing suite patterns](../brainstorms/2026-08-27-legacy/potential-features.md#extract--generalize-existing-suite-patterns) · Status: **Exploring**

## Description

A general "deprecated block converts itself on placement/load" system
(the pattern Hopper X-Treme uses for its filtered hoppers), so any mod can rename or merge blocks across
versions without DataFixerUpper: old ID → new block + component/NBT mapping, contents preserved.

## Decisions

_None yet._

## Related

- [`docs/BLOCK-MIGRATION.md`](../../../docs/BLOCK-MIGRATION.md) — the current per-mod approach this would generalize.

## Open questions

_None yet._
