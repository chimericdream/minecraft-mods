package com.chimericdream.logallthethings;

import com.chimericdream.lib.registries.ModRegistryHelper;
import com.google.common.base.Suppliers;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.registry.registries.RegistrarManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import com.chimericdream.logallthethings.carpetlog.CarpetLogBlocks;
import com.chimericdream.logallthethings.carpetlog.CarpetLogHelper;
import com.chimericdream.logallthethings.snowlog.SnowLogBlocks;
import com.chimericdream.logallthethings.snowlog.SnowLogHelper;
import com.chimericdream.logallthethings.windowlog.WindowLogBlocks;
import com.chimericdream.logallthethings.windowlog.WindowLogHelper;

public final class LogAllTheThingsMod {
    public static Supplier<RegistrarManager> MANAGER;
    public static final Logger LOGGER = LogManager.getLogger(ModInfo.MOD_ID);

    public static final ModRegistryHelper REGISTRY_HELPER = new ModRegistryHelper(ModInfo.MOD_ID, LOGGER);

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(ModInfo.MOD_ID));

        WindowLogBlocks.init();
        CarpetLogBlocks.init();
        SnowLogBlocks.init();

        REGISTRY_HELPER.init();

        // Architectury 20.0.7 (pinned on this branch)'s RightClickBlock.click still returns the older
        // InteractionResult, not the EventResult these helpers return for their GameTest callers'
        // benefit - EventResult#asMinecraft() is Architectury's own bridge between the two.
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> WindowLogHelper.tryWindowLog(player, hand, pos, face).asMinecraft());
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> CarpetLogHelper.tryCarpetLog(player, hand, pos, face).asMinecraft());
        InteractionEvent.RIGHT_CLICK_BLOCK.register((player, hand, pos, face) -> SnowLogHelper.tryPlaceSnow(player, hand, pos, face).asMinecraft());
        // Architectury 20.0.7's BlockEvent.Break also passes an IntValue (the drop-exp accumulator)
        // the payload's method-reference form didn't account for; none of these helpers need it.
        BlockEvent.BREAK.register((level, pos, state, player, exp) -> WindowLogHelper.tryPartialBreak(level, pos, state, player));
        BlockEvent.BREAK.register((level, pos, state, player, exp) -> CarpetLogHelper.tryPartialBreak(level, pos, state, player));
        BlockEvent.BREAK.register((level, pos, state, player, exp) -> SnowLogHelper.tryPartialBreak(level, pos, state, player));
    }
}
