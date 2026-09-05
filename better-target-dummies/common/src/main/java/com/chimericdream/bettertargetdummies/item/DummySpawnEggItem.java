package com.chimericdream.bettertargetdummies.item;

import com.chimericdream.bettertargetdummies.ModInfo;
import com.chimericdream.bettertargetdummies.client.screen.MobPickerMenu;
import com.chimericdream.bettertargetdummies.item.component.BoundMobComponentTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Survival-friendly alternative to a real spawn egg: pick the mob to bind from the mob picker
 * screen (right-click in the air) and right-click a Target Dummy with it to bind that mob. Unlike a
 * spawn egg, it isn't consumed -- the same egg can be re-picked and reused for a different mob at
 * any time. A plain anvil rename (e.g. "Zombie", or an explicit "somemod:custom_mob" id) still works
 * too, as a fallback for anyone who'd rather type the name than search for it.
 */
public class DummySpawnEggItem extends Item {
    public DummySpawnEggItem(Properties properties) {
        super(properties);
    }

    public static Optional<EntityType<?>> resolveEntityType(ItemStack stack) {
        Identifier pickedId = stack.get(BoundMobComponentTypes.BOUND_MOB_TYPE.get());
        if (pickedId != null) {
            Optional<EntityType<?>> picked = BuiltInRegistries.ENTITY_TYPE.getOptional(pickedId);
            if (picked.isPresent()) {
                return picked;
            }
        }

        Component customName = stack.get(DataComponents.CUSTOM_NAME);
        if (customName == null) {
            return Optional.empty();
        }

        String normalized = customName.getString().trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        if (normalized.isEmpty()) {
            return Optional.empty();
        }

        Identifier id = Identifier.tryParse(normalized);
        if (id == null) {
            return Optional.empty();
        }

        return BuiltInRegistries.ENTITY_TYPE.getOptional(id);
    }

    /** Applied by {@link MobPickerMenu} once the player picks a mob from the picker screen. */
    public static void setBoundMobType(ItemStack stack, EntityType<?> type) {
        stack.set(BoundMobComponentTypes.BOUND_MOB_TYPE.get(), EntityType.getKey(type));
    }

    @Override
    public @NotNull InteractionResult use(Level world, @NonNull Player player, @NonNull InteractionHand hand) {
        if (!world.isClientSide()) {
            if (player.isShiftKeyDown()) {
                ItemStack heldEgg = player.getItemInHand(hand);
                heldEgg.remove(BoundMobComponentTypes.BOUND_MOB_TYPE.get());
            } else {
                player.openMenu(new MenuProvider() {
                    @Override
                    public @NotNull Component getDisplayName() {
                        return Component.translatable(ModInfo.MOD_ID + ".mob_picker.title");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int syncId, @NonNull Inventory inv, @NonNull Player player) {
                        return new MobPickerMenu(syncId, inv);
                    }
                });
            }
        }

        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(
        @NonNull ItemStack stack,
        @NonNull TooltipContext context,
        @NonNull TooltipDisplay display,
        @NonNull Consumer<Component> tooltipAdder,
        @NonNull TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, context, display, tooltipAdder, tooltipFlag);

        Optional<EntityType<?>> bound = resolveEntityType(stack);
        if (bound.isPresent()) {
            tooltipAdder.accept(Component.translatable(ModInfo.MOD_ID + ".dummy_spawn_egg.bound", bound.get().getDescription()));
        } else {
            tooltipAdder.accept(Component.translatable(ModInfo.MOD_ID + ".dummy_spawn_egg.unbound"));
        }
    }
}
