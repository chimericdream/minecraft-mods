package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.Trims;
import java.util.function.Function;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Every {@code Items} registration eventually funnels through the private
 * {@code registerItem(ResourceKey, Function, Properties)} overload, so this is the one chokepoint that
 * can attach a {@code trimMaterial} component to vanilla items that don't have their own dedicated
 * {@link net.minecraft.world.item.Item} subclass to mixin into directly (unlike {@code EnderpearlItem}).
 */
@Mixin(Items.class)
public class EG$ItemsMixin {
    @ModifyVariable(
        method = "registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
        at = @At("HEAD"),
        argsOnly = true,
        name = "properties"
    )
    private static Item.Properties eg$addTrimMaterialComponent(Item.Properties properties, ResourceKey<Item> id, Function<Item.Properties, Item> itemFactory) {
        if (id.equals(ItemIds.BLAZE_POWDER)) {
            return properties.trimMaterial(Trims.BLAZE_POWDER_TRIM_ID);
        }

        if (id.equals(ItemIds.ECHO_SHARD)) {
            return properties.trimMaterial(Trims.ECHO_SHARD_TRIM_ID);
        }

        if (id.equals(ItemIds.ENCHANTED_GOLDEN_APPLE)) {
            return properties.trimMaterial(Trims.ENCHANTED_GOLDEN_APPLE_TRIM_ID);
        }

        if (id.equals(ItemIds.HONEYCOMB)) {
            return properties.trimMaterial(Trims.HONEYCOMB_TRIM_ID);
        }

        if (id.equals(ItemIds.NETHER_STAR)) {
            return properties.trimMaterial(Trims.NETHER_STAR_TRIM_ID);
        }

        if (id.equals(ItemIds.SLIME_BALL)) {
            return properties.trimMaterial(Trims.SLIMEBALL_TRIM_ID);
        }

        if (id.equals(ItemIds.TURTLE_SCUTE)) {
            return properties.trimMaterial(Trims.TURTLE_SCUTE_TRIM_ID);
        }

        return properties;
    }
}
