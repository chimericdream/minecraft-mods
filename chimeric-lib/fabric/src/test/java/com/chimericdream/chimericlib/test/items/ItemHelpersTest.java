package com.chimericdream.chimericlib.test.items;

import com.chimericdream.chimericlib.test.BootstrapMinecraft;
import com.chimericdream.lib.items.ItemHelpers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Resolves ids through BuiltInRegistries, so it bootstraps Minecraft (see BootstrapMinecraft).
public class ItemHelpersTest extends BootstrapMinecraft {
    @Test
    void getIdentifierReturnsRegisteredItemId() {
        Identifier id = ItemHelpers.getIdentifier(new ItemStack(Items.DIAMOND));

        assertEquals(Identifier.fromNamespaceAndPath("minecraft", "diamond"), id);
    }

    @Test
    void getIdentifierTreatsEmptyStackAsAir() {
        assertEquals(
            Identifier.fromNamespaceAndPath("minecraft", "air"),
            ItemHelpers.getIdentifier(ItemStack.EMPTY)
        );
    }
}
