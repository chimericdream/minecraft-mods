package com.chimericdream.lib.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

/**
 * A single command tree, contributed by ChimericLib itself or by any mod that depends on it.
 * <p>
 * Implementations build one top-level literal node (e.g. {@code chimericlib}, or a mod's own root).
 * Multiple {@link ChimericCommand}s that return the same root literal name merge together under
 * Brigadier's standard node-merging rules, so several features can share one command root without
 * coordinating with each other.
 */
public interface ChimericCommand {
    LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context);
}
