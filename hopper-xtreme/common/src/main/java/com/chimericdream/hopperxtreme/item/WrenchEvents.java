package com.chimericdream.hopperxtreme.item;

import com.chimericdream.hopperxtreme.block.HopperVariantBlock;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Routes the Hopper Wrench's Copy/Paste clicks on hoppers. This is an event rather than a block override so one
 * hook covers every hopper family, and so it runs before the hoppers' own click handling (which would otherwise
 * open the hopper screen, or toggle a multi-hopper arm).
 */
public final class WrenchEvents {
    private WrenchEvents() {
    }

    public static void init() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register(WrenchEvents::onRightClickBlock);
    }

    private static EventResult onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        ItemStack stack = player.getItemInHand(hand);

        if (!(stack.getItem() instanceof WrenchItem) || WrenchItem.getMode(stack) != WrenchItem.Mode.COPY_PASTE) {
            return EventResult.pass();
        }

        Level level = player.level();

        if (!player.mayBuild() || !(level.getBlockState(pos).getBlock() instanceof HopperVariantBlock)) {
            return EventResult.pass();
        }

        // The client only swallows the click; the server decides what happens and tells the player.
        if (level.isClientSide()) {
            return EventResult.interruptTrue();
        }

        FilterCopying.Result result = FilterCopying.copyOrPaste(stack, level, pos, player);

        if (result == FilterCopying.Result.NOT_APPLICABLE) {
            return EventResult.pass();
        }

        player.sendOverlayMessage(Component.translatable(result.messageKey()));

        if (result.isSuccess()) {
            level.playSound(null, pos, SoundEvents.SPYGLASS_USE, SoundSource.AMBIENT, 2.0F, 1.5F);
        }

        return EventResult.interruptTrue();
    }
}
