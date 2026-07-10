package com.chimericdream.miniblockmerchants.mixin;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractVillager.class)
abstract public class MMMerchantEntityMixin extends Mob {
    @Shadow
    protected MerchantOffers offers;

    @Shadow
    abstract public boolean isTrading();

    @Shadow
    abstract protected void addParticlesAroundSelf(ParticleOptions parameters);

    @Shadow
    @Nullable
    public abstract Player getTradingPlayer();

    protected MMMerchantEntityMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }
}
