package com.chimericdream.sneakytweaks.fabric;

import net.fabricmc.api.ModInitializer;

import com.chimericdream.sneakytweaks.SneakyTweaksMod;
import com.chimericdream.sneakytweaks.campfire.CampfireGraceHolder;
import com.chimericdream.sneakytweaks.fabric.campfire.CampfireGraceHolderImpl;

public final class SneakyTweaksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        SneakyTweaksMod.init();

        // Constructing the provider now (rather than lazily, whenever the campfire-grace mixins
        // first touch it) also runs CampfireGraceHolderImpl's <clinit>, registering its
        // AttachmentType during mod init.
        CampfireGraceHolder.setProvider(new CampfireGraceHolderImpl());
    }
}
