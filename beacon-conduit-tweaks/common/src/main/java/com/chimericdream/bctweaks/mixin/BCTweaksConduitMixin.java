package com.chimericdream.bctweaks.mixin;

import com.chimericdream.bctweaks.config.BCTweaksConfig;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;

@Mixin(value = ConduitBlockEntity.class, priority = 4101)
public class BCTweaksConduitMixin {
    @Mutable
    @Final
    @Shadow
    private static Block[] VALID_BLOCKS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void updateBlocks(CallbackInfo ci) {
        BCTweaksConfig config = BCTweaksConfig.HANDLER.instance();

        if (!config.conduitRangePerBlock.isEmpty()) {
            Set<Block> modifierBlocks = config.conduitRangePerBlock.keySet().stream()
                .map(Identifier::parse)
                .map(BuiltInRegistries.BLOCK::getValue)
                .collect(Collectors.toSet());

            Collections.addAll(modifierBlocks, VALID_BLOCKS);
            VALID_BLOCKS = modifierBlocks.toArray(new Block[]{});
        }
    }

    @ModifyVariable(method = "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)V", at = @At(value = "LOAD"), name = "j")
    private static int bct$modifiedRange(int _unused, Level world, BlockPos pos, List<BlockPos> activatingBlocks) {
        BCTweaksConfig config = BCTweaksConfig.HANDLER.instance();

        int i = activatingBlocks.size();
        int j = i / 7 * 16;

        if (!config.conduitAddVanillaRange) {
            j = 0;
        }

        double n = j;
        for (BlockPos p : activatingBlocks) {
            String blockId = BuiltInRegistries.BLOCK.wrapAsHolder(world.getBlockState(p).getBlock()).getRegisteredName();

            n += config.conduitRangePerBlock.getOrDefault(blockId, 0.0);
        }

        return (int) n;
    }
}
