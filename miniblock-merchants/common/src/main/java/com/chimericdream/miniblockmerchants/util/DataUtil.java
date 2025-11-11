package com.chimericdream.miniblockmerchants.util;

import com.chimericdream.miniblockmerchants.data.MiniblockTextures;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.util.Pair;
import net.minecraft.util.Uuids;

import java.util.UUID;

public class DataUtil {
    public static GameProfile makeGameProfile(String id) {
        Pair<String, int[]> textureData = MiniblockTextures.getTextures(id);
        if (textureData == null) {
            throw new IllegalArgumentException("Invalid texture id: " + id);
        }

        return makeGameProfile(id, textureData);
    }

    public static GameProfile makeGameProfile(String id, Pair<String, int[]> textureData) {
        UUID uuid = Uuids.toUuid(textureData.getRight());
        PropertyMap properties = new PropertyMap(
            ImmutableMultimap.<String, Property>builder()
                .put("textures", new Property("textures", textureData.getLeft()))
                .build()
        );

        return new GameProfile(uuid, id, properties);
    }
}
