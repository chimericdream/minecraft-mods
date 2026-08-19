package com.chimericdream.camelnostrils.mixin;

import com.chimericdream.camelnostrils.CamelNostrilsMod;
import com.chimericdream.camelnostrils.advancement.CamelNostrilsAdvancements;
import com.chimericdream.camelnostrils.entity.CN$CamelAccessor;
import com.chimericdream.camelnostrils.entity.CN$CamelSnoutState;
import com.chimericdream.lib.util.ProfileUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Camel.class)
public abstract class CN$CamelMixin implements Leashable, CN$CamelAccessor {
    @Unique
    private static ItemStack CAMEL_SNOUT;

    @Override
    public boolean cn$hasSnout() {
        return CN$CamelSnoutState.hasSnout((Camel) (Object) this);
    }

    @Override
    public void leashTooFarBehaviour() {
        Entity holder = this.getLeashHolder();
        Camel self = (Camel) (Object) this;

        if (
            CN$CamelSnoutState.hasSnout(self)
                && holder instanceof Player player
                && player.level() instanceof ServerLevel level
                && player.getRandom().nextFloat() < 0.99f
        ) {
            CamelNostrilsMod.LOGGER.info("Leashing too far... ripping nose off");
            if (CAMEL_SNOUT == null) {
                cn$setupSnout();
            }

            self.spawnAtLocation(level, CAMEL_SNOUT.copy());
            CN$CamelSnoutState.setHasSnout(self, false);

            if (player instanceof ServerPlayer serverPlayer) {
                CamelNostrilsAdvancements.award(serverPlayer, CamelNostrilsAdvancements.CAMEL_NOSTRILS);
            }
        }

        this.dropLeash();
    }

    @Unique
    private void cn$setupSnout() {
        Item headItem = Items.PLAYER_HEAD;
        GameProfile gameProfile = ProfileUtils.makeGameProfile(
            "cn_camel_snout",
            Pair.of(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzY0MmM5ZjcxMTMxYjVkZjRhOGMyMWM4YzZmMTA2ODRmMjJhYmFmYjhjZDY4YTFkNTVhYzRiZjI2M2E1M2EzMSJ9fX0=",
                new int[]{1132229726, 840910171, -1834758831, 1395974109}
            )
        );

        Component formattedName = MutableComponent.create(PlainTextContents.EMPTY)
            .append("Camel Snout")
            .setStyle(Style.EMPTY.withItalic(false));

        ItemStack headStack = new ItemStack(headItem);
        headStack.setCount(1);
        headStack.set(DataComponents.CUSTOM_NAME, formattedName);
        headStack.set(DataComponents.PROFILE, ResolvableProfile.createResolved(gameProfile));

        CN$CamelMixin.CAMEL_SNOUT = headStack.copy();
    }
}
