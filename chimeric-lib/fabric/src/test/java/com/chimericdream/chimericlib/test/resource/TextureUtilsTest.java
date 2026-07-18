package com.chimericdream.chimericlib.test.resource;

import com.chimericdream.chimericlib.test.BootstrapMinecraft;
import com.chimericdream.lib.resource.TextureUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

// The Block overloads look ids up in BuiltInRegistries, so this bootstraps Minecraft.
public class TextureUtilsTest extends BootstrapMinecraft {
    @Test
    void blockPrefixesIdentifierPath() {
        Identifier id = Identifier.fromNamespaceAndPath("mymod", "oak_planks");

        assertEquals(Identifier.fromNamespaceAndPath("mymod", "block/oak_planks"), TextureUtils.block(id));
    }

    @Test
    void blockPrefixesAndSuffixesIdentifierPath() {
        Identifier id = Identifier.fromNamespaceAndPath("mymod", "furnace");

        assertEquals(
            Identifier.fromNamespaceAndPath("mymod", "block/furnace_top"),
            TextureUtils.block(id, "_top")
        );
    }

    @Test
    void blockPreservesNamespace() {
        Identifier id = Identifier.fromNamespaceAndPath("othermod", "thing");

        assertEquals("othermod", TextureUtils.block(id).getNamespace());
        assertEquals("block/thing", TextureUtils.block(id).getPath());
    }

    @Test
    void blockResolvesRegisteredBlockId() {
        assertEquals(
            Identifier.fromNamespaceAndPath("minecraft", "block/stone"),
            TextureUtils.block(Blocks.STONE)
        );
        assertEquals(
            Identifier.fromNamespaceAndPath("minecraft", "block/stone_side"),
            TextureUtils.block(Blocks.STONE, "_side")
        );
    }
}
