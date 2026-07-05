package com.chimericdream.minekea.block.building.storage;

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

public class DyeBlock extends Block {
    public final ResourceLocation BLOCK_ID;
    public final String color;

    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public DyeBlock(String color) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEY_BLOCK).mapColor(DyeColor.byName(color, DyeColor.WHITE)).jumpFactor(0.5F).setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(color))));

        this.color = color;

        BLOCK_ID = makeId(color);
    }

    public static ResourceLocation makeId(String color) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("storage/dyes/%s", color));
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity entity, double fallDistance) {
        entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
        if (entity.causeFallDamage(fallDistance, 0.2F, world.damageSources().fall())) {
            entity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
        }
    }
}
