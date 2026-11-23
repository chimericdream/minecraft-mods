package com.chimericdream.pannotiacompanion.villager;

import com.chimericdream.lib.commands.ChimericCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

import static net.minecraft.commands.Commands.literal;

/**
 * {@code /pc fixheads}. Gives the right name to mob and streamer heads that a player got before the custom-name change.
 * See {@link PannotiaHeads#fixName}.
 */
public class PcFixHeadsCommand implements ChimericCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return literal("pc").then(literal("fixheads").executes(this::fixHeads));
    }

    private int fixHeads(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Inventory inventory = player.getInventory();

        int fixed = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (PannotiaHeads.fixName(inventory.getItem(slot))) {
                fixed++;
            }
        }

        if (fixed == 0) {
            ctx.getSource().sendSuccess(() -> Component.translatable("message.pannotiacompanion.fixheads.none"), false);
            return 0;
        }

        inventory.setChanged();
        int total = fixed;
        ctx.getSource().sendSuccess(
            () -> Component.translatable("message.pannotiacompanion.fixheads.fixed", total).withStyle(ChatFormatting.GOLD),
            false
        );
        return fixed;
    }
}
