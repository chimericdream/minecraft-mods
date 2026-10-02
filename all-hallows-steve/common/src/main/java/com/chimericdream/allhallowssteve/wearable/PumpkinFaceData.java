package com.chimericdream.allhallowssteve.wearable;

import com.chimericdream.allhallowssteve.ModInfo;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The server's record of which face each player has chosen for a worn decorated pumpkin. Stored with the
 * world so a choice survives logging out. A player who never turned their pumpkin has no entry, and a
 * player who cycles back to north has their entry removed, so the data only ever holds real choices.
 */
public class PumpkinFaceData extends SavedData {
    public static final Codec<PumpkinFaceData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.unboundedMap(UUIDUtil.STRING_CODEC, Direction.CODEC).optionalFieldOf("faces", Map.of()).forGetter(data -> data.faces)
    ).apply(instance, PumpkinFaceData::new));

    public static final SavedDataType<PumpkinFaceData> TYPE = new SavedDataType<>(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "worn_pumpkin_faces"),
        PumpkinFaceData::new,
        CODEC,
        DataFixTypes.LEVEL
    );

    private final Map<UUID, Direction> faces;

    public PumpkinFaceData() {
        this.faces = new HashMap<>();
    }

    private PumpkinFaceData(Map<UUID, Direction> faces) {
        this.faces = new HashMap<>(faces);
    }

    public static PumpkinFaceData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public Direction get(UUID player) {
        return faces.getOrDefault(player, PumpkinFaces.DEFAULT);
    }

    /** Turns {@code player}'s pumpkin to the next face and returns the new face. */
    public Direction cycle(UUID player) {
        Direction next = PumpkinFaces.next(get(player));

        if (next == PumpkinFaces.DEFAULT) {
            faces.remove(player);
        } else {
            faces.put(player, next);
        }

        setDirty();

        return next;
    }

    /** Every non-default choice, by player. */
    public Map<UUID, Direction> all() {
        return Map.copyOf(faces);
    }
}
