package com.chimericdream.camelnostrils.block;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.advancement.CamelNostrilsAdvancements;
import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    public static final MapCodec<BedBlock> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(DyeColor.CODEC.fieldOf("color").forGetter(BedBlock::getColor), propertiesCodec())
            .apply(instance, UpsideDownBedBlock::new)
    );

    private static final Map<Direction, VoxelShape> SHAPES;

    @Override
    public @NonNull MapCodec<BedBlock> codec() {
        return CODEC;
    }

    public UpsideDownBedBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(color, properties);
    }

    public static UpsideDownBedBlock create(DyeColor color) {
        return new UpsideDownBedBlock(color, Properties.ofFullCopy(vanillaBedFor(color)).setId(blockRegistryKey(color)));
    }

    /**
     * 26.1.2 has no {@code ColorCollection} grouping the sixteen bed colors under one field the way
     * 26.2 does - they're still separate flat constants here, same as every MC version before 26.2 -
     * so selecting one by {@link DyeColor} means switching over it directly.
     */
    private static Block vanillaBedFor(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_BED;
            case ORANGE -> Blocks.ORANGE_BED;
            case MAGENTA -> Blocks.MAGENTA_BED;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_BED;
            case YELLOW -> Blocks.YELLOW_BED;
            case LIME -> Blocks.LIME_BED;
            case PINK -> Blocks.PINK_BED;
            case GRAY -> Blocks.GRAY_BED;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_BED;
            case CYAN -> Blocks.CYAN_BED;
            case PURPLE -> Blocks.PURPLE_BED;
            case BLUE -> Blocks.BLUE_BED;
            case BROWN -> Blocks.BROWN_BED;
            case GREEN -> Blocks.GREEN_BED;
            case RED -> Blocks.RED_BED;
            case BLACK -> Blocks.BLACK_BED;
        };
    }

    public static Identifier blockId(DyeColor color) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, color.getName() + "_upside_down_bed");
    }

    public static ResourceKey<Block> blockRegistryKey(DyeColor color) {
        return ResourceKey.create(Registries.BLOCK, blockId(color));
    }

    public static ResourceKey<Item> itemRegistryKey(DyeColor color) {
        return ResourceKey.create(Registries.ITEM, blockId(color));
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
