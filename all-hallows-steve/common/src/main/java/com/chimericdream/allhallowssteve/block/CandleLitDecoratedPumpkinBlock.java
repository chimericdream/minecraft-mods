package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.chimericdream.allhallowssteve.stats.ModStats;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

/**
 * A decorated pumpkin holding one to four candles instead of a torch (see {@link LitDecoratedPumpkinBlock}
 * for the torch variants). One block covers every candle count and both the lit and snuffed states:
 * {@link #CANDLES} is the count, {@link #LIT} is whether the flame is burning, and the candles
 * themselves (so each one keeps its dye color) live on the shared {@link DecoratedPumpkinBlockEntity}.
 * Candle color never affects the glow.
 * <p>
 * Light level is vanilla's candle scale, {@code 3 * candles} (3 / 6 / 9 / 12), and only while lit. A
 * snuffed pumpkin renders with the plain unlit overlay, so the candles are invisible; the player
 * learns what is inside from {@link PumpkinContents}. Carved overlays for the lit states are named
 * {@code <stencil>_candlelit_<count>} (see {@link #overlaySuffix}).
 * <p>
 * Interactions:
 * <ul>
 *     <li>Shift + a candle adds one (to an unlit pumpkin or to one that already holds candles, lit or
 *     snuffed). Driven from {@code AHS$CandleBlockItemMixin} for the same reason torches are: a
 *     sneaking player with a nonempty hand never reaches {@link #useItemOn}. A hollow pumpkin lights
 *     with its first candle; adding to a snuffed pumpkin leaves it snuffed.</li>
 *     <li>An empty hand snuffs a lit pumpkin (see {@link #useWithoutItem}); sneaking with an empty hand
 *     only shows what the pumpkin holds (see {@link PumpkinContents}).</li>
 *     <li>Flint and steel relights a snuffed one.</li>
 *     <li>Shears take every candle back out and turn it back into a plain decorated pumpkin,
 *     preserving dye color and stencils.</li>
 * </ul>
 * Breaking the block drops a plain decorated pumpkin (carrying color and stencils, via the loot table)
 * plus every candle in the color it went in with ({@link #getDrops}). This block has no item of its own.
 */
