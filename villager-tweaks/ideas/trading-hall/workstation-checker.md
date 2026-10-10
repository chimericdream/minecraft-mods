# Workstation checker

> Shortlist #9 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

Fix the number one trading-hall debugging pain: "why won't you take the job". The agent note proposed a
"Job Posting Board" block. **Recommendation:** make it an item instead of a block, which matches the
"toggle, not a system" identity. The mod already has a `WorkstationCheckerItem` class that does nothing
and isn't registered, so it looks like you started this. Use it on a villager to highlight its claimed bed
and workstation for a few seconds, and show a message when it has none. Skip the block version.

## Decisions

_None yet._

## Related

- [Restock rules config](restock-rules.md) — same trading-hall group.
- [Shackles](../movement/shackles.md) — both help keep a hall working.

## Open questions

_None yet._
