package com.chimericdream.allhallowssteve.component.type;

import com.chimericdream.allhallowssteve.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

/**
 * Which carved face of a decorated pumpkin should be drawn at the wearer's front. A render-time hint only:
 * it is put on a temporary copy of the pumpkin when it is drawn on a player's head (see
 * {@code AHS$ItemModelResolverMixin}), and never on a real item stack, so it can't affect stacking. The
 * face itself belongs to the player (see {@code PumpkinFaceData}).
 */
public record WornFaceComponent(Direction face) {
    public static final Codec<WornFaceComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
        Direction.CODEC.fieldOf("face").forGetter(WornFaceComponent::face)
    ).apply(builder, WornFaceComponent::new));

    public static final Identifier COMPONENT_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "worn_face");
}
