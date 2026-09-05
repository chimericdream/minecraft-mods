package com.chimericdream.artificialheart.block;

import com.chimericdream.artificialheart.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public class PalePumpkinBlock extends Block {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pale_pumpkin");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public static final ResourceKey<LootTable> CARVE_PALE_PUMPKIN = ResourceKey.create(
        Registries.LOOT_TABLE,
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "carve/pale_pumpkin")
    );

    public PalePumpkinBlock() {
        super(Properties.ofFullCopy(Blocks.PUMPKIN).setId(BLOCK_REGISTRY_KEY));
    }

    protected @NonNull InteractionResult useItemOn(
        final ItemStack itemStack,
        final @NonNull BlockState state,
        final @NonNull Level level,
        final @NonNull BlockPos pos,
        final @NonNull Player player,
        final @NonNull InteractionHand hand,
        final @NonNull BlockHitResult hitResult
    ) {
        if (!itemStack.is(Items.SHEARS)) {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
        }

        if (level instanceof ServerLevel serverLevel) {
            Direction clickedDirection = hitResult.getDirection();
            Direction direction = clickedDirection.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : clickedDirection;

            dropFromBlockInteractLootTable(serverLevel, CARVE_PALE_PUMPKIN, state, level.getBlockEntity(pos), itemStack, player, (ignored, pumpkinSeeds) -> {
                ItemEntity entity = new ItemEntity(level, (double)pos.getX() + (double)0.5F + (double)direction.getStepX() * 0.65, (double)pos.getY() + 0.1, (double)pos.getZ() + (double)0.5F + (double)direction.getStepZ() * 0.65, pumpkinSeeds);
                RandomSource random = level.getRandom();
                entity.setDeltaMovement(0.05 * (double)direction.getStepX() + random.nextDouble() * 0.02, 0.05, 0.05 * (double)direction.getStepZ() + random.nextDouble() * 0.02);
                level.addFreshEntity(entity);
            });

            level.playSound((Entity) null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlock(pos, (BlockState) ModBlocks.PALE_CARVED_PUMPKIN_BLOCK.get().defaultBlockState().setValue(PaleCarvedPumpkinBlock.FACING, direction), 11);
            itemStack.hurtAndBreak(1, player, hand.asEquipmentSlot());
            level.gameEvent(player, GameEvent.SHEAR, pos);
            player.awardStat(Stats.ITEM_USED.get(Items.SHEARS));

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }
}
