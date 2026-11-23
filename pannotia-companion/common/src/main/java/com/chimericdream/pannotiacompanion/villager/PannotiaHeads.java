package com.chimericdream.pannotiacompanion.villager;

import com.mojang.authlib.properties.Property;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class PannotiaHeads {
    /**
     * Profile name on the wandering trader heads that this mod sold before. Vanilla names a player head
     * "<profile name>'s Head" and ignores {@code minecraft:item_name} when the profile has a name, so those heads
     * also carried a custom name. {@link #fixName} turns them into the plain form.
     */
    public static final String PROFILE_NAME = "pannotia";

    private PannotiaHeads() {
    }

    /**
     * Normalized names of the vanilla entity types ({@code zombie_villager} becomes {@code zombievillager}).
     * Heads from older sources have the mob's name as the profile name. Vanilla would show these as "Cow's Head", and
     * they do not stack with a loot-table head. Matching the entity registry covers every vanilla mob without a list.
     * Any profile name would be unsafe: it would also change heads of real players.
     */
    private static final class VanillaMobNames {
        private static final Set<String> NORMALIZED = BuiltInRegistries.ENTITY_TYPE.keySet().stream()
            .filter(id -> "minecraft".equals(id.getNamespace()))
            .map(id -> normalize(id.getPath()))
            .collect(Collectors.toUnmodifiableSet());
    }

    private static String normalize(String name) {
        return name.toLowerCase(Locale.ROOT).replaceAll("[ _]+", "");
    }

    /**
     * @return {@code true} if the name is the name of a vanilla mob, such as "Cow" or "Zombie Villager"
     */
    public static boolean isVanillaMobName(String name) {
        return VanillaMobNames.NORMALIZED.contains(normalize(name));
    }

    /**
     * Turns a head that this mod or an older source made into the plain form that the loot tables make: an
     * {@code item_name} and a profile with only a texture. That form shows the right name and stacks with the heads
     * that the loot tables drop. It handles a head whose texture is in {@link HeadNames} and whose profile has:
     * <ul>
     * <li>no name,</li>
     * <li>the name of a vanilla mob (see {@link #isVanillaMobName}), or</li>
     * <li>the name {@link #PROFILE_NAME}, which the wandering trader heads had. These also carry a custom name that
     * only repeats the item name, because vanilla ignores {@code item_name} for a named profile.</li>
     * </ul>
     * The head gets its name as the item name if it has none, loses a custom name that repeats the item name, and gets
     * a profile with only the current texture ({@link HeadNames#currentTexture}), so the {@code id} and name go. It
     * also resets a rarity that differs from the item's own, which the crafted mini blocks had.
     * A head that is not one of ours, or that a player renamed, does not change.
     *
     * @return {@code true} if the stack changed
     */
    public static boolean fixName(ItemStack stack) {
        if (!stack.is(Items.PLAYER_HEAD)) {
            return false;
        }

        ResolvableProfile profile = stack.get(DataComponents.PROFILE);
        if (profile == null) {
            return false;
        }

        String profileName = profile.name().orElse(null);
        if (profileName != null && !PROFILE_NAME.equals(profileName) && !isVanillaMobName(profileName)) {
            return false;
        }

        String texture = texture(profile);
        if (texture != null) {
            texture = HeadNames.currentTexture(texture);
        }

        String knownName = texture == null ? null : HeadNames.forTexture(texture);
        if (knownName == null) {
            return false;
        }

        Component ownName = ownItemName(stack);
        String displayName = ownName != null ? ownName.getString() : knownName;

        // A custom name that differs from the item name is a rename by the player. Leave that head alone.
        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName != null && !customName.getString().equals(displayName)) {
            return false;
        }

        boolean changed = false;
        if (customName != null) {
            stack.remove(DataComponents.CUSTOM_NAME);
            changed = true;
        }

        if (ownName == null) {
            stack.set(DataComponents.ITEM_NAME, Component.literal(knownName));
            changed = true;
        }

        ResolvableProfile textureOnly = textureOnlyProfile(texture);
        if (!textureOnly.equals(profile)) {
            stack.set(DataComponents.PROFILE, textureOnly);
            changed = true;
        }

        // The crafted mini blocks once set their rarity to common. A head keeps the item's own rarity.
        Rarity itemRarity = stack.getPrototype().get(DataComponents.RARITY);
        if (stack.get(DataComponents.RARITY) != itemRarity) {
            stack.set(DataComponents.RARITY, itemRarity);
            changed = true;
        }

        return changed;
    }

    /**
     * @return the base64 {@code textures} value of the profile, or {@code null} if it has none
     */
    private static String texture(ResolvableProfile profile) {
        for (Property property : profile.partialProfile().properties().get("textures")) {
            return property.value();
        }

        return null;
    }

    /**
     * Only a name that the stack sets itself counts, not the default name from the item prototype.
     */
    private static Component ownItemName(ItemStack stack) {
        Component name = stack.get(DataComponents.ITEM_NAME);
        return name == null || name.equals(stack.getPrototype().get(DataComponents.ITEM_NAME)) ? null : name;
    }

    /**
     * Builds the profile that a loot table makes from {@code {properties: [{name: "textures", value: ...}]}}: a texture
     * and nothing else. There is no factory for it, so decode it with the same codec.
     */
    public static ResolvableProfile textureOnlyProfile(String texture) {
        CompoundTag property = new CompoundTag();
        property.putString("name", "textures");
        property.putString("value", texture);

        ListTag properties = new ListTag();
        properties.add(property);

        CompoundTag tag = new CompoundTag();
        tag.put("properties", properties);

        return ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
    }
}
