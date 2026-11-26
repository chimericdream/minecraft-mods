package com.chimericdream.enchantingfixes.book;

import com.chimericdream.lib.commands.ChimericCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import static net.minecraft.commands.Commands.literal;

/**
 * {@code /enchfix fixbooks}. Removes the repair cost from the enchanted books in the inventory of the player, so that
 * they stack again. See {@link EnchantedBooks#fixRepairCost}.
 */
public class FixBooksCommand implements ChimericCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return literal("enchfix").then(literal("fixbooks").executes(this::fixBooks));
    }

    private int fixBooks(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Inventory inventory = player.getInventory();
        HolderGetter<Enchantment> enchantments = ctx.getSource().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        int fixed = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (EnchantedBooks.fixRepairCost(stack, enchantments)) {
                fixed += stack.getCount();
            }
        }

        if (fixed == 0) {
            ctx.getSource().sendSuccess(() -> Component.translatable("message.enchantingfixes.fixbooks.none"), false);
            return 0;
        }

        inventory.setChanged();
        int total = fixed;
        ctx.getSource().sendSuccess(
            () -> Component.translatable("message.enchantingfixes.fixbooks.fixed", total).withStyle(ChatFormatting.GOLD),
            false
        );
        return fixed;
    }
}
