package com.chimericdream.polycraft.command;

import com.chimericdream.polycraft.config.PolyCraftConfig;
import com.chimericdream.polycraft.core.ConflictDetector;
import com.chimericdream.polycraft.core.RecipeInfo;
import com.chimericdream.polycraft.data.PolyCraftChoices;
import com.chimericdream.polycraft.recipe.RecipeIndex;
import com.chimericdream.polycraft.rules.PolyCraftRules;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /polycraft}: {@code conflicts [mod]} lists the recipe conflicts PolyCraft found (anyone); {@code reset}
 * forgets your own remembered choices (anyone); {@code export}, {@code reload} and {@code reset <player>} need
 * operator permission. The conflict list covers conflicts that can be worked out from the recipes alone, which is
 * identical-input ones; overlapping inputs depend on what is actually in a station.
 */
public final class PolyCraftCommand {
    private static final int LISTED_LINES = 20;

    private PolyCraftCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("polycraft")
            .then(literal("conflicts")
                .executes(ctx -> conflicts(ctx, null))
                .then(argument("mod", StringArgumentType.word())
                    .executes(ctx -> conflicts(ctx, StringArgumentType.getString(ctx, "mod")))))
            .then(literal("export")
                .requires(PolyCraftCommand::isOperator)
                .executes(PolyCraftCommand::export))
            .then(literal("reload")
                .requires(PolyCraftCommand::isOperator)
                .executes(PolyCraftCommand::reload))
            .then(literal("reset")
                .executes(PolyCraftCommand::resetSelf)
                .then(argument("player", EntityArgument.player())
                    .requires(PolyCraftCommand::isOperator)
                    .executes(PolyCraftCommand::resetOther))));
    }

    private static boolean isOperator(CommandSourceStack source) {
        return source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS));
    }

    private static List<ConflictDetector.Conflict> findConflicts(CommandContext<CommandSourceStack> ctx) {
        RecipeIndex index = RecipeIndex.of(ctx.getSource().getServer().getRecipeManager());
        return index.allConflicts(PolyCraftConfig.get().recipeTypes(), PolyCraftRules.current()::isExcluded);
    }

    private static int conflicts(CommandContext<CommandSourceStack> ctx, String mod) {
        List<ConflictDetector.Conflict> all = findConflicts(ctx);
        List<ConflictDetector.Conflict> shown = mod == null
            ? all
            : all.stream().filter(conflict -> conflict.options().stream().anyMatch(option -> option.id().startsWith(mod + ":"))).toList();

        CommandSourceStack source = ctx.getSource();
        if (shown.isEmpty()) {
            source.sendSuccess(() -> Component.translatable(mod == null ? "polycraft.command.conflicts.none" : "polycraft.command.conflicts.none_for_mod", mod), false);
            return 0;
        }

        source.sendSuccess(() -> Component.translatable("polycraft.command.conflicts.header", shown.size()), false);
        for (ConflictDetector.Conflict conflict : shown.subList(0, Math.min(LISTED_LINES, shown.size()))) {
            String options = String.join(", ", conflict.optionIds());
            source.sendSuccess(() -> Component.translatable("polycraft.command.conflicts.line", conflict.options().size(), options), false);
        }

        if (shown.size() > LISTED_LINES) {
            int more = shown.size() - LISTED_LINES;
            source.sendSuccess(() -> Component.translatable("polycraft.command.conflicts.more", more), false);
        }

        return shown.size();
    }

    private static int export(CommandContext<CommandSourceStack> ctx) {
        List<ConflictDetector.Conflict> all = findConflicts(ctx);

        JsonArray groups = new JsonArray();
        for (ConflictDetector.Conflict conflict : all) {
            JsonObject group = new JsonObject();
            group.addProperty("key", conflict.groupKey());
            JsonArray options = new JsonArray();
            for (RecipeInfo option : conflict.options()) {
                JsonObject entry = new JsonObject();
                entry.addProperty("recipe", option.id());
                entry.addProperty("result", option.resultKey());
                entry.addProperty("count", option.resultCount());
                options.add(entry);
            }

            group.add("options", options);
            groups.add(group);
        }

        JsonObject root = new JsonObject();
        root.add("groups", groups);

        Path file = PolyCraftRules.configFile().resolveSibling("conflicts.json");
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, new GsonBuilder().setPrettyPrinting().create().toJson(root) + "\n", StandardCharsets.UTF_8);
        } catch (IOException e) {
            ctx.getSource().sendFailure(Component.translatable("polycraft.command.export.failed", e.getMessage()));
            return 0;
        }

        ctx.getSource().sendSuccess(() -> Component.translatable("polycraft.command.export.done", all.size(), file.toString()), true);
        return all.size();
    }

    private static int reload(CommandContext<CommandSourceStack> ctx) {
        PolyCraftRules.reloadConfigFile();
        ctx.getSource().sendSuccess(() -> Component.translatable("polycraft.command.reload.done"), true);
        return 1;
    }

    private static int resetSelf(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int removed = PolyCraftChoices.get(ctx.getSource().getServer()).reset(player.getUUID());
        ctx.getSource().sendSuccess(() -> Component.translatable("polycraft.command.reset.self", removed), false);
        return removed;
    }

    private static int resetOther(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
        int removed = PolyCraftChoices.get(ctx.getSource().getServer()).reset(target.getUUID());
        ctx.getSource().sendSuccess(() -> Component.translatable("polycraft.command.reset.other", target.getDisplayName(), removed), true);
        return removed;
    }
}
