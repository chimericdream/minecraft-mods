package com.chimericdream.miniblockmerchants.mixin;


import com.chimericdream.miniblockmerchants.ModInfo;
import com.chimericdream.miniblockmerchants.advancement.MMAdvancements;
import com.chimericdream.miniblockmerchants.item.VillagerConversionItem;
import com.chimericdream.miniblockmerchants.registry.ModProfessions;
import com.chimericdream.miniblockmerchants.registry.ModStats;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.storage.ReadView;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
abstract public class MMVillagerEntityMixin extends MMMerchantEntityMixin {
    @Shadow
    private void sayNo() {
    }

    @Shadow
    private int experience;

    @Shadow
    abstract public VillagerData getVillagerData();

    @Shadow
    abstract public void reinitializeBrain(ServerWorld world);

    @Shadow
    abstract public void setVillagerData(VillagerData villagerData);

    protected MMVillagerEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique
    private Text mm$getPlayerMessage(String profession) {
        MutableText msg = MutableText.of(PlainTextContent.EMPTY);
        Text lcarat = MutableText.of(PlainTextContent.EMPTY).append("<").formatted(Formatting.WHITE);
        Text rcarat = MutableText.of(PlainTextContent.EMPTY).append(">").formatted(Formatting.WHITE);
        Text name = Text.translatable(String.format("entity.minecraft.villager.%s", profession)).formatted(Formatting.GOLD);

        MutableText greeting = MutableText.of(PlainTextContent.EMPTY);
        greeting.append(Text.translatable(String.format("%s.message.conversion.%s", ModInfo.MOD_ID, profession)).formatted(Formatting.GREEN));
        if (profession.equals("mm$ritualist")) {
            greeting.append(" ").append(Text.translatable(String.format("%s.message.conversion.%s.obfuscated", ModInfo.MOD_ID, profession)).formatted(Formatting.GREEN).formatted(Formatting.OBFUSCATED));
        }

        msg.append(lcarat).append(name).append(rcarat).append(" ").append(greeting);

        return msg;
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void mm$tryConvertingVillager(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.getItem() instanceof VillagerConversionItem item && this.isAlive() && !this.hasCustomer() && !this.isSleeping()) {
            if (this.isBaby()) {
                this.sayNo();
                cir.setReturnValue(ActionResult.SUCCESS);

                return;
            }

            RegistryKey<VillagerProfession> currentProfession = this.getVillagerData().profession().getKey().orElse(VillagerProfession.NONE);
            if (currentProfession == VillagerProfession.NONE) {
                RegistryKey<VillagerProfession> newProfession = ModProfessions.get(item.profession);

                World world = this.getEntityWorld();

                if (world.isClient()) {
                    world.playSoundFromEntity(player, this, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                } else {
                    ((ServerPlayerEntity) player).sendMessage(mm$getPlayerMessage(item.profession), false);

                    RegistryEntry<VillagerType> villager = Registries.VILLAGER_TYPE.getEntry(Registries.VILLAGER_TYPE.get(VillagerType.PLAINS));
                    RegistryEntry<VillagerProfession> profession = Registries.VILLAGER_PROFESSION.getEntry(Registries.VILLAGER_PROFESSION.get(newProfession));

                    this.setVillagerData(new VillagerData(villager, profession, 5));

                    this.offers = ModProfessions.getOffersForProfession(item.profession);
                    this.experience = 250;

                    this.produceParticles(ParticleTypes.HAPPY_VILLAGER);
                    this.setPersistent();

                    this.reinitializeBrain((ServerWorld) world);

                    if (!player.isCreative()) {
                        itemStack.decrement(1);
                    }
                }

                player.incrementStat(Stats.TALKED_TO_VILLAGER);

                cir.setReturnValue(ActionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void mm$setBlockTraderOffers(ReadView view, CallbackInfo ci) {
        VillagerData data = view.read("VillagerData", VillagerData.CODEC).orElseGet(VillagerEntity::createVillagerData);
        String profession = data.profession().getIdAsString();

        if (!profession.startsWith(ModInfo.MOD_ID)) {
            return;
        }

        this.offers = ModProfessions.getOffersForProfession(profession);
        this.experience = 250;
    }

    @Inject(method = "afterUsing", at = @At("TAIL"))
    private void mm$incrementMiniblockTrade(TradeOffer offer, CallbackInfo ci) {
        if (this.getCustomer() instanceof ServerPlayerEntity player && ModProfessions.PROFESSION_LIST.contains(this.getVillagerData().profession())) {
            player.incrementStat(ModStats.TRADE_FOR_MINIBLOCK_ID);
            mm$checkPlayerAdvancements(player);
        }
    }

    @Unique
    private void mm$checkPlayerAdvancements(ServerPlayerEntity player) {
        int tradeCount = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(ModStats.TRADE_FOR_MINIBLOCK_ID));
        MinecraftServer server = player.getEntityWorld().getServer();

        PlayerAdvancementTracker tracker = player.getAdvancementTracker();

        if (tradeCount >= 100) {
            tracker.grantCriterion(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_100_TIMES), "magic");
        }

        if (tradeCount >= 250) {
            tracker.grantCriterion(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_250_TIMES), "magic");
        }

        if (tradeCount >= 500) {
            tracker.grantCriterion(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_500_TIMES), "magic");
        }

        if (tradeCount >= 1000) {
            tracker.grantCriterion(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_1000_TIMES), "magic");
        }

        if (tradeCount >= 5000) {
            tracker.grantCriterion(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_5000_TIMES), "magic");
        }

        if (tradeCount >= 10000) {
            tracker.grantCriterion(MMAdvancements.getAdvancement(server, MMAdvancements.TRADE_10000_TIMES), "magic");
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
    private Text mm$modifyTradeDisplayName(Text original) {
        // Check if this is a custom profession and return custom name
        VillagerData data = this.getVillagerData();
        String profession = data.profession().getIdAsString();

        Text customName = this.getCustomName();

        if (profession.startsWith(ModInfo.MOD_ID)) {
            Text profName = Text.translatable("entity.minecraft.villager." + profession.replace(ModInfo.MOD_ID + ":", ""));
            if (customName == null) {
                return profName;
            }

            return Text.translatable("miniblockmerchants.named_villager", new Object[]{customName, profName});
        }

        return original;
    }
}
