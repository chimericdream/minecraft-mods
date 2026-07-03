package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.GlazedHopperBlockEntity;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.GLAZED_HOPPER_BLOCK_ENTITY;

public class GlazedHopperBlock extends AbstractHopperBlock {
    public static final MapCodec<GlazedHopperBlock> CODEC = simpleCodec(GlazedHopperBlock::create);
    public static final String TOOLTIP_KEY = "block.hopperxtreme.honey_glazed_hopper.tooltip";

    private final int cooldownInTicks;
    private final String baseKey;
    private final boolean withFilter;

    static GlazedHopperBlock create(Properties settings) {
        return new GlazedHopperBlock(8, "default") {
        };
    }

    public GlazedHopperBlock(int cooldownInTicks, String translationKey) {
        this(cooldownInTicks, translationKey, false);
    }

    public GlazedHopperBlock(int cooldownInTicks, String translationKey, boolean withFilter) {
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
    protected @NotNull MapCodec<GlazedHopperBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlazedHopperBlockEntity(pos, state, cooldownInTicks, withFilter);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide() ? null : createTickerHelper(type, GLAZED_HOPPER_BLOCK_ENTITY.get(), GlazedHopperBlockEntity::serverTick);
    }

    @Override
    protected void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.is(state.getBlock())) {
            this.updateEnabled(world, pos, state);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GlazedHopperBlockEntity) {
                player.openMenu((GlazedHopperBlockEntity) blockEntity);
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
        boolean bl = !world.hasNeighborSignal(pos);
        if (bl != state.getValue(ENABLED)) {
            world.setBlock(pos, state.setValue(ENABLED, bl), 2);
        }
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier handler, boolean bl) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof GlazedHopperBlockEntity) {
            GlazedHopperBlockEntity.onEntityCollided(world, pos, state, entity, (GlazedHopperBlockEntity) blockEntity);
        }
    }
}
