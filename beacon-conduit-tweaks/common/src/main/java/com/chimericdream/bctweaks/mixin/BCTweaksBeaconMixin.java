package com.chimericdream.bctweaks.mixin;

import com.chimericdream.bctweaks.BeaconAccessor;
import com.chimericdream.bctweaks.BeaconSectionAccessor;
import com.chimericdream.bctweaks.config.BCTweaksConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
    @Unique
    private boolean bct$beamHidden = false;
    @Unique
    private boolean bct$ignoreNextTintedGlass = false;

    @Shadow
    private List<BeaconBeamOwner.Section> checkingBeamSections;

    public BCTweaksBeaconMixin(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.BEACON, pos, state);
    }

    /**
     * Tinted glass fully dampens light (like an opaque block), which would otherwise stop the beam
     * scan dead in its tracks. Treat it the same as bedrock here so the scan keeps going through it;
     * {@link #bct$maybeStartHiddenSection} is what actually splits the beam into visible/hidden runs.
     */
    @Redirect(method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
    private static boolean bct$treatTintedGlassAsPassthrough(BlockState state, Object other) {
        if (state.is(Blocks.TINTED_GLASS)) {
            return true;
        }

        return state.is((Block) other);
    }

    @Redirect(method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList()Ljava/util/ArrayList;"))
    private static ArrayList<BeaconBeamOwner.Section> bct$resetBeamHiddenOnRescan(Level level, BlockPos pos, BlockState selfState, BeaconBlockEntity entity) {
        ((BeaconAccessor) entity).bct$setBeamHidden(false);

        return Lists.newArrayList();
    }

    @Unique
    private static BeaconBeamOwner.Section bct$toggleHiddenLogic(BeaconBeamOwner.Section lastBeamSection, BeaconAccessor accessor) {
        boolean nowHidden = !accessor.bct$isBeamHidden();
        accessor.bct$setBeamHidden(nowHidden);

        BeaconBeamOwner.Section next = new BeaconBeamOwner.Section(lastBeamSection.getColor());
        BeaconSectionAccessor nextAccessor = (BeaconSectionAccessor) next;
        nextAccessor.bct$setHidden(nowHidden);
        nextAccessor.bct$resetHeight();

        accessor.bct$appendBeamSection(next);

        return next;
    }

    @ModifyVariable(method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BeaconBeamOwner$Section;increaseHeight()V", ordinal = 1), name = "lastBeamSection")
    private static BeaconBeamOwner.Section bct$maybeStartHiddenSection(BeaconBeamOwner.Section lastBeamSection, Level level, BlockPos pos, BlockState selfState, BeaconBlockEntity entity, @Local(name = "checkPos") BlockPos checkPos) {
        BlockState blockAbove = level.getBlockState(checkPos.above());
        BlockState thisBlock = level.getBlockState(checkPos);

        BeaconAccessor accessor = (BeaconAccessor) entity;
        if (blockAbove.is(Blocks.TINTED_GLASS) && !accessor.bct$isBeamHidden()) {
            BeaconBeamOwner.Section next = bct$toggleHiddenLogic(lastBeamSection, accessor);
            accessor.bct$ignoreNextTintedGlass();

            return next;
        }

        if (!blockAbove.is(Blocks.TINTED_GLASS) && thisBlock.is(Blocks.TINTED_GLASS) && accessor.bct$isBeamHidden()) {
            if (accessor.bct$shouldIgnoreTintedGlass()) {
                accessor.bct$stopIgnoringTintedGlass();
                return lastBeamSection;
            }

            return bct$toggleHiddenLogic(lastBeamSection, accessor);
        }

        return lastBeamSection;
    }

    @Inject(method = "getBeamSections", at = @At(value = "HEAD"), cancellable = true)
    private void bct$getBeamSections(CallbackInfoReturnable<List<BeaconBeamOwner.Section>> cir) {
        if (this.getLevel() instanceof Level bct$level) {
            BlockState blockAbove = bct$level.getBlockState(this.getBlockPos().above());
            if (blockAbove.is(BlockTags.WOOL_CARPETS)) {
                cir.setReturnValue(ImmutableList.of());
            }
        }
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

    @Override
    public boolean bct$shouldIgnoreTintedGlass() {
        return bct$ignoreNextTintedGlass;
    }

    @Override
    public void bct$ignoreNextTintedGlass() {
        bct$ignoreNextTintedGlass = true;
    }

    @Override
    public void bct$stopIgnoringTintedGlass() {
        bct$ignoreNextTintedGlass = false;
    }

    @Override
    public boolean bct$isBeamHidden() {
        return bct$beamHidden;
    }

    @Override
    public void bct$setBeamHidden(boolean hidden) {
        bct$beamHidden = hidden;
    }

    @Override
    public void bct$appendBeamSection(BeaconBeamOwner.Section section) {
        checkingBeamSections.add(section);
    }
}
