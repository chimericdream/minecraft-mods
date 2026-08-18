package com.chimericdream.artificialheart.block;

import com.chimericdream.artificialheart.ModInfo;
import com.chimericdream.artificialheart.PassiveCreakingAccessor;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
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
    private @Nullable BlockPattern creakingGolemBase;
    private @Nullable BlockPattern creakingGolemFull;
    private static final Predicate<BlockState> PALE_LOGS_PREDICATE;
    private static final Predicate<BlockState> PALE_PUMPKINS_PREDICATE;
    private static final Predicate<BlockState> CREAKING_HEARTS_PREDICATE;

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

    protected void onPlace(
        final BlockState state,
        final @NonNull Level level,
        final @NonNull BlockPos pos,
        final BlockState oldState,
        final boolean movedByPiston
    ) {
        if (!oldState.is(state.getBlock())) {
            this.trySpawnGolem(level, pos, state);
        }
    }

    public boolean canSpawnGolem(final LevelReader level, final BlockPos topPos) {
        return this.getOrCreateCreakingGolemBase().find(level, topPos) != null;
    }

    private void trySpawnGolem(final Level level, final BlockPos topPos, final BlockState state) {
        BlockPattern.BlockPatternMatch creakingGolemMatch = this.getOrCreateCreakingGolemFull().find(level, topPos);
        if (creakingGolemMatch != null) {
            Creaking creaking = (Creaking) EntityTypes.CREAKING.create(level, EntitySpawnReason.TRIGGERED);
            if (creaking != null) {
                AttributeInstance maxHealth = creaking.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealth != null) {
                    maxHealth.setBaseValue(DefaultAttributes.getSupplier(EntityTypes.ENDERMAN).getBaseValue(Attributes.MAX_HEALTH));
                }
                creaking.setHealth(creaking.getMaxHealth());
                creaking.setPersistenceRequired();
                ((PassiveCreakingAccessor) creaking).ah$setPassive(true);
                if (creakingGolemMatch.getBlock(1, 1, 0).getState().is(ModBlocks.ARTIFICIAL_CREAKING_HEART_BLOCK.get())) {
                    // A Detached Creaking Heart is fully decorative, so the golem it builds is inert - it stands
                    // in place instead of stalking players like one built on a real creaking heart.
                    creaking.setNoAi(true);
                }
                float golemYaw = state.getValue(FACING).toYRot();
                BlockPos spawnPos = creakingGolemMatch.getBlock(1, 2, 0).getPos();
                spawnGolemInWorld(level, creakingGolemMatch, creaking, spawnPos, golemYaw);
                return;
            }
        }
    }

    private static void spawnGolemInWorld(final Level level, final BlockPattern.BlockPatternMatch match, final Entity golem, final BlockPos spawnPos, final float yaw) {
        clearPatternBlocks(level, match);
        golem.snapTo((double)spawnPos.getX() + (double)0.5F, (double)spawnPos.getY() + 0.05, (double)spawnPos.getZ() + (double)0.5F, yaw, 0.0F);
        if (golem instanceof Mob mob) {
            // snapTo only sets yRot/xRot. The spawn packet's client-side body/head orientation comes from
            // yHeadRot instead, which otherwise stays at its construction-time default (0) until AI ticks
            // catch it up - so a NoAi golem would spawn facing the wrong way and never correct itself.
            mob.setYHeadRot(yaw);
            mob.setYBodyRot(yaw);
        }
        level.addFreshEntity(golem);

        for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, golem.getBoundingBox().inflate((double)5.0F))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(player, golem);
        }

        updatePatternBlocks(level, match);
    }

    public static void clearPatternBlocks(final Level level, final BlockPattern.BlockPatternMatch match) {
        for(int x = 0; x < match.getWidth(); ++x) {
            for(int y = 0; y < match.getHeight(); ++y) {
                BlockInWorld block = match.getBlock(x, y, 0);
                level.setBlock(block.getPos(), Blocks.AIR.defaultBlockState(), 2);
                level.levelEvent(2001, block.getPos(), Block.getId(block.getState()));
            }
        }
    }

    public static void updatePatternBlocks(final Level level, final BlockPattern.BlockPatternMatch match) {
        for(int x = 0; x < match.getWidth(); ++x) {
            for(int y = 0; y < match.getHeight(); ++y) {
                BlockInWorld block = match.getBlock(x, y, 0);
                level.updateNeighborsAt(block.getPos(), Blocks.AIR);
            }
        }
    }

    public BlockState getStateForPlacement(final BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    private BlockPattern getOrCreateCreakingGolemBase() {
        if (this.creakingGolemBase == null) {
            this.creakingGolemBase = BlockPatternBuilder.start()
                .aisle(new String[]{"~ ~", "fhf", "~#~"})
                .where('h', BlockInWorld.hasState(CREAKING_HEARTS_PREDICATE))
                .where('#', BlockInWorld.hasState(PALE_LOGS_PREDICATE))
                .where('f', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.PALE_OAK_FENCE)))
                .where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
                .build();
        }

        return this.creakingGolemBase;
    }

    private BlockPattern getOrCreateCreakingGolemFull() {
        if (this.creakingGolemFull == null) {
            this.creakingGolemFull = BlockPatternBuilder.start()
                .aisle(new String[]{"~^~", "fhf", "~#~"})
                .where('^', BlockInWorld.hasState(PALE_PUMPKINS_PREDICATE))
                .where('h', BlockInWorld.hasState(CREAKING_HEARTS_PREDICATE))
                .where('#', BlockInWorld.hasState(PALE_LOGS_PREDICATE))
                .where('f', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.PALE_OAK_FENCE)))
                .where('~', BlockInWorld.hasState(BlockBehaviour.BlockStateBase::isAir))
                .build();
        }

        return this.creakingGolemFull;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        PALE_LOGS_PREDICATE = (input) -> input.is(BlockTags.PALE_OAK_LOGS);
        PALE_PUMPKINS_PREDICATE = (input) -> input.is(ModBlocks.PALE_CARVED_PUMPKIN_BLOCK.get()) || input.is(ModBlocks.PALE_JACK_O_LANTERN_BLOCK.get());
        CREAKING_HEARTS_PREDICATE = (input) -> input.is(Blocks.CREAKING_HEART) || input.is(ModBlocks.ARTIFICIAL_CREAKING_HEART_BLOCK.get());
    }
}
