package com.chimericdream.rulesforthee.command;

import com.chimericdream.lib.commands.ChimericCommand;
import com.chimericdream.rulesforthee.data.PlayerEntry;
import com.chimericdream.rulesforthee.data.RulesData;
import com.chimericdream.rulesforthee.notify.RuleNotices;
import com.chimericdream.rulesforthee.notify.RuleNotifier;
import com.chimericdream.rulesforthee.rule.EffectiveRules;
import com.chimericdream.rulesforthee.rule.RuleOverrides;
import com.chimericdream.rulesforthee.rule.SupportedRules;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * {@code /rulesforthee}: manage profiles and per-player game rule overrides. Everything except {@code mine}
 * needs gamemaster permission, like {@code /gamerule}.
 *
 * <p>Output is literal text rather than translation keys, since the mod is server-side only and a vanilla
 * client can't translate keys it doesn't know.
 */
public class RulesForTheeCommand implements ChimericCommand {
    private static final Predicate<CommandSourceStack> OPS = Commands.hasPermission(Commands.LEVEL_GAMEMASTERS);

    private static final SuggestionProvider<CommandSourceStack> PROFILE_NAMES = (ctx, builder) ->
        SharedSuggestionProvider.suggest(RulesData.get(ctx.getSource().getServer()).profileNames(), builder);

    private final String root;

    public RulesForTheeCommand(String root) {
        this.root = root;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        LiteralArgumentBuilder<CommandSourceStack> profileSet = literal("set");
        LiteralArgumentBuilder<CommandSourceStack> profileClear = literal("clear");
        LiteralArgumentBuilder<CommandSourceStack> playerSet = literal("set");
        LiteralArgumentBuilder<CommandSourceStack> playerClear = literal("clear");

        for (GameRule<?> rule : SupportedRules.all()) {
            profileSet.then(setOnProfile(rule));
            profileClear.then(clearOnProfile(rule));
            playerSet.then(setOnPlayer(rule));
            playerClear.then(clearOnPlayer(rule));
        }

        return literal(root)
            .then(literal("mine").executes(this::mine))
            .then(literal("rules").requires(OPS).executes(this::listRules))
            .then(literal("profile").requires(OPS)
                .then(literal("list").executes(this::listProfiles))
                .then(literal("create")
                    .then(argument("name", StringArgumentType.word())
                        .executes(ctx -> createProfile(ctx, StringArgumentType.getString(ctx, "name")))))
                .then(literal("delete")
                    .then(argument("name", StringArgumentType.word()).suggests(PROFILE_NAMES)
                        .executes(ctx -> deleteProfile(ctx, StringArgumentType.getString(ctx, "name")))))
                .then(literal("show")
                    .then(argument("name", StringArgumentType.word()).suggests(PROFILE_NAMES)
                        .executes(ctx -> showProfile(ctx, StringArgumentType.getString(ctx, "name")))))
                .then(literal("edit")
                    .then(argument("name", StringArgumentType.word()).suggests(PROFILE_NAMES)
                        .then(profileSet)
                        .then(profileClear))))
            .then(literal("player").requires(OPS)
                .then(argument("player", GameProfileArgument.gameProfile())
                    .then(literal("show").executes(this::showPlayer))
                    .then(literal("profile")
                        .then(literal("set")
                            .then(argument("name", StringArgumentType.word()).suggests(PROFILE_NAMES)
                                .executes(ctx -> setPlayerProfile(ctx, StringArgumentType.getString(ctx, "name")))))
                        .then(literal("clear").executes(ctx -> setPlayerProfile(ctx, null))))
                    .then(playerSet)
                    .then(playerClear)));
    }

    // ---- value branches, built once per supported rule so each gets its own typed value argument ----

