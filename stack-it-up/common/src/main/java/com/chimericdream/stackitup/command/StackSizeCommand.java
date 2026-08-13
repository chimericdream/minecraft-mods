package com.chimericdream.stackitup.command;

import java.util.LinkedList;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.chimericdream.lib.commands.ChimericCommand;
import com.chimericdream.stackitup.config.ConfigManager;
import com.chimericdream.stackitup.util.ItemsHelper;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class StackSizeCommand implements ChimericCommand {
    private static final ItemsHelper itemsHelper = ItemsHelper.getItemsHelper();
    private static final ConfigManager configManager = ConfigManager.getConfigManager();

    private static int showItem(CommandSourceStack source, Item item) {
        source.sendSuccess(() -> Component.translatable("stackitup.command.show_item",
                Component.translatable(item.getDescriptionId()),
                itemsHelper.getCurrentCount(item),
                itemsHelper.getDefaultCount(item)), false);
        return 1;
    }

    private static int showAll(CommandSourceStack source) {
        LinkedList<Item> list = itemsHelper.getAllModifiedItems();
        if (list.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("stackitup.command.show_none"), false);
        }
        for (Item item : list) {
            source.sendSuccess(() -> Component.translatable("stackitup.command.show_item",
                    Component.translatable(item.getDescriptionId()),
                    itemsHelper.getCurrentCount(item),
                    itemsHelper.getDefaultCount(item)), false);
        }
        return 1;
    }

    private static int showItemOnHand(CommandSourceStack source, ServerPlayer serverPlayerEntity) throws CommandSyntaxException {
        Item item = getMainHandItem(source, serverPlayerEntity);
        if (item == null) {
            return 0;
        }
        return showItem(source, item);
    }

    private static int setItem(CommandSourceStack source, Item item, int count) {
        itemsHelper.setSingle(item, count);
        configManager.syncConfig();
        source.sendSuccess(() -> Component.translatable("stackitup.command.set_item",
                Component.translatable(item.getDescriptionId()),
                count,
                itemsHelper.getDefaultCount(item)), true);
        return 1;
    }

    private static int setItemOnHand(CommandSourceStack source, ServerPlayer serverPlayerEntity, int count) throws CommandSyntaxException {
        Item item = getMainHandItem(source, serverPlayerEntity);
        if (item == null) {
            return 0;
        }
        return setItem(source, item, count);
    }

    private static int setMatched(CommandSourceStack source, String type, int originalSize, int newSize) {
        int count = itemsHelper.setMatchedItems(originalSize, newSize, type);
        configManager.syncConfig();
        source.sendSuccess(() ->
                        Component.translatable(
                                "stackitup.command.set_matched",
                                count,
                                newSize,
                                type.equals("vanilla") ? Component.translatable("stackitup.command.default") : Component.translatable("stackitup.command.previous"),
                                originalSize
                        ),
                true
        );
        return 1;
    }

    private static int resetItem(CommandSourceStack source, Item item) {
        itemsHelper.resetItem(item);
        configManager.syncConfig();
        source.sendSuccess(() -> Component.translatable("stackitup.command.reset_item",
                Component.translatable(item.getDescriptionId())), true);
        return 1;
    }

    private static int resetAllItems(CommandSourceStack source) {
        itemsHelper.resetAll(true);
        configManager.resetAllItems();
        source.sendSuccess(() -> Component.translatable("stackitup.command.reset_all"), true);
        return 1;
    }

    private static int resetItemOnHand(CommandSourceStack source, ServerPlayer serverPlayerEntity) throws CommandSyntaxException {
        Item item = getMainHandItem(source, serverPlayerEntity);
        if (item == null) {
            return 0;
        }
        return resetItem(source, item);
    }

    private static Item getMainHandItem(CommandSourceStack source, ServerPlayer serverPlayerEntity) throws CommandSyntaxException {
        ItemStack itemStack = serverPlayerEntity.getMainHandItem();
        if (itemStack.isEmpty()) {
            String u1 = serverPlayerEntity.getName().getContents().toString();
            String u2 = source.getEntity() instanceof ServerPlayer ? source.getTextName() : "Server";

            source.sendFailure(Component.translatable("stackitup.command.error_empty_hand", u1.equals(u2) ? Component.translatable("stackitup.command.you") : serverPlayerEntity.getName()));
            return null;
        }
        return itemStack.getItem();
    }

    private static int reloadConfig(CommandSourceStack source) {
        configManager.setupConfig();
        source.sendSuccess(() -> {
            try {
                return Component.translatable("stackitup.command.reloaded", source.getPlayerOrException().getName());
            } catch (CommandSyntaxException e) {
                return Component.nullToEmpty(e.getMessage());
            }
        }, true);
        return 1;
    }

    private static int updateGlobalConfig(CommandSourceStack source, boolean allowAutoApply, boolean updateStackableList) {
        configManager.updateGlobalConfig(updateStackableList, allowAutoApply);
        source.sendSuccess(() -> Component.translatable("stackitup.command.updated_glob_conf"), true);
        return 1;
    }

    private static int fromGlobal(CommandSourceStack source) {
        configManager.applyGlobalToLocal();
        source.sendSuccess(() -> Component.translatable("stackitup.command.from_global"), true);
        return 1;
    }

    private static int restore(CommandSourceStack source) {
        if (configManager.restoreBackup()) {
            source.sendSuccess(() -> Component.translatable("stackitup.command.restored"), true);
            return 1;
        } else {
            source.sendFailure(Component.translatable("stackitup.command.nobk"));
            return 0;
        }
    }

    private static int help(CommandSourceStack source) {
        source.sendSuccess(() -> Component.translatable("stackitup.command.help"), false);
        return 1;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return literal("stackitup")
                .requires(source -> source.permissions().hasPermission(
                        new Permission.HasCommandLevel(PermissionLevel.byId(configManager.getRuleSetting("permissionLevel")))))
                .then(literal("help").executes(ctx -> help(ctx.getSource())))
                .then(literal("show")
                        .then(literal("hand")
                                .then(argument("targets", EntityArgument.player())
                                        .executes(ctx -> showItemOnHand(ctx.getSource(), EntityArgument.getPlayer(ctx, "targets"))))
                        )
                        .then(literal("all")
                                .executes(ctx -> showAll(ctx.getSource()))
                        )
                        .then(argument("item", ItemArgument.item(context))
                                .executes(ctx -> showItem(ctx.getSource(), ItemArgument.getItem(ctx, "item").item().value()))
                        )

                )
                .then(literal("reset")
                        .then(literal("hand")
                                .then(argument("targets", EntityArgument.player())
                                        .executes(ctx -> resetItemOnHand(ctx.getSource(), EntityArgument.getPlayer(ctx, "targets"))))
                        )
                        .then(literal("all")
                                .executes(ctx -> resetAllItems(ctx.getSource()))
                        )
                        .then(argument("item", ItemArgument.item(context))
                                .executes(ctx -> resetItem(ctx.getSource(), ItemArgument.getItem(ctx, "item").item().value()))
                        )
                )
                .then(literal("set")
                        .then(argument("item", ItemArgument.item(context))
                                .then(argument("count", IntegerArgumentType.integer(1, ItemsHelper.ItemMaxCount))
                                        .executes(ctx -> setItem(
                                                ctx.getSource(),
                                                ItemArgument.getItem(ctx, "item").item().value(),
                                                IntegerArgumentType.getInteger(ctx, "count"))))
                        )
                        .then(literal("hand")
                                .then(argument("targets", EntityArgument.player())
                                        .then(argument("count", IntegerArgumentType.integer(1, ItemsHelper.ItemMaxCount))
                                                .executes(ctx -> setItemOnHand(
                                                        ctx.getSource(),
                                                        EntityArgument.getPlayer(ctx, "targets"),
                                                        IntegerArgumentType.getInteger(ctx, "count"))))
                                )
                        )
                        .then(literal("vanilla")
                                .then(argument("vanillaSize", IntegerArgumentType.integer(1, ItemsHelper.ItemMaxCount))
                                        .then(argument("customSize", IntegerArgumentType.integer(1, ItemsHelper.ItemMaxCount))
                                                .executes(ctx -> setMatched(
                                                        ctx.getSource(),
                                                        "vanilla",
                                                        IntegerArgumentType.getInteger(ctx, "vanillaSize"),
                                                        IntegerArgumentType.getInteger(ctx, "customSize")
                                                ))
                                        )
                                )
                        )
                        .then(literal("modified")
                                .then(argument("previousSize", IntegerArgumentType.integer(1, ItemsHelper.ItemMaxCount))
                                        .then(argument("newSize", IntegerArgumentType.integer(1, ItemsHelper.ItemMaxCount))
                                                .executes(ctx -> setMatched(
                                                        ctx.getSource(),
                                                        "modified",
                                                        IntegerArgumentType.getInteger(ctx, "previousSize"),
                                                        IntegerArgumentType.getInteger(ctx, "newSize")
                                                ))
                                        )
                                )
                        )
                        .then(literal("all")
                                .then(argument("previousSize", IntegerArgumentType.integer(1, ItemsHelper.ItemMaxCount))
                                        .then(argument("newSize", IntegerArgumentType.integer(1, ItemsHelper.ItemMaxCount))
                                                .executes(ctx -> setMatched(
                                                        ctx.getSource(),
                                                        "all",
                                                        IntegerArgumentType.getInteger(ctx, "previousSize"),
                                                        IntegerArgumentType.getInteger(ctx, "newSize")
                                                ))
                                        )
                                )
                        )
                )
                .then(literal("config")
                        .then(literal("reload")
                                .executes(ctx -> reloadConfig(ctx.getSource()))
                        )
                        .then(literal("loadFromGlobal")
                                .executes(ctx -> fromGlobal(ctx.getSource()))
                        )
                        .then(literal("saveToGlobal")
                                .executes(ctx -> updateGlobalConfig(ctx.getSource(), false, true))
                        )
                        .then(literal("globalConfigAutoApply")
                                .then(literal("true").executes(ctx -> updateGlobalConfig(ctx.getSource(), true, false)))
                                .then(literal("false").executes(ctx -> updateGlobalConfig(ctx.getSource(), false, false)))
                        )
                        .then(literal("restore")
                                .executes(ctx -> restore(ctx.getSource()))
                        )
                );
    }
}
