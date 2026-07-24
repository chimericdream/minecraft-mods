package com.chimericdream.bannertweaks.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

@Mixin(MapItemSavedData.class)
abstract public class MapStateMixin {
    @Shadow
    @Final
    public ResourceKey<Level> dimension;

    @Shadow
    abstract protected void setDecorationsDirty();

    @Shadow
    abstract protected void removeDecoration(String id);

    @Shadow
    abstract public boolean isTrackedCountOverLimit(int iconCount);

    @Shadow
    @Final
    public int centerX;
    @Shadow
    @Final
    public int centerZ;
    @Shadow
    @Final
    public byte scale;
    @Shadow
    @Final
    private boolean unlimitedTracking;
    @Shadow
    private int trackedDecorationCount;

    @Shadow
    @Final
    Map<String, MapDecoration> decorations;
    @Shadow
    private @Final Map<String, MapBanner> bannerMarkers;

    /**
     * Fixes <a href="https://bugs.mojang.com/browse/MC-144406">MC-144406</a> (banner markers off the
     * edge of a map are dropped instead of clamped to the border).
     *
     * <p><b>Why {@code @Overwrite} rather than {@code @Inject}/{@code @ModifyArg}:</b> the fix is not
     * a single localized call — it changes vanilla's out-of-bounds branch (the
     * {@code f/g < -64 .. >= 64} clamping, the {@code removeDecoration}-vs-keep decisions, and the
     * {@code PLAYER_OFF_MAP}/{@code PLAYER_OFF_LIMITS} substitution) which is interleaved with the
     * shared decoration-tracking bookkeeping. There is no stable injection point that expresses the
     * corrected behavior, so the method is reimplemented wholesale. Consequence: this conflicts with
     * any other mod that also overwrites {@code MapItemSavedData#addDecoration} — accepted as the
     * cost of the fix. Kept verbatim vs. vanilla except the MC-144406 corrections.
     *
     * @author chimericdream (with additional credit to @jason-green-io)
     * @reason fixes MC-144406; corrections are distributed through the method with no clean inject point
     */
    @Overwrite
    public void addDecoration(Holder<MapDecorationType> type, @Nullable LevelAccessor world, String key, double x, double z, double rotation, @Nullable Component text) {
        int i = 1 << this.scale;
        float f = (float) (x - (double) this.centerX) / (float) i;
        float g = (float) (z - (double) this.centerZ) / (float) i;
        byte b = (byte) ((int) ((double) f * 2.0F));
        byte c = (byte) ((int) ((double) g * 2.0F));
        byte d;

        if (f >= -64.0F && g >= -64.0F && f <= 64.0F && g <= 64.0F) {
            rotation += rotation < 0.0 ? -8.0 : 8.0;
            d = (byte) ((int) (rotation * 16.0 / 360.0));
            if (this.dimension == Level.NETHER && world instanceof Level level) {
                int k = (int) (level.getDefaultClockTime() / 10L);
                d = (byte) (k * k * 34187121 + k * 121 >> 15 & 15);
            }
        } else {
            Optional<ResourceKey<MapDecorationType>> playerDecorationType = MapDecorationTypes.PLAYER.unwrapKey();
            if (playerDecorationType.isPresent() && !type.is(playerDecorationType.get())) {
                this.removeDecoration(key);
                return;
            }

            if (Math.abs(f) < 320.0F && Math.abs(g) < 320.0F) {
                type = MapDecorationTypes.PLAYER_OFF_MAP;
            } else {
                if (!this.unlimitedTracking) {
                    this.removeDecoration(key);
                    return;
                }

                type = MapDecorationTypes.PLAYER_OFF_LIMITS;
            }

            d = 0;
            if (f < -64.0F) {
                b = -128;
            }

            if (g < -64.0F) {
                c = -128;
            }

            if (f >= 64.0F) {
                b = 127;
            }

            if (g >= 64.0F) {
                c = 127;
            }
        }

        MapDecoration mapDecoration = new MapDecoration(type, b, c, d, Optional.ofNullable(text));
        MapDecoration mapDecoration2 = this.decorations.put(key, mapDecoration);
        if (!mapDecoration.equals(mapDecoration2)) {
            if (mapDecoration2 != null && mapDecoration2.type().value().trackCount()) {
                --this.trackedDecorationCount;
            }

            if (type.value().trackCount()) {
                ++this.trackedDecorationCount;
            }

            this.setDecorationsDirty();
        }

    }

    /**
     * Fixes <a href="https://bugs.mojang.com/browse/MC-144406">MC-144406</a>: pairs with
     * {@link #addDecoration} so a banned/re-toggled banner near the map edge is tracked with the
     * corrected bounds ({@code f/g < 64.0}, matching addDecoration's clamping).
     *
     * <p><b>Why {@code @Overwrite}:</b> same as {@link #addDecoration} — the bounds check and the
     * add/remove flow are the whole method, so there is no localized call to redirect. Reimplemented
     * wholesale; conflicts with other mods overwriting {@code toggleBanner}.
     *
     * @author chimericdream (with additional credit to @jason-green-io)
     * @reason fixes MC-144406; the corrected bounds check is the method body itself, no clean inject point
     */
    @Overwrite
    public boolean toggleBanner(LevelAccessor world, BlockPos pos) {
        double d = (double) pos.getX() + 0.5;
        double e = (double) pos.getZ() + 0.5;
        int i = 1 << this.scale;
        double f = (d - (double) this.centerX) / (double) i;
        double g = (e - (double) this.centerZ) / (double) i;

        if (f >= -64.0 && g >= -64.0 && f < 64.0 && g < 64.0) {
            MapBanner mapBannerMarker = MapBanner.fromWorld(world, pos);
            if (mapBannerMarker == null) {
                return false;
            }

            if (this.bannerMarkers.remove(mapBannerMarker.getId(), mapBannerMarker)) {
                this.removeDecoration(mapBannerMarker.getId());
                return true;
            }

            if (!this.isTrackedCountOverLimit(256)) {
                this.bannerMarkers.put(mapBannerMarker.getId(), mapBannerMarker);
                this.addDecoration(mapBannerMarker.getDecoration(), world, mapBannerMarker.getId(), d, e, 180.0, mapBannerMarker.name().orElse(null));
                return true;
            }
        }

        return false;
    }
}