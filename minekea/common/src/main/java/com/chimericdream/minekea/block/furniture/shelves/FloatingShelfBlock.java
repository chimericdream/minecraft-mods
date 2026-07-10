package com.chimericdream.minekea.block.furniture.shelves;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FloatingShelfBlock extends ShelfBlock {
    public FloatingShelfBlock(BlockConfig config) {
        super(config, makeId(config.getMaterial()));
    }

    public static Identifier makeId(String material) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/shelves/floating/%s", material));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction wall = state.getValue(WALL_SIDE);

        return switch (wall) {
            case EAST -> Block.box(0.0, 7.0, 0.0, 7.0, 9.0, 16.0);
            case SOUTH -> Block.box(0.0, 7.0, 0.0, 16.0, 9.0, 7.0);
            case WEST -> Block.box(9.0, 7.0, 0.0, 16.0, 9.0, 16.0);
            default -> Block.box(0.0, 7.0, 9.0, 16.0, 9.0, 16.0);
        };
    }
}
