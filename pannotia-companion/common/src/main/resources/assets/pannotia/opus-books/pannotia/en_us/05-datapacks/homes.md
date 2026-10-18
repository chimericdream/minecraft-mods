# Homes

Part of the Pannotia Companion mod. Save places as homes and teleport to them with `/pc` commands, even across dimensions. No operator permission is needed.

## How to use

| Command | What it does |
|---|---|
| `/pc homes set [name]` | Save your current position as a home. With no name it is called `home`. Using an existing name moves that home. |
| `/pc home [name or number]` | Teleport to a home. With no name it goes to the home called `home`, or to your only home. |
| `/pc homes` | List your homes. Click a name to go there. |
| `/pc homes rename <old> <new>` | Rename a home |
| `/pc homes delete <name or number>` | Delete a home |

Put names with spaces in quotes, like `/pc homes set "Mining base"`. You can also use a home's number from the list instead of its name.

## What it changes

- **One home per dimension by default.** Set a home in the Overworld, another in the Nether and another in the End. Beyond the limit you get a message and the home is not saved.
- The server's config can change the limit, add a stand-still delay (you must not move while it counts down) and add a cooldown between uses. Defaults are no delay and no cooldown. Cooldowns reset when the server restarts.
- Your previous position is saved so [Back](back.md) can return you.

## Notes

- Unlike the old datapack, chunks load for you, so you can go home to an area that is not loaded.
- See also [Spawn](spawn.md).
