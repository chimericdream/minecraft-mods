package com.chimericdream.villagertweaks.mixin;

import com.chimericdream.villagertweaks.advancement.VillagerTweaksAdvancements;
import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import com.chimericdream.villagertweaks.item.ModItems;
import com.chimericdream.villagertweaks.tag.ModTags;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

@Mixin(Villager.class)
public abstract class VTVillagerEntityMixin extends AbstractVillager {
    @Unique
    private final UUID GLOBAL_UUID = UUID.fromString("00000001-0000-0001-0000-000100000001");

    @Unique
    private final Gson vt$gson = new Gson();

    @Unique
    private Component vt$prevName = null;

    @Unique
    private boolean vt$wasPrevNameVisible = false;

    @Unique
    private boolean vt$isGrowthTimerShowing = false;

    @Shadow
    public @Final GossipContainer gossips;

    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    public abstract boolean getVillagerDataFinalized();

    public VTVillagerEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "getPlayerReputation", at = @At("HEAD"), cancellable = true)
    private void injected(Player player, CallbackInfoReturnable<Integer> cir) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        // Neither tweak is on: let vanilla read the player's own gossip, all types included.
        if (!config.enableGlobalReputation && config.enableBadReputation) {
            return;
        }

        // vt_overrideSettingGossip files every reputation event under GLOBAL_UUID, so reads have to
        // look there too — otherwise global reputation is written but never read back.
        UUID playerId = config.enableGlobalReputation ? GLOBAL_UUID : player.getUUID();

        // Vanilla counts every gossip type; disabling bad reputation just drops the negative ones.
        Predicate<GossipType> gossipTypes = config.enableBadReputation
            ? (t) -> true
            : (t) -> t != GossipType.MINOR_NEGATIVE && t != GossipType.MAJOR_NEGATIVE;

        cir.setReturnValue(this.gossips.getReputation(playerId, gossipTypes));
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

                if (player instanceof ServerPlayer serverPlayer) {
                    vt$checkPiedPiper(world, serverPlayer);
                }
            }
        }
    }

    /**
     * Counts every villager within luring range of {@code player} that's independently eligible to be
     * tempted toward them right now, and awards Pied Piper once 8 or more are tempted at once. Each
     * villager tempted this tick runs this same check, so the count only needs to be right, not
     * deduplicated across callers.
     */
    @Unique
    private void vt$checkPiedPiper(ServerLevel world, ServerPlayer player) {
        AABB searchArea = player.getBoundingBox().inflate(12.0);
        int temptedCount = 0;

        for (Villager villager : world.getEntitiesOfClass(Villager.class, searchArea)) {
            if (
                villager.isNoAi()
                    || villager.getLastHurtByMob() != null
                    || villager.isPanicking()
                    || villager.isTrading()
            ) {
                continue;
            }

            if (world.getNearestPlayer(villager, 12.0f) == player) {
                temptedCount++;
            }
        }

        if (temptedCount >= VillagerTweaksAdvancements.PIED_PIPER_THRESHOLD) {
            VillagerTweaksAdvancements.award(player, VillagerTweaksAdvancements.PIED_PIPER);
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void vt_bagTheVillager(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        if (!this.level().isClientSide()) {
            ItemStack itemStack = player.getItemInHand(hand);

            if (itemStack.getItem() == Items.LEAD && config.enableNitwitLeashing && this.getVillagerData().profession().is(VillagerProfession.NITWIT)) {
                cir.setReturnValue(InteractionResult.PASS);
                return;
            }

            if (itemStack.getItem() == Items.BUNDLE && player.isShiftKeyDown()) {
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

                if (this.getVillagerData().level() >= VillagerData.MAX_VILLAGER_LEVEL && player instanceof ServerPlayer serverPlayer) {
                    VillagerTweaksAdvancements.award(serverPlayer, VillagerTweaksAdvancements.BAG_AND_TAG);
                }

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

    @Inject(method = "tick", at = @At("HEAD"))
    private void vt$onGrowthTick(CallbackInfo ci) {
        VillagerTweaksConfig config = VillagerTweaksConfig.HANDLER.instance();

        if (this.level().isClientSide() || !this.isBaby() || !config.displayGrowUpTime || this.getAge() >= 0) {
            return;
        }

        int secondsLeft = -this.getAge() / 20;
        if (vt$isGrowthTimerShowing) {
            this.setCustomName(vt$getFormattedTime(secondsLeft));
        } else {
            if (this.hasCustomName()) {
                this.vt$prevName = this.getCustomName();
                this.vt$wasPrevNameVisible = this.isCustomNameVisible();
            }

            this.setCustomName(vt$getFormattedTime(secondsLeft));

            this.setCustomNameVisible(true);
            this.vt$isGrowthTimerShowing = true;
        }
    }

    @Inject(method = "ageBoundaryReached", at = @At("TAIL"))
    private void vt$onAgeBoundaryReached(CallbackInfo ci) {
        if (this.isBaby() || !this.vt$isGrowthTimerShowing) {
            return;
        }

        if (this.vt$prevName != null) {
            this.setCustomName(this.vt$prevName);
            this.setCustomNameVisible(this.vt$wasPrevNameVisible);
        } else {
            this.setCustomName(null);
            this.setCustomNameVisible(false);
        }

        this.vt$isGrowthTimerShowing = false;
        this.vt$prevName = null;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void vt$writePreviousGrowthNameData(ValueOutput view, CallbackInfo ci) {
        if (this.vt$prevName != null) {
            String json = this.vt$gson.toJson(ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, this.vt$prevName).getOrThrow());
            view.putString("VTPrevGrowthName", json);
            view.putBoolean("VTWasPrevGrowthNameVisible", this.vt$wasPrevNameVisible);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void vt$readPreviousGrowthNameData(ValueInput view, CallbackInfo ci) {
        if (view.getString("VTPrevGrowthName").isPresent()) {
            String jsonString = view.getStringOr("VTPrevGrowthName", "Uh oh!");

            this.vt$prevName = ComponentSerialization.CODEC
                .decode(JsonOps.INSTANCE, this.vt$gson.fromJson(jsonString, JsonElement.class))
                .getOrThrow()
                .getFirst();

            this.vt$wasPrevNameVisible = view.getBooleanOr("VTWasPrevGrowthNameVisible", false);
            this.vt$isGrowthTimerShowing = true;
        }
    }

    // @TODO: candidate for extracting into chimeric-lib
    @Unique
    private Component vt$getFormattedTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds - (minutes * 60);

        return Component.nullToEmpty(minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
    }
}
