package com.chimericdream.logallthethings.windowlog;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import com.chimericdream.logallthethings.ModInfo;

/**
 * The block a window-logged slab/stair actually becomes in the world. Carries no blockstate
 * properties of its own (see {@code windowed_block.json}, which points every state at the empty
 * {@code minecraft:block/air} model) — all the real state lives in its {@link WindowedBlockEntity},
 * and {@code RenderShape.INVISIBLE} hands rendering entirely to
 * {@code windowlog.client.WindowedBlockEntityRenderer}. Shape/collision delegate to the union of the
 * host and window sub-states; drops are hardcoded to one of each sub-block's item, which is safe
 * because every vanilla slab/stair/pane always drops exactly itself regardless of loot conditions.
 */
public class WindowedBlock extends Block implements EntityBlock {
    public WindowedBlock() {
        super(
            BlockBehaviour.Properties.of()
                .noOcclusion()
                .dynamicShape()
                .strength(2.0F)
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "windowed_block")))
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(properties -> new WindowedBlock());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WindowedBlockEntity(pos, state);
    }

    /**
     * MC 26.2's {@link RenderShape} only distinguishes {@code MODEL} from {@code INVISIBLE} — the old
     * {@code ENTITYBLOCK_ANIMATED} value is gone, and {@code BlockEntityRenderDispatcher} now dispatches
     * to a registered {@code BlockEntityRenderer} purely by block entity type, independent of this
     * value. {@code INVISIBLE} matches {@code windowed_block.json}'s empty {@code minecraft:block/air}
     * model and skips binding a (pointless) model for it.
     */
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public BlockState getHostState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof WindowedBlockEntity be ? be.getHostState() : Blocks.AIR.defaultBlockState();
    }

    public BlockState getWindowState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof WindowedBlockEntity be ? be.getWindowState() : Blocks.AIR.defaultBlockState();
    }

    /**
     * A stair's open notch is only half the block's width (the raised step occupies the other half),
     * but a real {@code minecraft:glass_pane} blockstate's connected shape always spans the block's
     * <em>full</em> 16px width/depth regardless — unioning it in directly (as this used to) covers the
     * already-solid host half too, so the visible collision box comes out full-width instead of fitted
     * to the notch (a "1x8x16" slab flush against the upper step, not the "8x8x1" the hand-authored
     * {@code WindowFrameRenderer} glass mesh actually occupies). For stairs, a shape sized to that same
     * mesh is used instead of the pane's own shape; slabs keep the pane's real shape since a slab's
     * missing half genuinely is the pane's full 16x16 footprint.
     */
    private static final double STAIR_WINDOW_HALF_THICKNESS = 0.03125; // half of 1px, matching the glass mesh's 7.5-8.5 Z range

    private static VoxelShape stairWindowShape(Direction facing, Half half) {
        double lowY = half == Half.BOTTOM ? 0.5 : 0.0;
        double highY = half == Half.BOTTOM ? 1.0 : 0.5;
        double centerLow = 0.5 - STAIR_WINDOW_HALF_THICKNESS;
        double centerHigh = 0.5 + STAIR_WINDOW_HALF_THICKNESS;

        return switch (facing) {
            case EAST -> Shapes.box(0.0, lowY, centerLow, 0.5, highY, centerHigh);
            case WEST -> Shapes.box(0.5, lowY, centerLow, 1.0, highY, centerHigh);
            case SOUTH -> Shapes.box(centerLow, lowY, 0.0, centerHigh, highY, 0.5);
            case NORTH -> Shapes.box(centerLow, lowY, 0.5, centerHigh, highY, 1.0);
            default -> Shapes.empty();
        };
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockState hostState = getHostState(level, pos);

        VoxelShape windowShape = hostState.getBlock() instanceof StairBlock
            ? stairWindowShape(hostState.getValue(StairBlock.FACING), hostState.getValue(StairBlock.HALF))
            : getWindowState(level, pos).getShape(level, pos, context);

        return Shapes.or(hostState.getShape(level, pos, context), windowShape);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (!(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof WindowedBlockEntity be)) {
            return List.of();
        }

        List<ItemStack> drops = new ArrayList<>(2);
        if (!be.getHostState().isAir()) {
            drops.add(new ItemStack(be.getHostState().getBlock()));
        }
        if (!be.getWindowState().isAir()) {
            drops.add(new ItemStack(be.getWindowState().getBlock()));
        }

        return drops;
    }
}
