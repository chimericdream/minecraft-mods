package com.chimericdream.bctweaks.mixin;

import com.chimericdream.bctweaks.config.BCTweaksConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    @Unique
    private static volatile boolean bct$validBlocksResolved = false;

    // Resolved on first use rather than in <clinit>, which runs before other mods have registered their blocks.
    // Ids that don't name a registered block are ignored (they are reported when the server starts).
    @Inject(method = "updateShape(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)Z", at = @At("HEAD"))
    private static void bct$resolveValidBlocks(Level level, BlockPos pos, List<BlockPos> frameBlocks, CallbackInfoReturnable<Boolean> cir) {
        if (bct$validBlocksResolved) {
            return;
        }

        synchronized (BCTweaksConduitMixin.class) {
            if (bct$validBlocksResolved) {
                return;
            }
            bct$validBlocksResolved = true;

            BCTweaksConfig config = BCTweaksConfig.CONFIG.instance();
            if (config.conduitRangePerBlock.isEmpty()) {
                return;
            }

            Set<Block> modifierBlocks = new HashSet<>();
            for (String id : config.conduitRangePerBlock.keySet()) {
                Identifier identifier = Identifier.tryParse(id);
                if (identifier != null && BuiltInRegistries.BLOCK.containsKey(identifier)) {
                    modifierBlocks.add(BuiltInRegistries.BLOCK.getValue(identifier));
                }
            }

            Collections.addAll(modifierBlocks, VALID_BLOCKS);
            VALID_BLOCKS = modifierBlocks.toArray(new Block[]{});
        }
    }

    @SuppressWarnings("ModifyVariableMayUseName")
    @ModifyVariable(method = "applyEffects(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)V", at = @At(value = "LOAD", ordinal = 0), ordinal = 1)
    private static int bct$modifiedRange(int _unused, Level world, BlockPos pos, List<BlockPos> activatingBlocks) {
        BCTweaksConfig config = BCTweaksConfig.CONFIG.instance();

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
