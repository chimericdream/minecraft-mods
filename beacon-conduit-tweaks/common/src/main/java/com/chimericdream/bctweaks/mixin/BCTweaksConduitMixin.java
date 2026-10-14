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

    // The frame blocks vanilla accepts, captured the first time the list is rebuilt.
    @Unique
    private static Block[] bct$vanillaBlocks;

    // The configured ids VALID_BLOCKS was last built from (null until the first build).
    @Unique
    private static volatile Set<String> bct$appliedIds;

    // Rebuilt on first use (not in <clinit>, which runs before other mods have registered their blocks) and again
    // whenever the configured ids change, so edits on the config screen apply without a restart. Ids that don't
    // name a registered block are ignored.
    @Inject(method = "updateShape(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Ljava/util/List;)Z", at = @At("HEAD"))
    private static void bct$resolveValidBlocks(Level level, BlockPos pos, List<BlockPos> frameBlocks, CallbackInfoReturnable<Boolean> cir) {
        Set<String> wanted = BCTweaksConfig.CONFIG.instance().conduitRangePerBlock.keySet();
        if (wanted.equals(bct$appliedIds)) {
            return;
        }

        synchronized (BCTweaksConduitMixin.class) {
            Set<String> ids = Set.copyOf(wanted);
            if (ids.equals(bct$appliedIds)) {
                return;
            }

            if (bct$vanillaBlocks == null) {
                bct$vanillaBlocks = VALID_BLOCKS;
            }

            Set<Block> blocks = new HashSet<>();
            Collections.addAll(blocks, bct$vanillaBlocks);
            for (String id : ids) {
                Identifier identifier = Identifier.tryParse(id);
                if (identifier != null && BuiltInRegistries.BLOCK.containsKey(identifier)) {
                    blocks.add(BuiltInRegistries.BLOCK.getValue(identifier));
                }
            }
            VALID_BLOCKS = blocks.toArray(new Block[]{});

            // The first build happens right after the server-start warning, so only warn again for later edits.
            if (bct$appliedIds != null) {
                BCTweaksConfig.warnAboutUnknownBlocks("Conduit Range / Block", BCTweaksConfig.CONFIG.instance().conduitRangePerBlock);
            }
            bct$appliedIds = ids;
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
