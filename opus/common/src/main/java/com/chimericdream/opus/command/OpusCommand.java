package com.chimericdream.opus.command;

import com.chimericdream.opus.component.BookIdComponent;
import com.chimericdream.opus.component.OpusComponentTypes;
import com.chimericdream.opus.config.OpusGuidesConfig;
import com.chimericdream.opus.item.OpusItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /opus give [<book>]}: hands the running player an Opus book. Open to everyone unless
 * {@code opsOnly} is set in {@code opus-guides.json}; the check lives in the permission requirement so it is
 * enforced on the server. The server cannot know which books exist (they are client-side resources), so any
 * well-formed id is accepted.
 */
public final class OpusCommand {
    private OpusCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("opus")
            .then(literal("give")
                .requires(source -> !OpusGuidesConfig.opsOnly() || source.permissions().hasPermission(
                    new net.minecraft.server.permissions.Permission.HasCommandLevel(net.minecraft.server.permissions.PermissionLevel.GAMEMASTERS)))
                .executes(ctx -> give(ctx, OpusGuidesConfig.defaultGuide()))
                .then(argument("book", IdentifierArgument.id())
                    .executes(ctx -> give(ctx, ctx.getArgument("book", Identifier.class).toString())))));
    }

    private static int give(CommandContext<CommandSourceStack> ctx, String bookId) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();

        ItemStack stack = new ItemStack(OpusItems.BOOK.get());
        stack.set(OpusComponentTypes.BOOK_ID.get(), new BookIdComponent(bookId));

        if (!player.getInventory().add(stack)) {
            ItemEntity drop = player.drop(stack, false);
            if (drop != null) {
                drop.setNoPickUpDelay();
                drop.setTarget(player.getUUID());
            }
        }

        ctx.getSource().sendSuccess(() -> Component.translatable("opus.command.give", bookId), false);
        return 1;
    }
}
