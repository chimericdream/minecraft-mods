package com.chimericdream.enchantingfixes.fabric.test;

import com.chimericdream.enchantingfixes.config.EnchantingFixesConfig;
import com.chimericdream.enchantingfixes.enchantment.AdvancedSilkTouch;
import com.chimericdream.enchantingfixes.enchantment.MaxLevels;
import com.chimericdream.enchantingfixes.enchantment.RandomLevels;
import com.chimericdream.enchantingfixes.enchantment.RandomLevels.Mode;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdvancedSilkTouchTest extends BootstrapMinecraft {
    /** The maximum level in the enchantment definition of the data pack. */
    private static final int DEFINITION_MAX_LEVEL = 3;

    @Test
    void theDefaultListHasLevel30() {
        EnchantingFixesConfig config = new EnchantingFixesConfig();
        assertEquals(30, MaxLevels.resolve(DEFINITION_MAX_LEVEL, AdvancedSilkTouch.ID, config, false));
    }

    @Test
    void theHardCapIs30() {
        EnchantingFixesConfig config = new EnchantingFixesConfig();
        config.maxLevels = Map.of(AdvancedSilkTouch.ID, 100);
        assertEquals(30, MaxLevels.resolve(DEFINITION_MAX_LEVEL, AdvancedSilkTouch.ID, config, false));
    }

    @Test
    void randomLevelsUseLevel3AsTheBase() {
        assertEquals(3, RandomLevels.cap(30, DEFINITION_MAX_LEVEL, Mode.OFF));
        assertEquals(5, RandomLevels.cap(30, DEFINITION_MAX_LEVEL, Mode.BALANCED));
        assertEquals(30, RandomLevels.cap(30, DEFINITION_MAX_LEVEL, Mode.INSANE));
    }

    @Test
    void eachBlockHasItsLevel() {
        assertEquals(0, AdvancedSilkTouch.requiredLevel(Blocks.STONE.defaultBlockState()));
        assertEquals(0, AdvancedSilkTouch.requiredLevel(Blocks.GLASS.defaultBlockState()));
        assertEquals(1, AdvancedSilkTouch.requiredLevel(Blocks.DIRT_PATH.defaultBlockState()));
        assertEquals(1, AdvancedSilkTouch.requiredLevel(Blocks.FARMLAND.defaultBlockState()));
        assertEquals(3, AdvancedSilkTouch.requiredLevel(Blocks.SUSPICIOUS_SAND.defaultBlockState()));
        assertEquals(3, AdvancedSilkTouch.requiredLevel(Blocks.SUSPICIOUS_GRAVEL.defaultBlockState()));
        assertEquals(5, AdvancedSilkTouch.requiredLevel(Blocks.BUDDING_AMETHYST.defaultBlockState()));
        assertEquals(7, AdvancedSilkTouch.requiredLevel(Blocks.INFESTED_STONE.defaultBlockState()));
        assertEquals(7, AdvancedSilkTouch.requiredLevel(Blocks.INFESTED_DEEPSLATE.defaultBlockState()));
        assertEquals(10, AdvancedSilkTouch.requiredLevel(Blocks.SPAWNER.defaultBlockState()));
        assertEquals(15, AdvancedSilkTouch.requiredLevel(Blocks.REINFORCED_DEEPSLATE.defaultBlockState()));
        assertEquals(30, AdvancedSilkTouch.requiredLevel(Blocks.BEDROCK.defaultBlockState()));
    }

    @Test
    void spawnerEntityComesFromTheSpawnData() {
        CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:zombie");
        CompoundTag spawnData = new CompoundTag();
        spawnData.put("entity", entity);
        CompoundTag tag = new CompoundTag();
        tag.put("SpawnData", spawnData);

        assertEquals(Optional.of(EntityTypes.ZOMBIE), AdvancedSilkTouch.spawnerEntity(tag));
        assertEquals(Optional.empty(), AdvancedSilkTouch.spawnerEntity(new CompoundTag()));
    }
}
