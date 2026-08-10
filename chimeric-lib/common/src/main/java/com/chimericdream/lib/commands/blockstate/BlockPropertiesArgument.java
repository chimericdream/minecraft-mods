package com.chimericdream.lib.commands.blockstate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.chat.Component;

import com.chimericdream.lib.commands.PlatformCommandArgumentTypes;

/**
 * Parses a bracketed, block-agnostic property list like {@code [facing=north,waterlogged=true]} —
 * the same visual syntax as vanilla's {@code BlockStateArgument} properties, but without a leading
 * block id, since the target block for a {@code blockstate modify} is whatever is already at the
 * given position. Property names/values are validated against that block at execution time, not
 * here (see {@code BlockStateCommand}).
 */
public final class BlockPropertiesArgument implements ArgumentType<Map<String, String>> {
    private static final Collection<String> EXAMPLES = List.of("[facing=north]", "[facing=north,waterlogged=true]");

    public static final SimpleCommandExceptionType ERROR_EXPECTED_PROPERTIES =
        new SimpleCommandExceptionType(Component.translatable("argument.chimericlib.block_properties.expected"));
    public static final DynamicCommandExceptionType ERROR_DUPLICATE_PROPERTY =
        new DynamicCommandExceptionType(key -> Component.translatableEscape("argument.chimericlib.block_properties.duplicate", key));
    public static final DynamicCommandExceptionType ERROR_EXPECTED_VALUE =
        new DynamicCommandExceptionType(key -> Component.translatableEscape("argument.chimericlib.block_properties.novalue", key));
    public static final SimpleCommandExceptionType ERROR_UNCLOSED =
        new SimpleCommandExceptionType(Component.translatable("argument.chimericlib.block_properties.unclosed"));

    public static BlockPropertiesArgument properties() {
        return new BlockPropertiesArgument();
    }

    /**
     * Registers this argument type with the platform's command-argument-sync machinery. Must be
     * called once during mod init (see {@code ChimericLib.init()}) — a custom {@link ArgumentType}
     * that isn't registered here can't be synced to the client, and any command using it will break
     * the whole command tree sync on player join.
     */
    public static void register() {
        PlatformCommandArgumentTypes.registerByClass(BlockPropertiesArgument.class, SingletonArgumentInfo.contextFree(BlockPropertiesArgument::properties));
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> getProperties(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, Map.class);
    }

    @Override
    public Map<String, String> parse(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead() || reader.peek() != '[') {
            throw ERROR_EXPECTED_PROPERTIES.createWithContext(reader);
        }

        reader.skip();
        reader.skipWhitespace();

        Map<String, String> properties = new LinkedHashMap<>();
        while (reader.canRead() && reader.peek() != ']') {
            reader.skipWhitespace();
            int keyStart = reader.getCursor();
            String key = reader.readString();
            if (properties.containsKey(key)) {
                reader.setCursor(keyStart);
                throw ERROR_DUPLICATE_PROPERTY.createWithContext(reader, key);
            }

            reader.skipWhitespace();
            if (!reader.canRead() || reader.peek() != '=') {
                throw ERROR_EXPECTED_VALUE.createWithContext(reader, key);
            }

            reader.skip();
            reader.skipWhitespace();
            properties.put(key, reader.readString());
            reader.skipWhitespace();

            if (reader.canRead() && reader.peek() != ']') {
                if (reader.peek() != ',') {
                    throw ERROR_UNCLOSED.createWithContext(reader);
                }
                reader.skip();
            }
        }

        if (!reader.canRead()) {
            throw ERROR_UNCLOSED.createWithContext(reader);
        }
        reader.skip();

        if (properties.isEmpty()) {
            throw ERROR_EXPECTED_PROPERTIES.createWithContext(reader);
        }

        return properties;
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
