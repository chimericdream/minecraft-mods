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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;
import static com.chimericdream.hopperxtreme.block.ModBlocks.GLAZED_HOPPER_BLOCK_ENTITY;

public class GlazedHopperBlock extends AbstractHopperBlock {
    public static final MapCodec<GlazedHopperBlock> CODEC = createCodec(GlazedHopperBlock::create);
    public static final String TOOLTIP_KEY = "block.hopperxtreme.honey_glazed_hopper.tooltip";

    private final int cooldownInTicks;
    private final String baseKey;
    private final boolean withFilter;

    static GlazedHopperBlock create(Settings settings) {
        return new GlazedHopperBlock(8, "default") {
        };
    }

    public GlazedHopperBlock(int cooldownInTicks, String translationKey) {
        this(cooldownInTicks, translationKey, false);
    }

    public GlazedHopperBlock(int cooldownInTicks, String translationKey, boolean withFilter) {
        super(
            Settings.copy(Blocks.HOPPER)
                .mapColor(MapColor.STONE_GRAY)
                .requiresTool()
                .strength(3.0F, 4.8F)
                .sounds(BlockSoundGroup.METAL)
                .nonOpaque()
                .registryKey(REGISTRY_HELPER.makeBlockRegistryKey(translationKey))
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
        return new GlazedHopperBlockEntity(pos, state, cooldownInTicks, withFilter);
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
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GlazedHopperBlockEntity) {
                player.openHandledScreen((GlazedHopperBlockEntity) blockEntity);
                player.incrementStat(Stats.INSPECT_HOPPER);
            }

            return ActionResult.CONSUME;
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
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
