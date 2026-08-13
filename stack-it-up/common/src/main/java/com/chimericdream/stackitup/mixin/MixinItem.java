package com.chimericdream.stackitup.mixin;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.chimericdream.stackitup.util.IItemMaxCount;
import com.chimericdream.stackitup.util.ItemsHelper;

// As of 26.1.2, Item no longer holds its DataComponentMap in a local field at all - components()
// delegates to builtInRegistryHolder().components(), and that Holder.Reference isn't bound
// (via bindComponents(...)) until a later registry-bake phase, well after Item's own constructor
// returns. So the old approach (capture the vanilla max count once, right after <init>) no longer
// works - components aren't populated yet at that point, and reading them would NPE
// ("Components not bound yet"). Capture lazily instead, on first actual use, which is always well
// after registry bootstrap completes (this mod only starts touching item counts during server
// lifecycle events).
@Mixin(Item.class)
public abstract class MixinItem implements IItemMaxCount {
    @Unique
    private boolean vanillaMaxCountCaptured = false;

    @Unique
    private int vanillaMaxCount;

    @Unique
    private void ensureVanillaMaxCountCaptured() {
        if (!vanillaMaxCountCaptured) {
            vanillaMaxCount = ((Item) (Object) this).components().getOrDefault(DataComponents.MAX_STACK_SIZE, ItemsHelper.ItemMaxCount);
            vanillaMaxCountCaptured = true;
        }
    }

    @Override
    public void revert() {
        setMaxCount(getVanillaMaxCount());
    }

    @Override
    public void setMaxCount(int i) {
        ensureVanillaMaxCountCaptured();
        Item self = (Item) (Object) this;
        DataComponentMap.Builder builder = DataComponentMap.builder().addAll(self.components());
        builder.set(DataComponents.MAX_STACK_SIZE, i);
        self.builtInRegistryHolder().bindComponents(builder.build());
    }

    @Override
    public int getVanillaMaxCount() {
        ensureVanillaMaxCountCaptured();
        return vanillaMaxCount;
    }

    @Override
    public void setVanillaMaxCount(int vanillaMaxCount) {
        this.vanillaMaxCount = vanillaMaxCount;
        this.vanillaMaxCountCaptured = true;
    }

    @Inject(method = "getDefaultMaxStackSize", at = @At("HEAD"), cancellable = true)
    private void injectGetMaxCount(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(((Item) (Object) this).components().getOrDefault(DataComponents.MAX_STACK_SIZE, ItemsHelper.ItemMaxCount));
    }

    // ItemStack.isEnchantable() (moved off Item as of 1.21.11) no longer derives enchantability
    // from stack/max count at all - it is purely component-based now - so there is nothing left
    // to redirect here.
}
