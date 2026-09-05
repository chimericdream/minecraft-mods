package com.chimericdream.sneakytweaks.campfire;

import com.chimericdream.sneakytweaks.advancement.SneakyTweaksAdvancements;
import com.chimericdream.sneakytweaks.config.SneakyTweaksConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class CampfireSneakingLogic {
    private static final int REGEN_PER_TICK = 4;

    // Matches vanilla's fireDamage ratio between the two campfire blocks (Blocks.CAMPFIRE: 1, Blocks.SOUL_CAMPFIRE: 2).
    private static final int REGULAR_DRAIN_PER_TICK = 1;
    private static final int SOUL_DRAIN_PER_TICK = 2;

    private CampfireSneakingLogic() {
    }

    /**
     * Checks every block the player's actual hitbox overlaps, not just {@code player.blockPosition()}
     * (a single point). Vanilla's {@code CampfireBlock.entityInside} damage trigger fires off the same
     * bounding-box overlap (see {@code Entity.checkInsideBlocks}), so a point check misses cases where
     * the player is standing at the very edge of a campfire block with their feet position technically
     * in the neighboring block while their hitbox still clips the campfire.
     */
    public static boolean isOnLitCampfire(Player player) {
        return getCampfireDrainPerTick(player) > 0;
    }

    private static int getCampfireDrainPerTick(Player player) {
        Level level = player.level();
        AABB aabb = player.getBoundingBox().deflate(1.0E-5);

        int minX = Mth.floor(aabb.minX);
        int maxX = Mth.floor(aabb.maxX);
        int minY = Mth.floor(aabb.minY);
        int maxY = Mth.floor(aabb.maxY);
        int minZ = Mth.floor(aabb.minZ);
        int maxZ = Mth.floor(aabb.maxZ);

        int drainPerTick = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockState state = level.getBlockState(new BlockPos(x, y, z));
                    if (isLitCampfire(state)) {
                        int candidateDrainPerTick = state.is(Blocks.SOUL_CAMPFIRE) ? SOUL_DRAIN_PER_TICK : REGULAR_DRAIN_PER_TICK;
                        drainPerTick = Math.max(drainPerTick, candidateDrainPerTick);
                    }
                }
            }
        }

        return drainPerTick;
    }

    private static boolean isLitCampfire(BlockState state) {
        return state.is(BlockTags.CAMPFIRES) && state.hasProperty(CampfireBlock.LIT) && state.getValue(CampfireBlock.LIT);
    }

    public static void tick(Player player) {
        SneakyTweaksConfig config = SneakyTweaksConfig.HANDLER.instance();
        int graceTicks = CampfireGraceHolder.getCampfireGraceTicks(player);

        int drainPerTick = config.enableCampfireSneaking && player.isCrouching() ? getCampfireDrainPerTick(player) : 0;

        if (drainPerTick > 0) {
            if (player instanceof ServerPlayer serverPlayer) {
                if (graceTicks > 0) {
                    SneakyTweaksAdvancements.award(serverPlayer, SneakyTweaksAdvancements.SNEAK_ON_CAMPFIRE);
                } else {
                    SneakyTweaksAdvancements.award(serverPlayer, SneakyTweaksAdvancements.OVERSTAY_YOUR_WELCOME);
                }
            }

            CampfireGraceHolder.setCampfireGraceTicks(player, Math.max(0, graceTicks - drainPerTick));
        } else if (graceTicks < config.campfireGraceTicks) {
            CampfireGraceHolder.setCampfireGraceTicks(player, Math.min(config.campfireGraceTicks, graceTicks + REGEN_PER_TICK));
        }
    }
}
