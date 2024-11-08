package com.chimericdream.bctweaks.mixin;

import com.chimericdream.bctweaks.BeaconAccessor;
import com.chimericdream.bctweaks.config.BCTweaksConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(value = BeaconBlockEntity.class, priority = 4101)
public class BCTweaksBeaconMixin extends BlockEntity implements BeaconAccessor {
    private static final Map<Vec3i, BeaconAccessor> bct$beacons = new HashMap<>();
    double bct$range = 0.0;

    public BCTweaksBeaconMixin(BlockPos pos, BlockState state) {
        super(BlockEntityType.BEACON, pos, state);
    }

    @Inject(method = "updateLevel(Lnet/minecraft/world/World;III)I", at = @At(value = "HEAD"))
    private static void bct$updateLevelHead(World world, int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        BlockEntity entity = world.getBlockEntity(new BlockPos(x, y, z));

        if (entity instanceof BeaconAccessor e) {
            e.bct$resetRange();
            bct$beacons.put(new Vec3i(x, y, z), e);
        }
    }

    @Redirect(method = "updateLevel(Lnet/minecraft/world/World;III)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private static boolean bct$updateLevel(BlockState instance, TagKey<Block> tag, World world, int x, int y, int z) {
        BCTweaksConfig config = BCTweaksConfig.HANDLER.instance();

        BeaconAccessor entity = bct$beacons.get(new Vec3i(x, y, z));

        if (entity != null && instance.isIn(tag)) {
            String blockId = Registries.BLOCK.getEntry(instance.getBlock()).getIdAsString();
            entity.bct$addRange(config.beaconRangePerBlock.getOrDefault(blockId, 0.0));
        }

        return instance.isIn(tag);
    }

    @Inject(method = "updateLevel(Lnet/minecraft/world/World;III)I", at = @At(value = "TAIL"))
    private static void bct$updateLevelTail(World world, int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
        bct$beacons.remove(new Vec3i(x, y, z));
    }

    @ModifyVariable(method = "applyPlayerEffects(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/registry/entry/RegistryEntry;)V", at = @At(value = "LOAD", ordinal = 0))
    private static double bct$modifiedRange(double d, World world, BlockPos pos, int beaconLevel, @Nullable RegistryEntry<StatusEffect> primaryEffect, @Nullable RegistryEntry<StatusEffect> secondaryEffect) {
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
