package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import com.chimericdream.villagertweaks.item.ModItems;
import com.chimericdream.villagertweaks.tag.ModTags;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerGossipType;
import net.minecraft.village.VillagerGossips;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(VillagerEntity.class)
public abstract class VTVillagerEntityMixin extends MerchantEntity {
    @Unique
    private final UUID GLOBAL_UUID = UUID.fromString("00000001-0000-0001-0000-000100000001");

    @Shadow
    public @Final VillagerGossips gossip;

    public VTVillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getReputation", at = @At("HEAD"), cancellable = true)
    private void injected(PlayerEntity player, CallbackInfoReturnable<Integer> cir) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();
        UUID playerId = config.enableGlobalReputation ? GLOBAL_UUID : player.getUuid();

        if (config.enableBadReputation) {
            return;
        }

        cir.setReturnValue(this.gossip.getReputationFor(playerId, (t) -> t != VillagerGossipType.MINOR_NEGATIVE && t != VillagerGossipType.MAJOR_NEGATIVE));
    }

    // This used to be a TemptGoal, and the better way to do it is probably a task, but I couldn't get that to work
    @Inject(method = "mobTick", at = @At("TAIL"))
    public void vt$mobTick(ServerWorld world, CallbackInfo ci) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();
        if (
            this.isAiDisabled()
                || this.getAttacker() != null
                || this.isPanicking()
                || this.hasCustomer()
                || !config.enableEmeraldTemptation
        ) {
            return;
        }

        PlayerEntity player = world.getClosestPlayer(this, 12.0f);
        if (player != null) {
            ItemStack mainHandItem = player.getStackInHand(Hand.MAIN_HAND);
            ItemStack offHandItem = player.getStackInHand(Hand.OFF_HAND);

            if (mainHandItem.isIn(ModTags.TEMPTATION_ITEMS) || offHandItem.isIn(ModTags.TEMPTATION_ITEMS)) {
                this.getMoveControl().moveTo(player.getX(), player.getY(), player.getZ(), 0.5f);
            }
        }
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void vt_bagTheVillager(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!this.getEntityWorld().isClient()) {
            ItemStack itemStack = player.getStackInHand(hand);

            if (itemStack.getItem() == Items.BUNDLE && player.isSneaking()) {
                VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

                this.gossip.startGossip(
                    config.enableGlobalReputation ? GLOBAL_UUID : player.getUuid(),
                    VillagerGossipType.MINOR_NEGATIVE,
                    25
                );

                ItemStack newItemStack = new ItemStack(ModItems.BAGGED_VILLAGER_ITEM.get());
                NbtWriteView writeView = NbtWriteView.create(new ErrorReporter.Impl());
                this.writeCustomData(writeView);
                NbtComponent nbtComponent = NbtComponent.of(writeView.getNbt());

                newItemStack.set(DataComponentTypes.CUSTOM_NAME, this.getCustomName());
                newItemStack.set(DataComponentTypes.CUSTOM_DATA, nbtComponent);

                player.giveItemStack(newItemStack);
                itemStack.decrement(1);
                this.discard();
                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "onInteractionWith", at = @At(value = "HEAD"), cancellable = true)
    private void vt_overrideSettingGossip(EntityInteraction interaction, Entity entity, CallbackInfo ci) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();
        if (!config.enableGlobalReputation) {
            return;
        }

        if (entity instanceof PlayerEntity) {
            if (interaction == EntityInteraction.ZOMBIE_VILLAGER_CURED) {
                this.gossip.startGossip(GLOBAL_UUID, VillagerGossipType.MAJOR_POSITIVE, 20);
                this.gossip.startGossip(GLOBAL_UUID, VillagerGossipType.MINOR_POSITIVE, 25);
            } else if (interaction == EntityInteraction.TRADE) {
                this.gossip.startGossip(GLOBAL_UUID, VillagerGossipType.TRADING, 2);
            } else if (interaction == EntityInteraction.VILLAGER_HURT) {
                this.gossip.startGossip(GLOBAL_UUID, VillagerGossipType.MINOR_NEGATIVE, 25);
            } else if (interaction == EntityInteraction.VILLAGER_KILLED) {
                this.gossip.startGossip(GLOBAL_UUID, VillagerGossipType.MAJOR_NEGATIVE, 25);
            }

            ci.cancel();
        }
    }
}
