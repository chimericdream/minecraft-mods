package com.chimericdream.camelnostrils.block;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.advancement.CamelNostrilsAdvancements;
import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Optional;

public class UpsideDownBedBlock extends BedBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "upside_down_bed");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public static final MapCodec<BedBlock> CODEC = simpleCodec(UpsideDownBedBlock::new);

    private static final Map<Direction, VoxelShape> SHAPES;

    @Override
    public @NonNull MapCodec<BedBlock> codec() {
        return CODEC;
    }

    public UpsideDownBedBlock() {
        this(Properties.ofFullCopy(Blocks.BED.white()).setId(BLOCK_REGISTRY_KEY));
    }

    public UpsideDownBedBlock(BlockBehaviour.Properties properties) {
        super(DyeColor.WHITE, properties);
    }

    public static UpsideDownBedBlock create() {
        return new UpsideDownBedBlock();
    }

    @Override
    protected @NonNull VoxelShape getShape(@NonNull BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPES.get(getConnectedDirection(state).getOpposite());
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        boolean wasSleeping = player.isSleeping();
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hitResult);

        if (!wasSleeping && player.isSleeping() && !level.isClientSide() && level instanceof ServerLevel serverLevel) {
            flipToNight(serverLevel);

            if (player instanceof ServerPlayer serverPlayer) {
                CamelNostrilsAdvancements.award(serverPlayer, CamelNostrilsAdvancements.NAP_TIME);
            }

            player.stopSleepInBed(true, true);
        }

        return result;
    }

    private static void flipToNight(ServerLevel level) {
        Optional<Holder<WorldClock>> clock = level.dimensionType().defaultClock();

        if (clock.isPresent() && Boolean.TRUE.equals(level.getGameRules().get(GameRules.ADVANCE_TIME))) {
            level.clockManager().moveToTimeMarker(clock.get(), ClockTimeMarkers.NIGHT);
        }
    }

    static {
        VoxelShape northWestLeg = Block.box(0.0, 13.0, 0.0, 3.0, 16.0, 3.0);
        VoxelShape northEastLeg = Shapes.rotate(northWestLeg, OctahedralGroup.BLOCK_ROT_Y_90);
        SHAPES = Util.make(
            () -> Shapes.rotateHorizontal(Shapes.or(Block.column(16.0, 7.0, 13.0), northWestLeg, northEastLeg))
        );
    }
}
