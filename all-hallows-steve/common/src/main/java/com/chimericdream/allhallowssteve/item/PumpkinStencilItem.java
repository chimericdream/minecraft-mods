package com.chimericdream.allhallowssteve.item;

import com.chimericdream.allhallowssteve.ModInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

public class PumpkinStencilItem extends Item {
    public static final int DEFAULT_STACK_SIZE = 16;

    public final Identifier ITEM_ID;
    public final String stencil;
    public final String stencilName;

    public PumpkinStencilItem(String stencil, String stencilName) {
        this(stencil, stencilName, 16);
    }

    @SuppressWarnings("UnstableApiUsage")
    public PumpkinStencilItem(String stencil, String stencilName, int maxStackSize) {
        super(new Item.Properties().arch$tab(CreativeModeTabs.INGREDIENTS).setId(REGISTRY_HELPER.makeItemRegistryKey(makeId(stencil))).stacksTo(maxStackSize));

        ITEM_ID = makeId(stencil);

        this.stencil = stencil;
        this.stencilName = stencilName;
    }

    public static Identifier makeId(String stencil) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("pumpkin_stencils/%s", stencil));
    }
}
