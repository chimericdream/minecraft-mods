package com.chimericdream.allhallowssteve.wearable;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

/**
 * Which of a worn decorated pumpkin's four carved faces is the front, and the rules around it.
 * <p>
 * The wearer picks the face with a keybind (see {@code PumpkinKeybindings}); the choice belongs to the
 * player, not the pumpkin item, so identical pumpkins always stack. A face is named by the canonical
 * direction a stencil is stored against ({@code PumpkinStencilsComponent}), and {@link #DEFAULT} (north)
 * is what a pumpkin shows until the player turns it.
 */
public final class PumpkinFaces {
    public static final Direction DEFAULT = Direction.NORTH;

    /** The camera overlay named on the wearable item; the client keeps a dynamic texture at this path (see {@code WornPumpkinOverlay}). */
    public static final Identifier CAMERA_OVERLAY = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "misc/worn_pumpkin_overlay");

    private PumpkinFaces() {
    }

    /** The face after {@code face} in the cycle north, east, south, west, back to north. Anything else counts as {@link #DEFAULT}. */
    public static Direction next(Direction face) {
        return switch (face) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    /**
     * The block-style {@code FACING} a pumpkin should be drawn with so that canonical face {@code front}
     * lands at the wearer's front: the stencil renderer rotates canonical north to {@code FACING}, so
     * bringing east to the front takes the opposite turn (west), and vice versa. North and south are
     * their own opposites.
     */
    public static Direction renderFacing(Direction front) {
        return switch (front) {
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
            default -> front;
        };
    }

    /** Whether {@code stack} is a wearable decorated pumpkin (the unlit one; lit variants can't be worn). */
    public static boolean isWearable(ItemStack stack) {
        return stack.is(ModBlocks.DECORATED_PUMPKIN.get().asItem());
    }
}
