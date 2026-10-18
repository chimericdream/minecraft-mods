package com.chimericdream.pannotiacompanion.home;

import com.chimericdream.lib.commands.ChimericCommand;
import com.chimericdream.pannotiacompanion.config.PannotiaCompanionConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.Set;

import static net.minecraft.commands.Commands.literal;

/**
 * {@code /pc back}. Replaces the Back Vanilla Tweaks datapack. Using it swaps places with the saved point, so
 * running it twice bounces between the two spots.
 */
public class PcBackCommand implements ChimericCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return literal("pc").then(literal("back").executes(this::goBack));
    }

    private int goBack(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();

        Optional<BackPoint> found = BackData.get(ctx.getSource().getServer()).get(player.getUUID());
        if (found.isEmpty()) {
            ctx.getSource().sendFailure(Component.translatable("message.pannotiacompanion.back.none").withStyle(ChatFormatting.RED));
            return 0;
        }

        BackPoint point = found.get();
        ServerLevel target = ctx.getSource().getServer().getLevel(point.dimension());
        if (target == null) {
            ctx.getSource().sendFailure(Component.translatable("message.pannotiacompanion.home.dimension_missing", point.dimension().identifier().toString()).withStyle(ChatFormatting.RED));
            return 0;
        }

        PannotiaCompanionConfig config = PannotiaCompanionConfig.get();
        boolean started = TeleportManager.request(player, TeleportManager.BACK_COOLDOWNS, config.backDelayTicks, config.backCooldownSeconds, p -> {
            BackData.record(p);
            p.teleportTo(target, point.x(), point.y(), point.z(), Set.of(), point.yRot(), point.xRot(), true);
            p.sendSystemMessage(Component.translatable("message.pannotiacompanion.back.teleported").withStyle(ChatFormatting.GOLD));
        });
        return started ? 1 : 0;
    }
}
