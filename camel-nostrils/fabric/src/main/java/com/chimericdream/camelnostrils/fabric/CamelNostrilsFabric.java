package com.chimericdream.camelnostrils.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.camelnostrils.CamelNostrilsMod;
import com.chimericdream.camelnostrils.entity.CN$CamelSnoutState;
import com.chimericdream.camelnostrils.fabric.attachment.CN$CamelSnoutStateImpl;

public final class CamelNostrilsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CamelNostrilsMod.init();

        // Constructing the provider now (rather than lazily, whenever the camel-nostril mixins first
        // touch it) also runs CN$CamelSnoutStateImpl's <clinit>, registering its AttachmentType during
        // mod init.
        CN$CamelSnoutState.setProvider(new CN$CamelSnoutStateImpl());
    }
}
