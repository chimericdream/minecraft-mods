package com.chimericdream.logallthethings.carpetlog;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import com.chimericdream.logallthethings.ModInfo;

/**
 * The block a carpet-logged slab/stair actually becomes in the world. Carries no blockstate
 * properties of its own (see {@code carpeted_block.json}, which points every state at the empty
 * {@code minecraft:block/air} model) — all the real state lives in its {@link CarpetedBlockEntity},
 * and {@code RenderShape.INVISIBLE} hands rendering entirely to
 * {@code carpetlog.client.CarpetedBlockEntityRenderer}. Shape/collision delegate to the host plus a
 * thin footprint fitted to whichever part of the host is actually flat enough for a carpet to lie on
 * (see {@link #carpetFootprintShape}); drops are hardcoded to one of each sub-block's item, which is
 * safe because every vanilla slab/stair/carpet always drops exactly itself regardless of loot
 * conditions.
 */
public class CarpetedBlock extends Block implements EntityBlock {
    public CarpetedBlock() {
        super(
            BlockBehaviour.Properties.of()
                .noOcclusion()
                .dynamicShape()
                .forceSolidOn()
                .strength(2.0F)
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "carpeted_block")))
        );
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(properties -> new CarpetedBlock());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CarpetedBlockEntity(pos, state);
    }

    /**
     * See {@code WindowedBlock#getRenderShape} for why {@code INVISIBLE} is correct here under MC
     * 26.2's render-feature overhaul.
     */
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public BlockState getHostState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CarpetedBlockEntity be ? be.getHostState() : Blocks.AIR.defaultBlockState();
    }

    public BlockState getCarpetState(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CarpetedBlockEntity be ? be.getCarpetState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockState hostState = getHostState(level, pos);
        return Shapes.or(hostState.getShape(level, pos, context), carpetFootprintShape(hostState));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    /**
     * Without this, mining speed comes from this block's own fixed {@code strength(2.0F)} regardless
     * of which part is targeted — mirrors {@code WindowedBlock#getDestroyProgress}.
     */
    @Override
    protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof CarpetedBlockEntity be && level instanceof Level realLevel) {
            BlockState targeted = CarpetLogHelper.pickTargetedState(realLevel, pos, be.getCarpetState(), be.getHostState(), player);

            if (!targeted.isAir()) {
                return targeted.getDestroyProgress(player, level, pos);
            }
        }

        return super.getDestroyProgress(state, player, level, pos);
    }

    /**
     * Mirrors {@code WindowedBlock#spawnDestroyParticles} — this block's own single, blockstate-less
     * state has no real texture to draw break particles from, so this delegates to whichever sub-state
     * was actually aimed at.
     */
    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof CarpetedBlockEntity be) {
            BlockState targeted = CarpetLogHelper.pickTargetedState(level, pos, be.getCarpetState(), be.getHostState(), player);

            if (!targeted.isAir()) {
                level.levelEvent(player, 2001, pos, Block.getId(targeted));
                return;
            }
        }

        super.spawnDestroyParticles(level, player, pos, state);
    }

    /**
     * This block has no registered {@code BlockItem} of its own, so vanilla's default here would
     * return an empty pick-block result — mirrors {@code WindowedBlock#getCloneItemStack}.
     */
    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        if (level.getBlockEntity(pos) instanceof CarpetedBlockEntity be) {
            BlockState targeted = CarpetLogHelper.pickTargetedStateForPickBlock(level, pos, be.getCarpetState(), be.getHostState());

            if (!targeted.isAir()) {
                return new ItemStack(targeted.getBlock());
            }
        }

        return super.getCloneItemStack(level, pos, state, includeData);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (!(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CarpetedBlockEntity be)) {
            return List.of();
        }

        List<ItemStack> drops = new ArrayList<>(2);
        if (!be.getHostState().isAir()) {
            drops.add(new ItemStack(be.getHostState().getBlock()));
        }
        if (!be.getCarpetState().isAir()) {
            drops.add(new ItemStack(be.getCarpetState().getBlock()));
        }

        return drops;
    }

    /** Matches the 1px-thick layer {@code assets/logallthethings/models/block/*_carpet.json} draws. */
    private static final double CARPET_THICKNESS = 1.0 / 16.0;

    /**
     * A real {@code CarpetBlock}'s own shape is a full 16x16 square — fine for a slab (its missing
     * half genuinely is that full footprint, whether the carpet rests on top of a bottom slab or hangs
     * beneath a top one), but wrong for a stair: only the low, flat run is actually carpet-eligible in
     * the hand-authored models this mirrors ({@code stairs_carpet.json} / {@code top_stairs_carpet.json}),
     * the raised corner is not. Unioning in the real full-width shape there would make the corner
     * readable as "carpet" for pick-block/mining-speed purposes even though nothing is drawn on it.
     *
     * <p>The stair case is authored assuming {@code FACING == EAST} (flat run on the west half) and
     * rotated to the real facing — same convention and per-facing degree table as
     * {@code CarpetFrameRenderer#select}, so the collision box always matches what's actually drawn.
     */
    private static VoxelShape carpetFootprintShape(BlockState hostState) {
        if (hostState.getBlock() instanceof StairBlock) {
            if (hostState.getValue(StairBlock.SHAPE) != StairsShape.STRAIGHT) {
                return Shapes.empty();
            }

            boolean top = hostState.getValue(StairBlock.HALF) == Half.TOP;
            double y0 = top ? 0.0 : 0.5;
            double y1 = y0 + CARPET_THICKNESS;

            return rotateFootprintY(0.0, y0, 0.0, 0.5, y1, 1.0, stairsYRotation(hostState.getValue(StairBlock.FACING)));
        }

        if (hostState.getBlock() instanceof SlabBlock) {
            boolean top = hostState.getValue(SlabBlock.TYPE) == SlabType.TOP;
            double y0 = top ? 0.0 : 0.5;
            double y1 = y0 + CARPET_THICKNESS;

            return Shapes.box(0.0, y0, 0.0, 1.0, y1, 1.0);
        }

        return Shapes.empty();
    }

    /**
     * Same per-facing degree table as {@code CarpetFrameRenderer#select} — kept in sync deliberately so
     * this collision box always matches whatever that renderer actually draws.
     */
    private static int stairsYRotation(Direction facing) {
        return switch (facing) {
            case EAST -> 0;
            case SOUTH -> 270;
            case WEST -> 180;
            case NORTH -> 90;
            default -> 0;
        };
    }

    /**
     * Rotates an axis-aligned XZ footprint about the block's vertical center by a multiple of 90
     * degrees, using the same rotation direction as {@code PoseStack#mulPose(Axis.YP.rotationDegrees)}
     * (the mechanism {@code CarpetFrameRenderer} uses to orient the drawn mesh) so the two never
     * disagree about which side of the block is "flat."
     */
    private static VoxelShape rotateFootprintY(double x0, double y0, double z0, double x1, double y1, double z1, int degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;

        for (double[] corner : new double[][]{{x0, z0}, {x1, z0}, {x1, z1}, {x0, z1}}) {
            double px = corner[0] - 0.5;
            double pz = corner[1] - 0.5;
            double rx = px * cos + pz * sin + 0.5;
            double rz = -px * sin + pz * cos + 0.5;

            minX = Math.min(minX, rx);
            maxX = Math.max(maxX, rx);
            minZ = Math.min(minZ, rz);
            maxZ = Math.max(maxZ, rz);
        }

        return Shapes.box(minX, y0, minZ, maxX, y1, maxZ);
    }
}
