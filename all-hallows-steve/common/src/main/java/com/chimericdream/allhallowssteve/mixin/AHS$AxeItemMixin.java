package com.chimericdream.allhallowssteve.mixin;

import com.chimericdream.allhallowssteve.spice.Spices;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Stripping a cherry, mangrove, or jungle log can drop cinnamon (see {@link Spices#onStrip}).
 * <p>
 * Injected just before {@code useOn}'s {@code setBlock} so the clicked block is still the unstripped
 * log. Scraping copper and removing wax also reach that call, but those blocks aren't in the cinnamon
 * tag, so they're ignored.
 */
@Mixin(AxeItem.class)
abstract public class AHS$AxeItemMixin {
    @Inject(
        method = "useOn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
        )
    )
    private void ahs$dropCinnamon(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (context.getLevel() instanceof ServerLevel level) {
            Spices.onStrip(level, context.getClickedPos(), level.getBlockState(context.getClickedPos()));
        }
    }
}
