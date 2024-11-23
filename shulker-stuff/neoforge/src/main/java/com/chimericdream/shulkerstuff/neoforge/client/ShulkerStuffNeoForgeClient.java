package com.chimericdream.shulkerstuff.neoforge.client;

import com.chimericdream.shulkerstuff.ModInfo;
import com.chimericdream.shulkerstuff.client.ShulkerStuffClient;
import com.chimericdream.shulkerstuff.neoforge.client.render.item.ShulkerBoxItemRenderer;
import net.minecraft.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = ModInfo.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ShulkerStuffNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(RegisterEvent event) {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ShulkerStuffClient.onInitializeClient();
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(
            new ShulkerBoxItemRenderer.ShulkerBoxItemRendererExtension(),
            Items.SHULKER_BOX
        );
    }
}
