package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.XtremeHopperBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.XTREME_HOPPER_BLOCK_ENTITY;

public class XtremeHopperBlock extends AbstractHopperBlock {
    public static final MapCodec<XtremeHopperBlock> CODEC = simpleCodec(XtremeHopperBlock::create);

    private final int cooldownInTicks;
    private final String baseKey;
    private final boolean withFilter;

    static XtremeHopperBlock create(Properties settings) {
        return new XtremeHopperBlock(8, "default") {
        };
    }

    public XtremeHopperBlock(int cooldownInTicks, String translationKey) {
        this(cooldownInTicks, translationKey, false);
    }

    public XtremeHopperBlock(int cooldownInTicks, String translationKey, boolean withFilter) {
        super(
            Properties.ofFullCopy(Blocks.HOPPER)
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 4.8F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .setId(REGISTRY_HELPER.makeBlockRegistryKey(translationKey))
        );

        this.cooldownInTicks = cooldownInTicks;
        this.baseKey = translationKey;
        this.withFilter = withFilter;
    }

    public int getCooldownInTicks() {
        return cooldownInTicks;
    }

    public String getBaseKey() {
        return baseKey;
    }

    @Override
    protected MapCodec<XtremeHopperBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new XtremeHopperBlockEntity(pos, state, cooldownInTicks, withFilter);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, XTREME_HOPPER_BLOCK_ENTITY.get(), XtremeHopperBlockEntity::serverTick);
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.is(state.getBlock())) {
            this.updateEnabled(world, pos, state);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof XtremeHopperBlockEntity) {
                player.openMenu((XtremeHopperBlockEntity) blockEntity);
                player.awardStat(Stats.INSPECT_HOPPER);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(Level world, BlockPos pos, BlockState state) {
        if (baseKey.equals("copper_hopper")) {
            return;
        }

        boolean bl = !world.hasNeighborSignal(pos);
        if (bl != state.getValue(ENABLED)) {
            world.setBlock(pos, state.setValue(ENABLED, bl), 2);
        }
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof XtremeHopperBlockEntity) {
            XtremeHopperBlockEntity.onEntityCollided(world, pos, state, entity, (XtremeHopperBlockEntity) blockEntity);
        }
    }
}
