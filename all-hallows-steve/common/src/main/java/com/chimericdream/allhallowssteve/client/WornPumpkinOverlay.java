package com.chimericdream.allhallowssteve.client;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import com.chimericdream.allhallowssteve.config.AllHallowsSteveConfig;
import com.chimericdream.allhallowssteve.wearable.PumpkinFaces;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * The first-person vision overlay for a worn decorated pumpkin. Vanilla draws an equippable's
 * {@code camera_overlay} texture across the whole screen, in first person only, which is exactly the
 * behavior wanted; the only difference is that this texture isn't a file. The decorated pumpkin item
 * points its camera overlay at {@link #CAMERA_OVERLAY}, and this class keeps the one dynamic texture at
 * that path in step with whatever the local player is wearing: the carved openings of the stencil on
 * their chosen face are see-through, and everything else is the pumpkin's dye color at the opacity set
 * in the config. A pumpkin with nothing carved on that face is all cover, so it blocks the view entirely
 * (at full opacity), which is intentional.
 * <p>
 * The openings are built at runtime from the same 16x16 stencil overlay textures used on the block, so
 * no per-stencil art is needed. Like vanilla's own pumpkin blur, the texture is stretched over the whole
 * screen.
 */
public final class WornPumpkinOverlay {
    /** The camera overlay named on the item (vanilla prefixes it with {@code textures/} and suffixes {@code .png}). */
    public static final Identifier CAMERA_OVERLAY = PumpkinFaces.CAMERA_OVERLAY;

    private static final Identifier TEXTURE_ID = CAMERA_OVERLAY.withPath(path -> "textures/" + path + ".png");

    /** A 16:9 canvas, so one stencil pixel becomes a 20x11.25-ish block when stretched over a typical screen. */
    private static final int WIDTH = 320;
    private static final int HEIGHT = 180;
    private static final int STENCIL_SIZE = 16;

    private static @Nullable DynamicTexture texture;
    private static @Nullable Key lastKey;

    private WornPumpkinOverlay() {
    }

    private record Key(@Nullable String stencil, int color, int opacity) {
    }

    /** Called every client tick; rebuilds the overlay only when the worn pumpkin, its face, or the opacity setting changes. */
    public static void tick(Minecraft minecraft) {
        ensureTexture(minecraft);

        LocalPlayer player = minecraft.player;
        ItemStack head = player == null ? ItemStack.EMPTY : player.getItemBySlot(EquipmentSlot.HEAD);
        if (!PumpkinFaces.isWearable(head)) {
            lastKey = null;

            return;
        }

        Direction face = ClientPumpkinFaces.get(player.getUUID());
        PumpkinStencilsComponent stencils = head.getOrDefault(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), PumpkinStencilsComponent.EMPTY);
        Optional<String> stencil = stencils.get(face);
        int color = head.getOrDefault(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get(), DyedColorComponent.DEFAULT).color();

        Key key = new Key(stencil.orElse(null), color, AllHallowsSteveConfig.pumpkinOverlayOpacity());
        if (key.equals(lastKey)) {
            return;
        }

        lastKey = key;
        rebuild(minecraft, key);
    }

    private static void ensureTexture(Minecraft minecraft) {
        if (texture != null && minecraft.getTextureManager().getTexture(TEXTURE_ID) == texture) {
            return;
        }

        texture = new DynamicTexture("worn_pumpkin_overlay", WIDTH, HEIGHT, true);
        minecraft.getTextureManager().register(TEXTURE_ID, texture);
        lastKey = null;
    }

    private static void rebuild(Minecraft minecraft, Key key) {
        boolean[][] opening = readOpenings(minecraft, key.stencil());
        int cover = ARGB.color(key.opacity() * 255 / 100, ARGB.scaleRGB(ARGB.opaque(key.color()), 0.85F));

        NativeImage pixels = texture.getPixels();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                boolean isOpen = opening != null && opening[y * STENCIL_SIZE / HEIGHT][x * STENCIL_SIZE / WIDTH];
                pixels.setPixel(x, y, isOpen ? 0 : cover);
            }
        }

        texture.upload();
    }

    /** Which of the stencil's 16x16 pixels are carved away, or {@code null} if there is no stencil on this face (or it can't be read). */
    private static boolean @Nullable [][] readOpenings(Minecraft minecraft, @Nullable String stencil) {
        if (stencil == null) {
            return null;
        }

        Identifier path = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/block/decorated_pumpkin/overlays/" + stencil + ".png");

        try (InputStream stream = minecraft.getResourceManager().open(path); NativeImage image = NativeImage.read(stream)) {
            boolean[][] opening = new boolean[STENCIL_SIZE][STENCIL_SIZE];
            for (int y = 0; y < STENCIL_SIZE; y++) {
                for (int x = 0; x < STENCIL_SIZE; x++) {
                    opening[y][x] = x < image.getWidth() && y < image.getHeight() && ARGB.alpha(image.getPixel(x, y)) > 0;
                }
            }

            return opening;
        } catch (IOException | RuntimeException e) {
            return null;
        }
    }
}
