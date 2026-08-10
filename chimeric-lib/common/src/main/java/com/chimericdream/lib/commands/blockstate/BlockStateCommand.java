package com.chimericdream.lib.commands.blockstate;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.util.Map;
import java.util.Optional;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import com.chimericdream.lib.ChimericLib;
import com.chimericdream.lib.commands.ChimericCommand;

/**
 * {@code /chimericlib blockstate get|set|modify <pos>}, modeled after vanilla's
 * {@code /data get block} (read-only query) and {@code /setblock} (full replace via
 * {@link BlockStateArgument}). {@code modify} is the one addition with no vanilla equivalent: it
 * merges only the given properties onto whatever block is already at the position.
 */
public final class BlockStateCommand implements ChimericCommand {
    private static final int UPDATE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS;

    private static final SimpleCommandExceptionType ERROR_SET_FAILED =
        new SimpleCommandExceptionType(Component.translatable("commands.chimericlib.blockstate.set.failed"));
    private static final SimpleCommandExceptionType ERROR_MODIFY_FAILED =
        new SimpleCommandExceptionType(Component.translatable("commands.chimericlib.blockstate.modify.failed"));
    private static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType(
        (block, property) -> Component.translatableEscape("commands.chimericlib.blockstate.modify.unknown_property", block, property)
    );
    private static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType(
        (block, value, property) -> Component.translatableEscape("commands.chimericlib.blockstate.modify.invalid_value", block, value, property)
    );

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> build(CommandBuildContext context) {
        return Commands.literal(ChimericLib.MOD_ID)
            .then(Commands.literal("blockstate")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("get")
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(ctx -> get(
                            ctx.getSource(),
                            BlockPosArgument.getLoadedBlockPos(ctx, "pos")
                        ))))
                .then(Commands.literal("set")
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(Commands.argument("block", BlockStateArgument.block(context))
                            .executes(ctx -> set(
                                ctx.getSource(),
                                BlockPosArgument.getLoadedBlockPos(ctx, "pos"),
                                BlockStateArgument.getBlock(ctx, "block")
                            )))))
                .then(Commands.literal("modify")
                    .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(Commands.argument("properties", BlockPropertiesArgument.properties())
                            .executes(ctx -> modify(
                                ctx.getSource(),
                                BlockPosArgument.getLoadedBlockPos(ctx, "pos"),
                                BlockPropertiesArgument.getProperties(ctx, "properties")
                            ))))));
    }

    private static int get(CommandSourceStack source, BlockPos pos) {
        String serialized = BlockStateParser.serialize(source.getLevel().getBlockState(pos));
        source.sendSuccess(() -> Component.translatable(
            "commands.chimericlib.blockstate.get.success", pos.getX(), pos.getY(), pos.getZ(), serialized
        ), false);
        return 1;
    }

    private static int set(CommandSourceStack source, BlockPos pos, BlockInput block) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        BlockState oldState = level.getBlockState(pos);
        if (!block.place(level, pos, UPDATE_FLAGS)) {
            throw ERROR_SET_FAILED.create();
        }
        level.updateNeighboursOnBlockSet(pos, oldState);

        String serialized = BlockStateParser.serialize(block.getState());
        source.sendSuccess(() -> Component.translatable(
            "commands.chimericlib.blockstate.set.success", pos.getX(), pos.getY(), pos.getZ(), serialized
        ), true);
        return 1;
    }

    private static int modify(CommandSourceStack source, BlockPos pos, Map<String, String> rawProperties) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        BlockState oldState = level.getBlockState(pos);
        BlockState newState = oldState;

        for (Map.Entry<String, String> entry : rawProperties.entrySet()) {
            newState = applyProperty(newState, entry.getKey(), entry.getValue());
        }

        if (!level.setBlock(pos, newState, UPDATE_FLAGS)) {
            throw ERROR_MODIFY_FAILED.create();
        }
        level.updateNeighboursOnBlockSet(pos, oldState);

        BlockState finalState = newState;
        String serialized = BlockStateParser.serialize(finalState);
        source.sendSuccess(() -> Component.translatable(
            "commands.chimericlib.blockstate.modify.success", pos.getX(), pos.getY(), pos.getZ(), serialized
        ), true);
        return 1;
    }

    private static BlockState applyProperty(BlockState state, String key, String rawValue) throws CommandSyntaxException {
        Property<?> property = state.getBlock().getStateDefinition().getProperty(key);
        if (property == null) {
            throw ERROR_UNKNOWN_PROPERTY.create(blockId(state), key);
        }
        return setValue(state, property, rawValue);
    }

    private static <T extends Comparable<T>> BlockState setValue(BlockState state, Property<T> property, String rawValue) throws CommandSyntaxException {
        Optional<T> value = property.getValue(rawValue);
        if (value.isEmpty()) {
            throw ERROR_INVALID_VALUE.create(blockId(state), rawValue, property.getName());
        }
        return (BlockState) state.setValue(property, value.get());
    }

    private static Identifier blockId(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock());
    }
}
