package com.chimericdream.villagertweaks.item;

import com.chimericdream.villagertweaks.ModInfo;
import com.chimericdream.villagertweaks.config.VillagerTweaksConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.Block.canSupportCenter;

public class BaggedVillagerItem extends Item {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "bagged_villager");

    public BaggedVillagerItem(Properties settings) {
        super(settings);
    }

    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();

        if (context.getClickedFace() != Direction.UP || !canSupportCenter(world, context.getClickedPos(), Direction.UP)) {
            return InteractionResult.FAIL;
        }

        if (!(world.getBlockState(context.getClickedPos().above(1)).isAir() && world.getBlockState(context.getClickedPos().above(2)).isAir())) {
            return InteractionResult.FAIL;
        }

        return spawnVillager(context.getLevel(), context.getClickedPos().above(), context.getPlayer(), context.getHand());
    }

    public @NotNull InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!user.onGround()) {
            return InteractionResult.PASS;
        }

        return spawnVillager(world, user.blockPosition(), user, hand);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(
        final @NotNull ItemStack stack,
        final @NotNull TooltipContext context,
        final @NotNull TooltipDisplay display,
        final @NotNull Consumer<Component> builder,
        final @NotNull TooltipFlag flag
    ) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || context.registries() == null) {
            return;
        }

        RegistryOps<Tag> ops = context.registries().createSerializationContext(NbtOps.INSTANCE);
        CompoundTag tag = data.copyTag();

        Optional<VillagerData> villagerData = read(ops, tag, "VillagerData", VillagerData.CODEC);
        if (villagerData.isEmpty()) {
            return;
        }

        VillagerData vd = villagerData.get();
        Component type = vd.type().unwrapKey()
            .map(key -> typeName(key.identifier().getNamespace(), key.identifier().getPath()))
            .orElse(Component.empty())
            .copy().withStyle(ChatFormatting.WHITE);
        Component level = Component.translatable(
            "tooltip.villagertweaks.bagged_villager.level",
            vd.level(),
            Component.translatable("merchant.level." + vd.level())
        ).withStyle(levelColor(vd.level()));
        builder.accept(Component.translatable(
            "tooltip.villagertweaks.bagged_villager.summary",
            type,
            vd.profession().value().name().copy().withStyle(ChatFormatting.DARK_AQUA),
            level
        ).withStyle(ChatFormatting.DARK_GRAY));

        int maxTrades = VillagerTweaksConfig.CONFIG.instance().baggedTooltipMaxTrades;
        MerchantOffers offers = read(ops, tag, "Offers", MerchantOffers.CODEC).orElse(null);
        if (maxTrades <= 0 || offers == null || offers.isEmpty()) {
            return;
        }

        if (!Minecraft.getInstance().hasShiftDown()) {
            builder.accept(Component.translatable("tooltip.villagertweaks.bagged_villager.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        int shown = Math.min(maxTrades, offers.size());
        for (int i = 0; i < shown; i++) {
            builder.accept(Component.literal("• ").append(resultName(offers.get(i))).withStyle(ChatFormatting.GRAY));
        }

        if (offers.size() > shown) {
            builder.accept(Component.translatable("tooltip.villagertweaks.bagged_villager.more", offers.size() - shown).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static ChatFormatting levelColor(int level) {
        return switch (level) {
            case 1 -> ChatFormatting.GRAY;
            case 2 -> ChatFormatting.GREEN;
            case 3 -> ChatFormatting.AQUA;
            case 4 -> ChatFormatting.LIGHT_PURPLE;
            default -> ChatFormatting.GOLD;
        };
    }

    private static <T> Optional<T> read(DynamicOps<Tag> ops, CompoundTag tag, String key, Codec<T> codec) {
        Tag value = tag.get(key);
        if (value == null) {
            return Optional.empty();
        }

        return codec.parse(ops, value).result();
    }

    private static Component typeName(String namespace, String path) {
        String key = "villagertweaks.villager_type." + namespace + "." + path;
        if (Language.getInstance().has(key)) {
            return Component.translatable(key);
        }

        String pretty = path.replace('_', ' ');
        return Component.literal(pretty.substring(0, 1).toUpperCase(Locale.ROOT) + pretty.substring(1));
    }

    private static Component resultName(MerchantOffer offer) {
        ItemStack result = offer.getResult();

        // "Enchanted Book" tells you nothing; show the enchantment it carries instead.
        ItemEnchantments stored = result.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (!stored.isEmpty()) {
            var entry = stored.entrySet().iterator().next();
            return Enchantment.getFullname(entry.getKey(), entry.getIntValue());
        }

        if (result.getCount() > 1) {
            return Component.literal(result.getCount() + " × ").append(result.getHoverName());
        }

        return result.getHoverName();
    }

    private InteractionResult spawnVillager(Level world, BlockPos pos, Player user, InteractionHand hand) {
        Villager villager = EntityTypes.VILLAGER.create(world, EntitySpawnReason.BUCKET);
        assert villager != null;

        try {
            CustomData component = user.getItemInHand(hand).get(DataComponents.CUSTOM_DATA);
            assert component != null;
            ValueInput view = TagValueInput.create(ProblemReporter.DISCARDING, user.registryAccess(), component.copyTag());

            villager.setCustomName(user.getItemInHand(hand).get(DataComponents.CUSTOM_NAME));
            villager.readAdditionalSaveData(view);
            villager.snapTo(pos, 0, 0);

            world.addFreshEntity(villager);

            user.setItemInHand(hand, Items.BUNDLE.getDefaultInstance());
        } catch (Exception e) {
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }
}
