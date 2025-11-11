package com.chimericdream.miniblockmerchants.item;

import com.chimericdream.miniblockmerchants.util.DataUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import static com.chimericdream.miniblockmerchants.MiniblockMerchantsMod.REGISTRY_HELPER;

public class VillagerConversionItem extends Item {
    public final String id;
    public final String profession;

    public VillagerConversionItem(String id, String profession) {
        super(
            new Item.Settings()
                .registryKey(REGISTRY_HELPER.makeItemRegistryKey(id))
                .arch$tab(ItemGroups.FUNCTIONAL)
                .component(DataComponentTypes.PROFILE, ProfileComponent.ofStatic(DataUtil.makeGameProfile(id)))
        );

        this.id = id;
        this.profession = profession;
    }
}
