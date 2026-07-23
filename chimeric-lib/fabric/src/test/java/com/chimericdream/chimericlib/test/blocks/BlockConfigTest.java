package com.chimericdream.chimericlib.test.blocks;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Derives the fallback texture through BuiltInRegistries, so it bootstraps Minecraft.
public class BlockConfigTest extends BootstrapMinecraft {
    private static final Identifier CUSTOM_TEXTURE =
        Identifier.fromNamespaceAndPath("chimericlib", "block/custom");

    /**
     * The regression: {@code getTexture()} used {@code Map.getOrDefault}, which evaluates its default
     * argument unconditionally. A config with an explicit texture but no ingredient therefore blew up
     * inside {@code getIngredient()} even though the texture it was asked for was right there.
     */
    @Test
    void explicitTextureIsReturnedWithoutAnIngredient() {
        BlockConfig config = new BlockConfig().texture(CUSTOM_TEXTURE);

        assertDoesNotThrow(() -> config.getTexture());
        assertEquals(CUSTOM_TEXTURE, config.getTexture());
    }

    @Test
    void explicitTextureWinsOverTheIngredient() {
        BlockConfig config = new BlockConfig()
            .ingredient(Blocks.OAK_PLANKS)
            .texture(CUSTOM_TEXTURE);

        assertEquals(CUSTOM_TEXTURE, config.getTexture());
    }

    @Test
    void fallsBackToTheIngredientTextureWhenNoneIsSet() {
        BlockConfig config = new BlockConfig().ingredient(Blocks.OAK_PLANKS);

        assertEquals(
            Identifier.fromNamespaceAndPath("minecraft", "block/oak_planks"),
            config.getTexture()
        );
    }

    /** With neither a texture nor an ingredient there is nothing to derive, so the throw stands. */
    @Test
    void stillThrowsWithNeitherATextureNorAnIngredient() {
        BlockConfig config = new BlockConfig();

        assertThrows(IllegalStateException.class, config::getTexture);
    }

    @Test
    void namedTextureLookupIsUnaffected() {
        BlockConfig config = new BlockConfig()
            .ingredient(Blocks.OAK_PLANKS)
            .texture("side", CUSTOM_TEXTURE);

        assertEquals(CUSTOM_TEXTURE, config.getTexture("side"));
        assertNull(config.getTexture("top"));
    }
}
