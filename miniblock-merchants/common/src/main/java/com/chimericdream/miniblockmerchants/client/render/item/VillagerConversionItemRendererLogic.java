package com.chimericdream.miniblockmerchants.client.render.item;

import com.chimericdream.miniblockmerchants.data.MiniblockTextures;
import com.chimericdream.miniblockmerchants.item.VillagerConversionItem;
import com.chimericdream.miniblockmerchants.util.NbtHelpers;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;

public class VillagerConversionItemRendererLogic {
    private static final SkullEntityModel MODEL = new SkullEntityModel(SkullEntityModel.getHeadTexturedModelData().createModel());

    private static Pair<String, int[]> getHeadData(Item item) {
        VillagerConversionItem conversionItem = (VillagerConversionItem) item;

        return switch (conversionItem.id) {
            case "ancient_shell" -> MiniblockTextures.ANCIENT_SHELL;
            case "book_of_rituals" -> MiniblockTextures.BOOK_OF_RITUALS;
            case "budding_cactus" -> MiniblockTextures.BUDDING_CACTUS;
            case "crystal_phial" -> MiniblockTextures.CRYSTAL_PHIAL;
            case "cultivated_sapling" -> MiniblockTextures.CULTIVATED_SAPLING;
            case "drenched_score_sheet" -> MiniblockTextures.DRENCHED_SCORE_SHEET;
            case "enchanted_red_delicious" -> MiniblockTextures.ENCHANTED_RED_DELICIOUS;
            case "endless_bookshelf" -> MiniblockTextures.ENDLESS_BOOKSHELF;
            case "fine_thread" -> MiniblockTextures.FINE_THREAD;
            case "forgotten_scrap_metal" -> MiniblockTextures.FORGOTTEN_SCRAP_METAL;
            case "fragrant_flower" -> MiniblockTextures.FRAGRANT_FLOWER;
            case "galilean_spyglass" -> MiniblockTextures.GALILEAN_SPYGLASS;
            case "mastercrafted_iron" -> MiniblockTextures.MASTERCRAFTED_IRON;
            case "mixology_station" -> MiniblockTextures.MIXOLOGY_STATION;
            case "overgrown_carrot" -> MiniblockTextures.OVERGROWN_CARROT;
            case "prismatic_honeycomb" -> MiniblockTextures.PRISMATIC_HONEYCOMB;
            case "pure_gold" -> MiniblockTextures.PURE_GOLD;
            case "radiating_redstone" -> MiniblockTextures.RADIATING_REDSTONE;
            case "rotting_recycling_bin" -> MiniblockTextures.ROTTING_RECYCLING_BIN;
            case "sculpting_clay" -> MiniblockTextures.SCULPTING_CLAY;
            case "shimmering_wheat" -> MiniblockTextures.SHIMMERING_WHEAT;
            case "soaked_villager_plushie" -> MiniblockTextures.SOAKED_VILLAGER_PLUSHIE;
            case "sparkling_blaze_powder" -> MiniblockTextures.SPARKLING_BLAZE_POWDER;
            case "stabilized_explosion" -> MiniblockTextures.STABILIZED_EXPLOSION;
            case "unusually_dense_rock" -> MiniblockTextures.UNUSUALLY_DENSE_ROCK;
            case "wagyu_beef" -> MiniblockTextures.WAGYU_BEEF;

            // https://minecraft-heads.com/custom-heads/miscellaneous/54713-trollface
            default ->
                new Pair<>("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWNjZWMzZmMwNmExYjZiZjk2MmQyMzA3MDRiNjYyM2JiZmI0MzA0M2YwNjY3OTY2YzIwYzg5YzMwYzRhMzMwIn19fQ==", new int[]{-1628763693, 1365655993, -1207198870, 1241365991});
        };
    }

    public static void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Pair<String, int[]> data = getHeadData(stack.getItem());

        NbtCompound headTexture = new NbtCompound();
        headTexture.putString("Value", data.getLeft());
        NbtList textureList = new NbtList();
        textureList.add(headTexture);

        NbtCompound properties = new NbtCompound();
        properties.put("textures", textureList);

        NbtCompound owner = new NbtCompound();
        owner.putIntArray("Id", data.getRight());
        owner.put("Properties", properties);

        GameProfile gameProfile = NbtHelpers.toGameProfile(owner);

        RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(SkullBlock.Type.PLAYER, new ProfileComponent(gameProfile));
        SkullBlockEntityRenderer.renderSkull((Direction) null, 180.0F, 0.0F, matrices, vertexConsumers, light, MODEL, renderLayer);
    }
}
