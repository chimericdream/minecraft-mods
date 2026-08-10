package com.chimericdream.lib.commands;

import dev.architectury.event.events.common.CommandRegistrationEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared registry of {@link ChimericCommand}s. Any mod may call {@link #register(ChimericCommand)}
 * during its own init (e.g. from a {@code ModInitializer}/{@code @Mod} constructor) to have its
 * command tree registered on both Fabric and NeoForge, without wiring up
 * {@link CommandRegistrationEvent} itself.
 */
public final class ChimericCommands {
    private static final List<ChimericCommand> COMMANDS = new ArrayList<>();

    static {
        CommandRegistrationEvent.EVENT.register((dispatcher, context, selection) ->
            COMMANDS.forEach(command -> dispatcher.register(command.build(context))));
    }

    private ChimericCommands() {
    }

    public static void register(ChimericCommand command) {
        COMMANDS.add(command);
    }
}
