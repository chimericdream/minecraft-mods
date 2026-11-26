package com.chimericdream.enchantingfixes.neoforge;

import com.chimericdream.enchantingfixes.EnchantingFixesMod;
import com.chimericdream.enchantingfixes.ModInfo;
import com.chimericdream.enchantingfixes.enchantment.ScalingEnchantments;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(ModInfo.MOD_ID)
public final class EnchantingFixesNeoForge {
    public EnchantingFixesNeoForge(IEventBus bus) {
        EnchantingFixesMod.init();

        bus.addListener(EnchantingFixesNeoForge::addPacks);
    }

    private static void addPacks(AddPackFindersEvent event) {
        // The second argument of PackSource.create is false, so new worlds do not turn the pack on.
        event.addPackFinders(
            Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, ScalingEnchantments.PACK_PATH),
            PackType.SERVER_DATA,
            Component.translatable("pack.enchantingfixes.scaling_enchantments"),
            PackSource.create(PackSource.NO_DECORATION, false),
            false,
            Pack.Position.TOP
        );
    }
}
