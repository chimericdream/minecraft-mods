package com.chimericdream.minekea.block.furniture.pillows;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class PillowBlock extends Block {
    public final ResourceLocation BLOCK_ID;
    public final String color;

    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public PillowBlock(String color) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL).mapColor(DyeColor.byName(color, DyeColor.WHITE)).setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(color))));

        this.color = color;

        BLOCK_ID = makeId(color);
    }

    public static ResourceLocation makeId(String color) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("furniture/pillows/%s", color));
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        entity.playSound(SoundEvents.WOOL_STEP, 1.0F, 1.0F);
        if (entity.causeFallDamage(fallDistance, 0F, world.damageSources().fall())) {
            entity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
        }
    }
}
