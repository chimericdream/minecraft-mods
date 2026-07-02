package com.chimericdream.miniblockmerchants.mixin;


import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.miniblockmerchants.advancement.MMAdvancements;
import com.chimericdream.miniblockmerchants.item.VillagerConversionItem;
import com.chimericdream.miniblockmerchants.registry.ModProfessions;
import com.chimericdream.miniblockmerchants.registry.ModStats;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
abstract public class MMVillagerEntityMixin extends MMMerchantEntityMixin {
    @Shadow
    private void sayNo() {
    }

    @Shadow
    private int experience;

    @Shadow
    abstract public VillagerData getVillagerData();

    @Shadow
    abstract public void reinitializeBrain(ServerLevel world);

    @Shadow
    abstract public void setVillagerData(VillagerData villagerData);

    protected MMVillagerEntityMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Unique
    private Component mm$getPlayerMessage(String profession) {
        MutableComponent msg = MutableComponent.create(PlainTextContents.EMPTY);
        Component lcarat = MutableComponent.create(PlainTextContents.EMPTY).append("<").withStyle(ChatFormatting.WHITE);
        Component rcarat = MutableComponent.create(PlainTextContents.EMPTY).append(">").withStyle(ChatFormatting.WHITE);
        Component name = Component.translatable(String.format("entity.minecraft.villager.%s", profession)).withStyle(ChatFormatting.GOLD);

        MutableComponent greeting = MutableComponent.create(PlainTextContents.EMPTY);
        greeting.append(Component.translatable(String.format("%s.message.conversion.%s", ModInfo.MOD_ID, profession)).withStyle(ChatFormatting.GREEN));
        if (profession.equals("mm$ritualist")) {
            greeting.append(" ").append(Component.translatable(String.format("%s.message.conversion.%s.obfuscated", ModInfo.MOD_ID, profession)).withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.OBFUSCATED));
        }

        msg.append(lcarat).append(name).append(rcarat).append(" ").append(greeting);

        return msg;
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void mm$tryConvertingVillager(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() instanceof VillagerConversionItem item && this.isAlive() && !this.hasCustomer() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                cir.setReturnValue(InteractionResult.SUCCESS);

                return;
            }

            ResourceKey<VillagerProfession> currentProfession = this.getVillagerData().profession().unwrapKey().orElse(VillagerProfession.NONE);
            if (currentProfession == VillagerProfession.NONE) {
                ResourceKey<VillagerProfession> newProfession = ModProfessions.get(item.profession);

                Level world = this.level();

                if (world.isClientSide()) {
                    world.playSound(player, this, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
                } else {
                    ((ServerPlayer) player).displayClientMessage(mm$getPlayerMessage(item.profession), false);

                    Holder<VillagerType> villager = BuiltInRegistries.VILLAGER_TYPE.wrapAsHolder(BuiltInRegistries.VILLAGER_TYPE.getValue(VillagerType.PLAINS));
                    Holder<VillagerProfession> profession = BuiltInRegistries.VILLAGER_PROFESSION.wrapAsHolder(BuiltInRegistries.VILLAGER_PROFESSION.getValue(newProfession));

                    this.setVillagerData(new VillagerData(villager, profession, 5));

                    this.offers = ModProfessions.getOffersForProfession(item.profession);
                    this.experience = 250;

                    this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
                    this.setPersistenceRequired();

                    this.reinitializeBrain((ServerLevel) world);

                    if (!player.isCreative()) {
                        itemStack.shrink(1);
                    }
                }

                player.awardStat(Stats.TALKED_TO_VILLAGER);

                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void mm$setBlockTraderOffers(ValueInput view, CallbackInfo ci) {
        VillagerData data = view.read("VillagerData", VillagerData.CODEC).orElseGet(Villager::createDefaultVillagerData);
        String profession = data.profession().getRegisteredName();

        if (!profession.startsWith(ModInfo.MOD_ID)) {
            return;
        }

        this.offers = ModProfessions.getOffersForProfession(profession);
        this.experience = 250;
    }

    @Inject(method = "afterUsing", at = @At("TAIL"))
    private void mm$incrementMiniblockTrade(MerchantOffer offer, CallbackInfo ci) {
        if (this.getCustomer() instanceof ServerPlayer player && ModProfessions.isMiniblockMerchant(this.getVillagerData().profession())) {
            player.awardStat(ModStats.TRADE_FOR_MINIBLOCK_ID);
            mm$checkPlayerAdvancements(player);
        }
    }

    @Unique
    private void mm$checkPlayerAdvancements(ServerPlayer player) {
        int tradeCount = player.getStats().getValue(Stats.CUSTOM.get(ModStats.TRADE_FOR_MINIBLOCK_ID));
        MinecraftServer server = player.level().getServer();

        PlayerAdvancements tracker = player.getAdvancements();

        if (tradeCount >= 100) {
            tracker.award(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_100_TIMES), "magic");
        }

        if (tradeCount >= 250) {
            tracker.award(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_250_TIMES), "magic");
        }

        if (tradeCount >= 500) {
            tracker.award(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_500_TIMES), "magic");
        }

        if (tradeCount >= 1000) {
            tracker.award(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_1000_TIMES), "magic");
        }

        if (tradeCount >= 5000) {
            tracker.award(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_5000_TIMES), "magic");
        }

        if (tradeCount >= 10000) {
            tracker.award(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_10000_TIMES), "magic");
        }
    }

    @ModifyArg(
        method = "beginTradeWith",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/passive/VillagerEntity;sendOffers(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/text/Text;I)V"
        ),
        index = 1
    )
    private Component mm$modifyTradeDisplayName(Component original) {
        // Check if this is a custom profession and return custom name
        VillagerData data = this.getVillagerData();
        String profession = data.profession().getRegisteredName();

        Component customName = this.getCustomName();

        if (profession.startsWith(ModInfo.MOD_ID)) {
            Component profName = Component.translatable("entity.minecraft.villager." + profession.replace(ModInfo.MOD_ID + ":", ""));
            if (customName == null) {
                return profName;
            }

            return Component.translatable("miniblockmerchants.named_villager", new Object[]{customName, profName});
        }

        return original;
    }
}
