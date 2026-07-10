package com.chimericdream.hopperxtreme.item;

import com.chimericdream.hopperxtreme.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

public class WrenchItem extends Item {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tools/wrench");

    @SuppressWarnings("UnstableApiUsage")
    public WrenchItem() {
        super(
            new Properties()
                .stacksTo(1)
                .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .useItemDescriptionPrefix()
                .setId(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID))
        );
    }

    private boolean tryPlacing(BlockPos pos, BlockState state, Level world) {
        if (state.canSurvive(world, pos)) {
            world.setBlockAndUpdate(pos, state);
            world.blockEntityChanged(pos);

            return true;
        }

        return false;
    }

    private boolean tryFacing(BlockState state, BlockPos pos, Level world) {
        if (state.getOptionalValue(BlockStateProperties.FACING).isPresent()) {
            if (tryPlacing(pos, state.cycle(BlockStateProperties.FACING), world)) {
                return true;
            }
        }

        if (state.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).isPresent()) {
            if (tryPlacing(pos, state.cycle(BlockStateProperties.HORIZONTAL_FACING), world)) {
                return true;
            }
        }

        if (state.getOptionalValue(BlockStateProperties.FACING_HOPPER).isPresent()) {
            return tryPlacing(pos, state.cycle(BlockStateProperties.FACING_HOPPER), world);
        }

        return false;
    }

    private boolean tryAxes(BlockState state, BlockPos pos, Level world) {
        if (state.getOptionalValue(BlockStateProperties.AXIS).isPresent()) {
            if (tryPlacing(pos, state.cycle(BlockStateProperties.AXIS), world)) {
                return true;
            }
        }

        if (state.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).isPresent()) {
            return tryPlacing(pos, state.cycle(BlockStateProperties.HORIZONTAL_AXIS), world);
        }

        return false;
    }

    private boolean trySlab(BlockState state, BlockPos pos, Level world) {
        if (state.getBlock() instanceof SlabBlock) {
            BlockState newState = state;

            if (state.getValue(BlockStateProperties.SLAB_TYPE).equals(SlabType.DOUBLE)) {
                return false;
            }

            if (state.getValue(BlockStateProperties.SLAB_TYPE).equals(SlabType.BOTTOM)) {
                newState = state.setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP);
            } else if (state.getValue(BlockStateProperties.SLAB_TYPE).equals(SlabType.TOP)) {
                newState = state.setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM);
            }

            world.setBlockAndUpdate(pos, newState);
            world.blockEntityChanged(pos);

            return true;
        }

        return false;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);

        if (
            context.getPlayer() == null
                || world.isClientSide()
                || state.getBlock() instanceof ChestBlock
                || state.getBlock() instanceof BedBlock
        ) {
            return InteractionResult.PASS;
        }

        if (tryFacing(state, pos, world) || tryAxes(state, pos, world) || trySlab(state, pos, world)) {
            if (!world.isClientSide()) {
                world.playSound(null, pos, SoundEvents.SPYGLASS_USE, SoundSource.AMBIENT, 2.0F, 1.5F);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
