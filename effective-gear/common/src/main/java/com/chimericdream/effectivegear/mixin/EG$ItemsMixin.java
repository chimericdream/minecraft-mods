package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.item.armor.Trims;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Every {@code Items} registration eventually funnels through the private
 * {@code registerItem(ResourceKey, Function, Properties)} overload, so this is the one chokepoint that
 * can attach a {@code trimMaterial} component to vanilla items that don't have their own dedicated
 * {@link net.minecraft.world.item.Item} subclass to mixin into directly (unlike {@code EnderpearlItem}).
 *
 * <p>{@code net.minecraft.references.ItemIds} only exposes {@code PUMPKIN_SEEDS}/{@code MELON_SEEDS}
 * on this version (the rest of these constants are a later addition), so the keys below are built
 * directly from each vanilla item's registry path instead of reusing that class.
 */
@Mixin(Items.class)
public class EG$ItemsMixin {
    @Unique
    private static ResourceKey<Item> eg$vanillaKey(String path) {
        return ResourceKey.create(Registries.ITEM, Identifier.withDefaultNamespace(path));
    }

    @Unique
    private static final ResourceKey<Item> BLAZE_POWDER = eg$vanillaKey("blaze_powder");
    @Unique
    private static final ResourceKey<Item> ECHO_SHARD = eg$vanillaKey("echo_shard");
    @Unique
    private static final ResourceKey<Item> ENCHANTED_GOLDEN_APPLE = eg$vanillaKey("enchanted_golden_apple");
    @Unique
    private static final ResourceKey<Item> HONEYCOMB = eg$vanillaKey("honeycomb");
    @Unique
    private static final ResourceKey<Item> NETHER_STAR = eg$vanillaKey("nether_star");
    @Unique
    private static final ResourceKey<Item> PRISMARINE_SHARD = eg$vanillaKey("prismarine_shard");
    @Unique
    private static final ResourceKey<Item> SLIME_BALL = eg$vanillaKey("slime_ball");
    @Unique
    private static final ResourceKey<Item> TURTLE_SCUTE = eg$vanillaKey("turtle_scute");

    @ModifyVariable(
        method = "registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
        at = @At("HEAD"),
        argsOnly = true,
        name = "properties"
    )
    private static Item.Properties eg$addTrimMaterialComponent(Item.Properties properties, ResourceKey<Item> id, Function<Item.Properties, Item> itemFactory) {
        if (id.equals(BLAZE_POWDER)) {
            return properties.trimMaterial(Trims.BLAZE_POWDER_TRIM_ID);
        }

        if (id.equals(ECHO_SHARD)) {
            return properties.trimMaterial(Trims.ECHO_SHARD_TRIM_ID);
        }

        if (id.equals(ENCHANTED_GOLDEN_APPLE)) {
            return properties.trimMaterial(Trims.ENCHANTED_GOLDEN_APPLE_TRIM_ID);
        }

        if (id.equals(HONEYCOMB)) {
            return properties.trimMaterial(Trims.HONEYCOMB_TRIM_ID);
        }

        if (id.equals(NETHER_STAR)) {
            return properties.trimMaterial(Trims.NETHER_STAR_TRIM_ID);
        }

        if (id.equals(PRISMARINE_SHARD)) {
            return properties.trimMaterial(Trims.PRISMARINE_SHARD_TRIM_ID);
        }

        if (id.equals(SLIME_BALL)) {
            return properties.trimMaterial(Trims.SLIMEBALL_TRIM_ID);
        }

        if (id.equals(TURTLE_SCUTE)) {
            return properties.trimMaterial(Trims.TURTLE_SCUTE_TRIM_ID);
        }

        return properties;
    }
}
