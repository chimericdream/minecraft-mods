package com.chimericdream.minekea.item.currency;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class NuggetBag extends Item {
    public final Identifier ITEM_ID;
    public final String material;
    public final String materialName;
    public final Item ingredient;

    public NuggetBag(String material, String materialName, Item ingredient) {
        super(new Item.Settings().arch$tab(ItemGroups.INGREDIENTS).registryKey(REGISTRY_HELPER.makeItemRegistryKey(makeId(material))));

        ITEM_ID = makeId(material);

        this.material = material;
        this.materialName = materialName;
        this.ingredient = ingredient;
    }

    public static Identifier makeId(String material) {
        return Identifier.of(ModInfo.MOD_ID, String.format("currency/%s_nugget_bag", material));
    }
}
