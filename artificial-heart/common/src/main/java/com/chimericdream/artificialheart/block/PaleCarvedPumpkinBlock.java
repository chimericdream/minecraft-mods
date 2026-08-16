package com.chimericdream.artificialheart.block;

import com.chimericdream.artificialheart.ModInfo;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

public class PaleCarvedPumpkinBlock extends HorizontalDirectionalBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pale_carved_pumpkin");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public static final Identifier JOL_BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pale_jack_o_lantern");
    public static final ResourceKey<Block> JOL_BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, JOL_BLOCK_ID);
    public static final ResourceKey<Item> JOL_ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, JOL_BLOCK_ID);

    public static final MapCodec<CarvedPumpkinBlock> CODEC = simpleCodec(CarvedPumpkinBlock::new);
    public static final EnumProperty<Direction> FACING;
    private @Nullable BlockPattern snowGolemBase;
    private @Nullable BlockPattern snowGolemFull;
    private @Nullable BlockPattern ironGolemBase;
    private @Nullable BlockPattern ironGolemFull;
    private @Nullable BlockPattern copperGolemBase;
    private @Nullable BlockPattern copperGolemFull;
    private static final Predicate<BlockState> PUMPKINS_PREDICATE;

    public @NonNull MapCodec<? extends CarvedPumpkinBlock> codec() {
        return CODEC;
    }

    public PaleCarvedPumpkinBlock() {
        this(Properties.ofFullCopy(Blocks.CARVED_PUMPKIN).setId(BLOCK_REGISTRY_KEY));
    }

    public PaleCarvedPumpkinBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public static PaleCarvedPumpkinBlock createJackOLantern() {
        return new PaleCarvedPumpkinBlock(Properties.ofFullCopy(Blocks.JACK_O_LANTERN).setId(JOL_BLOCK_REGISTRY_KEY));
    }

    protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
//        if (!oldState.is(state.getBlock())) {
//            this.trySpawnGolem(level, pos);
//        }
    }

    public boolean canSpawnGolem(final LevelReader level, final BlockPos topPos) {
        return false;
//        return this.getOrCreateSnowGolemBase().find(level, topPos) != null || this.getOrCreateIronGolemBase().find(level, topPos) != null || this.getOrCreateCopperGolemBase().find(level, topPos) != null;
    }

    private void trySpawnGolem(final Level level, final BlockPos topPos) {
//        BlockPattern.BlockPatternMatch snowGolemMatch = this.getOrCreateSnowGolemFull().find(level, topPos);
//        if (snowGolemMatch != null) {
//            SnowGolem snowGolem = (SnowGolem) EntityTypes.SNOW_GOLEM.create(level, EntitySpawnReason.TRIGGERED);
//            if (snowGolem != null) {
//                spawnGolemInWorld(level, snowGolemMatch, snowGolem, snowGolemMatch.getBlock(0, 2, 0).getPos());
//                return;
//            }
//        }
//
//        BlockPattern.BlockPatternMatch ironGolemMatch = this.getOrCreateIronGolemFull().find(level, topPos);
//        if (ironGolemMatch != null) {
//            IronGolem ironGolem = (IronGolem)EntityTypes.IRON_GOLEM.create(level, EntitySpawnReason.TRIGGERED);
//            if (ironGolem != null) {
//                ironGolem.setPlayerCreated(true);
//                spawnGolemInWorld(level, ironGolemMatch, ironGolem, ironGolemMatch.getBlock(1, 2, 0).getPos());
//                return;
//            }
//        }
//
//        BlockPattern.BlockPatternMatch copperGolemMatch = this.getOrCreateCopperGolemFull().find(level, topPos);
//        if (copperGolemMatch != null) {
//            CopperGolem copperGolem = (CopperGolem)EntityTypes.COPPER_GOLEM.create(level, EntitySpawnReason.TRIGGERED);
//            if (copperGolem != null) {
//                spawnGolemInWorld(level, copperGolemMatch, copperGolem, copperGolemMatch.getBlock(0, 0, 0).getPos());
//                this.replaceCopperBlockWithChest(level, copperGolemMatch);
//                copperGolem.spawn(this.getWeatherStateFromPattern(copperGolemMatch));
//            }
//        }
    }

    private static void spawnGolemInWorld(final Level level, final BlockPattern.BlockPatternMatch match, final Entity golem, final BlockPos spawnPos) {
//        clearPatternBlocks(level, match);
//        golem.snapTo((double)spawnPos.getX() + (double)0.5F, (double)spawnPos.getY() + 0.05, (double)spawnPos.getZ() + (double)0.5F, 0.0F, 0.0F);
//        level.addFreshEntity(golem);
//
//        for(ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate((double)5.0F))) {
//            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, golem);
//        }
//
//        updatePatternBlocks(level, match);
    }

    public static void clearPatternBlocks(final Level level, final BlockPattern.BlockPatternMatch match) {
//        for(int x = 0; x < match.getWidth(); ++x) {
//            for(int y = 0; y < match.getHeight(); ++y) {
//                BlockInWorld block = match.getBlock(x, y, 0);
//                level.setBlock(block.getPos(), Blocks.AIR.defaultBlockState(), 2);
//                level.levelEvent(2001, block.getPos(), Block.getId(block.getState()));
//            }
//        }
    }

    public static void updatePatternBlocks(final Level level, final BlockPattern.BlockPatternMatch match) {
//        for(int x = 0; x < match.getWidth(); ++x) {
//            for(int y = 0; y < match.getHeight(); ++y) {
//                BlockInWorld block = match.getBlock(x, y, 0);
//                level.updateNeighborsAt(block.getPos(), Blocks.AIR);
//            }
//        }
    }

    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

//    private BlockPattern getOrCreateSnowGolemBase() {
//        if (this.snowGolemBase == null) {
//            this.snowGolemBase = BlockPatternBuilder.start().aisle(new String[]{" ", "#", "#"}).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
//        }
//
//        return this.snowGolemBase;
//    }
//
//    private BlockPattern getOrCreateSnowGolemFull() {
//        if (this.snowGolemFull == null) {
//            this.snowGolemFull = BlockPatternBuilder.start().aisle(new String[]{"^", "#", "#"}).where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
//        }
//
//        return this.snowGolemFull;
//    }
//
//    private BlockPattern getOrCreateIronGolemBase() {
//        if (this.ironGolemBase == null) {
//            this.ironGolemBase = BlockPatternBuilder.start().aisle(new String[]{"~ ~", "###", "~#~"}).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir)).build();
//        }
//
//        return this.ironGolemBase;
//    }
//
//    private BlockPattern getOrCreateIronGolemFull() {
//        if (this.ironGolemFull == null) {
//            this.ironGolemFull = BlockPatternBuilder.start().aisle(new String[]{"~^~", "###", "~#~"}).where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir)).build();
//        }
//
//        return this.ironGolemFull;
//    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        PUMPKINS_PREDICATE = (input) -> input.is(Blocks.CARVED_PUMPKIN) || input.is(Blocks.JACK_O_LANTERN);
    }
}
