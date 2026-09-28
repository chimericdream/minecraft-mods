package com.chimericdream.chimericlib.test.dispenser;

import com.chimericdream.lib.dispenser.BlockTargetDispenseBehavior;
import com.chimericdream.lib.dispenser.DispenserBehaviors;
import com.chimericdream.lib.testkit.BootstrapMinecraft;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Covers {@link DispenserBehaviors#wrap}'s registry bookkeeping: which fallback gets captured and
 * what ends up registered. The dispatch itself needs a live {@code ServerLevel}, so it's covered by
 * consumer mods' GameTests (e.g. all-hallows-steve's {@code DispenserPumpkinGameTest}).
 */
public class DispenserBehaviorsTest extends BootstrapMinecraft {
    private static final BlockTargetDispenseBehavior.Handler PASS_ALL =
        (level, target, state, stack, source) -> BlockTargetDispenseBehavior.Result.PASS;

    private static final List<Item> TOUCHED = List.of(Items.SHEARS, Items.DIAMOND_HELMET, Items.TORCH, Items.SOUL_TORCH);

    private final Map<Item, DispenseItemBehavior> saved = new HashMap<>();

    @BeforeEach
    void snapshotRegistry() {
        saved.clear();
        for (Item item : TOUCHED) {
            saved.put(item, DispenserBlock.DISPENSER_REGISTRY.get(item));
        }
    }

    @AfterEach
    void restoreRegistry() {
        saved.forEach((item, behavior) -> {
            if (behavior == null) {
                DispenserBlock.DISPENSER_REGISTRY.remove(item);
            } else {
                DispenserBlock.DISPENSER_REGISTRY.put(item, behavior);
            }
        });
    }

    @Test
    void wrapRegistersTheWrapperAndKeepsThePreviousBehaviorAsFallback() {
        DispenseItemBehavior vanillaShears = DispenserBlock.DISPENSER_REGISTRY.get(Items.SHEARS);
        assertNotNull(vanillaShears);

        BlockTargetDispenseBehavior wrapper = DispenserBehaviors.wrap(Items.SHEARS, PASS_ALL);

        assertSame(wrapper, DispenserBlock.DISPENSER_REGISTRY.get(Items.SHEARS));
        assertSame(vanillaShears, wrapper.fallbackFor(new ItemStack(Items.SHEARS)));
    }

    @Test
    void wrapWithNoPreviousBehaviorKeepsVanillaPerStackDefault() {
        // Armor has no registry entry; vanilla picks EquipmentDispenseItemBehavior per stack via
        // DispenserBlock#getDefaultDispenseMethod, which the fallback must preserve. (The plain
        // "drop the item" branch can't be checked here: it sits behind an item-tag check, and tags
        // aren't bound in a headless JUnit bootstrap. Consumer GameTests cover it instead.)
        BlockTargetDispenseBehavior wrapper = DispenserBehaviors.wrap(Items.DIAMOND_HELMET, PASS_ALL);

        assertInstanceOf(EquipmentDispenseItemBehavior.class, wrapper.fallbackFor(new ItemStack(Items.DIAMOND_HELMET)));
    }

    @Test
    void wrappingTwiceChainsNewestFirst() {
        BlockTargetDispenseBehavior first = DispenserBehaviors.wrap(Items.SHEARS, PASS_ALL);
        BlockTargetDispenseBehavior second = DispenserBehaviors.wrap(Items.SHEARS, PASS_ALL);

        assertSame(second, DispenserBlock.DISPENSER_REGISTRY.get(Items.SHEARS));
        assertSame(first, second.fallbackFor(new ItemStack(Items.SHEARS)));
    }

    @Test
    void wrapCollectionRegistersEachItem() {
        DispenserBehaviors.wrap(List.of(Items.TORCH, Items.SOUL_TORCH), PASS_ALL);

        assertInstanceOf(BlockTargetDispenseBehavior.class, DispenserBlock.DISPENSER_REGISTRY.get(Items.TORCH));
        assertInstanceOf(BlockTargetDispenseBehavior.class, DispenserBlock.DISPENSER_REGISTRY.get(Items.SOUL_TORCH));
    }
}
