package com.chimericdream.lib.util;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public class ProfileUtils {
    public static GameProfile makeGameProfile(String id, Pair<String, int[]> textureData) {
        UUID uuid = UUIDUtil.uuidFromIntArray(textureData.getSecond());
        PropertyMap properties = new PropertyMap(
            ImmutableMultimap.<String, Property>builder()
                .put("textures", new Property("textures", textureData.getFirst()))
                .build()
        );

        return new GameProfile(uuid, id, properties);
    }
}
