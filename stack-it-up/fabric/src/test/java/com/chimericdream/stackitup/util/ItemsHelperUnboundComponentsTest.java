package com.chimericdream.stackitup.util;

import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Regression test: on a client, Minecraft.disconnect() runs ItemsHelper.resetAll() when the player
// clicks Join, and that used to throw "Components not bound yet" for items whose holders had no
// components yet - aborting the disconnect and freezing the client.
//
// Bootstrap.bootStrap() alone leaves every item's components unbound (the same state), so the
// "unbound" test must run BEFORE the components are baked. This class therefore does not extend
// BootstrapMinecraft (which bakes in @BeforeAll) and orders its tests explicitly.
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ItemsHelperUnboundComponentsTest {
    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    @Order(1)
    void resetAllDoesNotThrowWhileComponentsAreUnbound() {
        assertDoesNotThrow(() -> ItemsHelper.getItemsHelper().resetAll(false));
    }

    @Test
    @Order(2)
    void resetAllAndSetMaxCountStillWorkOnceComponentsAreBound() {
        HolderLookup.Provider provider = VanillaRegistries.createLookup();
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(provider).forEach(pending -> pending.apply());

        ItemsHelper helper = ItemsHelper.getItemsHelper();
        Item item = Items.DIRT;
        int vanilla = helper.getDefaultCount(item);

        helper.setSingle(item, 16);
        assertEquals(16, helper.getCurrentCount(item));

        helper.resetAll(false);
        assertEquals(vanilla, helper.getCurrentCount(item));
    }
}
