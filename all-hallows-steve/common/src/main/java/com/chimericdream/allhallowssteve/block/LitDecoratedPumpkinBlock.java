package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

/**
 * A lit variant of {@link DecoratedPumpkinBlock}, one per torch color (see {@code ModBlocks}'
 * registration calls for the four instances). Shares {@link DecoratedPumpkinBlockEntity} with the
 * unlit block — block identity alone carries the light level, the carved-stencil overlay suffix
 * (see {@code DecoratedPumpkinStencilRenderer}), and which torch item this variant is crafted from /
 * drops — so no extra state needs to live on the block entity.
 * <p>
 * Crafted from a plain {@link DecoratedPumpkinBlock} plus this variant's torch item via a
 * {@code TransmuteRecipe} (see {@code LitDecoratedPumpkinBlockDataGenerator}), which carries the input
 * pumpkin's own {@link DyedColorComponent}/{@link PumpkinStencilsComponent} onto the result. Right-
 * clicking with shears reverses that: this block turns back into a plain {@code DecoratedPumpkinBlock}
 * (preserving color/stencils) and drops the torch, mirroring vanilla {@code PumpkinBlock}'s own
 * shears-carve interaction.
 */
public class LitDecoratedPumpkinBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public final Identifier BLOCK_ID;
    public final String overlaySuffix;
    public final String displayName;
    public final Item torchItem;

    private final MapCodec<LitDecoratedPumpkinBlock> codec;

    public LitDecoratedPumpkinBlock(Identifier blockId, int lightLevel, String overlaySuffix, String displayName, Item torchItem) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.PUMPKIN)
            .setId(REGISTRY_HELPER.makeBlockRegistryKey(blockId))
            .lightLevel(state -> lightLevel)
            // Without this, a solid opaque cube's own light-map cell doesn't pick up skylight (light
            // only propagates into it via the block-emission clamp in LightCoordsUtil.getLightCoords),
            // so the lower-light variants (soul/redstone torch) render visibly dimmer than the
            // higher-light ones (torch/copper torch) even outdoors in daylight. Always-full-bright
            // rendering (same mechanism vanilla uses for e.g. an active sculk sensor) keeps all four
            // variants' carved faces uniformly vivid, matching the item form.
            .emissiveRendering(state -> true));

        this.BLOCK_ID = blockId;
        this.overlaySuffix = overlaySuffix;
        this.displayName = displayName;
        this.torchItem = torchItem;
        this.codec = simpleCodec(properties -> new LitDecoratedPumpkinBlock(blockId, lightLevel, overlaySuffix, displayName, torchItem));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public @NotNull MapCodec<LitDecoratedPumpkinBlock> codec() {
        return codec;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecoratedPumpkinBlockEntity(pos, state);
    }

    /** Un-lights this pumpkin with shears: reverts to a plain {@code DecoratedPumpkinBlock} and drops {@link #torchItem}. */
    @Override
    protected @NotNull InteractionResult useItemOn(
        @NonNull ItemStack itemStack,
        @NonNull BlockState state,
        Level level,
        @NonNull BlockPos pos,
        @NonNull Player player,
        @NonNull InteractionHand hand,
        @NonNull BlockHitResult hitResult
    ) {
        if (!itemStack.is(Items.SHEARS)) {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
        }

        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }

        int color = DyedColorComponent.DEFAULT_COLOR;
        PumpkinStencilsComponent stencils = PumpkinStencilsComponent.EMPTY;
        if (level.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity decorated) {
            color = decorated.getColor();
            stencils = decorated.getStencils();
        }

        level.setBlock(pos, ModBlocks.DECORATED_PUMPKIN.get().defaultBlockState().setValue(DecoratedPumpkinBlock.FACING, state.getValue(FACING)), Block.UPDATE_ALL);

        if (level.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity relit) {
            relit.setColor(color);
            relit.setStencils(stencils);
        }

        Block.popResource(level, pos, new ItemStack(torchItem));

        itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
        level.gameEvent(player, GameEvent.SHEAR, pos);
        level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
        player.awardStat(Stats.ITEM_USED.get(Items.SHEARS));

        return InteractionResult.SUCCESS;
    }
}
