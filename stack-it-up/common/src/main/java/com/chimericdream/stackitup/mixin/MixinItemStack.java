package com.chimericdream.stackitup.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;

import com.chimericdream.stackitup.config.ConfigManager;
import com.chimericdream.stackitup.util.ItemsHelper;

@Mixin(ItemStack.class)
public class MixinItemStack {

    // As of 26.1.2, ItemStack no longer physically declares getMaxStackSize() - it's a pure
    // inherited default method from the new ItemInstance interface (this.getOrDefault(DataComponents.MAX_STACK_SIZE, 1)),
    // with no method body on ItemStack itself for Mixin's @Inject to patch. So this has to be a
    // genuine override (merged into ItemStack directly) rather than an injection, falling back to
    // the same default-interface-method logic in the normal case.
    //
    // The "split stacked tools on damage" behavior (formerly also in this class) is platform-specific
    // as of NeoForge 26.2.0.15-beta: NeoForge splits ItemStack.applyDamage(int, ServerPlayer, Consumer)
    // into a thin delegating overload plus a new applyDamage(int, LivingEntity, Consumer) that actually
    // calls setDamageValue, while Fabric/vanilla keeps the single ServerPlayer-descriptor method. See
    // fabric/.../fabric/mixin/MixinItemStackDamage.java and neoforge/.../neoforge/mixin/MixinItemStackDamage.java.
    public int getMaxStackSize() {
        if (ConfigManager.getConfigManager().getRuleSetting("stackEmptyShulkerBoxOnly") == 1) {
            ItemStack self = (ItemStack) (Object) this;
            if ((self.getItem() instanceof BlockItem) && (((BlockItem) self.getItem()).getBlock() instanceof ShulkerBoxBlock)) {
                if (ItemsHelper.shulkerBoxHasItems(self)) {
                    return 1;
                }
            }
        }
        return ((ItemStack) (Object) this).getOrDefault(DataComponents.MAX_STACK_SIZE, 1);
    }
}
