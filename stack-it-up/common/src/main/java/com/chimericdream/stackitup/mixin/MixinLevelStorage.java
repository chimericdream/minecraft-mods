package com.chimericdream.stackitup.mixin;

import java.nio.file.Path;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.chimericdream.stackitup.config.ConfigManager;

@Mixin(LevelStorageSource.class)
public class MixinLevelStorage {
    // As of 26.1.2, item data components (including MAX_STACK_SIZE) aren't bound to the registry
    // yet at this point - validateAndCreateAccess runs before the DataComponentInitializers bake
    // step, which needs a full HolderLookup.Provider that doesn't exist this early. Only point
    // ConfigManager at the right per-world file here; let StackItUpMod's SERVER_STARTING handler
    // (well after registries are frozen) actually load the file and apply item counts.
    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorageSource;getLevelPath(Ljava/lang/String;)Ljava/nio/file/Path;"), method = "validateAndCreateAccess")
    private Path initConfig(LevelStorageSource instance, String name, Operation<Path> original) {
        Path path = original.call(instance, name);
        ConfigManager.getConfigManager().passConfigFile(path.resolve("stackitup-config.json").toFile());
        return path;
    }
}
