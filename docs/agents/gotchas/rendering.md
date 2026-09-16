# Custom block-model rendering: check for z-fighting on coplanar surfaces

Whenever a custom/overlay model draws a surface flush against another block's real geometry — a decal
sitting on a host's face, a full block model rendered via `submitMovingBlock` inside/against another
block's space — check for z-fighting (two coplanar quads at the same depth flicker between which one
wins per pixel) wherever those surfaces are actually coincident. This bit both carpet-logging and
snow-logging in `log-all-the-things` (a carpet/snow overlay's own bottom/side faces landing exactly on
a wall/fence/bars host's own faces) before being fixed by nudging the overlay a hair off the coincident
plane — imperceptible at the nudge magnitude used (1/2048 of a block), so it reads as flush with no
visible gap. See `log-all-the-things`'s `client.QuadEmitter#SURFACE_NUDGE` for the per-vertex version
(hand-authored/hand-cut geometry) and `CarpetedBlockEntityRenderer`/`SnowedBlockEntityRenderer`'s
whole-block `poseStack` translate/scale nudge for the case where the overlay is a real, unmodified
block model submitted via `submitMovingBlock` (so individual vertices aren't available to nudge).
