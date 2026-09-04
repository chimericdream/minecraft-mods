package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.enchantment.PreservingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * Surfaces the Preserving enchantment's effect on a mined leaf item before it's placed, reusing the
 * same {@code BLOCK_STATE} tooltip hook vanilla uses for the beehive honey-level tooltip.
 */
@Mixin(BlockItemStateProperties.class)
public abstract class EG$BlockItemStatePropertiesMixin {
    @Shadow
    public abstract <T extends Comparable<T>> T get(Property<T> property);

    @Inject(method = "addToTooltip", at = @At("TAIL"))
    private void eg$addPreservingTooltip(
        TooltipContext context,
        Consumer<Component> consumer,
        TooltipFlag flag,
        DataComponentGetter components,
        CallbackInfo ci
    ) {
        if (Boolean.TRUE.equals(this.get(PreservingHelper.PRESERVED))) {
            consumer.accept(Component.translatable("item.effectivegear.preserving.tooltip").withStyle(ChatFormatting.GRAY));
        }
    }
}
