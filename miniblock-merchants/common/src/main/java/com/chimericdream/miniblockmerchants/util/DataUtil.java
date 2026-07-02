package com.chimericdream.miniblockmerchants.util;

import com.chimericdream.miniblockmerchants.data.MiniblockTextures;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.Tuple;

public class DataUtil {
    public static GameProfile makeGameProfile(String id) {
        Tuple<String, int[]> textureData = MiniblockTextures.getTextures(id);
        if (textureData == null) {
            throw new IllegalArgumentException("Invalid texture id: " + id);
        }

        return makeGameProfile(id, textureData);
    }

    public static GameProfile makeGameProfile(String id, Tuple<String, int[]> textureData) {
        UUID uuid = UUIDUtil.uuidFromIntArray(textureData.getB());
        PropertyMap properties = new PropertyMap(
            ImmutableMultimap.<String, Property>builder()
                .put("textures", new Property("textures", textureData.getA()))
                .build()
        );

        return new GameProfile(uuid, id, properties);
    }
}
