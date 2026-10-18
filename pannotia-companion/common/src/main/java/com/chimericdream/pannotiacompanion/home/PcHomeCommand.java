package com.chimericdream.pannotiacompanion.home;

import com.chimericdream.lib.commands.ChimericCommand;
import com.chimericdream.pannotiacompanion.config.PannotiaCompanionConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /pc home}, {@code /pc homes ...} and {@code /pc spawn}. Replaces the Homes and Spawn Vanilla
 * Tweaks datapacks. Several {@link ChimericCommand}s may share the {@code pc} root; Brigadier merges them.
 */
public class PcHomeCommand implements ChimericCommand {
    private static final SuggestionProvider<CommandSourceStack> HOME_NAMES = (ctx, builder) -> {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player != null) {
            HomeBook book = HomeData.get(ctx.getSource().getServer()).book(player.getUUID());
            return SharedSuggestionProvider.suggest(
                book.homes().stream().map(h -> h.name().contains(" ") ? "\"" + h.name() + "\"" : h.name()),
                builder
            );
        }

        return builder.buildFuture();
    };

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return literal("pc")
            .then(literal("home")
                .executes(ctx -> goHome(ctx, null))
                .then(argument("name", StringArgumentType.string())
                    .suggests(HOME_NAMES)
                    .executes(ctx -> goHome(ctx, StringArgumentType.getString(ctx, "name")))))
            .then(literal("homes")
                .executes(this::listHomes)
                .then(literal("list").executes(this::listHomes))
                .then(literal("set")
                    .executes(ctx -> setHome(ctx, HomeBook.DEFAULT_NAME))
                    .then(argument("name", StringArgumentType.string())
                        .suggests(HOME_NAMES)
                        .executes(ctx -> setHome(ctx, StringArgumentType.getString(ctx, "name")))))
                .then(literal("delete")
                    .then(argument("name", StringArgumentType.string())
                        .suggests(HOME_NAMES)
                        .executes(ctx -> deleteHome(ctx, StringArgumentType.getString(ctx, "name")))))
                .then(literal("rename")
                    .then(argument("old", StringArgumentType.string())
                        .suggests(HOME_NAMES)
                        .then(argument("new", StringArgumentType.string())
                            .executes(ctx -> renameHome(ctx, StringArgumentType.getString(ctx, "old"), StringArgumentType.getString(ctx, "new")))))))
            .then(literal("spawn").executes(this::goSpawn));
    }

    private static HomeBook book(CommandContext<CommandSourceStack> ctx, ServerPlayer player) {
        return HomeData.get(ctx.getSource().getServer()).book(player.getUUID());
    }

    private static Component error(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.RED);
    }

    private static Component ok(String key, Object... args) {
        return Component.translatable(key, args).withStyle(ChatFormatting.GOLD);
    }

    private static Component homeName(String name) {
        return Component.literal(name).withStyle(ChatFormatting.YELLOW);
    }

    private int setHome(CommandContext<CommandSourceStack> ctx, String name) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();

        if (!HomeBook.isValidName(name)) {
            ctx.getSource().sendFailure(error("message.pannotiacompanion.home.bad_name", HomeBook.MAX_NAME_LENGTH));
            return 0;
        }

        HomeData data = HomeData.get(ctx.getSource().getServer());
        HomeBook book = data.book(player.getUUID());
        int limit = PannotiaCompanionConfig.get().maxHomesPerDimension;
        Home home = new Home(name.trim(), player.level().dimension(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());

        HomeBook.SetResult result = book.set(home, limit);
        if (result == HomeBook.SetResult.LIMIT_REACHED) {
            ctx.getSource().sendFailure(error("message.pannotiacompanion.home.limit", limit));
            return 0;
        }

        data.changed();
        ctx.getSource().sendSuccess(() -> ok(
            result == HomeBook.SetResult.ADDED ? "message.pannotiacompanion.home.added" : "message.pannotiacompanion.home.replaced",
            homeName(home.name())
        ), false);
        return 1;
    }

    private int goHome(CommandContext<CommandSourceStack> ctx, String query) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        HomeBook book = book(ctx, player);

        if (book.isEmpty()) {
            ctx.getSource().sendFailure(error("message.pannotiacompanion.home.none"));
            return 0;
        }

        Optional<Home> found = query == null ? book.resolveDefault() : book.resolve(query);
        if (found.isEmpty()) {
            ctx.getSource().sendFailure(query == null
                ? error("message.pannotiacompanion.home.choose")
                : error("message.pannotiacompanion.home.not_found", query));
            return 0;
        }

        Home home = found.get();
        MinecraftServer server = ctx.getSource().getServer();
        ServerLevel target = server.getLevel(home.dimension());
        if (target == null) {
            ctx.getSource().sendFailure(error("message.pannotiacompanion.home.dimension_missing", home.dimension().identifier().toString()));
            return 0;
        }

        PannotiaCompanionConfig config = PannotiaCompanionConfig.get();
        boolean started = TeleportManager.request(player, TeleportManager.HOME_COOLDOWNS, config.homeDelayTicks, config.homeCooldownSeconds, p -> {
            p.teleportTo(target, home.x(), home.y(), home.z(), Set.of(), home.yRot(), home.xRot(), true);
            p.sendSystemMessage(ok("message.pannotiacompanion.home.teleported", homeName(home.name())));
        });
        return started ? 1 : 0;
    }

    private int listHomes(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        List<Home> homes = book(ctx, player).homes();

        if (homes.isEmpty()) {
            ctx.getSource().sendSuccess(() -> ok("message.pannotiacompanion.home.none"), false);
            return 0;
        }

        ctx.getSource().sendSuccess(() -> ok("message.pannotiacompanion.home.list_header", homes.size()), false);
        for (int i = 0; i < homes.size(); i++) {
            Home home = homes.get(i);
            int id = i + 1;
            String quoted = "\"" + home.name().replace("\"", "\\\"") + "\"";

            MutableComponent line = Component.literal(id + ". ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(home.name()).withStyle(style -> style
                    .withColor(ChatFormatting.YELLOW)
                    .withClickEvent(new ClickEvent.RunCommand("/pc home " + quoted))
                    .withHoverEvent(new HoverEvent.ShowText(Component.translatable("message.pannotiacompanion.home.click_to_teleport")))))
                .append(Component.literal(" (" + home.dimension().identifier() + " " + (int) home.x() + ", " + (int) home.y() + ", " + (int) home.z() + ")")
                    .withStyle(ChatFormatting.DARK_GRAY));
            ctx.getSource().sendSuccess(() -> line, false);
        }

        return homes.size();
    }

    private int deleteHome(CommandContext<CommandSourceStack> ctx, String query) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        HomeData data = HomeData.get(ctx.getSource().getServer());

        Optional<Home> removed = data.book(player.getUUID()).remove(query);
        if (removed.isEmpty()) {
            ctx.getSource().sendFailure(error("message.pannotiacompanion.home.not_found", query));
            return 0;
        }

        data.changed();
        ctx.getSource().sendSuccess(() -> ok("message.pannotiacompanion.home.deleted", homeName(removed.get().name())), false);
        return 1;
    }

    private int renameHome(CommandContext<CommandSourceStack> ctx, String oldName, String newName) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();

        if (!HomeBook.isValidName(newName)) {
            ctx.getSource().sendFailure(error("message.pannotiacompanion.home.bad_name", HomeBook.MAX_NAME_LENGTH));
            return 0;
        }

        HomeData data = HomeData.get(ctx.getSource().getServer());
        HomeBook.RenameResult result = data.book(player.getUUID()).rename(oldName, newName.trim());
        switch (result) {
            case NOT_FOUND -> {
                ctx.getSource().sendFailure(error("message.pannotiacompanion.home.not_found", oldName));
                return 0;
            }
            case NAME_TAKEN -> {
                ctx.getSource().sendFailure(error("message.pannotiacompanion.home.name_taken", newName));
                return 0;
            }
            default -> {
                data.changed();
                ctx.getSource().sendSuccess(() -> ok("message.pannotiacompanion.home.renamed", homeName(oldName), homeName(newName.trim())), false);
                return 1;
            }
        }
    }

    private int goSpawn(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        MinecraftServer server = ctx.getSource().getServer();

        PannotiaCompanionConfig config = PannotiaCompanionConfig.get();
        boolean started = TeleportManager.request(player, TeleportManager.SPAWN_COOLDOWNS, config.spawnDelayTicks, config.spawnCooldownSeconds, p -> {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            BlockPos spawn = overworld.getRespawnData().pos();
            BlockPos landing = p.adjustSpawnLocation(overworld, spawn);
            p.teleportTo(overworld, landing.getX() + 0.5, landing.getY(), landing.getZ() + 0.5, Set.of(), p.getYRot(), p.getXRot(), true);
            p.sendSystemMessage(ok("message.pannotiacompanion.spawn.teleported"));
        });
        return started ? 1 : 0;
    }
}