public class CandleLitDecoratedPumpkinBlock extends BaseEntityBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "decorated_pumpkin_candlelit");
    public static final MapCodec<CandleLitDecoratedPumpkinBlock> CODEC = simpleCodec(properties -> new CandleLitDecoratedPumpkinBlock());

    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public static final int MAX_CANDLES = 4;
    public static final int LIGHT_PER_CANDLE = 3;

    /** Every vanilla candle item. Dispenser behaviors are registered before item tags load, so they can't use {@link ItemTags#CANDLES}. */
    public static final List<Item> CANDLE_ITEMS = List.of(
        Items.CANDLE,
        Items.DYED_CANDLE.white(),
        Items.DYED_CANDLE.orange(),
        Items.DYED_CANDLE.magenta(),
        Items.DYED_CANDLE.lightBlue(),
        Items.DYED_CANDLE.yellow(),
        Items.DYED_CANDLE.lime(),
        Items.DYED_CANDLE.pink(),
        Items.DYED_CANDLE.gray(),
        Items.DYED_CANDLE.lightGray(),
        Items.DYED_CANDLE.cyan(),
        Items.DYED_CANDLE.purple(),
        Items.DYED_CANDLE.blue(),
        Items.DYED_CANDLE.brown(),
        Items.DYED_CANDLE.green(),
        Items.DYED_CANDLE.red(),
        Items.DYED_CANDLE.black()
    );

    public CandleLitDecoratedPumpkinBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.PUMPKIN)
            .setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID))
            .lightLevel(state -> state.getValue(LIT) ? state.getValue(CANDLES) * LIGHT_PER_CANDLE : 0)
            // See LitDecoratedPumpkinBlock: keeps the carved faces fully vivid instead of dim in daylight.
            .emissiveRendering(state -> state.getValue(LIT)));

        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(CANDLES, 1)
            .setValue(LIT, true));
    }

    public @NotNull MapCodec<CandleLitDecoratedPumpkinBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CANDLES, LIT);
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

    /** The carved-overlay texture suffix for {@code state}: the lit overlay for its candle count, or {@code ""} (the plain unlit overlay) when snuffed. */
    public static String overlaySuffix(BlockState state) {
        return state.getValue(LIT) ? "_candlelit_" + state.getValue(CANDLES) : "";
    }

    /** Whether {@code stack} is any candle. */
    public static boolean isCandle(ItemStack stack) {
        return stack.is(ItemTags.CANDLES);
    }

    /** Whether a candle could be added to the pumpkin at {@code state}: an unlit decorated pumpkin, or a candle pumpkin with room. */
    public static boolean canAddCandle(BlockState state) {
        if (state.getBlock() instanceof DecoratedPumpkinBlock) {
            return true;
        }

        return state.getBlock() instanceof CandleLitDecoratedPumpkinBlock && state.getValue(CANDLES) < MAX_CANDLES;
    }

    /**
     * Player entry point for adding a candle, called from {@code AHS$CandleBlockItemMixin}. Returns
     * {@link InteractionResult#PASS} when this isn't something a candle can go into, so the candle falls
     * through to vanilla placement.
     */
    public static InteractionResult tryAddCandle(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player) {
        if (!canAddCandle(state)) {
            return InteractionResult.PASS;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        boolean wasHollow = state.getBlock() instanceof DecoratedPumpkinBlock;

        addCandle(serverLevel, pos, state, itemStack.getItem(), player);

        if (!player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        PumpkinContents.show(player, level, pos);

        if (wasHollow) {
            player.awardStat(ModStats.LIGHT_DECORATED_PUMPKIN);
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Adds {@code candle} to the pumpkin at {@code pos}, which must satisfy {@link #canAddCandle}. A
     * hollow decorated pumpkin becomes a lit candle pumpkin (keeping facing, dye color and stencils); a
     * candle pumpkin gains one candle and keeps its lit/snuffed state. Consuming the item is left to the
     * caller. Shared by the player path and the dispenser path (see {@code ModDispenserBehaviors}).
     */
    public static void addCandle(ServerLevel level, BlockPos pos, BlockState state, Item candle, @Nullable Entity cause) {
        if (state.getBlock() instanceof DecoratedPumpkinBlock) {
            int color = DyedColorComponent.DEFAULT_COLOR;
            PumpkinStencilsComponent stencils = PumpkinStencilsComponent.EMPTY;
            if (level.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity decorated) {
                color = decorated.getColor();
                stencils = decorated.getStencils();
            }

            level.setBlock(pos, ModBlocks.CANDLE_LIT_DECORATED_PUMPKIN.get().defaultBlockState()
                .setValue(FACING, state.getValue(DecoratedPumpkinBlock.FACING))
                .setValue(CANDLES, 1)
                .setValue(LIT, true), Block.UPDATE_ALL);

            if (level.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity placed) {
                placed.setColor(color);
                placed.setStencils(stencils);
                placed.setCandles(List.of(candle));
            }
        } else if (level.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity pumpkin) {
            List<Item> candles = new ArrayList<>(pumpkin.getCandles());
            candles.add(candle);
            pumpkin.setCandles(candles);

            level.setBlock(pos, state.setValue(CANDLES, Math.min(candles.size(), MAX_CANDLES)), Block.UPDATE_ALL);
        }

        level.gameEvent(cause, GameEvent.BLOCK_CHANGE, pos);
        level.playSound(null, pos, SoundEvents.CANDLE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /** Snuffs the flame, keeping the candles. */
    public static void snuff(ServerLevel level, BlockPos pos, BlockState state, @Nullable Entity cause) {
        level.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);

        level.gameEvent(cause, GameEvent.BLOCK_CHANGE, pos);
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /** Relights a snuffed pumpkin. Damaging the flint and steel is left to the caller. */
    public static void relight(ServerLevel level, BlockPos pos, BlockState state, @Nullable Entity cause) {
        level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL);

        level.gameEvent(cause, GameEvent.BLOCK_CHANGE, pos);
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /**
     * Reverts the pumpkin at {@code pos} to a plain {@code DecoratedPumpkinBlock} (keeping its facing,
     * dye color and stencils) and drops every candle in the color it went in with. Shared by the player
     * shears path ({@link #useItemOn}) and the dispenser path; damaging the shears is left to the caller.
     */
    public static void removeCandles(ServerLevel level, BlockPos pos, BlockState state, @Nullable Entity cause) {
        if (!(state.getBlock() instanceof CandleLitDecoratedPumpkinBlock)) {
            return;
        }

        int color = DyedColorComponent.DEFAULT_COLOR;
        PumpkinStencilsComponent stencils = PumpkinStencilsComponent.EMPTY;
        List<Item> candles = List.of();
        if (level.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity decorated) {
            color = decorated.getColor();
            stencils = decorated.getStencils();
            candles = decorated.getCandles();
        }

        level.setBlock(pos, ModBlocks.DECORATED_PUMPKIN.get().defaultBlockState().setValue(DecoratedPumpkinBlock.FACING, state.getValue(FACING)), Block.UPDATE_ALL);

        if (level.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity plain) {
            plain.setColor(color);
            plain.setStencils(stencils);
        }

        for (Item candle : candles) {
            Block.popResource(level, pos, new ItemStack(candle));
        }

        level.gameEvent(cause, GameEvent.SHEAR, pos);
        level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

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
        if (itemStack.is(Items.SHEARS)) {
            if (level instanceof ServerLevel serverLevel) {
                removeCandles(serverLevel, pos, state, player);
                PumpkinContents.show(player, level, pos);

                itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
            }

            return InteractionResult.SUCCESS;
        }

        if (itemStack.is(Items.FLINT_AND_STEEL) && !state.getValue(LIT)) {
            if (level instanceof ServerLevel serverLevel) {
                relight(serverLevel, pos, state, player);
                PumpkinContents.show(player, level, pos);

                itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
            }

            return InteractionResult.SUCCESS;
        }

        return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
    }

    /** An empty hand snuffs a lit pumpkin; sneaking with an empty hand only shows what it holds (see {@link PumpkinContents}). */
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (PumpkinContents.isInspecting(player)) {
            PumpkinContents.show(player, level, pos);

            return InteractionResult.SUCCESS;
        }

        if (!state.getValue(LIT) || !player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }

        if (level instanceof ServerLevel serverLevel) {
            snuff(serverLevel, pos, state, player);
            PumpkinContents.show(player, level, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(@NonNull BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, params));

        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof DecoratedPumpkinBlockEntity pumpkin) {
            for (Item candle : pumpkin.getCandles()) {
                drops.add(new ItemStack(candle));
            }
        }

        return drops;
    }

    /** This block has no item, so pick-block gives the plain decorated pumpkin (with its color and stencils when data is included). */
    @Override
    protected @NotNull ItemStack getCloneItemStack(@NonNull LevelReader level, @NonNull BlockPos pos, @NonNull BlockState state, boolean includeData) {
        ItemStack stack = new ItemStack(ModBlocks.DECORATED_PUMPKIN.get());

        if (includeData && level.getBlockEntity(pos) instanceof DecoratedPumpkinBlockEntity pumpkin) {
            stack.set(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get(), new DyedColorComponent(pumpkin.getColor()));
            stack.set(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), pumpkin.getStencils());
        }

        return stack;
    }
}
