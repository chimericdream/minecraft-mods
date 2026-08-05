package com.chimericdream.sneakytweaks.neoforge.campfire;

import com.chimericdream.sneakytweaks.ModInfo;
import com.chimericdream.sneakytweaks.campfire.CampfireGraceHolder;
import com.chimericdream.sneakytweaks.config.SneakyTweaksConfig;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class CampfireGraceHolderImpl implements CampfireGraceHolder.Provider {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ModInfo.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Integer>> CAMPFIRE_GRACE_TICKS = ATTACHMENT_TYPES.register(
        "campfire_grace_ticks",
        () -> AttachmentType.builder(() -> SneakyTweaksConfig.HANDLER.instance().campfireGraceTicks)
            .sync(ByteBufCodecs.VAR_INT)
            .build()
    );

    @Override
    public int getCampfireGraceTicks(Player player) {
        return player.getData(CAMPFIRE_GRACE_TICKS.get());
    }

    @Override
    public void setCampfireGraceTicks(Player player, int ticks) {
        player.setData(CAMPFIRE_GRACE_TICKS.get(), ticks);
    }
}
