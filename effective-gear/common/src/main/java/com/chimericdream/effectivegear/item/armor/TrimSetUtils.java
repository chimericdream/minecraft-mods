package com.chimericdream.effectivegear.item.armor;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;

/**
 * Shared helpers for checking whether an entity is wearing a full set of armor that qualifies for
 * one of this mod's set bonuses: either all four pieces trimmed with a specific material, all four
 * pieces trimmed with a specific pattern (regardless of material), or all four pieces belonging to a
 * specific item tag (e.g. netherite armor).
 * <p>
 * Also understands the Armored Elytra mod's combined items without depending on it: those are a plain
 * elytra whose merged chestplate (and its trim) is stored in the elytra's custom data.
 */
public class TrimSetUtils {
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private static final String ARMORED_ELYTRA_CHESTPLATE = "armored_elytra:chestplate";

    private TrimSetUtils() {
    }

    /** Material and pattern identifiers, compared by id so no registry access is needed. */
    private record TrimIds(Identifier material, Identifier pattern) {
    }

    /** The stack's real trim, or the trim of the chestplate merged into an Armored Elytra. */
    private static TrimIds trimIds(ItemStack stack) {
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim != null) {
            return new TrimIds(
                trim.material().unwrapKey().map(ResourceKey::identifier).orElse(null),
                trim.pattern().unwrapKey().map(ResourceKey::identifier).orElse(null)
            );
        }

        CompoundTag chestplate = armoredElytraChestplate(stack);
        if (chestplate == null) {
            return null;
        }

        CompoundTag trimTag = chestplate.getCompound("components")
            .flatMap(components -> components.getCompound("minecraft:trim"))
            .orElse(null);
        if (trimTag == null) {
            return null;
        }

        return new TrimIds(
            trimTag.getString("material").map(Identifier::tryParse).orElse(null),
            trimTag.getString("pattern").map(Identifier::tryParse).orElse(null)
        );
    }

    private static CompoundTag armoredElytraChestplate(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return null;
        }

        return data.copyTag().getCompound(ARMORED_ELYTRA_CHESTPLATE).orElse(null);
    }

    private static boolean chestIs(ItemStack stack, TagKey<Item> chestplates) {
        if (stack.is(chestplates)) {
            return true;
        }

        CompoundTag chestplate = armoredElytraChestplate(stack);
        if (chestplate == null) {
            return false;
        }

        return chestplate.getString("id")
            .map(Identifier::tryParse)
            .flatMap(id -> BuiltInRegistries.ITEM.get(id))
            .map(holder -> holder.is(chestplates))
            .orElse(false);
    }

    public static boolean isWearingFullTrim(LivingEntity entity, ResourceKey<TrimMaterial> trimId) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) {
                return false;
            }

            TrimIds trim = trimIds(stack);
            if (trim == null || !trimId.identifier().equals(trim.material())) {
                return false;
            }
        }

        return true;
    }

    public static boolean isWearingFullPattern(LivingEntity entity, ResourceKey<TrimPattern> patternId) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (stack.isEmpty()) {
                return false;
            }

            TrimIds trim = trimIds(stack);
            if (trim == null || !patternId.identifier().equals(trim.pattern())) {
                return false;
            }
        }

        return true;
    }

    public static boolean isWearingFullSet(LivingEntity entity, TagKey<Item> helmets, TagKey<Item> chestplates, TagKey<Item> leggings, TagKey<Item> boots) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).is(helmets)
            && chestIs(entity.getItemBySlot(EquipmentSlot.CHEST), chestplates)
            && entity.getItemBySlot(EquipmentSlot.LEGS).is(leggings)
            && entity.getItemBySlot(EquipmentSlot.FEET).is(boots);
    }
}
