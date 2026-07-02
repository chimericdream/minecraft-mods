package com.chimericdream.archaeologytweaks.block;

import com.chimericdream.archaeologytweaks.ModInfo;
import com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

public class SuspiciousRedSandBlock extends BrushableBlock {
    public static final ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "suspicious_red_sand");

    public SuspiciousRedSandBlock() {
        super(
            Blocks.RED_SAND,
            SoundEvents.BRUSH_SAND,
            SoundEvents.BRUSH_SAND_COMPLETED,
            BlockBehaviour.Properties
                .of()
                .mapColor(MapColor.COLOR_ORANGE)
                .strength(0.25F)
                .sound(SoundType.SUSPICIOUS_SAND)
                .pushReaction(PushReaction.DESTROY)
                .setId(ResourceKey.create(Registries.BLOCK, BLOCK_ID))
        );
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ATBrushableBlockEntity(pos, state);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        BlockEntity var6 = world.getBlockEntity(pos);
        if (var6 instanceof ATBrushableBlockEntity brushableBlockEntity) {
            brushableBlockEntity.scheduledTick(world);
        }
    }
}
