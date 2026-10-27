# Registry dump command

> Source: legacy backlog, [§ Developer & testing tools](../brainstorms/2026-08-27-legacy/potential-features.md#developer--testing-tools) · Status: **Exploring**

## Description

`/chimericlib dump <blocks|items|tags>` writing a report to disk, for
debugging cross-mod registration issues.

## Decisions

_None yet._

## Related

- [Block migration framework](../../../forwarding-address/ideas/core/block-migration-framework.md) — the standalone Forwarding
  Address mod has its own `/forwardingaddress dump <namespace>` command, which writes a starter
  migration file. It is separate from this one; they share only the "write a report to disk" step.

## Open questions

_None yet._
