package com.chimericdream.hopperxtreme.block;

import com.chimericdream.hopperxtreme.entity.GlazedHopperBlockEntity;
import com.chimericdream.lib.text.TextHelpers;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.chimericdream.hopperxtreme.block.ModBlocks.GLAZED_HOPPER_BLOCK_ENTITY;

public class GlazedHopperBlock extends AbstractHopperBlock {
    public static final MapCodec<GlazedHopperBlock> CODEC = createCodec(GlazedHopperBlock::create);
    public static final String TOOLTIP_KEY = "block.hopperxtreme.honey_glazed_hopper.tooltip";

    private final int cooldownInTicks;
    private final String baseKey;

    static GlazedHopperBlock create(Settings settings) {
        return new GlazedHopperBlock(8, "default") {
        };
    }

    public GlazedHopperBlock(int cooldownInTicks, String translationKey) {
        super(Settings.copy(Blocks.HOPPER).mapColor(MapColor.STONE_GRAY).requiresTool().strength(3.0F, 4.8F).sounds(BlockSoundGroup.METAL).nonOpaque());

        this.cooldownInTicks = cooldownInTicks;
        this.baseKey = translationKey;
    }

    public int getCooldownInTicks() {
        return cooldownInTicks;
    }

    public String getBaseKey() {
        return baseKey;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        if (!baseKey.equals("honey_glazed_hopper")) {
            return;
        }

        tooltip.add(TextHelpers.getTooltip(TOOLTIP_KEY));
    }

    @Override
    protected MapCodec<GlazedHopperBlock> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GlazedHopperBlockEntity(pos, state, cooldownInTicks);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : validateTicker(type, GLAZED_HOPPER_BLOCK_ENTITY.get(), GlazedHopperBlockEntity::serverTick);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!oldState.isOf(state.getBlock())) {
            this.updateEnabled(world, pos, state);
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
        if (blockEntity instanceof GlazedHopperBlockEntity) {
            GlazedHopperBlockEntity.onEntityCollided(world, pos, state, entity, (GlazedHopperBlockEntity) blockEntity);
        }
    }
}
