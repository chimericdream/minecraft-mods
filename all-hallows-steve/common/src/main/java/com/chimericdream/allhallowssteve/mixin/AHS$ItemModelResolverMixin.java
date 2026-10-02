package com.chimericdream.allhallowssteve.mixin;

import com.chimericdream.allhallowssteve.client.ClientPumpkinFaces;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.WornFaceComponent;
import com.chimericdream.allhallowssteve.wearable.PumpkinFaces;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Turns a decorated pumpkin on a player's head to the face that player chose. The pumpkin's special model
 * renderer only ever sees the item stack, never the entity wearing it, so the face is handed to it on a
 * temporary copy of the stack carrying a {@link WornFaceComponent}; the real item is never touched, so
 * identical pumpkins still stack. Armor stands and every other context draw the stack unchanged.
 */
@Mixin(ItemModelResolver.class)
abstract public class AHS$ItemModelResolverMixin {
    @Inject(method = "updateForLiving", at = @At("HEAD"), cancellable = true)
    private void ahs$turnWornPumpkin(ItemStackRenderState output, ItemStack item, ItemDisplayContext displayContext, LivingEntity entity, CallbackInfo ci) {
        if (displayContext != ItemDisplayContext.HEAD || !(entity instanceof Player player) || !PumpkinFaces.isWearable(item)) {
            return;
        }

        Direction face = ClientPumpkinFaces.get(player.getUUID());
        if (face == PumpkinFaces.DEFAULT) {
            return;
        }

        ItemStack turned = item.copy();
        turned.set(AllHallowsSteveComponentTypes.WORN_FACE_COMPONENT.get(), new WornFaceComponent(face));

        ((ItemModelResolver) (Object) this).updateForTopItem(output, turned, displayContext, entity.level(), entity, entity.getId() + displayContext.ordinal());
        ci.cancel();
    }
}
