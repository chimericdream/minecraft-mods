package com.chimericdream.minekea.network;

import com.chimericdream.minekea.ModInfo;
import com.chimericdream.minekea.data.nbt.NbtHelpers;
import com.chimericdream.minekea.item.tools.BlockPainterItem;
import com.chimericdream.minekea.registry.ColoredBlocksRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import java.util.List;

public class ServerNetworking {
    public static Identifier CYCLE_PAINTER_COLOR = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "events/items/painter/cycle");

    public static void init() {
    }

    protected static void handleCyclePainterColorPacket(MinecraftServer server, ServerPlayer player) {
        if (server != null && player != null) {
            server.execute(() -> {
                ItemStack heldItem = ItemStack.EMPTY;

                if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof BlockPainterItem) {
                    heldItem = player.getMainHandItem();
                }
                if (!player.getOffhandItem().isEmpty() && player.getOffhandItem().getItem() instanceof BlockPainterItem) {
                    heldItem = player.getOffhandItem();
                }

                if (heldItem.isEmpty()) {
                    return;
                }

                ColoredBlocksRegistry.BlockColor nextColor = BlockPainterItem.getNextColor(heldItem);

                CompoundTag nbt = new CompoundTag();
                nbt.putString("current_color", nextColor.toString());
                heldItem.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                heldItem.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(nextColor.getModelNumber()), List.of()));

                NbtHelpers.setCustomDataFromNbt(heldItem, BlockPainterItem.makeNbt(nbt, nextColor));
            });
        }
    }
}
