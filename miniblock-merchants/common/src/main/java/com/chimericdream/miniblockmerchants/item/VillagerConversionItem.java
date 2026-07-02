package com.chimericdream.miniblockmerchants.item;

import com.chimericdream.miniblockmerchants.util.DataUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ResolvableProfile;

import static com.chimericdream.miniblockmerchants.MiniblockMerchantsMod.REGISTRY_HELPER;

public class VillagerConversionItem extends Item {
    public final String id;
    public final String profession;

    public VillagerConversionItem(String id, String profession) {
        super(
            new Item.Properties()
                .setId(REGISTRY_HELPER.makeItemRegistryKey(id))
                .arch$tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
                .component(DataComponents.PROFILE, ResolvableProfile.createResolved(DataUtil.makeGameProfile(id)))
        );

        this.id = id;
        this.profession = profession;
    }
}
