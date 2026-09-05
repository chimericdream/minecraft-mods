package com.chimericdream.flatbedrock.mixin;

import com.chimericdream.flatbedrock.FlatBedrockContext;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
abstract public class NoiseBasedChunkGeneratorMixin {
    @Inject(
        method = "buildSurface(Lnet/minecraft/server/level/WorldGenRegion;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;)V",
        at = @At("HEAD")
    )
    private void fb$captureDimension(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess protoChunk, CallbackInfo ci) {
        FlatBedrockContext.set(region.getLevel().dimension());
    }

    @Inject(
        method = "buildSurface(Lnet/minecraft/server/level/WorldGenRegion;Lnet/minecraft/world/level/StructureManager;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/chunk/ChunkAccess;)V",
        at = @At("TAIL")
    )
    private void fb$releaseDimension(WorldGenRegion region, StructureManager structureManager, RandomState randomState, ChunkAccess protoChunk, CallbackInfo ci) {
        FlatBedrockContext.clear();
    }
}
