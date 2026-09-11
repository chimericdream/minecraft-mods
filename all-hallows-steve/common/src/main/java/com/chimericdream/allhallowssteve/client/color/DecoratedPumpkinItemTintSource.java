package com.chimericdream.allhallowssteve.client.color;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Item-model tint for the dyed pumpkin item's grayscale textures, reading the color the carving
 * station stored on the stack. Registered into the vanilla {@code ItemTintSources} dispatch registry
 * by {@link com.chimericdream.allhallowssteve.mixin.AHS$ItemTintSourcesMixin} — that
 * registry has no mod-facing registration hook, so the mixin injects a {@code put()} call into its
 * bootstrap the same way vanilla registers its own "potion"/"dye"/"firework" tint sources.
 */
public record DecoratedPumpkinItemTintSource(int defaultColor) implements ItemTintSource {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "decorated_pumpkin");

    public static final MapCodec<DecoratedPumpkinItemTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(DecoratedPumpkinItemTintSource::defaultColor)
    ).apply(i, DecoratedPumpkinItemTintSource::new));

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        DyedColorComponent component = itemStack.get(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get());
        return component != null ? ARGB.opaque(component.color()) : ARGB.opaque(defaultColor);
    }

    @Override
    public MapCodec<DecoratedPumpkinItemTintSource> type() {
        return MAP_CODEC;
    }
}
