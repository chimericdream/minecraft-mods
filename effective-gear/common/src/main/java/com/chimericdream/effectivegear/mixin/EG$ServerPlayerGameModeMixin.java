package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.block.EGBlocks;
import com.chimericdream.effectivegear.item.armor.TrimSetUtils;
import com.chimericdream.effectivegear.util.RedstoneTrimPulses;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A full redstone trim armor set lets an otherwise-inert empty-handed click send a momentary
 * redstone pulse, exactly like pressing a button would, but only where a button could actually be
 * placed. Rather than invent a synthetic power source, this places {@link EGBlocks#REDSTONE_TRIM_PULSE_BUTTON}
 * (reusing button placement-validity and press logic, but with no model or hitbox so nothing visibly
 * flashes into existence), then has {@link RedstoneTrimPulses} remove it a tick later instead of
 * leaving it sitting there or waiting out its normal press duration. The result is {@code
 * SUCCESS_SERVER} rather than {@code SUCCESS} because there's no real client-predicted interaction
 * to have already swung the arm locally — {@code SwingSource.SERVER} is what tells
 * {@code ServerGamePacketListenerImpl} to swing it for us and broadcast that swing to everyone
 * tracking the player, same as a real button press does.
 */
@Mixin(ServerPlayerGameMode.class)
public class EG$ServerPlayerGameModeMixin {
    @Inject(method = "useItemOn", at = @At("RETURN"), cancellable = true)
    private void eg$redstoneTrimPulse(
        ServerPlayer player,
        Level level,
        ItemStack itemStack,
        InteractionHand hand,
        BlockHitResult hitResult,
        CallbackInfoReturnable<InteractionResult> cir
    ) {
        if (!itemStack.isEmpty() || cir.getReturnValue().consumesAction()) {
            return;
        }

        if (!TrimSetUtils.isWearingFullTrim(player, TrimMaterials.REDSTONE)) {
            return;
        }

        BlockPlaceContext context = new BlockPlaceContext(player, hand, ItemStack.EMPTY, hitResult);
        if (!context.canPlace()) {
            return;
        }

        BlockState buttonState = EGBlocks.REDSTONE_TRIM_PULSE_BUTTON.get().getStateForPlacement(context);
        if (buttonState == null) {
            return;
        }

        if (EGBlocks.REDSTONE_TRIM_PULSE_BUTTON.get() instanceof ButtonBlock buttonBlock && level instanceof ServerLevel serverLevel) {
            BlockPos placementPos = context.getClickedPos();
            buttonBlock.press(buttonState, level, placementPos, player);
            RedstoneTrimPulses.schedule(serverLevel, placementPos);
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
        }
    }
}
