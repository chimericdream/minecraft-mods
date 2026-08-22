package com.chimericdream.effectivegear.item.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

/**
 * Shared helpers for checking whether an entity is wearing a full set of armor that qualifies for
 * one of this mod's set bonuses: either all four pieces trimmed with a specific material, or all
 * four pieces belonging to a specific item tag (e.g. netherite armor).
 */
public class TrimSetUtils {
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private TrimSetUtils() {
    }

    public static boolean isWearingFullTrim(LivingEntity entity, ResourceKey<TrimMaterial> trimId) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) {
                return false;
            }

            ArmorTrim trim = stack.get(DataComponents.TRIM);
            if (trim == null || !trim.material().is(trimId)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isWearingFullSet(LivingEntity entity, TagKey<Item> helmets, TagKey<Item> chestplates, TagKey<Item> leggings, TagKey<Item> boots) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(helmets)
            && entity.getItemBySlot(EquipmentSlot.CHEST).is(chestplates)
            && entity.getItemBySlot(EquipmentSlot.LEGS).is(leggings)
            && entity.getItemBySlot(EquipmentSlot.FEET).is(boots);
    }
}
