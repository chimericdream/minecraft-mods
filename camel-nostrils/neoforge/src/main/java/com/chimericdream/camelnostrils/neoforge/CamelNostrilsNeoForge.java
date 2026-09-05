package com.chimericdream.camelnostrils.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

import com.chimericdream.camelnostrils.CamelNostrilsMod;
import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.client.CamelNostrilsClient;
import com.chimericdream.camelnostrils.entity.CN$CamelSnoutState;
import com.chimericdream.camelnostrils.neoforge.attachment.CN$CamelSnoutStateImpl;

@Mod(ModInfo.MOD_ID)
public final class CamelNostrilsNeoForge {
    public CamelNostrilsNeoForge(IEventBus bus) {
        CamelNostrilsMod.init();

        CN$CamelSnoutState.setProvider(new CN$CamelSnoutStateImpl());
        CN$CamelSnoutStateImpl.ATTACHMENT_TYPES.register(bus);

        // Must run before construction finishes: architectury's own RegisterRenderers listener fires on
        // architectury's mod bus before ours, so any lifecycle event hook here is already too late.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            CamelNostrilsClient.registerEntityRenderers();
        }
    }
}
