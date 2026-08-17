package com.chimericdream.bannertweaks.mixin;

import com.chimericdream.bannertweaks.config.BannerTweaksConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackBannerTooltipMixin {
    @Inject(method = "addToTooltip", at = @At("TAIL"))
    private <T extends TooltipProvider> void bannertweaks$addLayerCountTooltip(
        DataComponentType<T> type,
        TooltipContext context,
        TooltipDisplay display,
        Consumer<Component> consumer,
        TooltipFlag flag,
        CallbackInfo ci
    ) {
        if (type != DataComponents.BANNER_PATTERNS) {
            return;
        }

        ItemStack self = (ItemStack) (Object) this;
        if (!(self.getItem() instanceof BannerItem)) {
            return;
        }

        BannerPatternLayers patterns = self.get(DataComponents.BANNER_PATTERNS);
        if (patterns == null) {
            return;
        }

        int max = BannerTweaksConfig.HANDLER.instance().maxBannerLayers;
        consumer.accept(Component.literal(patterns.layers().size() + "/" + max + " layers").withStyle(ChatFormatting.GRAY));
    }
}
