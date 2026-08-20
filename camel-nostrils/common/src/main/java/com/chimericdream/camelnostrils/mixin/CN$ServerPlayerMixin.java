package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.block.UpsideDownBedBlock;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.advancements.triggers.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The upside-down bed needs to be usable during the day - that's the whole point, sleeping in it is
 * what flips day to night - so this bypasses vanilla's night-only {@code BedRule.canSleep} gate
 * specifically when the bed at the target position is ours. Every other bed in the game (vanilla or
 * modded) is untouched.
 * <p>
 * It also needs its own obstruction check: vanilla's {@code bedBlocked} always checks for two clear
 * blocks *above* the bed (the headroom a floor-mounted bed expects), which our ceiling-mounted bed
 * can never satisfy - there's supposed to be a solid block up there holding it to the ceiling. For our
 * bed specifically, the equivalent clearance is *below* it instead, where the player is actually
 * standing.
 * <p>
 * Finally, this bed isn't meant to be a "real" sleep - {@link com.chimericdream.camelnostrils.block.UpsideDownBedBlock}
 * wakes the player back up the instant they lie down, so the vanilla side effects of a normal
 * overnight sleep (setting a respawn point, the vanilla "Sweet Dreams" advancement) don't make sense
 * here and are suppressed for this bed specifically.
 */
@Mixin(ServerPlayer.class)
public abstract class CN$ServerPlayerMixin {
    @Unique
    private boolean cn$upsideDownBedSleep = false;

    @Redirect(
        method = "startSleepInBed",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z")
    )
    private boolean cn$allowSleepAnytimeInUpsideDownBed(BedRule rule, Level level, @Local(argsOnly = true) BlockPos pos) {
        this.cn$upsideDownBedSleep = level.getBlockState(pos).getBlock() instanceof UpsideDownBedBlock;
        return this.cn$upsideDownBedSleep || rule.canSleep(level);
    }

    @Redirect(
        method = "startSleepInBed",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/BedRule;canSetSpawn(Lnet/minecraft/world/level/Level;)Z")
    )
    private boolean cn$neverSetSpawnFromUpsideDownBed(BedRule rule, Level level, @Local(argsOnly = true) BlockPos pos) {
        return !(level.getBlockState(pos).getBlock() instanceof UpsideDownBedBlock) && rule.canSetSpawn(level);
    }

    @Redirect(
        method = "lambda$startSleepInBed$1",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/triggers/PlayerTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;)V")
    )
    private void cn$suppressVanillaSleepAdvancementForUpsideDownBed(PlayerTrigger trigger, ServerPlayer player) {
        if (!this.cn$upsideDownBedSleep) {
            trigger.trigger(player);
        }
    }

    @Redirect(
        method = "startSleepInBed",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;bedBlocked(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z")
    )
    private boolean cn$bedBlockedAllowsCeilingAttachment(ServerPlayer self, BlockPos pos, Direction direction) {
        CN$PlayerAccessor accessor = (CN$PlayerAccessor) self;

        if (self.level().getBlockState(pos).getBlock() instanceof UpsideDownBedBlock) {
            BlockPos below = pos.below();
            return !accessor.cn$freeAt(below) || !accessor.cn$freeAt(below.relative(direction.getOpposite()));
        }

        BlockPos above = pos.above();
        return !accessor.cn$freeAt(above) || !accessor.cn$freeAt(above.relative(direction.getOpposite()));
    }
}
