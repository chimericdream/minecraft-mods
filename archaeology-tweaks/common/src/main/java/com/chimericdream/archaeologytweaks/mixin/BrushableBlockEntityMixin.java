package com.chimericdream.archaeologytweaks.mixin;

import com.chimericdream.archaeologytweaks.advancement.ArchaeologyTweaksAdvancements;
import com.chimericdream.archaeologytweaks.enchantment.GentleTouchHelper;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Extends the Gentle Touch enchantment's "brush again instead of breaking" behavior to vanilla
 * suspicious sand/gravel, which use vanilla's own {@link BrushableBlockEntity} rather than
 * {@link com.chimericdream.archaeologytweaks.block.entity.ATBrushableBlockEntity}.
 */
@Mixin(BrushableBlockEntity.class)
abstract public class BrushableBlockEntityMixin {
    @Unique
    private static final String GENTLE_TOUCH_SOURCE_LOOT_TABLE_TAG = "archtweaks_gentle_touch_source_loot_table";

    @Shadow
    private int brushCount;

    @Shadow
    @Nullable
    private Direction hitDirection;

    @Shadow
    @Nullable
    private ResourceKey<LootTable> lootTable;

    @Unique
    @Nullable
    private ResourceKey<LootTable> at$sourceLootTable;

    @Invoker("dropContent")
    abstract void at$dropContent(ServerLevel world, LivingEntity brusher, ItemStack brush);

    @Inject(
        method = "setLootTable(Lnet/minecraft/resources/ResourceKey;J)V",
        at = @At("TAIL")
    )
    private void at$captureSourceLootTable(ResourceKey<LootTable> lootTable, long seed, CallbackInfo ci) {
        this.at$sourceLootTable = lootTable;
    }

    @Inject(
        method = "brushingCompleted(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void at$gentleTouch(ServerLevel world, LivingEntity brusher, ItemStack brush, CallbackInfo ci) {
        if (this.at$sourceLootTable == null) {
            return;
        }

        int gentleTouchLevel = GentleTouchHelper.getLevel(world, brush);
        if (!GentleTouchHelper.rolls(world, gentleTouchLevel)) {
            return;
        }

        BrushableBlockEntity self = (BrushableBlockEntity) (Object) this;

        this.at$dropContent(world, brusher, brush);

        if (brusher instanceof ServerPlayer serverPlayer) {
            ArchaeologyTweaksAdvancements.award(serverPlayer, ArchaeologyTweaksAdvancements.LUCKY_BLOCK);
        }

        this.brushCount = 0;
        this.hitDirection = null;
        self.setLootTable(this.at$sourceLootTable, world.getRandom().nextLong());

        world.setBlock(self.getBlockPos(), self.getBlockState().setValue(BlockStateProperties.DUSTED, 0), 3);
        self.setChanged();

        ci.cancel();
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void at$loadSourceLootTable(ValueInput data, CallbackInfo ci) {
        this.at$sourceLootTable = data.read(GENTLE_TOUCH_SOURCE_LOOT_TABLE_TAG, LootTable.KEY_CODEC).orElse(this.lootTable);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void at$saveSourceLootTable(ValueOutput data, CallbackInfo ci) {
        if (this.at$sourceLootTable != null) {
            data.store(GENTLE_TOUCH_SOURCE_LOOT_TABLE_TAG, LootTable.KEY_CODEC, this.at$sourceLootTable);
        }
    }
}
