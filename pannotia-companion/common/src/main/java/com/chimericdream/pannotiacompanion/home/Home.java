package com.chimericdream.pannotiacompanion.home;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record Home(String name, ResourceKey<Level> dimension, double x, double y, double z, float yRot, float xRot) {
    public static final Codec<Home> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(Home::name),
        Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(Home::dimension),
        Codec.DOUBLE.fieldOf("x").forGetter(Home::x),
        Codec.DOUBLE.fieldOf("y").forGetter(Home::y),
        Codec.DOUBLE.fieldOf("z").forGetter(Home::z),
        Codec.FLOAT.optionalFieldOf("y_rot", 0f).forGetter(Home::yRot),
        Codec.FLOAT.optionalFieldOf("x_rot", 0f).forGetter(Home::xRot)
    ).apply(instance, Home::new));

    public Home withName(String newName) {
        return new Home(newName, dimension, x, y, z, yRot, xRot);
    }
}
