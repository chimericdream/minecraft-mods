package com.chimericdream.shulkerstuff.fabric.test;

import com.chimericdream.shulkerstuff.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Vacuum used to write the updated container contents onto the whole stack of shulker boxes, so
 * with stackable boxes (e.g. Carpet Mod) every box in the stack received a copy of the vacuumed
 * items.
 */
@SuppressWarnings("unused")
public class VacuumEnchantmentGameTest {
    private static final int BOX_SLOT = 0;

    private static ItemStack vacuumBox(GameTestHelper context, int count, int level) {
        Holder<Enchantment> vacuum = context.getLevel()
            .registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(ModEnchantments.VACUUM);

        ItemStack box = new ItemStack(Items.SHULKER_BOX);
        box.set(DataComponents.MAX_STACK_SIZE, 16);
        box.setCount(count);
        box.enchant(vacuum, level);

        return box;
    }

    private static int itemsInBox(ItemStack box) {
        return box.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
            .nonEmptyItemCopyStream()
            .mapToInt(ItemStack::getCount)
            .sum();
    }

    private static int itemsInInventory(ServerPlayer player, ItemStack of) {
        int total = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(of.getItem())) {
                total += stack.getCount();
            }
        }

        return total;
    }

    /** A lone Vacuum box still absorbs picked-up items. */
    @GameTest
    public void singleVacuumBoxAbsorbsPickedUpItems(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        ItemStack box = vacuumBox(context, 1, 2);
        player.getInventory().setItem(BOX_SLOT, box);

        player.getInventory().add(new ItemStack(Items.COBBLESTONE, 5));

        if (itemsInBox(box) != 5) {
            context.fail("Expected the box to hold 5 cobblestone, got " + itemsInBox(box));
        }

        context.succeed();
    }

    /** A stack of Vacuum boxes must leave the items to the rest of the inventory, not copy them. */
    @GameTest
    public void stackedVacuumBoxesDoNotDuplicateItems(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        ItemStack boxes = vacuumBox(context, 2, 2);
        player.getInventory().setItem(BOX_SLOT, boxes);

        ItemStack pickup = new ItemStack(Items.COBBLESTONE, 5);
        player.getInventory().add(pickup);

        if (itemsInBox(boxes) != 0) {
            context.fail("A stacked Vacuum box should be skipped, but it holds " + itemsInBox(boxes) + " item(s)");
        }

        int inInventory = itemsInInventory(player, new ItemStack(Items.COBBLESTONE));
        if (inInventory != 5) {
            context.fail("Expected the 5 cobblestone to land in the inventory, found " + inInventory);
        }

        context.succeed();
    }

    /** A skipped stack must not block a later, unstacked Vacuum box from collecting the items. */
    @GameTest
    public void unstackedVacuumBoxStillWorksAfterAStackedOne(GameTestHelper context) {
        ServerPlayer player = context.makeMockServerPlayerInLevel();
        ItemStack stacked = vacuumBox(context, 2, 2);
        ItemStack single = vacuumBox(context, 1, 2);
        player.getInventory().setItem(BOX_SLOT, stacked);
        player.getInventory().setItem(BOX_SLOT + 1, single);

        player.getInventory().add(new ItemStack(Items.COBBLESTONE, 5));

        if (itemsInBox(stacked) != 0) {
            context.fail("The stacked box should be untouched, but it holds " + itemsInBox(stacked) + " item(s)");
        }
        if (itemsInBox(single) != 5) {
            context.fail("Expected the single box to hold 5 cobblestone, got " + itemsInBox(single));
        }

        context.succeed();
    }
}
