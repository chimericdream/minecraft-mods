package com.chimericdream.allhallowssteve.fabric.item;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

/**
 * Cider, berry juices, and pomace from the press, plus the mulled cider ladled out of a cauldron. Every bottled
 * drink shares one bottle texture with a separate liquid overlay that is tinted per item; pomace uses a plain
 * flat item model.
 */
public class CiderItemDataGenerator {
    private static final Material BOTTLE = texture("cider_bottle");
    private static final Material LIQUID = texture("cider_liquid");

    /** Tint colors multiplied over the grayscale-ish liquid overlay. */
    private static final int APPLE_CIDER_TINT = 0xFFFFC364;
    private static final int MULLED_APPLE_CIDER_TINT = 0xFFCE6840;
    private static final int SWEET_BERRY_TINT = 0xFFE63A5A;
    private static final int GLOW_BERRY_TINT = 0xFFFFE24A;

    public static void configureItemModels(ItemModelGenerators itemModelGenerator) {
        generateTintedDrink(itemModelGenerator, ModItems.APPLE_CIDER.get(), APPLE_CIDER_TINT);
        generateTintedDrink(itemModelGenerator, ModItems.MULLED_APPLE_CIDER.get(), MULLED_APPLE_CIDER_TINT);
        generateTintedDrink(itemModelGenerator, ModItems.SWEET_BERRY_JUICE.get(), SWEET_BERRY_TINT);
        generateTintedDrink(itemModelGenerator, ModItems.GLOW_BERRY_JUICE.get(), GLOW_BERRY_TINT);
        itemModelGenerator.generateFlatItem(ModItems.POMACE.get(), ModelTemplates.FLAT_ITEM);
    }

    private static Material texture(String name) {
        return new Material(Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "item/" + name));
    }

    /** The bottle stays untinted; only the liquid layer takes the tint. */
    private static void generateTintedDrink(ItemModelGenerators itemModelGenerator, Item item, int tint) {
        Identifier model = ModelTemplates.TWO_LAYERED_ITEM.create(item, TextureMapping.layered(BOTTLE, LIQUID), itemModelGenerator.modelOutput);

        itemModelGenerator.itemModelOutput.accept(
            item,
            ItemModelUtils.tintedModel(model, ItemModelUtils.constantTint(-1), ItemModelUtils.constantTint(tint))
        );
    }

    public static void configureTranslations(HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder translationBuilder) {
        translationBuilder.add(ModItems.APPLE_CIDER.get(), "Apple Cider");
        translationBuilder.add(ModItems.SWEET_BERRY_JUICE.get(), "Sweet Berry Juice");
        translationBuilder.add(ModItems.GLOW_BERRY_JUICE.get(), "Glow Berry Juice");
        translationBuilder.add(ModItems.MULLED_APPLE_CIDER.get(), "Mulled Apple Cider");
        translationBuilder.add(ModItems.POMACE.get(), "Pomace");
    }
}
