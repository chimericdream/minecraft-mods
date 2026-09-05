package com.chimericdream.artificialheart.item;

import com.chimericdream.artificialheart.ModInfo;
import com.chimericdream.artificialheart.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class PalePumpkinSeedsItem extends BlockItem {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "pale_pumpkin_seeds");
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, ITEM_ID);

    public PalePumpkinSeedsItem() {
        super(
            ModBlocks.PALE_PUMPKIN_STEM_BLOCK.get(),
            new Item.Properties().arch$tab(CreativeModeTabs.NATURAL_BLOCKS).useItemDescriptionPrefix().setId(ITEM_REGISTRY_KEY)
        );
    }
}
