package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import com.chimericdream.villagertweaks.item.ModItems;
import com.chimericdream.villagertweaks.tag.ModTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;

@Mixin(Villager.class)
public abstract class VTVillagerEntityMixin extends AbstractVillager {
    @Unique
    private final UUID GLOBAL_UUID = UUID.fromString("00000001-0000-0001-0000-000100000001");

    @Shadow
    public @Final GossipContainer gossips;

    public VTVillagerEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "getPlayerReputation", at = @At("HEAD"), cancellable = true)
    private void injected(Player player, CallbackInfoReturnable<Integer> cir) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();
        UUID playerId = config.enableGlobalReputation ? GLOBAL_UUID : player.getUUID();

        if (config.enableBadReputation) {
            return;
        }

        cir.setReturnValue(this.gossips.getReputation(playerId, (t) -> t != GossipType.MINOR_NEGATIVE && t != GossipType.MAJOR_NEGATIVE));
    }

    // This used to be a TemptGoal, and the better way to do it is probably a task, but I couldn't get that to work
    @Inject(method = "customServerAiStep", at = @At("TAIL"))
    public void vt$mobTick(ServerLevel world, CallbackInfo ci) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();
        if (
            this.isNoAi()
                || this.getLastHurtByMob() != null
                || this.isPanicking()
                || this.isTrading()
                || !config.enableEmeraldTemptation
        ) {
            return;
        }

        Player player = world.getNearestPlayer(this, 12.0f);
        if (player != null) {
            ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);

            if (mainHandItem.is(ModTags.TEMPTATION_ITEMS) || offHandItem.is(ModTags.TEMPTATION_ITEMS)) {
                this.getMoveControl().setWantedPosition(player.getX(), player.getY(), player.getZ(), 0.5f);
            }
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void vt_bagTheVillager(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!this.level().isClientSide()) {
            ItemStack itemStack = player.getItemInHand(hand);

            if (itemStack.getItem() == Items.BUNDLE && player.isShiftKeyDown()) {
                VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

                this.gossips.add(
                    config.enableGlobalReputation ? GLOBAL_UUID : player.getUUID(),
                    GossipType.MINOR_NEGATIVE,
                    25
                );

                ItemStack newItemStack = new ItemStack(ModItems.BAGGED_VILLAGER_ITEM.get());
                TagValueOutput writeView = TagValueOutput.createWithoutContext(new ProblemReporter.Collector());
                this.addAdditionalSaveData(writeView);
                CustomData nbtComponent = CustomData.of(writeView.buildResult());

                newItemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
                newItemStack.set(DataComponents.CUSTOM_DATA, nbtComponent);

                player.addItem(newItemStack);
                itemStack.shrink(1);
                this.discard();
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "onReputationEventFrom", at = @At(value = "HEAD"), cancellable = true)
    private void vt_overrideSettingGossip(ReputationEventType interaction, Entity entity, CallbackInfo ci) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();
        if (!config.enableGlobalReputation) {
            return;
        }

        if (entity instanceof Player) {
            if (interaction == ReputationEventType.ZOMBIE_VILLAGER_CURED) {
                this.gossips.add(GLOBAL_UUID, GossipType.MAJOR_POSITIVE, 20);
                this.gossips.add(GLOBAL_UUID, GossipType.MINOR_POSITIVE, 25);
            } else if (interaction == ReputationEventType.TRADE) {
                this.gossips.add(GLOBAL_UUID, GossipType.TRADING, 2);
            } else if (interaction == ReputationEventType.VILLAGER_HURT) {
                this.gossips.add(GLOBAL_UUID, GossipType.MINOR_NEGATIVE, 25);
            } else if (interaction == ReputationEventType.VILLAGER_KILLED) {
                this.gossips.add(GLOBAL_UUID, GossipType.MAJOR_NEGATIVE, 25);
            }

            ci.cancel();
        }
    }
}
