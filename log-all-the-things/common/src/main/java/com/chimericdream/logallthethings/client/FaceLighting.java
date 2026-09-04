package com.chimericdream.logallthethings.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;

/**
 * Per-face packed light, sampled the way vanilla's non-AO block rendering does it
 * ({@code BlockModelLighter#prepareQuadFlat}: a face's light comes from the neighbor position in its
 * own direction, not from the host block's own position) — for {@code carpetlog}/{@code windowlog}'s
 * hand-rolled overlay geometry ({@code CarpetFrameRenderer}/{@code WindowFrameRenderer}), which used to
 * light every face from one flat, position-only sample. Combined with {@code CardinalLighting#byFace}
 * for the direction-dependent diffuse darkening vanilla also applies to non-top faces, this is what
 * makes those overlays' side/bottom faces match a real block's instead of rendering uniformly bright.
 * Doesn't attempt full ambient occlusion (corner darkening near neighboring solid blocks) — that would
 * require reproducing {@code BlockModelLighter}'s per-vertex corner-blend algorithm for arbitrary
 * hand-cut geometry, which is out of proportion with how small a sliver of the block these overlays
 * actually cover.
 */
public final class FaceLighting {
    private FaceLighting() {
    }

    /** Indexed by {@link Direction#get3DDataValue()}. */
    public static int[] neighborLight(ClientLevel level, BlockPos pos) {
        int[] light = new int[Direction.values().length];
        BlockPos.MutableBlockPos scratch = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            scratch.setWithOffset(pos, direction);
            light[direction.get3DDataValue()] = LightCoordsUtil.getLightCoords(level, scratch);
        }
        return light;
    }
}
