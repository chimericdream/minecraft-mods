# Hopper X-Treme + Vanilla Tweaks Compat

Resource pack that adds the [Vanilla Tweaks](https://vanillatweaks.net) hopper arrows to
Hopper X-Treme's hoppers. Load it above Hopper X-Treme's built-in resources.

- Targets Minecraft 26.1 - 26.3 (pack formats 84 - 97).
- Covers hoppers and huppers (upward hoppers). Multi-hoppers and multi-huppers are not changed.
- Honeyed hoppers get the plain arrow on top; honey-glazed hoppers keep the honey top (no arrow there).
- Glazed hoppers only get the arrow on the outer walls.

## Regenerating

The models are generated from the mod's `*_hopper_side.json` files and the bundled arrow
textures in `assets/minecraft/textures/block/` (from Vanilla Tweaks). After changing a hopper model:

```
python generate.py
```

Only `assets/` and `pack.mcmeta` belong in the distributed zip.
