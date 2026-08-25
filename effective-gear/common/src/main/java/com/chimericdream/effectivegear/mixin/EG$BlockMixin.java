package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.enchantment.PreservingHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * When a leaves block preservable by the Preserving enchantment is mined with a tool carrying it, tags
 * the dropped leaf item(s) with the vanilla {@code BLOCK_STATE} component so the block keeps its
 * default color no matter where it's placed next (see {@link EG$LeavesBlockMixin}). This overload of
 * {@code getDrops} is only reached from {@code Block#playerDestroy}, so explosions and other block
 * removal paths are unaffected.
 */
@Mixin(Block.class)
public abstract class EG$BlockMixin {
    @Inject(
        method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;)Ljava/util/List;",
        at = @At("RETURN")
    )
    private static void eg$tagPreservedLeafDrops(
        BlockState state,
        ServerLevel level,
        BlockPos pos,
        BlockEntity blockEntity,
        Entity breaker,
        ItemInstance tool,
        CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        Block block = state.getBlock();
        if (!PreservingHelper.isPreservable(block) || PreservingHelper.getLevel(level, tool) <= 0) {
            return;
        }

        Item leafItem = block.asItem();
        for (ItemStack stack : cir.getReturnValue()) {
            if (stack.is(leafItem)) {
                stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(PreservingHelper.PRESERVED, true));
            }
        }
    }
}
