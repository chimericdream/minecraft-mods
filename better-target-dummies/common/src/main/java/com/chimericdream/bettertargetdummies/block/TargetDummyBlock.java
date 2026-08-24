package com.chimericdream.bettertargetdummies.block;

import com.chimericdream.bettertargetdummies.ModInfo;
import com.chimericdream.bettertargetdummies.block.entity.TargetDummyBlockEntity;
import com.chimericdream.bettertargetdummies.item.DummySpawnEggItem;
import com.chimericdream.bettertargetdummies.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.chimericdream.bettertargetdummies.BetterTargetDummiesMod.REGISTRY_HELPER;

public class TargetDummyBlock extends BaseEntityBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "target_dummy");
    public static final MapCodec<TargetDummyBlock> CODEC = simpleCodec(TargetDummyBlock::create);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    static TargetDummyBlock create(BlockBehaviour.Properties settings) {
        return new TargetDummyBlock();
    }

    public TargetDummyBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.TARGET).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull MapCodec<TargetDummyBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
        return this.defaultBlockState()
            .setValue(POWERED, powered)
            .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TargetDummyBlockEntity(pos, state);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean isDummyEgg = stack.is(ModItems.DUMMY_SPAWN_EGG.get());
        EntityType<?> type = isDummyEgg ? null : SpawnEggItem.getType(stack);

        if (!isDummyEgg && type == null) {
            // Not a spawn egg (including an empty hand) -- fall through to useWithoutItem. Returning
            // plain PASS here would NOT do that: ServerPlayerGameMode only retries useWithoutItem when
            // useItemOn's result is specifically TRY_WITH_EMPTY_HAND (see BlockBehaviour's own
            // unoverridden default), so PASS silently drops the interaction instead.
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (isDummyEgg) {
            type = DummySpawnEggItem.resolveEntityType(stack).orElse(null);
            if (type == null) {
                if (!world.isClientSide()) {
                    player.sendOverlayMessage(Component.translatable(ModInfo.MOD_ID + ".dummy_spawn_egg.unresolved"));
                }
                return InteractionResult.FAIL;
            }
        }

        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(world.getBlockEntity(pos) instanceof TargetDummyBlockEntity dummy)) {
            return InteractionResult.FAIL;
        }

        // The dummy egg carries no vanilla ENTITY_DATA component to apply to the spawned mob, and
        // (unlike a real spawn egg) is never consumed — it stays in hand to be renamed and reused.
        @Nullable ItemStack componentSource = isDummyEgg ? null : stack;
        boolean bound = dummy.bindMob((ServerLevel) world, type, componentSource, player);
        if (bound) {
            if (!isDummyEgg) {
                stack.consume(1, player);
            }
            player.sendOverlayMessage(dummy.describeBoundMob());
        }

        return bound ? InteractionResult.CONSUME : InteractionResult.FAIL;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(world.getBlockEntity(pos) instanceof TargetDummyBlockEntity dummy)) {
            return InteractionResult.PASS;
        }

        if (player.isCrouching()) {
            dummy.clearBoundEntity((ServerLevel) world);
            player.sendOverlayMessage(Component.translatable(ModInfo.MOD_ID + ".target_dummy.cleared"));
        } else {
            dummy.rotateBoundMob((ServerLevel) world);
            player.sendOverlayMessage(dummy.describeBoundMob());
        }

        return InteractionResult.SUCCESS;
    }

    // The dummy only exists while powered: a redstone signal is the one reliable way to make the
    // bound mob appear/disappear on demand (breaking the block or /kill both work too, but this is
    // the intended day-to-day toggle).
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, @Nullable Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, orientation, movedByPiston);

        if (level.isClientSide()) {
            return;
        }

        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED)) {
            return;
        }

        level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_ALL);

        if (!(level instanceof ServerLevel serverLevel) || !(level.getBlockEntity(pos) instanceof TargetDummyBlockEntity dummy)) {
            return;
        }

        if (powered) {
            dummy.onPowered(serverLevel);
        } else {
            dummy.onUnpowered(serverLevel);
        }
    }
}
