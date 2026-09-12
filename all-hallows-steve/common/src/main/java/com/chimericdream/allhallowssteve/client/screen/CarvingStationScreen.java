package com.chimericdream.allhallowssteve.client.screen;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CarvingStationScreen extends AbstractContainerScreen<CarvingStationScreenHandler> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/gui/block/carving_station.png");

    /** One full spin of the output preview every this many milliseconds. */
    private static final long SPIN_PERIOD_MS = 6000L;

    public CarvingStationScreen(CarvingStationScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY) {
        if (slot == this.menu.getSlot(CarvingStationScreenHandler.OUTPUT_SLOT_INDEX) && hasStencils(slot.getItem())) {
            extractRotatingPreview(guiGraphics, slot);
            return;
        }

        super.extractSlot(guiGraphics, slot, mouseX, mouseY);
    }

    private static boolean hasStencils(ItemStack stack) {
        return !stack.getOrDefault(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), PumpkinStencilsComponent.EMPTY).isEmpty();
    }

    /**
     * Spins the carved pumpkin in the output slot in place of the usual flat slot icon, so the player
     * can see every carved face without taking it out. Resolves the item's model fresh each frame the
     * same way a normal slot icon does ({@link ItemModelResolver#updateForTopItem}, under
     * {@code ItemDisplayContext.GUI}) — so it keeps the model's own correct GUI scale/tilt — then
     * overrides each resulting layer's own {@code localTransform} with a Y-axis rotation computed
     * directly from wall-clock time. That local transform composes <em>after</em> the GUI tilt (see
     * {@code ItemStackRenderState.LayerRenderState#applyTransform}), so it spins the model around its
     * own vertical axis exactly the way a real turntable would, with no borrowed dropped-item bob and
     * no discontinuity (a rotation angle wraps seamlessly at 360°, unlike modulo-ing the time input
     * itself).
     *
     * <p>{@code GuiGraphicsExtractor#guiRenderState} has no public accessor — widened in
     * {@code allhallowssteve.accesswidener} alongside this mod's other reach-ins to private datagen/GUI
     * internals that have no supported public equivalent.
     */
    private void extractRotatingPreview(GuiGraphicsExtractor guiGraphics, Slot slot) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemStack stack = slot.getItem();

        RotatingItemStackRenderState state = new RotatingItemStackRenderState();
        ItemModelResolver itemModelResolver = minecraft.getItemModelResolver();
        itemModelResolver.updateForTopItem(state, stack, ItemDisplayContext.GUI, minecraft.level, minecraft.player, 0);

        float angleDegrees = (Util.getMillis() % SPIN_PERIOD_MS) / (float) SPIN_PERIOD_MS * 360.0F;
        // The model's own local space runs from (0,0,0) to (1,1,1) — rotating around the raw origin
        // pivots around a corner, so translate the pivot to the model's center first.
        Matrix4f rotation = new Matrix4f()
            .translate(0.5f, 0.5f, 0.5f)
            .rotateY((float) Math.toRadians(angleDegrees))
            .translate(-0.5f, -0.5f, -0.5f);
        for (ItemStackRenderState.LayerRenderState layer : state.layers()) {
            layer.setLocalTransform(rotation);
        }
        // The GUI item cache keys on model identity; bucketing the angle keeps it from rebaking a
        // fresh texture on literally every frame while still updating often enough to read as smooth.
        state.appendModelIdentityElement(Math.round(angleDegrees / 6.0F));

        // Unlike GuiGraphicsExtractor#entity, GuiItemRenderState's x/y are relative to the captured
        // pose matrix (which already carries this screen's leftPos/topPos) — matching how the default
        // extractSlot passes raw slot.x/slot.y into GuiGraphicsExtractor#item. Adding leftPos/topPos
        // here (as the earlier ItemEntity-based attempt correctly needed for #entity's absolute pixels)
        // double-counts the offset for this API.
        Matrix3x2f pose = new Matrix3x2f(guiGraphics.pose());

        guiGraphics.guiRenderState.addItem(new GuiItemRenderState(pose, state, slot.x, slot.y, null));
    }

    /** Tracks every layer created while resolving the model, so its baked transform can be overridden afterward. */
    private static class RotatingItemStackRenderState extends TrackingItemStackRenderState {
        private final List<LayerRenderState> layers = new ArrayList<>();

        @Override
        public LayerRenderState newLayer() {
            LayerRenderState layer = super.newLayer();
            layers.add(layer);
            return layer;
        }

        List<LayerRenderState> layers() {
            return layers;
        }
    }
}
