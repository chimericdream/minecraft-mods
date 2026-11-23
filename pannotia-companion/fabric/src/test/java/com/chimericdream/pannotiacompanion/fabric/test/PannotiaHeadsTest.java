package com.chimericdream.pannotiacompanion.fabric.test;

import com.chimericdream.lib.testkit.BootstrapMinecraft;
import com.chimericdream.pannotiacompanion.villager.HeadNames;
import com.chimericdream.pannotiacompanion.villager.PannotiaHeads;
import com.chimericdream.pannotiacompanion.villager.WanderingTraderHeadData;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ResolvableProfile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PannotiaHeadsTest extends BootstrapMinecraft {
    // "Blue Wither Projectile Head" from loot_table/pannotia/heads/wither.json
    private static final String WITHER_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM2ODJiMDYyMDNiOWRlNGMyODU0MTA3MWEyNmNkYzM0MGRkMjVkNGMzNzJiNzAyM2VjMmY0MTIwMjFkNjJmNyJ9fX0=";

    // An older "Light Gray Sheep Head" texture that /pc fixheads swaps for the one in sheep/light_gray.json
    private static final String OLD_LIGHT_GRAY_SHEEP_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQyZTJlOTNhMTQyYmZkNDNmMjQwZDM3ZGU4ZjliMDk3NmU3NmU2NWIyMjY1MTkwODI1OWU0NmRiNzcwZSJ9fX0=";

    private static ResolvableProfile profile(String texture, boolean withId) {
        return profile(texture, withId, null);
    }

    private static ResolvableProfile profile(String texture, boolean withId, String profileName) {
        CompoundTag property = new CompoundTag();
        property.putString("name", "textures");
        property.putString("value", texture);

        ListTag properties = new ListTag();
        properties.add(property);

        CompoundTag tag = new CompoundTag();
        tag.put("properties", properties);
        if (profileName != null) {
            tag.putString("name", profileName);
        }
        if (withId) {
            tag.put("id", new IntArrayTag(new int[]{-1907584814, -692829797, -1460246851, -1397511329}));
        }

        return ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }

    private static ItemStack head(String texture, boolean withId, String itemName) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.set(DataComponents.PROFILE, profile(texture, withId));
        if (itemName != null) {
            stack.set(DataComponents.ITEM_NAME, Component.literal(itemName));
        }

        return stack;
    }

    @Test
    void oldLootHeadBecomesTheSameAsANewOne() {
        ItemStack old = head(WITHER_TEXTURE, true, null);
        ItemStack fresh = head(WITHER_TEXTURE, false, "Blue Wither Projectile Head");

        assertFalse(ItemStack.isSameItemSameComponents(old, fresh));
        assertTrue(PannotiaHeads.fixName(old));
        assertTrue(ItemStack.isSameItemSameComponents(old, fresh));
    }

    @Test
    void aHeadThatIsAlreadyCorrectDoesNotChange() {
        ItemStack fresh = head(WITHER_TEXTURE, false, "Blue Wither Projectile Head");

        assertFalse(PannotiaHeads.fixName(fresh));
    }

    @Test
    void anOldHeadThatKeptItsNameStillLosesTheId() {
        ItemStack old = head(WITHER_TEXTURE, true, "Blue Wither Projectile Head");

        assertTrue(PannotiaHeads.fixName(old));
        assertTrue(ItemStack.isSameItemSameComponents(old, head(WITHER_TEXTURE, false, "Blue Wither Projectile Head")));
        assertFalse(PannotiaHeads.fixName(old));
    }

    @Test
    void aHeadWithTheLegacyVillagerProfileNameBecomesTheSameAsANewOne() {
        String texture = textureOf("Villager Head");
        ItemStack old = new ItemStack(Items.PLAYER_HEAD);
        old.set(DataComponents.PROFILE, profile(texture, true, "Villager"));
        ItemStack fresh = head(texture, false, "Villager Head");

        assertTrue(PannotiaHeads.fixName(old));
        assertTrue(ItemStack.isSameItemSameComponents(old, fresh));
        assertFalse(PannotiaHeads.fixName(old));
    }

    @Test
    void anOldLightGraySheepTextureIsSwappedForTheCurrentOne() {
        ItemStack old = head(OLD_LIGHT_GRAY_SHEEP_TEXTURE, true, null);
        ItemStack fresh = head(textureOf("Light Gray Sheep Head"), false, "Light Gray Sheep Head");

        assertTrue(PannotiaHeads.fixName(old));
        assertTrue(ItemStack.isSameItemSameComponents(old, fresh));
        assertFalse(PannotiaHeads.fixName(old));
    }

    @Test
    void aHeadWithAnyVanillaMobProfileNameBecomesTheSameAsANewOne() {
        for (String mob : List.of("Blaze", "Evoker", "Enderman", "Donkey", "Cow", "Ocelot", "Phantom", "Piglin", "Pillager", "Strider",
            "Zombie_Villager", "ZombieVillager", "zombified_piglin", "IronGolem", "EnderDragon")) {
            ItemStack old = new ItemStack(Items.PLAYER_HEAD);
            old.set(DataComponents.PROFILE, profile(WITHER_TEXTURE, true, mob));
            ItemStack fresh = head(WITHER_TEXTURE, false, "Blue Wither Projectile Head");

            assertTrue(PannotiaHeads.isVanillaMobName(mob), mob);
            assertTrue(PannotiaHeads.fixName(old), mob);
            assertTrue(ItemStack.isSameItemSameComponents(old, fresh), mob);
        }
    }

    @Test
    void mobNamesMatchWhateverTheSpacingAndCase() {
        assertTrue(PannotiaHeads.isVanillaMobName("Zombie Villager"));
        assertTrue(PannotiaHeads.isVanillaMobName("zombie_villager"));
        assertTrue(PannotiaHeads.isVanillaMobName("ZOMBIEVILLAGER"));
    }

    @Test
    void aNameThatIsNotAVanillaMobIsNotTreatedAsOne() {
        assertFalse(PannotiaHeads.isVanillaMobName("SomePlayer"));
        assertFalse(PannotiaHeads.isVanillaMobName("Xisuma"));
        assertFalse(PannotiaHeads.isVanillaMobName(PannotiaHeads.PROFILE_NAME));
    }

    @Test
    void aKnownTextureWithAnotherProfileNameDoesNotChange() {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.set(DataComponents.PROFILE, profile(WITHER_TEXTURE, true, "SomePlayer"));
        ItemStack copy = stack.copy();

        assertFalse(PannotiaHeads.fixName(stack));
        assertTrue(ItemStack.isSameItemSameComponents(stack, copy));
    }

    @Test
    void anUnknownHeadDoesNotChange() {
        ItemStack unknown = head("bm90LW9uZS1vZi1vdXJzCg==", true, null);
        ItemStack copy = unknown.copy();

        assertFalse(PannotiaHeads.fixName(unknown));
        assertTrue(ItemStack.isSameItemSameComponents(unknown, copy));
    }

    @Test
    void aCustomNameIsNeverOverwritten() {
        ItemStack stack = head(WITHER_TEXTURE, true, null);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Mine"));

        assertFalse(PannotiaHeads.fixName(stack));
        assertEquals(Component.literal("Mine"), stack.get(DataComponents.CUSTOM_NAME));
    }

    /** A head as the wandering trader sold it before: profile name "pannotia", an id, and a repeated custom name. */
    private static ItemStack tradeHead(WanderingTraderHeadData.HeadTrade head, boolean withCustomName) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.set(DataComponents.ITEM_NAME, Component.literal(head.name()));
        if (withCustomName) {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal(head.name()).withStyle(style -> style.withItalic(false)));
        }

        PropertyMap properties = new PropertyMap(
            ImmutableMultimap.<String, Property>builder().put("textures", new Property("textures", head.texture())).build()
        );
        stack.set(DataComponents.PROFILE, ResolvableProfile.createResolved(
            new GameProfile(UUID.nameUUIDFromBytes(head.texture().getBytes(StandardCharsets.UTF_8)), PannotiaHeads.PROFILE_NAME, properties)
        ));

        return stack;
    }

    private static List<WanderingTraderHeadData.HeadTrade> allTradeHeads() {
        List<WanderingTraderHeadData.HeadTrade> heads = new ArrayList<>(WanderingTraderHeadData.STREAMER_HEADS);
        heads.addAll(WanderingTraderHeadData.MINI_BLOCKS);
        return heads;
    }

    @Test
    void aTradeHeadBecomesTheSameAsTheDatapackHead() {
        // The Mini Enchanting Table pair from the bug report.
        WanderingTraderHeadData.HeadTrade head = allTradeHeads().stream()
            .filter(h -> h.key().equals("mini_enchanting_table")).findFirst().orElseThrow();
        ItemStack trade = tradeHead(head, true);
        ItemStack datapack = head(head.texture(), false, "Mini Enchanting Table");

        assertFalse(ItemStack.isSameItemSameComponents(trade, datapack));
        assertTrue(PannotiaHeads.fixName(trade));
        assertTrue(ItemStack.isSameItemSameComponents(trade, datapack));
        assertFalse(PannotiaHeads.fixName(trade));
    }

    @Test
    void everyTradeHeadBecomesThePlainHead() {
        for (WanderingTraderHeadData.HeadTrade head : allTradeHeads()) {
            for (boolean withCustomName : new boolean[]{true, false}) {
                ItemStack trade = tradeHead(head, withCustomName);

                assertTrue(PannotiaHeads.fixName(trade), head.key());
                assertTrue(ItemStack.isSameItemSameComponents(trade, head(head.texture(), false, head.name())), head.key());
                assertFalse(PannotiaHeads.fixName(trade), head.key());
            }
        }
    }

    @Test
    void aCraftedMiniBlockWithCommonRarityBecomesTheSameAsABoughtOne() {
        // Both a mini block that the trader also sells and one that only the stonecutter makes.
        for (String name : List.of("Mini Acacia Log", "Mini Crafter")) {
            String texture = textureOf(name);
            ItemStack crafted = head(texture, false, name);
            crafted.set(DataComponents.RARITY, Rarity.COMMON);
            ItemStack bought = head(texture, false, name);

            assertFalse(ItemStack.isSameItemSameComponents(crafted, bought), name);
            assertTrue(PannotiaHeads.fixName(crafted), name);
            assertTrue(ItemStack.isSameItemSameComponents(crafted, bought), name);
            assertFalse(PannotiaHeads.fixName(crafted), name);
        }
    }

    @Test
    void theMiniBlockRecipesNoLongerSetARarity() throws IOException {
        int checked = 0;
        for (Path file : lootTableFiles()) {
            if (file.toString().replace(File.separatorChar, '/').contains("/recipe/mini_blocks/")) {
                checked++;
                assertFalse(Files.readString(file, StandardCharsets.UTF_8).contains("minecraft:rarity"), file.toString());
            }
        }

        assertTrue(checked > 150, "Expected to find the mini block recipes, found " + checked);
    }

    @Test
    void aRenamedTradeHeadKeepsItsName() {
        ItemStack trade = tradeHead(allTradeHeads().getFirst(), true);
        trade.set(DataComponents.CUSTOM_NAME, Component.literal("My Head"));

        assertFalse(PannotiaHeads.fixName(trade));
        assertEquals("My Head", trade.get(DataComponents.CUSTOM_NAME).getString());
    }

    @Test
    void villagerProfessionsWithOneSharedSkinGetThePlainName() {
        assertEquals("Villager Head", HeadNames.forTexture(textureOf("Leatherworker Villager Head")));
        assertEquals("Villager Head", HeadNames.forTexture(textureOf("Mason Villager Head")));
        assertEquals("Zombie Villager Head", HeadNames.forTexture(textureOf("Zombie Toolsmith Head")));
        // Professions with their own skin keep their own name.
        assertEquals("Librarian Villager Head", HeadNames.forTexture(textureOf("Librarian Villager Head")));
        assertEquals("Zombie Fisherman Head", HeadNames.forTexture(textureOf("Zombie Fisherman Head")));
    }

    @Test
    void everyLootTableHeadTextureHasAName() throws Exception {
        List<String> missing = new ArrayList<>();
        int checked = 0;

        for (Path file : lootTableFiles()) {
            JsonElement root = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
            List<String> textures = new ArrayList<>();
            collectTextures(root, textures);

            for (String texture : textures) {
                checked++;
                if (HeadNames.forTexture(texture) == null) {
                    missing.add(file.getFileName() + ": " + texture);
                }
            }
        }

        assertTrue(checked > 200, "Expected to find the mob head loot tables, found " + checked + " textures");
        assertTrue(missing.isEmpty(), "Loot table heads missing from HeadNames: " + missing);
    }

    private static String textureOf(String lootName) {
        // Find the texture for a loot-table entry by its item name, so the test does not hard-code base64.
        try {
            for (Path file : lootTableFiles()) {
                String found = findTexture(JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)), lootName);
                if (found != null) {
                    return found;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return fail("No loot table head named " + lootName);
    }

    /**
     * Everything that gives a head: the mob head loot tables and the mini block recipes. These are inside the common
     * module's jar on the test classpath, so read the source files. Gradle runs unit tests with the fabric subproject
     * as the working directory.
     */
    private static List<Path> lootTableFiles() throws IOException {
        List<Path> result = new ArrayList<>();

        for (String sub : List.of("loot_table", "recipe/mini_blocks")) {
            Path dir = Path.of("../common/src/main/resources/data/pannotiacompanion/" + sub);
            assertTrue(Files.isDirectory(dir), "Directory not found: " + dir.toAbsolutePath());

            try (Stream<Path> files = Files.walk(dir)) {
                result.addAll(files.filter(path -> path.toString().endsWith(".json")).toList());
            }
        }

        return result;
    }

    private static void collectTextures(JsonElement element, List<String> out) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("minecraft:profile") && object.has("minecraft:item_name")) {
                String texture = firstTexture(object.getAsJsonObject("minecraft:profile"));
                if (texture != null) {
                    out.add(texture);
                }
            }

            object.entrySet().forEach(entry -> collectTextures(entry.getValue(), out));
        } else if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(child -> collectTextures(child, out));
        }
    }

    private static String findTexture(JsonElement element, String itemName) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            if (object.has("minecraft:profile") && object.has("minecraft:item_name")
                && itemName.equals(object.get("minecraft:item_name").getAsString())) {
                return firstTexture(object.getAsJsonObject("minecraft:profile"));
            }

            for (var entry : object.entrySet()) {
                String found = findTexture(entry.getValue(), itemName);
                if (found != null) {
                    return found;
                }
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                String found = findTexture(child, itemName);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    private static String firstTexture(JsonObject profile) {
        if (!profile.has("properties") || profile.getAsJsonArray("properties").isEmpty()) {
            return null;
        }

        return profile.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
    }
}
