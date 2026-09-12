package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.chimericdream.allhallowssteve.stats.ModStats;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

/**
 * A pumpkin dyed by the {@link CarvingStationBlock}. A separate block from vanilla's pumpkin rather
 * than a retexture of it, so the stored {@link com.chimericdream.allhallowssteve.component.type.DyedColorComponent}
 * has somewhere of its own to live and render from (see {@link DecoratedPumpkinBlockEntity}) without
 * touching vanilla pumpkin's stem-growth behavior.
 * <p>
 * {@code FACING} points at the placer, matching {@link CarvingStationBlock}'s convention — the same
 * side a future stencil overlay is meant to face.
 */
public class DecoratedPumpkinBlock extends BaseEntityBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "decorated_pumpkin");
    public static final MapCodec<DecoratedPumpkinBlock> CODEC = simpleCodec(DecoratedPumpkinBlock::create);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    static DecoratedPumpkinBlock create(BlockBehaviour.Properties settings) {
        return new DecoratedPumpkinBlock() {
        };
    }

    public DecoratedPumpkinBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.PUMPKIN).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public @NotNull MapCodec<DecoratedPumpkinBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    /**
     * Converts this pumpkin into the lit variant matching {@code itemStack}'s torch type, carrying
     * over the stored dye color/stencils. Called from {@code AHS$TorchBlockItemMixin} rather than a
     * {@code useItemOn} override on this block: {@code ServerPlayerGameMode#useItemOn} skips
     * {@code BlockState#useItemOn} entirely whenever the player is sneaking with a nonempty hand (its
     * {@code suppressUsingBlock} check), going straight to the held item's own placement logic
     * instead — and since lighting is deliberately shift-gated, this block's own {@code useItemOn}
     * would never actually be reached for this interaction.
     */
    public static InteractionResult tryLight(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        RegistrySupplier<Block> litVariant;
        if (itemStack.is(Items.TORCH)) {
            litVariant = ModBlocks.LIT_DECORATED_PUMPKIN;
        } else if (itemStack.is(Items.SOUL_TORCH)) {
            litVariant = ModBlocks.LIT_DECORATED_PUMPKIN_BLUE;
        } else if (itemStack.is(Items.COPPER_TORCH)) {
            litVariant = ModBlocks.LIT_DECORATED_PUMPKIN_GREEN;
        } else if (itemStack.is(Items.REDSTONE_TORCH)) {
            litVariant = ModBlocks.LIT_DECORATED_PUMPKIN_RED;
        } else {
            return InteractionResult.PASS;
        }

        int color = DyedColorComponent.DEFAULT_COLOR;
        PumpkinStencilsComponent stencils = PumpkinStencilsComponent.EMPTY;
        if (serverLevel.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity decorated) {
            color = decorated.getColor();
            stencils = decorated.getStencils();
        }

        serverLevel.setBlock(pos, litVariant.get().defaultBlockState().setValue(FACING, state.getValue(FACING)), Block.UPDATE_ALL);

        if (serverLevel.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity lit) {
            lit.setColor(color);
            lit.setStencils(stencils);
        }

        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        serverLevel.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        serverLevel.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        player.awardStat(ModStats.LIGHT_DECORATED_PUMPKIN);

        return InteractionResult.SUCCESS;
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
}
