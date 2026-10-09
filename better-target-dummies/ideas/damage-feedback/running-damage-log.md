# Running damage log

> Shortlist #7 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

Keeps the last N hits per dummy (weapon used, damage dealt) instead of only the most recent action-bar
message.
**Recommendation:** store the hits in memory only, and surface them through a chat command, not a GUI
(a GUI would push this to L). If #1 is built, consider whether the log is still needed; it is mainly
valuable for seeing the individual hits behind an average. Make N a config option (A4).

## Decisions

_None yet._

## Brainstorm variants

From [`brainstorms/2026-08-24-legacy/potential-features.md`](../brainstorms/2026-08-24-legacy/potential-features.md)
(Damage feedback):

- **Running damage log** — track the last N hits (weapon used, damage dealt) per dummy instead of only the most recent action-bar message, viewable via GUI or command.

## Related

- [DPS / average damage readout](dps-average-damage-readout.md) — the summary this log would back up

## Open questions

- Is the log still wanted once the DPS / average readout exists?
