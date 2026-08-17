package com.chimericdream.effectivegear.mixin;

import com.chimericdream.effectivegear.ModInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EnderpearlItem.class)
public class EG$EnderpearlItemMixin {
    @Unique
    private static ResourceKey<TrimMaterial> ENDER_PEARL_TRIM_ID = ResourceKey.create(Registries.TRIM_MATERIAL, Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "ender_pearl"));

    @ModifyVariable(method = "<init>(Lnet/minecraft/world/item/Item$Properties;)V", at = @At("HEAD"), ordinal = 0)
    private static Item.Properties eg$addTrimMaterialComponent(Item.Properties properties) {
        return properties.trimMaterial(ENDER_PEARL_TRIM_ID);
    }
}
