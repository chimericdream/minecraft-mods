package com.chimericdream.pannotiacompanion.home;

import com.chimericdream.pannotiacompanion.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Every player's homes, stored with the world. */
public class HomeData extends SavedData {
    public static final Codec<HomeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(UUIDUtil.STRING_CODEC, HomeBook.CODEC).optionalFieldOf("players", Map.of()).forGetter(data -> data.books)
    ).apply(instance, HomeData::new));

    public static final SavedDataType<HomeData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "homes"),
        HomeData::new,
        CODEC,
        DataFixTypes.LEVEL
    );

    private final Map<UUID, HomeBook> books;

    public HomeData() {
        this.books = new HashMap<>();
    }

    private HomeData(Map<UUID, HomeBook> books) {
        this.books = new HashMap<>(books);
    }

    public static HomeData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public HomeBook book(UUID player) {
        return books.computeIfAbsent(player, id -> new HomeBook());
    }

    /** Call after changing a {@link HomeBook} obtained from {@link #book}. */
    public void changed() {
        setDirty();
    }
}
