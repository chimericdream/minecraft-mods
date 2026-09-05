package com.chimericdream.camelnostrils.neoforge.mixin;

import com.chimericdream.camelnostrils.block.UpsideDownBedBlock;
import com.chimericdream.camelnostrils.mixin.CN$PlayerAccessor;
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
 * NeoForge-only counterpart to {@code com.chimericdream.camelnostrils.mixin.CN$ServerPlayerMixin}
 * (registered via {@code camelnostrils.fabric.mixins.json}, Fabric-only). NeoForge's
 * {@code EventHooks.canPlayerStartSleeping} patch moves the entire body of vanilla's
 * {@code ServerPlayer#startSleepInBed} - the {@code BedRule} checks, the {@code bedBlocked} call, and
 * the sleep-advancement trigger - out of that method and into synthetic lambdas
 * ({@code lambda$startSleepInBed$0} and {@code lambda$startSleepInBed$2}) so it can preview the
 * would-be result for the event before falling through to {@code super.startSleepInBed(pos)}
 * (`Player`'s, unpatched) for the actual state change. The lambda numbering doesn't match
 * vanilla/Fabric's, so the redirect targets here differ from the common mixin's even though the
 * injected logic is identical. Confirmed via javap against
 * {@code minecraft-merged-official-at-patched.jar}; see docs/NEOFORGE.md.
 */
@Mixin(ServerPlayer.class)
public abstract class CN$ServerPlayerMixin {
    @Unique
    private boolean cn$upsideDownBedSleep = false;

    @Redirect(
        method = "lambda$startSleepInBed$0",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z")
    )
    private boolean cn$allowSleepAnytimeInUpsideDownBed(BedRule rule, Level level, @Local(argsOnly = true) BlockPos pos) {
        this.cn$upsideDownBedSleep = level.getBlockState(pos).getBlock() instanceof UpsideDownBedBlock;
        return this.cn$upsideDownBedSleep || rule.canSleep(level);
    }

    @Redirect(
        method = "lambda$startSleepInBed$0",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/attribute/BedRule;canSetSpawn(Lnet/minecraft/world/level/Level;)Z")
    )
    private boolean cn$neverSetSpawnFromUpsideDownBed(BedRule rule, Level level, @Local(argsOnly = true) BlockPos pos) {
        return !(level.getBlockState(pos).getBlock() instanceof UpsideDownBedBlock) && rule.canSetSpawn(level);
    }

    @Redirect(
        method = "lambda$startSleepInBed$2",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/triggers/PlayerTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;)V")
    )
    private void cn$suppressVanillaSleepAdvancementForUpsideDownBed(PlayerTrigger trigger, ServerPlayer player) {
        if (!this.cn$upsideDownBedSleep) {
            trigger.trigger(player);
        }
    }

    @Redirect(
        method = "lambda$startSleepInBed$0",
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
