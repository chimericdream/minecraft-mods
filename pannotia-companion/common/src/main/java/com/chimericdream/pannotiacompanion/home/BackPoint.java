package com.chimericdream.pannotiacompanion.home;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/** Where {@code /pc back} sends a player: the spot they last left (or died at). */
public record BackPoint(ResourceKey<Level> dimension, double x, double y, double z, float yRot, float xRot) {
    public static final Codec<BackPoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(BackPoint::dimension),
        Codec.DOUBLE.fieldOf("x").forGetter(BackPoint::x),
        Codec.DOUBLE.fieldOf("y").forGetter(BackPoint::y),
        Codec.DOUBLE.fieldOf("z").forGetter(BackPoint::z),
        Codec.FLOAT.optionalFieldOf("y_rot", 0f).forGetter(BackPoint::yRot),
        Codec.FLOAT.optionalFieldOf("x_rot", 0f).forGetter(BackPoint::xRot)
    ).apply(instance, BackPoint::new));

    public static BackPoint of(ServerPlayer player) {
        return new BackPoint(player.level().dimension(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
    }
}
