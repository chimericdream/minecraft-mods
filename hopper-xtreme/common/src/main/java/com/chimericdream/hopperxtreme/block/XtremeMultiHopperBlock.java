package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.XtremeMultiHopperBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.hopperxtreme.block.ModBlocks.XTREME_MULTI_HOPPER_BLOCK_ENTITY;

public class XtremeMultiHopperBlock extends AbstractMultiHopperBlock {
    public static final MapCodec<XtremeMultiHopperBlock> CODEC = createCodec(XtremeMultiHopperBlock::create);

    private final int cooldownInTicks;
    private final String baseKey;
    private final boolean withFilter;

    static XtremeMultiHopperBlock create(Settings settings) {
        return new XtremeMultiHopperBlock(8, "default");
    }

    public XtremeMultiHopperBlock(int cooldownInTicks, String baseKey) {
        this(cooldownInTicks, baseKey, false);
    }

    public XtremeMultiHopperBlock(int cooldownInTicks, String baseKey, boolean withFilter) {
        super(Settings.copy(Blocks.HOPPER).mapColor(MapColor.STONE_GRAY).requiresTool().strength(3.0F, 4.8F).sounds(BlockSoundGroup.METAL).nonOpaque());

        this.cooldownInTicks = cooldownInTicks;
        this.baseKey = baseKey;
        this.withFilter = withFilter;
    }

    public int getCooldownInTicks() {
        return cooldownInTicks;
    }

    public String getBaseKey() {
        return baseKey;
    }

    @Override
    protected MapCodec<XtremeMultiHopperBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new XtremeMultiHopperBlockEntity(pos, state, cooldownInTicks, withFilter);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, XTREME_MULTI_HOPPER_BLOCK_ENTITY.get(), XtremeMultiHopperBlockEntity::serverTick);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.updateEnabled(world, pos, state);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof XtremeMultiHopperBlockEntity) {
                player.openHandledScreen((XtremeMultiHopperBlockEntity) blockEntity);
                player.incrementStat(Stats.INSPECT_HOPPER);
            }

            return ActionResult.CONSUME;
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        this.updateEnabled(world, pos, state);
    }

    private void updateEnabled(World world, BlockPos pos, BlockState state) {
        boolean bl = !world.isReceivingRedstonePower(pos);
        if (bl != state.get(ENABLED)) {
            world.setBlockState(pos, state.with(ENABLED, bl), 2);
        }

    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof XtremeMultiHopperBlockEntity) {
            XtremeMultiHopperBlockEntity.onEntityCollided(world, pos, state, entity, (XtremeMultiHopperBlockEntity) blockEntity);
        }

    }
}
