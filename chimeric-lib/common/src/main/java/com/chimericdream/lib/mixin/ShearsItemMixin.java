package com.chimericdream.lib.mixin;

import com.chimericdream.lib.tags.CommonBlockTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {
    @Inject(
            method = "createToolProperties()Lnet/minecraft/world/item/component/Tool;",
            at = @At(value = "RETURN"),
            cancellable = true
    )
    private static void addBlocksToRuleList(CallbackInfoReturnable<Tool> cir) {
        Tool original = cir.getReturnValue();

        HolderGetter<Block> lookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        Optional<HolderSet.Named<Block>> blocks = lookup.get(CommonBlockTags.SHEARS_MINEABLE);

        if (blocks.isEmpty()) {
            return;
        }

        List<Tool.Rule> rules = new ArrayList<>(original.rules());
        rules.add(Tool.Rule.overrideSpeed(blocks.get(), 5.0F));

        cir.setReturnValue(new Tool(rules, original.defaultMiningSpeed(), original.damagePerBlock(), true));
    }
}
