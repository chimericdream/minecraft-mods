package com.chimericdream.chimericlib.test.blocks.family;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.lib.blocks.family.BlockFamily;
import com.chimericdream.lib.blocks.family.BlockFamilyVariant;
import com.chimericdream.lib.registries.ModRegistryHelper;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlockFamilyTest extends BootstrapMinecraft {
    private static final ModRegistryHelper HELPER = new ModRegistryHelper("chimericlibtest", LogManager.getLogger("chimericlibtest"));

    private static final BlockFamily PARTIAL_FAMILY;
    private static final BlockFamily FULL_FAMILY;
    private static final BlockFamily OVERRIDDEN_ID_FAMILY;
    private static final Identifier OVERRIDDEN_STAIRS_ID = Identifier.fromNamespaceAndPath("chimericlibtest", "custom/path/stairs");

    // Queued on HELPER before the family below, but not actually constructed until HELPER.init()
    // runs — same as a real mod's own base block sitting in the same DeferredRegister as a family
    // built from it.
    private static final RegistrySupplier<Block> DEFERRED_BASE_BLOCK = HELPER.registerBlock(
        "deferred_base",
        () -> new Block(BlockBehaviour.Properties.of().setId(HELPER.makeBlockRegistryKey("deferred_base")))
    );
    private static final BlockFamily DEFERRED_INGREDIENT_FAMILY;

    static {
        BlockConfig template = new BlockConfig()
            .materialName("Test Brick")
            .ingredient(Blocks.STONE_BRICKS)
            .flammable();

        PARTIAL_FAMILY = BlockFamily.builder(HELPER, "partial_family", template)
            .variants(BlockFamilyVariant.STAIRS, BlockFamilyVariant.SLAB)
            .build();

        FULL_FAMILY = BlockFamily.builder(HELPER, "full_family", template)
            .variants(BlockFamilyVariant.STAIRS, BlockFamilyVariant.SLAB, BlockFamilyVariant.WALL)
            .build();

        OVERRIDDEN_ID_FAMILY = BlockFamily.builder(HELPER, "overridden_family", template)
            .variants(BlockFamilyVariant.STAIRS)
            .stairsId(OVERRIDDEN_STAIRS_ID)
            .build();

        DEFERRED_INGREDIENT_FAMILY = BlockFamily.builder(HELPER, "deferred_family", new BlockConfig()
                .materialName("Deferred")
                .ingredient(DEFERRED_BASE_BLOCK))
            .variants(BlockFamilyVariant.STAIRS)
            .build();

        HELPER.init();
    }

    @Test
    void onlyRequestedVariantsAreRegistered() {
        assertTrue(PARTIAL_FAMILY.getStairs().isPresent());
        assertTrue(PARTIAL_FAMILY.getSlab().isPresent());
        assertTrue(PARTIAL_FAMILY.getWall().isEmpty());
    }

    @Test
    void allThreeVariantsCanBeRegisteredTogether() {
        assertTrue(FULL_FAMILY.getStairs().isPresent());
        assertTrue(FULL_FAMILY.getSlab().isPresent());
        assertTrue(FULL_FAMILY.getWall().isPresent());
    }

    @Test
    void variantBlocksAreTheExpectedVanillaTypes() {
        assertInstanceOf(StairBlock.class, FULL_FAMILY.getStairs().orElseThrow().get());
        assertInstanceOf(SlabBlock.class, FULL_FAMILY.getSlab().orElseThrow().get());
        assertInstanceOf(WallBlock.class, FULL_FAMILY.getWall().orElseThrow().get());
    }

    @Test
    void defaultIdsAreSuffixedOffTheMaterialKey() {
        assertEquals(
            HELPER.makeId("full_family_stairs"),
            FULL_FAMILY.getStairs().orElseThrow().get().builtInRegistryHolder().key().identifier()
        );
        assertEquals(
            HELPER.makeId("full_family_slab"),
            FULL_FAMILY.getSlab().orElseThrow().get().builtInRegistryHolder().key().identifier()
        );
        assertEquals(
            HELPER.makeId("full_family_wall"),
            FULL_FAMILY.getWall().orElseThrow().get().builtInRegistryHolder().key().identifier()
        );
    }

    @Test
    void anIdOverrideWinsOverTheDefaultSuffix() {
        assertEquals(
            OVERRIDDEN_STAIRS_ID,
            OVERRIDDEN_ID_FAMILY.getStairs().orElseThrow().get().builtInRegistryHolder().key().identifier()
        );
    }

    @Test
    void ingredientCanBeAnotherNotYetRegisteredModBlock() {
        assertInstanceOf(StairBlock.class, DEFERRED_INGREDIENT_FAMILY.getStairs().orElseThrow().get());
    }

    @Test
    void derivedConfigsInheritFromTheTemplateButPointTheIngredientAtTheBase() {
        BlockConfig stairsConfig = FULL_FAMILY.getConfig(BlockFamilyVariant.STAIRS).orElseThrow();

        assertEquals("Test Brick", stairsConfig.getMaterialName());
        assertEquals(Blocks.STONE_BRICKS, stairsConfig.getIngredient());
        assertTrue(stairsConfig.isFlammable());
    }
}
