package com.chimericdream.effectivegear.item.armor;

import com.chimericdream.lib.trims.TrimMaterialRegistryHelper;
import net.minecraft.SharedConstants;
import net.minecraft.core.Cloner;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.Bootstrap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPatterns;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Armored Elytra support: the combined item is a plain elytra whose merged chestplate (and trim)
 * lives in custom data. The NBT below is copied from real Armored Elytra items.
 */
class TrimSetUtilsArmoredElytraTest {
    /**
     * Same bake as chimeric-lib's {@code BootstrapMinecraft}, but with this mod's trim materials added
     * to the lookup: the mod's smithing templates are registered on the test classpath and resolve
     * those materials while components are being bound, which the vanilla-only lookup can't satisfy.
     */
    @BeforeAll
    static void bootstrapMinecraft() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        Cloner.Factory cloner = new Cloner.Factory();
        RegistryDataLoader.WORLDGEN_REGISTRIES.forEach(registryData -> registryData.runWithArguments(cloner::addCodec));

        HolderLookup.Provider provider = new RegistrySetBuilder()
            .add(Registries.TRIM_MATERIAL, context -> TrimMaterialRegistryHelper.bootstrap(context, Trims.MATERIALS))
            .buildPatch(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), VanillaRegistries.createLookup(), cloner)
            .full();

        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(provider).forEach(pending -> pending.apply());
    }

    private static final String PLAIN_NETHERITE = """
        {"armored_elytra:elytra": {count: 1, id: "minecraft:elytra"},
         "armored_elytra:chestplate": {count: 1, id: "minecraft:netherite_chestplate"}}""";

    private static final String TRIMMED_NETHERITE = """
        {"armored_elytra:trim_material": "minecraft:amethyst",
         "armored_elytra:elytra": {count: 1, id: "minecraft:elytra"},
         "armored_elytra:chestplate": {
             components: {"minecraft:trim": {material: "minecraft:amethyst", pattern: "minecraft:silence"}},
             count: 1,
             id: "minecraft:netherite_chestplate"}}""";

    /** Stands in for the netherite-chestplate tag, which is unbound without loaded data packs. */
    private static boolean isNetheriteChestplate(Holder<Item> holder) {
        return holder.value() == Items.NETHERITE_CHESTPLATE;
    }

    private static ItemStack elytra(String customDataSnbt) throws Exception {
        ItemStack stack = new ItemStack(Items.ELYTRA);
        CompoundTag tag = TagParser.parseCompoundFully(customDataSnbt);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    @Test
    void trimmedArmoredElytraMatchesItsTrimMaterial() throws Exception {
        ItemStack stack = elytra(TRIMMED_NETHERITE);

        assertTrue(TrimSetUtils.hasTrimMaterial(stack, TrimMaterials.AMETHYST));
    }

    @Test
    void trimmedArmoredElytraMatchesItsTrimPattern() throws Exception {
        ItemStack stack = elytra(TRIMMED_NETHERITE);

        assertTrue(TrimSetUtils.hasTrimPattern(stack, TrimPatterns.SILENCE));
    }

    @Test
    void trimmedArmoredElytraDoesNotMatchOtherMaterialOrPattern() throws Exception {
        ItemStack stack = elytra(TRIMMED_NETHERITE);

        assertFalse(TrimSetUtils.hasTrimMaterial(stack, TrimMaterials.GOLD));
        assertFalse(TrimSetUtils.hasTrimPattern(stack, TrimPatterns.SNOUT));
    }

    @Test
    void untrimmedArmoredElytraHasNoTrim() throws Exception {
        ItemStack stack = elytra(PLAIN_NETHERITE);

        assertFalse(TrimSetUtils.hasTrimMaterial(stack, TrimMaterials.AMETHYST));
        assertFalse(TrimSetUtils.hasTrimPattern(stack, TrimPatterns.SILENCE));
    }

    @Test
    void plainElytraHasNoTrim() {
        ItemStack stack = new ItemStack(Items.ELYTRA);

        assertFalse(TrimSetUtils.hasTrimMaterial(stack, TrimMaterials.AMETHYST));
        assertFalse(TrimSetUtils.hasTrimPattern(stack, TrimPatterns.SILENCE));
    }

    @Test
    void emptyStackHasNoTrim() {
        assertFalse(TrimSetUtils.hasTrimMaterial(ItemStack.EMPTY, TrimMaterials.AMETHYST));
    }

    @Test
    void malformedChestplateDataIsIgnored() throws Exception {
        ItemStack notACompound = elytra("{\"armored_elytra:chestplate\": \"minecraft:netherite_chestplate\"}");
        ItemStack badTrim = elytra("""
            {"armored_elytra:chestplate": {id: "minecraft:netherite_chestplate",
              components: {"minecraft:trim": {material: 5, pattern: "not a valid id!"}}}}""");

        assertFalse(TrimSetUtils.hasTrimMaterial(notACompound, TrimMaterials.AMETHYST));
        assertFalse(TrimSetUtils.chestMatches(notACompound, TrimSetUtilsArmoredElytraTest::isNetheriteChestplate));
        assertFalse(TrimSetUtils.hasTrimMaterial(badTrim, TrimMaterials.AMETHYST));
        assertFalse(TrimSetUtils.hasTrimPattern(badTrim, TrimPatterns.SILENCE));
    }

    @Test
    void armoredElytraCountsAsItsMergedChestplateForSetTags() throws Exception {
        assertTrue(TrimSetUtils.chestMatches(elytra(PLAIN_NETHERITE), TrimSetUtilsArmoredElytraTest::isNetheriteChestplate));
        assertTrue(TrimSetUtils.chestMatches(elytra(TRIMMED_NETHERITE), TrimSetUtilsArmoredElytraTest::isNetheriteChestplate));
    }

    @Test
    void armoredElytraWithNonMatchingChestplateDoesNotCountForSetTags() throws Exception {
        ItemStack iron = elytra("""
            {"armored_elytra:chestplate": {count: 1, id: "minecraft:iron_chestplate"}}""");

        assertFalse(TrimSetUtils.chestMatches(iron, TrimSetUtilsArmoredElytraTest::isNetheriteChestplate));
    }

    @Test
    void plainElytraAndDirectChestplatesBehaveAsBefore() {
        assertFalse(TrimSetUtils.chestMatches(new ItemStack(Items.ELYTRA), TrimSetUtilsArmoredElytraTest::isNetheriteChestplate));
        assertTrue(TrimSetUtils.chestMatches(new ItemStack(Items.NETHERITE_CHESTPLATE), TrimSetUtilsArmoredElytraTest::isNetheriteChestplate));
    }
}
