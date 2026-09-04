package com.chimericdream.camelnostrils.fabric.attachment;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.entity.CN$CamelSnoutState;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.camel.Camel;

public final class CN$CamelSnoutStateImpl implements CN$CamelSnoutState.Provider {
    public static final AttachmentType<Boolean> HAS_SNOUT = AttachmentRegistry.create(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "camel_has_snout"),
        builder -> builder
            .initializer(() -> true)
            .persistent(Codec.BOOL)
            .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all())
    );

    @Override
    public boolean hasSnout(Camel camel) {
        return ((AttachmentTarget) camel).getAttachedOrCreate(HAS_SNOUT);
    }

    @Override
    public void setHasSnout(Camel camel, boolean hasSnout) {
        ((AttachmentTarget) camel).setAttached(HAS_SNOUT, hasSnout);
    }
}
