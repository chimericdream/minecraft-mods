package com.chimericdream.minekea.block.containers.crates;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.entity.block.containers.CrateBlockEntity;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrappedCrateBlock extends CrateBlock {
    public final RegistrySupplier<Block> BASE_CRATE;

    public TrappedCrateBlock(Properties settings) {
        this(Crates.CONFIGS.get("oak"), Crates.CRATES.get("oak"));
    }

    public TrappedCrateBlock(BlockConfig config, RegistrySupplier<Block> baseCrate) {
        super(config, makeId(config.getMaterial()));

        BLOCK_ID = makeId(config.getMaterial());
        BASE_CRATE = baseCrate;
    }

    public static ResourceLocation makeId(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("containers/crates/trapped/%s", material));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(Crates.CRATE_BLOCK_ENTITY.get(), pos, state, true);
    }

    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    protected int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return Mth.clamp(CrateBlockEntity.getPlayersLookingInCrateCount(world, pos), 0, 15);
    }

    protected int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        return state.getSignal(world, pos, direction);
    }
}
