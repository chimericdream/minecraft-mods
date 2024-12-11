package com.chimericdream.hopperxtreme.compat;

import dev.architectury.platform.Platform;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliFlags {
    public static void init() {
        PatchouliAPI.IPatchouliAPI patchouli = PatchouliAPI.get();

        patchouli.setConfigFlag("hopperxtreme:wrench_enabled", !Platform.isModLoaded("minekea"));
    }
}
