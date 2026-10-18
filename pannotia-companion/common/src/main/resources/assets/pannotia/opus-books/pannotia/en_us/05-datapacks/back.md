# Back

Part of the Pannotia Companion mod. `/pc back` returns you to the place you last teleported from using [Homes](homes.md) or [Spawn](spawn.md). Optionally (off by default) it also remembers where you died.

## How to use

| Command | What it does |
|---|---|
| `/pc back` | Teleport to your saved return point. Works across dimensions. |

Typical flow: use `/pc home` or `/pc spawn`, then `/pc back` to return to where you were. Using Back also records the place you just left, so repeating `/pc back` swaps between two places.

## What it changes

- `/pc home` and `/pc spawn` store your position, rotation and dimension before teleporting.
- **Save death location:** off by default. When the server turns it on, Back also remembers where you died.
- The server's config can add a stand-still delay and a cooldown. Both default to none, and cooldowns reset when the server restarts.
- With no saved place you see "You have nowhere to go back to."

## Notes

- Your return point is saved with the world, so it survives restarts.
