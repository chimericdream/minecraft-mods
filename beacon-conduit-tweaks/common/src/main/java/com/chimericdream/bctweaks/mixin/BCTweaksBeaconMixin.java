package com.chimericdream.bctweaks.mixin;

import com.chimericdream.bctweaks.BeaconAccessor;
import com.chimericdream.bctweaks.config.BCTweaksConfig;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = BeaconBlockEntity.class, priority = 4101)
public class BCTweaksBeaconMixin extends BlockEntity implements BeaconAccessor {
    @Unique
    private static final Map<Vec3i, BeaconAccessor> bct$beacons = new HashMap<>();
    @Unique
    double bct$range = 0.0;

    public BCTweaksBeaconMixin(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.BEACON, pos, state);
    }

    @Inject(method = "updateBase(Lnet/minecraft/world/level/Level;III)I", at = @At(value = "HEAD"))
    private static void bct$updateLevelHead(Level world, int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        BlockEntity entity = world.getBlockEntity(new BlockPos(x, y, z));

        if (entity instanceof BeaconAccessor e) {
            e.bct$resetRange();
            bct$beacons.put(new Vec3i(x, y, z), e);
        }
    }

    @Redirect(method = "updateBase(Lnet/minecraft/world/level/Level;III)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private static boolean bct$updateLevel(BlockState instance, TagKey<Block> tag, Level world, int x, int y, int z) {
        BCTweaksConfig config = BCTweaksConfig.HANDLER.instance();

        BeaconAccessor entity = bct$beacons.get(new Vec3i(x, y, z));

        if (entity != null && instance.is(tag)) {
            String blockId = BuiltInRegistries.BLOCK.wrapAsHolder(instance.getBlock()).getRegisteredName();
            entity.bct$addRange(config.beaconRangePerBlock.getOrDefault(blockId, 0.0));
        }

        return instance.is(tag);
    }

    @Inject(method = "updateBase(Lnet/minecraft/world/level/Level;III)I", at = @At(value = "TAIL"))
    private static void bct$updateLevelTail(Level world, int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        bct$beacons.remove(new Vec3i(x, y, z));
    }

    @SuppressWarnings("ModifyVariableMayUseName")
    @ModifyVariable(method = "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/Holder;Lnet/minecraft/core/Holder;)V", at = @At(value = "LOAD", ordinal = 0), ordinal = 0)
    private static double bct$modifiedRange(double d, Level world, BlockPos pos, int beaconLevel, @Nullable Holder<MobEffect> primaryEffect, @Nullable Holder<MobEffect> secondaryEffect) {
        BCTweaksConfig config = BCTweaksConfig.HANDLER.instance();
        BlockEntity entity = world.getBlockEntity(pos);

        if (entity instanceof BeaconBlockEntity) {
            return ((BeaconAccessor) entity).bct$getRange() + config.beaconRangePerLevel * beaconLevel;
        }

        return d;
    }

    @Override
    public void bct$addRange(double d) {
        bct$range += d;
    }

    @Override
    public void bct$resetRange() {
        BCTweaksConfig config = BCTweaksConfig.HANDLER.instance();

        bct$range = config.beaconBaseRange;
    }

    @Override
    public double bct$getRange() {
        return bct$range;
    }
}
