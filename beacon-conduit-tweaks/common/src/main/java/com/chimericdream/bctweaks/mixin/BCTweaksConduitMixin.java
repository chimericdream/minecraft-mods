package com.chimericdream.bctweaks.mixin;

import com.chimericdream.bctweaks.config.BCTweaksConfig;
import net.minecraft.block.Block;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(value = ConduitBlockEntity.class, priority = 4101)
public class BCTweaksConduitMixin {
    @Mutable
    @Final
    @Shadow
    private static Block[] ACTIVATING_BLOCKS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void updateBlocks(CallbackInfo ci) {
        BCTweaksConfig config = BCTweaksConfig.HANDLER.instance();

        if (!config.conduitRangePerBlock.isEmpty()) {
            Set<Block> modifierBlocks = config.conduitRangePerBlock.keySet().stream()
                .map(Identifier::of)
                .map(Registries.BLOCK::get)
                .collect(Collectors.toSet());

            Collections.addAll(modifierBlocks, ACTIVATING_BLOCKS);
            ACTIVATING_BLOCKS = modifierBlocks.toArray(new Block[]{});
        }
    }

    @ModifyVariable(method = "givePlayersEffects(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/List;)V", at = @At(value = "LOAD"), ordinal = 1)
    private static int bct$modifiedRange(int _unused, World world, BlockPos pos, List<BlockPos> activatingBlocks) {
        BCTweaksConfig config = BCTweaksConfig.HANDLER.instance();

        int i = activatingBlocks.size();
        int j = i / 7 * 16;

        if (!config.conduitAddVanillaRange) {
            j = 0;
        }

        double n = j;
        for (BlockPos p : activatingBlocks) {
            String blockId = Registries.BLOCK.getEntry(world.getBlockState(p).getBlock()).getIdAsString();

            n += config.conduitRangePerBlock.getOrDefault(blockId, 0.0);
        }

        return (int) n;
    }
}
