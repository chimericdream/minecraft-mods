package com.chimericdream.enchantingfixes.enchantment;

import com.chimericdream.enchantingfixes.ModInfo;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.resources.Identifier;

/**
 * State and formulas for the optional Scaling Enchantments data pack. The pack changes the enchantment
 * data. This class holds the changes that the data cannot make.
 */
public final class ScalingEnchantments {
    /** The path of the pack in the mod jar. Fabric and NeoForge both read it from here. */
    public static final String PACK_PATH = "resourcepacks/scaling_enchantments";
    /** A file that only the pack contains. The pack ID is different on each loader, so the mod looks for this file. */
    public static final Identifier MARKER = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "scaling_enchantments.json");

    /** The protection points where vanilla stops, and the damage reduction there (20 / 25). */
    public static final float VANILLA_MAX_POINTS = 20.0F;
    public static final float VANILLA_MAX_REDUCTION = 0.8F;

    /**
     * The enchantment registry loads only when the server starts. A {@code /reload} that turns the pack on
     * or off does not change the enchantments, so this value also changes only when the server starts.
     */
    private static volatile boolean active = false;

    private ScalingEnchantments() {
    }

    public static void init() {
        LifecycleEvent.SERVER_STARTING.register(server -> active = server.getResourceManager().getResource(MARKER).isPresent());
        LifecycleEvent.SERVER_STOPPED.register(server -> active = false);
    }

    public static boolean isActive() {
        return active;
    }

    /**
     * The damage reduction for a number of protection points. Up to 20 points, this is the vanilla value.
     * Above 20 points, the reduction gets closer to {@code limit} but does not reach it.
     *
     * @param limit the reduction limit, from 0.8 to 1.0
     */
    public static float protectionReduction(float points, float limit) {
        if (points <= VANILLA_MAX_POINTS) {
            return Math.max(points, 0.0F) / 25.0F;
        }

        return limit - (limit - VANILLA_MAX_REDUCTION) * VANILLA_MAX_POINTS / points;
    }
}
