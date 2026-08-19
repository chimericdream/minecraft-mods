package com.chimericdream.camelnostrils.neoforge.attachment;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.entity.CN$CamelSnoutState;
import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.animal.camel.Camel;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class CN$CamelSnoutStateImpl implements CN$CamelSnoutState.Provider {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ModInfo.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> HAS_SNOUT = ATTACHMENT_TYPES.register(
        "camel_has_snout",
        () -> AttachmentType.builder(() -> true)
            .serialize(Codec.BOOL.fieldOf("has_snout"))
            .sync(ByteBufCodecs.BOOL)
            .build()
    );

    @Override
    public boolean hasSnout(Camel camel) {
        return camel.getData(HAS_SNOUT.get());
    }

    @Override
    public void setHasSnout(Camel camel, boolean hasSnout) {
        camel.setData(HAS_SNOUT.get(), hasSnout);
    }
}
