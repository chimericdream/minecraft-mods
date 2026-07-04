package com.chimericdream.miniblockmerchants.util;

import com.chimericdream.miniblockmerchants.data.MiniblockTextures;
import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import java.util.Collections;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

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

    public static MerchantOffers makeOfferList(MerchantOffer... offers) {
        MerchantOffers list = new MerchantOffers();
        Collections.addAll(list, offers);
        return list;
    }

    public static MerchantOffer makeOffer(String name, String texture, int[] id) {
        return makeOffer(new ItemCost(Items.EMERALD), name, texture, id);
    }

    public static MerchantOffer makeOffer(ItemCost buyItem, String name, String texture, int[] id) {
        GameProfile gameProfile = DataUtil.makeGameProfile("mmminiblock", new Tuple<>(texture, id));

        ItemStack sellHead = Items.PLAYER_HEAD.getDefaultInstance();
        sellHead.set(DataComponents.PROFILE, ResolvableProfile.createResolved(gameProfile));
        sellHead.set(DataComponents.ITEM_NAME, Component.nullToEmpty(name));

        return new MerchantOffer(buyItem, sellHead, 99999, 0, 0.0f);
    }
}
