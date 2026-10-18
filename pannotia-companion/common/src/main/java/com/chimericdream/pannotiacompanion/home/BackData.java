package com.chimericdream.pannotiacompanion.home;

import com.chimericdream.pannotiacompanion.ModInfo;
import com.chimericdream.pannotiacompanion.config.PannotiaCompanionConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Each player's {@code /pc back} destination, stored with the world. Home, spawn and back teleports record where
 * the player was leaving from; dying records the death spot when enabled in the config.
 */
public class BackData extends SavedData {
    public static final Codec<BackData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(UUIDUtil.STRING_CODEC, BackPoint.CODEC).optionalFieldOf("players", Map.of()).forGetter(data -> data.points)
    ).apply(instance, BackData::new));

    public static final SavedDataType<BackData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "back"),
        BackData::new,
        CODEC,
        DataFixTypes.LEVEL
    );

    private final Map<UUID, BackPoint> points;

    public BackData() {
        this.points = new HashMap<>();
    }

    private BackData(Map<UUID, BackPoint> points) {
        this.points = new HashMap<>(points);
    }

    public static BackData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    /** Remembers where {@code player} is standing now. Call just before teleporting them away. */
    public static void record(ServerPlayer player) {
        get(player.level().getServer()).set(player.getUUID(), BackPoint.of(player));
    }

    /** Remembers where {@code player} died, if death locations are enabled. */
    public static void recordDeath(ServerPlayer player) {
        if (PannotiaCompanionConfig.get().saveDeathLocation) {
            record(player);
        }
    }

    public Optional<BackPoint> get(UUID player) {
        return Optional.ofNullable(points.get(player));
    }

    public void set(UUID player, BackPoint point) {
        points.put(player, point);
        setDirty();
    }
}
