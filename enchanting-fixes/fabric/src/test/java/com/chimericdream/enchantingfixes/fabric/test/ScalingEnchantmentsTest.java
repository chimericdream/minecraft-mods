package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.enchantment.MaxLevels;
import com.chimericdream.enchantingfixes.enchantment.ScalingEnchantments;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScalingEnchantmentsTest extends BootstrapMinecraft {
    private static final float EPSILON = 1.0E-4F;

    private static JsonElement enchantmentJson(String name) {
        String path = "/" + ScalingEnchantments.PACK_PATH + "/data/minecraft/enchantment/" + name + ".json";
        InputStream stream = ScalingEnchantmentsTest.class.getResourceAsStream(path);
        assertNotNull(stream, path);
        return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    /** Follows object keys (strings) and array indexes (integers) to one level-based value in the file. */
    private static LevelBasedValue value(String enchantment, Object... path) {
        JsonElement node = enchantmentJson(enchantment);
        for (Object step : path) {
            node = step instanceof Integer index ? node.getAsJsonArray().get(index) : node.getAsJsonObject().get((String) step);
        }
        return LevelBasedValue.CODEC.parse(JsonOps.INSTANCE, node).getOrThrow();
    }

    /**
     * The test registries have no item tags. So this decodes only the effects (the pack changes only the
     * effects), and it removes the match_tool conditions, which use item tags. The pack does not change them.
     */
    @Test
    void packEffectsDecode() {
        HolderLookup.Provider registries = VanillaRegistries.createLookup();
        RegistryOps<JsonElement> ops = registries.createSerializationContext(JsonOps.INSTANCE);
        for (String name : List.of("lure", "quick_charge", "breach", "thorns", "unbreaking")) {
            JsonObject effects = enchantmentJson(name).getAsJsonObject().getAsJsonObject("effects");
            for (JsonElement component : effects.asMap().values()) {
                if (component.isJsonArray()) {
                    for (JsonElement entry : component.getAsJsonArray()) {
                        JsonObject object = entry.getAsJsonObject();
                        if (object.has("requirements") && object.get("requirements").toString().contains("minecraft:match_tool")) {
                            object.remove("requirements");
                        }
                    }
                }
            }
            EnchantmentEffectComponents.CODEC.parse(ops, effects).getOrThrow();
        }
    }

    @Test
    void lureMatchesVanillaToVAndNeverBreaksFishing() {
        LevelBasedValue lure = value("lure", "effects", "minecraft:fishing_time_reduction", 0, "effect", "value");
        for (int level = 1; level <= 5; level++) {
            assertEquals(5.0F * level, lure.calculate(level), EPSILON);
        }
        assertEquals(27.22F, lure.calculate(6), 0.01F);

        float previous = lure.calculate(5);
        for (int level = 6; level <= 255; level++) {
            float seconds = lure.calculate(level);
            assertTrue(seconds > previous && seconds < 29.0F, "level " + level);
            previous = seconds;
        }
    }

    @Test
    void quickChargeMatchesVanillaToIIIAndReachesZeroTicksAt30() {
        LevelBasedValue quickCharge = value("quick_charge", "effects", "minecraft:crossbow_charge_time", "value");
        for (int level = 1; level <= 3; level++) {
            assertEquals(-0.25F * level, quickCharge.calculate(level), EPSILON);
        }
        assertEquals(0.375F, 1.25F + quickCharge.calculate(4), EPSILON);

        // The same float steps as EnchantmentHelper.modifyCrossbowChargingTime and CrossbowItem.getChargeDuration.
        assertEquals(1, Mth.floor((1.25F + quickCharge.calculate(29)) * 20.0F));
        assertEquals(0, Mth.floor((1.25F + quickCharge.calculate(30)) * 20.0F));
        assertEquals(MaxLevels.SCALING_HARD_CAPS.get("minecraft:quick_charge"), 30);
    }

    @Test
    void breachMatchesVanillaToIVAndStaysAboveFullBypass() {
        LevelBasedValue breach = value("breach", "effects", "minecraft:armor_effectiveness", 0, "effect", "value");
        for (int level = 1; level <= 4; level++) {
            assertEquals(-0.15F * level, breach.calculate(level), EPSILON);
        }

        float previous = breach.calculate(4);
        for (int level = 5; level <= 255; level++) {
            float amount = breach.calculate(level);
            assertTrue(amount < previous && amount > -0.8F, "level " + level);
            previous = amount;
        }
    }

    @Test
    void thornsMatchesVanillaToIIIThenScales() {
        LevelBasedValue chance = value("thorns", "effects", "minecraft:post_attack", 0, "requirements", "chance", "amount");
        LevelBasedValue minDamage = value("thorns", "effects", "minecraft:post_attack", 0, "effect", "effects", 0, "min_damage");
        LevelBasedValue maxDamage = value("thorns", "effects", "minecraft:post_attack", 0, "effect", "effects", 0, "max_damage");
        LevelBasedValue durability = value("thorns", "effects", "minecraft:post_attack", 0, "effect", "effects", 1, "amount");

        for (int level = 1; level <= 3; level++) {
            assertEquals(0.15F * level, chance.calculate(level), EPSILON);
            assertEquals(1.0F, minDamage.calculate(level), EPSILON);
            assertEquals(5.0F, maxDamage.calculate(level), EPSILON);
            assertEquals(2.0F, durability.calculate(level), EPSILON);
        }

        assertEquals(1.5F, minDamage.calculate(4), EPSILON);
        assertEquals(6.5F, maxDamage.calculate(4), EPSILON);
        assertEquals(1.0F, durability.calculate(4), EPSILON);
        assertEquals(1.0F, durability.calculate(255), EPSILON);

        float previous = chance.calculate(3);
        for (int level = 4; level <= 255; level++) {
            float value = chance.calculate(level);
            assertTrue(value > previous && value < 1.0F, "level " + level);
            previous = value;
        }
    }

    @Test
    void unbreakingArmorMatchesVanillaToIIIThenGetsCloserTo100Percent() {
        LevelBasedValue armor = value("unbreaking", "effects", "minecraft:item_damage", 0, "effect", "chance");
        for (int level = 1; level <= 3; level++) {
            assertEquals(2.0F * level / (5.0F * level + 5.0F), armor.calculate(level), EPSILON);
        }

        float previous = armor.calculate(3);
        for (int level = 4; level <= 255; level++) {
            float value = armor.calculate(level);
            assertTrue(value > previous && value < 1.0F, "level " + level);
            previous = value;
        }
        assertEquals(0.627F, armor.calculate(10), 0.001F);
        assertTrue(armor.calculate(100) > 0.9F);
    }

    @Test
    void protectionIsVanillaUpTo20Points() {
        assertEquals(0.0F, ScalingEnchantments.protectionReduction(0.0F, 0.95F), EPSILON);
        assertEquals(0.64F, ScalingEnchantments.protectionReduction(16.0F, 0.95F), EPSILON);
        assertEquals(0.8F, ScalingEnchantments.protectionReduction(20.0F, 0.95F), EPSILON);
    }

    @Test
    void protectionAbove20PointsGetsCloserToTheLimit() {
        assertEquals(0.875F, ScalingEnchantments.protectionReduction(40.0F, 0.95F), EPSILON);
        assertEquals(0.9125F, ScalingEnchantments.protectionReduction(80.0F, 0.95F), EPSILON);
        assertTrue(ScalingEnchantments.protectionReduction(10_000.0F, 0.95F) < 0.95F);
        assertEquals(0.8F, ScalingEnchantments.protectionReduction(80.0F, 0.8F), EPSILON);
    }
}
