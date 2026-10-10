# Villager Tweaks — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`ideas-md.md`](ideas-md.md), your older notes,
and [`potential-features.md`](potential-features.md), the agent brainstorm). There is no
`combined-ideas.md` for a legacy backlog, so this list was built straight from the source notes and
checked against what the mod ships today. Everything that isn't ranked is in
[What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on. The
three specific advancements from the agent note are handled below (two already ship; one is ranked as #8).

## How items were scored

- **Effort**, for a two-loader (Fabric + NeoForge) Architectury mod, counting art, datagen and rendering,
  not just code:
  - **S**: a day or two, mostly assets and data, reuses existing code.
  - **M**: a new block or block entity, a new interaction, or a moderate rendering change.
  - **L**: a new entity, custom rendering, worldgen, or a new UI.
  - **XL**: a system that touches many other features.
- **Value**, ★ to ★★★: fit with the mod's identity statement, and how much players would notice it.
- **Votes**: how many independent brainstorm answers suggested it. Legacy items have none, so this column
  shows `—`.

Ranking is by value relative to effort. The identity is "small, configurable fixes for real villager
headaches... every idea should stay a toggle, not a system", so config options for existing vanilla
behavior rank above new items, and new blocks or systems rank lowest. Every item that changes behavior is a
config option that is **off by default**, so servers and existing worlds are not changed (N1, A2). The mod
already has a YACL config with Trading, Zombie Conversion, Villager Growth and Misc. sections, so new
options slot into those.

## Already shipped

- **Leashing nitwits** (a partial version of "follow leash"). Only nitwits for now; see #3.
- **Capped curing discounts** (the "Cap max discount" tweak). This is the opposite of the "stacked curing
  discounts" in your notes; see #2.
- **Advancements "Bag and Tag" and "Pied Piper"** (from the agent note). "Fresh Start" is not in yet; see #8.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Bagged Villager tooltip](../../bagged-villager/bagged-villager-tooltip.md) | S | ★★★ | — | Yes |
| 2 | Stacked curing discounts | S | ★★ | — | Already a feature (max discount cap) |
| 3 | Leash any villager | S | ★★ | — | No |
| 4 | Configurable lure items | S | ★★ | — | Already a feature |
| 5 | [Breeding requirements config](../../population/breeding-requirements.md) | S | ★★ | — | Maybe |
| 6 | [Panic toggle](../../population/panic-toggle.md) | S | ★★ | — | Maybe |
| 7 | Lure speed and priority | S | ★ | — | No |
| 8 | ["Fresh Start" advancement](../../progression/fresh-start-advancement.md) | S | ★ | — | Maybe |
| | **Tier 2 — Solid mid-size features** | | | | |
| 9 | [Workstation checker](../../trading-hall/workstation-checker.md) | M | ★★★ | — | Yes |
| 10 | [Restock rules config](../../trading-hall/restock-rules.md) | M | ★★ | — | Maybe |
| 11 | Profession contract | M | ★★ | — | Trading with a villager already locks their trades |
| 12 | [Shackles](../../movement/shackles.md) | M | ★★ | — | Yes |
| 13 | [Reputation viewer](../../reputation/reputation-viewer.md) | M | ★★ | — | Maybe |
| 14 | Bag more mobs | M | ★★ | — | No |
| 15 | Per-player cure discounts | M | ★ | — | This is how vanilla works; my "Global reputation" config changes it to a shared value |
| | **Tier 3 — Big bets** | | | | |
| 16 | [Trade preview on locked trades](../../trading-hall/trade-preview.md) | L | ★ | — | Maybe: I think the trade set is deterministic after the initial roll |

## Tier 1 — Quick wins

### 1. Bagged Villager tooltip — S · ★★★

The Bagged Villager item shows the villager's profession, level and a short trade summary on hover, so a
chest of bagged villagers isn't a lottery. It is the item the mod is best known for, and today every
bagged villager looks identical. **Recommendation:** profession and level on one line, plus the first few
trades (capped at a handful, with "and N more"). Read the data from the item's stored villager, no new
data needed. Make the trade summary a client setting in case the tooltip gets long (A4).

### 2. Stacked curing discounts — S · ★★

Bring back stacking of the curing discount, so curing the same villager repeatedly keeps lowering prices
(the classic trading-hall trick of older versions). It's from your notes. **Recommendation:** a toggle,
default off, in the Reputation section. It interacts with the existing "Cap max discount", which is the
safety limit for exactly this case, so say that in the option text. Decide how it stacks (each cure adds a
fixed amount, up to the cap).

### 3. Leash any villager — S · ★★

Extend the existing "Leash nitwits" toggle so leads can be put on any villager. It's the "follow leash"
stretch from the agent note: same problem as bagging, for players who want the journey and not the
teleport. **Recommendation:** make it a choice of "nitwits only", "all villagers" or off, rather than a
second toggle, so the current option keeps working. Zombie villagers and baby villagers need a look.

### 4. Configurable lure items — S · ★★

Drive luring from an item tag instead of the fixed list of emerald block and emerald ore, so packs and
servers can pick what villagers follow. **Recommendation:** add the tag (the mod already has a `ModTags`
class), default to today's items, and keep the existing toggle as the on/off switch. Datapack-friendly, no
UI needed.

### 5. Breeding requirements config — S · ★★

Toggles for the two fiddly halves of every breeder: the bed requirement, and the food thresholds.
**Recommendation:** two options in a new "Breeding" section, default to vanilla. The mod already has a
breeding mixin (`VTVillagerMakeLoveMixin`), so this reuses a hook that exists. Be careful with the
willingness check, which is how vanilla decides a villager is ready.

### 6. Panic toggle — S · ★★

Villagers don't panic-sprint from zombies when safely behind glass, or a blanket "no panic" for decorative
and trading-hall villagers. It pairs with the existing "zombies always convert" option.
**Recommendation:** start with the blanket toggle (simple and predictable), and treat "only when behind
glass" as a stretch, since it needs a line-of-sight check.

### 7. Lure speed and priority — S · ★

Config for how fast villagers follow the lure, and whether luring overrides their work schedule.
**Recommendation:** only the speed to start. Overriding schedules could strand a villager away from its
job site, so add it only if someone asks.

### 8. "Fresh Start" advancement — S · ★

Cure and re-employ the same villager. It's the third specific advancement from the agent note, next to the
two that ship. **Recommendation:** tracking "the same villager" needs a stored flag on cure, which the mod
may not have. Decide that when you build it; if it is awkward, skip the advancement.

## Tier 2 — Solid mid-size features

### 9. Workstation checker — M · ★★★

Fix the number one trading-hall debugging pain: "why won't you take the job". The agent note proposed a
"Job Posting Board" block. **Recommendation:** make it an item instead of a block, which matches the
"toggle, not a system" identity. The mod already has a `WorkstationCheckerItem` class that does nothing
and isn't registered, so it looks like you started this. Use it on a villager to highlight its claimed bed
and workstation for a few seconds, and show a message when it has none. Skip the block version.

### 10. Restock rules config — M · ★★

Plain toggles and sliders for restocking: how many times per day, whether the villager must sleep or reach
its workstation, and how long between restocks. **Recommendation:** put these in the Trading section next
to the existing max-trades override, with vanilla as the default. The restock logic touches the villager
brain, so budget time to test it.

### 11. Profession contract — M · ★★

An item that locks a villager's profession and trades permanently, ending accidental workstation-break
rerolls. Craftable with paper and an emerald, and applied like a name tag. **Recommendation:** worth it, but
it is the first idea that starts to look like a system, so keep it small: one item, one effect, and a way to
remove it. Add a config toggle to disable the item entirely (N1).

### 12. Shackles — M · ★★

From your notes: an item that can be dispensed onto a villager to stop it from moving. **Recommendation:**
overlaps a lot with the contract (#11) and the leash (#3), so decide whether it is a different idea at all.
If it stays, use a dispenser to apply it, keep the villager trading as normal, and let it be removed with
the same item. A pure "stay put" flag, with no other behavior change.

### 13. Reputation viewer — M · ★★

An inspect mode (sneak with an empty hand) that shows your numeric reputation with a villager, so the
existing global and negative-reputation toggles have visible feedback. **Recommendation:** an actionbar
message, off by default and only for the player who triggers it. A clean companion to #2 and #15.

### 14. Bag more mobs — M · ★★

Config toggles to allow bagging zombie villagers (careful: mid-cure), wandering traders, and maybe allays.
Each is its own headache the bundle trick could solve. **Recommendation:** start with wandering traders (a
clean fit) and zombie villagers (preserve the cure timer), and leave allays out. Each mob is a separate
toggle, off by default.

### 15. Per-player cure discounts — M · ★

The "real feature" behind the agent note's "cure keepsake": a villager remembers who cured it, and the
discount applies to that player. The particles are free flavor. **Recommendation:** only if you want to
change how the discount works, since vanilla already ties reputation to the player unless "Global
reputation" is on. Might be redundant; check what the vanilla data already gives you.

## Tier 3 — Big bets

### 16. Trade preview on locked trades — L · ★

Show what a locked trade will restock to, or reveal all trade tiers greyed out so hall builders can plan
without leveling every villager. **Recommendation:** hard, because later trade tiers aren't generated until
the villager levels up, so showing them means predicting generation. Skip unless you find a simple way to
do it.

## Honorable mentions

- **Villager Transit Coupon.** Bagged villagers get "cranky" after too many days bagged, raising prices.
  An optional cost for servers that want it, default off. Fun, but it is a system on top of #1.

## What was cut and why

**Not this mod.**
- **Nitwit dignity option.** The note itself says this is Miniblock Merchants' call, not this mod's.

**Too vague to act on.**
- **Thematic advancements** (dropped; see the intro).

## Suggested first arc

Do #1 (the tooltip) and the config options #2–#7, since they extend sections the mod already has and need
no new content. Then #9 (the workstation checker, which you've already started) and #10. Items #11–#14 are
the optional new items and behaviors, in the order you want them. #16 is not worth doing for now.
