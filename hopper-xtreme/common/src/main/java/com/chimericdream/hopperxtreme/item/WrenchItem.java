package com.chimericdream.hopperxtreme.item;

import com.chimericdream.lib.item.AbstractWrenchItem;
import com.chimericdream.hopperxtreme.ModInfo;
import com.chimericdream.hopperxtreme.component.HopperXtremeComponentTypes;
import com.chimericdream.hopperxtreme.component.HopperXtremeWrenchModeComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.hopperxtreme.HopperXtremeMod.REGISTRY_HELPER;

/**
 * The Hopper Wrench. In {@link Mode#ROTATE} mode (the default) it is the shared wrench: it turns blocks and, via
 * the hopper blocks, switches multi-hopper sides. In {@link Mode#COPY_PASTE} mode it copies a hopper's filter and
 * pastes it onto other hoppers instead (see {@link FilterCopying} and {@link WrenchEvents}). A keybind switches
 * modes (see {@code WrenchKeybindings}).
 */
public class WrenchItem extends AbstractWrenchItem {
    public static final Identifier ITEM_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "tools/wrench");

    public enum Mode {
        ROTATE("rotate"),
        COPY_PASTE("copy_paste");

        private final String id;

        Mode(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public Mode next() {
            Mode[] modes = values();

            return modes[(ordinal() + 1) % modes.length];
        }

        public static Mode fromId(String id) {
            for (Mode mode : values()) {
                if (mode.id.equals(id)) {
                    return mode;
                }
            }

            return ROTATE;
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public WrenchItem() {
        super(
            new Properties()
                .stacksTo(1)
                .arch$tab(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .useItemDescriptionPrefix()
                .setId(REGISTRY_HELPER.makeItemRegistryKey(ITEM_ID))
        );
    }

    public static Mode getMode(ItemStack stack) {
        HopperXtremeWrenchModeComponent component = stack.get(HopperXtremeComponentTypes.HOPPER_XTREME_WRENCH_MODE_COMPONENT.get());

        return component == null ? Mode.ROTATE : Mode.fromId(component.mode());
    }

    /** Switches modes. Anything copied is forgotten, so re-entering Copy/Paste mode starts with a clean slate. */
    public static void setMode(ItemStack stack, Mode mode) {
        stack.set(HopperXtremeComponentTypes.HOPPER_XTREME_WRENCH_MODE_COMPONENT.get(), new HopperXtremeWrenchModeComponent(mode.id()));
        stack.remove(HopperXtremeComponentTypes.HOPPER_XTREME_COPIED_FILTER_COMPONENT.get());
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        // Copy/Paste mode never rotates anything. Its hopper clicks are handled earlier by WrenchEvents.
        if (getMode(context.getItemInHand()) == Mode.COPY_PASTE) {
            return InteractionResult.PASS;
        }

        return super.useOn(context);
    }

    /**
     * Switches the mode of the Hopper Wrench the player is holding (main hand first). Called on the server when the
     * player presses the mode keybind; does nothing if they aren't holding one.
     */
    public static void cycleMode(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);

            if (stack.getItem() instanceof WrenchItem) {
                Mode newMode = getMode(stack).next();

                setMode(stack, newMode);
                player.sendOverlayMessage(Component.translatable("item.hopperxtreme.tools.wrench.mode." + newMode.id()));

                return;
            }
        }
    }
}
