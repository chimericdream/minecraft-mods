package com.chimericdream.camelnostrils.block;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.advancement.CamelNostrilsAdvancements;
import com.chimericdream.camelnostrils.entity.ModEntities;
import com.chimericdream.camelnostrils.stats.ModStats;
import com.chimericdream.lib.blocks.FallingUpwardBlock;
import com.chimericdream.lib.entities.FallingUpwardBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class LivnaBlock extends FallingUpwardBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "livna");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public static final Identifier CHIPPED_BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "chipped_livna");
    public static final ResourceKey<Block> CHIPPED_BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, CHIPPED_BLOCK_ID);
    public static final ResourceKey<Item> CHIPPED_ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, CHIPPED_BLOCK_ID);

    public static final Identifier DAMAGED_BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "damaged_livna");
    public static final ResourceKey<Block> DAMAGED_BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, DAMAGED_BLOCK_ID);
    public static final ResourceKey<Item> DAMAGED_ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, DAMAGED_BLOCK_ID);

    public static final MapCodec<LivnaBlock> CODEC = simpleCodec(LivnaBlock::new);

    public static final EnumProperty<Direction> FACING;
    private static final Map<Direction.Axis, VoxelShape> SHAPES;
    private static final Component CONTAINER_TITLE;

    @Override
    protected @NonNull MapCodec<? extends FallingUpwardBlock> codec() {
        return CODEC;
    }

    public LivnaBlock() {
        this(Properties.ofFullCopy(Blocks.ANVIL).setId(BLOCK_REGISTRY_KEY));
    }

    public LivnaBlock(BlockBehaviour.Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public static LivnaBlock create() {
        return new LivnaBlock();
    }

    public static LivnaBlock createChipped() {
        return new LivnaBlock(Properties.ofFullCopy(Blocks.CHIPPED_ANVIL).setId(CHIPPED_BLOCK_REGISTRY_KEY));
    }

    public static LivnaBlock createDamaged() {
        return new LivnaBlock(Properties.ofFullCopy(Blocks.DAMAGED_ANVIL).setId(DAMAGED_BLOCK_REGISTRY_KEY));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable LivingEntity placer, @NonNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide() && placer instanceof ServerPlayer serverPlayer) {
            CamelNostrilsAdvancements.award(serverPlayer, CamelNostrilsAdvancements.WHAT_GOES_UP);
        }
    }

    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.openMenu(state.getMenuProvider(level, pos));
            player.awardStat(ModStats.INTERACT_WITH_LIVNA);
        }

        return InteractionResult.SUCCESS;
    }

    protected @Nullable MenuProvider getMenuProvider(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos) {
        // @TODO: replace with LivnaMenu, a mirrored copy of the AnvilMenu
        return new SimpleMenuProvider((containerId, inventory, player) -> new AnvilMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }

    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        return SHAPES.get(state.getValue(FACING).getAxis());
    }

    @Override
    protected EntityType<? extends FallingUpwardBlockEntity> getFallingUpwardEntityType() {
        return ModEntities.FALLING_UPWARD_BLOCK_ENTITY.get();
    }

    protected void rising(FallingUpwardBlockEntity entity) {
        entity.setHurtsEntities(2.0F, 40);
    }

    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replacedBlock, FallingUpwardBlockEntity entity) {
        if (!entity.isSilent()) {
            level.levelEvent(1031, pos, 0);
        }

    }

    public void onBrokenAfterRise(Level level, BlockPos pos, FallingUpwardBlockEntity entity) {
        if (!entity.isSilent()) {
            level.levelEvent(1029, pos, 0);
        }
    }

    public DamageSource getRiseDamageSource(Entity entity) {
        return entity.damageSources().anvil(entity);
    }

    public static @Nullable BlockState damage(BlockState blockState) {
        if (blockState.is(ModBlocks.LIVNA_BLOCK.get())) {
            return ModBlocks.CHIPPED_LIVNA_BLOCK.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING));
        }

        return blockState.is(ModBlocks.CHIPPED_LIVNA_BLOCK.get())
            ? ModBlocks.DAMAGED_LIVNA_BLOCK.get().defaultBlockState().setValue(FACING, blockState.getValue(FACING))
            : null;
    }

    protected @NonNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    protected boolean isPathfindable(@NonNull BlockState state, @NonNull PathComputationType type) {
        return false;
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter level, BlockPos pos) {
        return 0xFF707070;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        SHAPES = Shapes.rotateHorizontalAxis(
            Shapes.or(
                Block.column(12.0f, 12.0f, 16.0f),
                new VoxelShape[] {
                    Block.column(8.0f, 10.0f, 11.0f, 12.0f),
                    Block.column(4.0f, 8.0f, 6.0f, 11.0f),
                    Block.column(10.0f, 16.0f, 0.0f, 6.0f)
                }
            )
        );
        CONTAINER_TITLE = Component.translatable("container.repair");
    }
}
