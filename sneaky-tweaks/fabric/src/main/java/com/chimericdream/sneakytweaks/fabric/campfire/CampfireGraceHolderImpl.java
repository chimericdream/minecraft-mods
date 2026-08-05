package com.chimericdream.sneakytweaks.fabric.campfire;

import com.chimericdream.sneakytweaks.ModInfo;
import com.chimericdream.sneakytweaks.campfire.CampfireGraceHolder;
import com.chimericdream.sneakytweaks.config.SneakyTweaksConfig;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public final class CampfireGraceHolderImpl implements CampfireGraceHolder.Provider {
    public static final AttachmentType<Integer> CAMPFIRE_GRACE_TICKS = AttachmentRegistry.create(
        Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "campfire_grace_ticks"),
        builder -> builder
            .initializer(() -> SneakyTweaksConfig.HANDLER.instance().campfireGraceTicks)
            .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.all())
    );

    @Override
    public int getCampfireGraceTicks(Player player) {
        return ((AttachmentTarget) player).getAttachedOrCreate(CAMPFIRE_GRACE_TICKS);
    }

    @Override
    public void setCampfireGraceTicks(Player player, int ticks) {
        ((AttachmentTarget) player).setAttached(CAMPFIRE_GRACE_TICKS, ticks);
    }
}