    private static <T> LiteralArgumentBuilder<CommandSourceStack> setOnProfile(GameRule<T> rule) {
        return literal(SupportedRules.nameOf(rule)).then(argument("value", rule.argument()).executes(ctx ->
            editProfile(ctx, profile -> profile.set(rule, ctx.getArgument("value", rule.valueClass())))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> clearOnProfile(GameRule<?> rule) {
        return literal(SupportedRules.nameOf(rule)).executes(ctx -> editProfile(ctx, profile -> profile.clear(rule)));
    }

    private static <T> LiteralArgumentBuilder<CommandSourceStack> setOnPlayer(GameRule<T> rule) {
        return literal(SupportedRules.nameOf(rule)).then(argument("value", rule.argument()).executes(ctx ->
            editPlayers(ctx, entry -> entry.overrides().set(rule, ctx.getArgument("value", rule.valueClass())))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> clearOnPlayer(GameRule<?> rule) {
        return literal(SupportedRules.nameOf(rule)).executes(ctx -> editPlayers(ctx, entry -> entry.overrides().clear(rule)));
    }

    // ---- edits ----

    private static int editProfile(CommandContext<CommandSourceStack> ctx, java.util.function.Consumer<RuleOverrides> edit) {
        String name = StringArgumentType.getString(ctx, "name");
        MinecraftServer server = ctx.getSource().getServer();
        RulesData data = RulesData.get(server);

        Optional<RuleOverrides> profile = data.profile(name);
        if (profile.isEmpty()) {
            ctx.getSource().sendFailure(error("There is no profile named '" + name + "'."));
            return 0;
        }

        edit.accept(profile.get());
        data.changed();
        ctx.getSource().sendSuccess(() -> ok("Updated profile '" + name + "'."), true);

        // Everyone in the profile has new effective rules.
        data.players().forEach((id, entry) -> {
            if (entry.profile().filter(name::equals).isPresent()) {
                RuleNotifier.refresh(server, id);
            }
        });

        return 1;
    }

    private static int editPlayers(CommandContext<CommandSourceStack> ctx, java.util.function.Consumer<PlayerEntry> edit) throws CommandSyntaxException {
        MinecraftServer server = ctx.getSource().getServer();
        RulesData data = RulesData.get(server);
        Collection<NameAndId> targets = GameProfileArgument.getGameProfiles(ctx, "player");

        for (NameAndId target : targets) {
            PlayerEntry entry = data.entry(target.id());
            entry.setLastKnownName(target.name());
            edit.accept(entry);
            data.changed();
            RuleNotifier.refresh(server, target.id());
        }

        ctx.getSource().sendSuccess(() -> ok("Updated " + targets.size() + " player(s)."), true);

        return targets.size();
    }

    private int setPlayerProfile(CommandContext<CommandSourceStack> ctx, String profileName) throws CommandSyntaxException {
        if (profileName != null && RulesData.get(ctx.getSource().getServer()).profile(profileName).isEmpty()) {
            ctx.getSource().sendFailure(error("There is no profile named '" + profileName + "'."));
            return 0;
        }

        return editPlayers(ctx, entry -> entry.setProfile(Optional.ofNullable(profileName)));
    }

    private int createProfile(CommandContext<CommandSourceStack> ctx, String name) {
        RulesData data = RulesData.get(ctx.getSource().getServer());

        if (!RulesData.isValidProfileName(name)) {
            ctx.getSource().sendFailure(error("Profile names use lowercase letters, numbers, '_' and '-', up to 32 characters."));
            return 0;
        }

        if (!data.createProfile(name)) {
            ctx.getSource().sendFailure(error("A profile named '" + name + "' already exists."));
            return 0;
        }

        ctx.getSource().sendSuccess(() -> ok("Created profile '" + name + "'."), true);

        return 1;
    }

    private int deleteProfile(CommandContext<CommandSourceStack> ctx, String name) {
        MinecraftServer server = ctx.getSource().getServer();
        RulesData data = RulesData.get(server);

        // Collect the members first: deleting the profile removes them from it.
        java.util.List<java.util.UUID> members = data.players().entrySet().stream()
            .filter(e -> e.getValue().profile().filter(name::equals).isPresent())
            .map(java.util.Map.Entry::getKey)
            .toList();

        if (!data.deleteProfile(name)) {
            ctx.getSource().sendFailure(error("There is no profile named '" + name + "'."));
            return 0;
        }

        members.forEach(id -> RuleNotifier.refresh(server, id));
        ctx.getSource().sendSuccess(() -> ok("Deleted profile '" + name + "'."), true);

        return 1;
    }

    // ---- read-only output ----

    private int listRules(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(() -> ok("Rules that can be overridden per player:"), false);
        for (GameRule<?> rule : SupportedRules.all()) {
            ctx.getSource().sendSuccess(() -> line(SupportedRules.nameOf(rule) + " (" + rule.gameRuleType().getSerializedName() + ")"), false);
        }

        return SupportedRules.all().size();
    }

    private int listProfiles(CommandContext<CommandSourceStack> ctx) {
        RulesData data = RulesData.get(ctx.getSource().getServer());

        if (data.profileNames().isEmpty()) {
            ctx.getSource().sendSuccess(() -> ok("There are no profiles yet. Create one with /" + root + " profile create <name>."), false);
            return 0;
        }

        ctx.getSource().sendSuccess(() -> ok("Profiles:"), false);
        data.profileNames().forEach(name -> ctx.getSource().sendSuccess(() -> line(name), false));

        return data.profileNames().size();
    }

    private int showProfile(CommandContext<CommandSourceStack> ctx, String name) {
        Optional<RuleOverrides> profile = RulesData.get(ctx.getSource().getServer()).profile(name);
        if (profile.isEmpty()) {
            ctx.getSource().sendFailure(error("There is no profile named '" + name + "'."));
            return 0;
        }

        ctx.getSource().sendSuccess(() -> ok("Profile '" + name + "':"), false);
        sendRules(ctx, profile.get());

        return 1;
    }

    private int showPlayer(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        RulesData data = RulesData.get(ctx.getSource().getServer());

        for (NameAndId target : GameProfileArgument.getGameProfiles(ctx, "player")) {
            Optional<PlayerEntry> entry = data.playerEntry(target.id());
            String profile = entry.flatMap(PlayerEntry::profile).orElse("none");

            ctx.getSource().sendSuccess(() -> ok(target.name() + " (profile: " + profile + "):"), false);
            RuleOverrides effective = EffectiveRules.effective(data, target.id());
            if (effective.isEmpty()) {
                ctx.getSource().sendSuccess(() -> line("no overrides; the world's rules apply"), false);
            } else {
                sendRules(ctx, effective);
            }
        }

        return 1;
    }

    private int mine(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        RuleOverrides effective = EffectiveRules.effective(RulesData.get(ctx.getSource().getServer()), player.getUUID());

        // Not the notification text: this is a lookup, so show the world's value beside each override.
        if (effective.isEmpty()) {
            ctx.getSource().sendSuccess(() -> ok("No custom game rules apply to you; the world's rules do."), false);
            return 1;
        }

        ctx.getSource().sendSuccess(() -> ok("Your game rules are different from the world's:"), false);
        for (GameRule<?> rule : SupportedRules.all()) {
            appendMine(ctx, player, effective, rule);
        }

        return 1;
    }

    private static <T> void appendMine(CommandContext<CommandSourceStack> ctx, ServerPlayer player, RuleOverrides effective, GameRule<T> rule) {
        effective.get(rule).ifPresent(value -> {
            T world = player.level().getGameRules().get(rule);
            ctx.getSource().sendSuccess(() -> line(
                SupportedRules.nameOf(rule) + " = " + rule.serialize(value) + " (world: " + rule.serialize(world) + ")"
            ), false);
        });
    }

    private static void sendRules(CommandContext<CommandSourceStack> ctx, RuleOverrides rules) {
        if (rules.isEmpty()) {
            ctx.getSource().sendSuccess(() -> line("(no rules set)"), false);
            return;
        }

        rules.entries().forEach((key, value) ->
            ctx.getSource().sendSuccess(() -> line(RuleNotices.shortName(key) + " = " + value), false));
    }

    private static Component ok(String text) {
        return Component.literal(text).withStyle(ChatFormatting.GOLD);
    }

    private static Component line(String text) {
        return Component.literal("  " + text).withStyle(ChatFormatting.YELLOW);
    }

    private static Component error(String text) {
        return Component.literal(text).withStyle(ChatFormatting.RED);
    }
}
