package com.chimericdream.polycraft.client;

import com.chimericdream.polycraft.ModInfo;
import com.chimericdream.polycraft.config.PolyCraftConfig;
import com.chimericdream.polycraft.network.ConflictOptionsPayload;
import com.chimericdream.polycraft.network.SelectRecipePayload;
import com.chimericdream.polycraft.network.SetPinPayload;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The recipe picker on a crafting or furnace screen: a small button that, when the server says there is a choice, opens a
 * horizontal list of the possible results. It draws and handles input itself on top of the screen, so it needs no
 * changes to the screen classes.
 * <p>
 * All sizes come from the constants below, and the textures are the maintainer's own art
 * ({@code toggle_list_button}, {@code toggle_list_button_pressed}, {@code result_list_item},
 * {@code result_list_item_active}).
 */
public final class ConflictOverlay {
    public static final int BUTTON_SIZE = 16;
    public static final int LOCK_SIZE = 12;
    public static final int ITEM_SIZE = 30;
    public static final int ARROW_WIDTH = 12;
    public static final int ARROW_HEIGHT = 17;

    private static final int PADDING = 4;
    private static final int ARROW_GAP = 2;
    private static final int LOCK_GAP = 2;
    private static final int ITEM_INSET = (ITEM_SIZE - 16) / 2;

    private static final Identifier BUTTON = texture("toggle_list_button");
    private static final Identifier BUTTON_PRESSED = texture("toggle_list_button_pressed");
    private static final Identifier LOCK = texture("lock_toggle");
    private static final Identifier LOCK_ON = texture("lock_toggle_on");
    private static final Identifier ITEM = texture("result_list_item");
    private static final Identifier ITEM_ACTIVE = texture("result_list_item_active");
    private static final Identifier ARROW_FORWARD = Identifier.withDefaultNamespace("recipe_book/page_forward");
    private static final Identifier ARROW_FORWARD_HOVER = Identifier.withDefaultNamespace("recipe_book/page_forward_highlighted");
    private static final Identifier ARROW_BACK = Identifier.withDefaultNamespace("recipe_book/page_backward");
    private static final Identifier ARROW_BACK_HOVER = Identifier.withDefaultNamespace("recipe_book/page_backward_highlighted");

    private static final int PANEL_BODY = 0xFFC6C6C6;
    private static final int PANEL_LIGHT = 0xFFFFFFFF;
    private static final int PANEL_DARK = 0xFF555555;
    private static final int PANEL_EDGE = 0xFF000000;

    private static final int KEY_ESCAPE = 256;
    private static final int KEY_ENTER = 257;
    private static final int KEY_KP_ENTER = 335;
    private static final int KEY_SPACE = 32;
    private static final int KEY_RIGHT = 262;
    private static final int KEY_LEFT = 263;

    private static @Nullable ConflictOverlay current;

    private final AbstractContainerScreen<?> screen;
    private boolean open;
    private int scroll;
    private int cursor;

    private ConflictOverlay(AbstractContainerScreen<?> screen) {
        this.screen = screen;
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "textures/gui/" + name + ".png");
    }

    /** True for the screens PolyCraft puts a picker on. */
    public static boolean supports(Screen screen) {
        return screen instanceof AbstractContainerScreen<?> container
            && (container.getMenu() instanceof AbstractCraftingMenu || container.getMenu() instanceof AbstractFurnaceMenu
            || container.getMenu() instanceof CrafterMenu || container.getMenu() instanceof SmithingMenu);
    }

    public static void onInit(Screen screen) {
        current = supports(screen) ? new ConflictOverlay((AbstractContainerScreen<?>) screen) : null;
    }

    public static void onRender(Screen screen, GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        ConflictOverlay overlay = forScreen(screen);
        if (overlay != null) {
            overlay.render(graphics, mouseX, mouseY);
        }
    }

    public static boolean onMouseClicked(Screen screen, double x, double y, int button) {
        ConflictOverlay overlay = forScreen(screen);
        return overlay != null && overlay.mouseClicked(x, y, button);
    }

    public static boolean onMouseScrolled(Screen screen, double x, double y, double scrollY) {
        ConflictOverlay overlay = forScreen(screen);
        return overlay != null && overlay.mouseScrolled(x, y, scrollY);
    }

    public static boolean onKeyPressed(Screen screen, int key) {
        ConflictOverlay overlay = forScreen(screen);
        return overlay != null && overlay.keyPressed(key);
    }

    private static @Nullable ConflictOverlay forScreen(Screen screen) {
        ConflictOverlay overlay = current;
        return overlay != null && overlay.screen == screen ? overlay : null;
    }

    // ---- state ----

    private @Nullable ConflictOptionsPayload payload() {
        ConflictOptionsPayload payload = ClientConflictState.forContainer(screen.getMenu().containerId);
        if (payload == null) {
            open = false;
        }

        return payload;
    }

    private int visibleCount(ConflictOptionsPayload payload) {
        return Math.min(payload.options().size(), PolyCraftConfig.get().visibleOptionCount());
    }

    private boolean needsArrows(ConflictOptionsPayload payload) {
        return payload.options().size() > visibleCount(payload);
    }

    private void clampScroll(ConflictOptionsPayload payload) {
        scroll = Math.max(0, Math.min(scroll, payload.options().size() - visibleCount(payload)));
    }

    private void toggle(ConflictOptionsPayload payload) {
        open = !open;
        if (open) {
            cursor = Math.max(0, payload.selected());
            scroll = cursor - visibleCount(payload) / 2;
            clampScroll(payload);
        }
    }

    private void choose(ConflictOptionsPayload payload, int index) {
        ConflictOptionsPayload.Entry entry = payload.options().get(index);
        if (!entry.enabled()) {
            return;
        }

        open = false;
        if (index == payload.selected()) {
            return;
        }

        NetworkManager.sendToServer(new SelectRecipePayload(payload.containerId(), payload.groupKey(), entry.recipeId()));
        // Show the choice straight away; the server's own update follows and replaces this.
        ClientConflictState.set(new ConflictOptionsPayload(payload.containerId(), payload.groupKey(), index, payload.options(), payload.pinnable(), payload.pinned()));
    }

    // ---- geometry ----

    private Slot resultSlot() {
        AbstractContainerMenu menu = screen.getMenu();
        if (menu instanceof AbstractFurnaceMenu furnace) {
            return furnace.getResultSlot();
        }

        if (menu instanceof SmithingMenu smithing) {
            return smithing.getSlot(smithing.getResultSlot());
        }

        if (menu instanceof CrafterMenu) {
            // The Crafter's preview slot is the last one: the nine grid slots and the inventory come before it.
            return menu.slots.getLast();
        }

        return ((AbstractCraftingMenu) menu).getResultSlot();
    }

    /** Above the result slot on every screen, in line with its left edge. */
    private int buttonX() {
        return screen.leftPos + resultSlot().x;
    }

    private int buttonY() {
        return screen.topPos + resultSlot().y - 22;
    }

    private int lockX() {
        return buttonX() + BUTTON_SIZE + LOCK_GAP;
    }

    private int lockY() {
        return buttonY() + (BUTTON_SIZE - LOCK_SIZE) / 2;
    }

    private boolean overLock(double x, double y) {
        return within(x, y, lockX(), lockY(), LOCK_SIZE, LOCK_SIZE);
    }

    private int panelWidth(ConflictOptionsPayload payload) {
        int arrows = needsArrows(payload) ? 2 * (ARROW_WIDTH + ARROW_GAP) : 0;
        return visibleCount(payload) * ITEM_SIZE + arrows + 2 * PADDING;
    }

    private int panelHeight() {
        return ITEM_SIZE + 2 * PADDING;
    }

    private int panelX(ConflictOptionsPayload payload) {
        int centered = screen.leftPos + screen.imageWidth / 2 - panelWidth(payload) / 2;
        return Math.max(2, Math.min(centered, screen.width - panelWidth(payload) - 2));
    }

    private int panelY() {
        int above = screen.topPos - panelHeight() - 2;
        if (above >= 2) {
            return above;
        }

        return Math.min(screen.topPos + screen.imageHeight + 2, screen.height - panelHeight() - 2);
    }

    private int itemsLeft(ConflictOptionsPayload payload) {
        return panelX(payload) + PADDING + (needsArrows(payload) ? ARROW_WIDTH + ARROW_GAP : 0);
    }

    private int arrowY() {
        return panelY() + (panelHeight() - ARROW_HEIGHT) / 2;
    }

    private int leftArrowX(ConflictOptionsPayload payload) {
        return panelX(payload) + PADDING;
    }

    private int rightArrowX(ConflictOptionsPayload payload) {
        return panelX(payload) + panelWidth(payload) - PADDING - ARROW_WIDTH;
    }

    private static boolean within(double px, double py, int x, int y, int w, int h) {
        return px >= x && px < x + w && py >= y && py < y + h;
    }

    private boolean overButton(double x, double y) {
        return within(x, y, buttonX(), buttonY(), BUTTON_SIZE, BUTTON_SIZE);
    }

    private boolean overPanel(ConflictOptionsPayload payload, double x, double y) {
        return within(x, y, panelX(payload), panelY(), panelWidth(payload), panelHeight());
    }

    /** The index of the option under the pointer, or -1. */
    private int optionAt(ConflictOptionsPayload payload, double x, double y) {
        int left = itemsLeft(payload);
        if (!within(x, y, left, panelY() + PADDING, visibleCount(payload) * ITEM_SIZE, ITEM_SIZE)) {
            return -1;
        }

        return scroll + (int) ((x - left) / ITEM_SIZE);
    }

    private boolean overLeftArrow(ConflictOptionsPayload payload, double x, double y) {
        return needsArrows(payload) && scroll > 0 && within(x, y, leftArrowX(payload), arrowY(), ARROW_WIDTH, ARROW_HEIGHT);
    }

    private boolean overRightArrow(ConflictOptionsPayload payload, double x, double y) {
        return needsArrows(payload) && scroll + visibleCount(payload) < payload.options().size()
            && within(x, y, rightArrowX(payload), arrowY(), ARROW_WIDTH, ARROW_HEIGHT);
    }

    // ---- drawing ----

    private void render(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        ConflictOptionsPayload payload = payload();
        if (payload == null) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        drawButton(g, font, payload, mouseX, mouseY);
        if (payload.pinnable()) {
            drawLock(g, font, payload, mouseX, mouseY);
        }

        if (open) {
            clampScroll(payload);
            drawPanel(g, font, payload, mouseX, mouseY);
        }
    }

    private void drawButton(GuiGraphicsExtractor g, Font font, ConflictOptionsPayload payload, int mouseX, int mouseY) {
        int x = buttonX();
        int y = buttonY();
        g.blit(RenderPipelines.GUI_TEXTURED, open ? BUTTON_PRESSED : BUTTON, x, y, 0f, 0f, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);

        boolean hovered = overButton(mouseX, mouseY);
        if (hovered && !open) {
            g.fill(x, y, x + BUTTON_SIZE, y + BUTTON_SIZE, 0x40FFFFFF);
        }

        if (PolyCraftConfig.get().showConflictBadge) {
            String count = String.valueOf(payload.options().size());
            g.text(font, count, x + BUTTON_SIZE + 1 - font.width(count), y - 3, 0xFFFFFFFF, true);
        }

        if (hovered && !open) {
            g.setTooltipForNextFrame(font, List.of(Component.translatable("polycraft.ui.choose_recipe")), Optional.empty(), mouseX, mouseY);
        }
    }

    private void drawLock(GuiGraphicsExtractor g, Font font, ConflictOptionsPayload payload, int mouseX, int mouseY) {
        int x = lockX();
        int y = lockY();
        g.blit(RenderPipelines.GUI_TEXTURED, payload.pinned() ? LOCK_ON : LOCK, x, y, 0f, 0f, LOCK_SIZE, LOCK_SIZE, LOCK_SIZE, LOCK_SIZE);

        if (overLock(mouseX, mouseY)) {
            g.fill(x, y, x + LOCK_SIZE, y + LOCK_SIZE, 0x40FFFFFF);
            Component name = payload.options().get(Math.max(0, payload.selected())).result().getHoverName();
            Component line = payload.pinned()
                ? Component.translatable("polycraft.ui.pin.on", name)
                : Component.translatable("polycraft.ui.pin.off");
            g.setTooltipForNextFrame(font, List.of(line), Optional.empty(), mouseX, mouseY);
        }
    }

    private void drawPanel(GuiGraphicsExtractor g, Font font, ConflictOptionsPayload payload, int mouseX, int mouseY) {
        int x = panelX(payload);
        int y = panelY();
        int w = panelWidth(payload);
        int h = panelHeight();

        g.fill(x, y, x + w, y + h, PANEL_EDGE);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, PANEL_BODY);
        g.fill(x + 1, y + 1, x + w - 1, y + 2, PANEL_LIGHT);
        g.fill(x + 1, y + 1, x + 2, y + h - 1, PANEL_LIGHT);
        g.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, PANEL_DARK);
        g.fill(x + w - 2, y + 1, x + w - 1, y + h - 1, PANEL_DARK);

        int left = itemsLeft(payload);
        int top = y + PADDING;
        int hovered = optionAt(payload, mouseX, mouseY);
        for (int i = 0; i < visibleCount(payload); i++) {
            int index = scroll + i;
            int cellX = left + i * ITEM_SIZE;
            Identifier background = index == payload.selected() ? ITEM_ACTIVE : ITEM;
            g.blit(RenderPipelines.GUI_TEXTURED, background, cellX, top, 0f, 0f, ITEM_SIZE, ITEM_SIZE, ITEM_SIZE, ITEM_SIZE);

            ItemStack stack = payload.options().get(index).result();
            g.item(stack, cellX + ITEM_INSET, top + ITEM_INSET);
            g.itemDecorations(font, stack, cellX + ITEM_INSET, top + ITEM_INSET);

            if (!payload.options().get(index).enabled()) {
                g.fill(cellX + 1, top + 1, cellX + ITEM_SIZE - 1, top + ITEM_SIZE - 1, 0x99C6C6C6);
            } else if (index == hovered) {
                g.fill(cellX + 1, top + 1, cellX + ITEM_SIZE - 1, top + ITEM_SIZE - 1, 0x40FFFFFF);
            }

            if (index == cursor) {
                g.fill(cellX, top, cellX + ITEM_SIZE, top + 1, PANEL_LIGHT);
                g.fill(cellX, top + ITEM_SIZE - 1, cellX + ITEM_SIZE, top + ITEM_SIZE, PANEL_LIGHT);
                g.fill(cellX, top, cellX + 1, top + ITEM_SIZE, PANEL_LIGHT);
                g.fill(cellX + ITEM_SIZE - 1, top, cellX + ITEM_SIZE, top + ITEM_SIZE, PANEL_LIGHT);
            }
        }

        if (needsArrows(payload)) {
            if (scroll > 0) {
                Identifier sprite = overLeftArrow(payload, mouseX, mouseY) ? ARROW_BACK_HOVER : ARROW_BACK;
                g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, leftArrowX(payload), arrowY(), ARROW_WIDTH, ARROW_HEIGHT);
            }

            if (scroll + visibleCount(payload) < payload.options().size()) {
                Identifier sprite = overRightArrow(payload, mouseX, mouseY) ? ARROW_FORWARD_HOVER : ARROW_FORWARD;
                g.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, rightArrowX(payload), arrowY(), ARROW_WIDTH, ARROW_HEIGHT);
            }
        }

        if (hovered >= 0 && hovered < payload.options().size()) {
            ConflictOptionsPayload.Entry entry = payload.options().get(hovered);
            g.setTooltipForNextFrame(font, tooltipFor(entry), Optional.empty(), mouseX, mouseY);
        }
    }

    private static List<Component> tooltipFor(ConflictOptionsPayload.Entry entry) {
        List<Component> lines = new ArrayList<>();
        lines.add(entry.result().getHoverName());
        lines.add(Component.translatable("polycraft.ui.from_mod", modName(entry.namespace())).withStyle(net.minecraft.ChatFormatting.GRAY));
        if (!entry.enabled()) {
            lines.add(Component.translatable("polycraft.ui.cant_take").withStyle(net.minecraft.ChatFormatting.RED));
        }
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            lines.add(Component.literal(entry.recipeId()).withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
        }

        return lines;
    }

    private static String modName(String namespace) {
        if (namespace.equals("minecraft")) {
            return "Minecraft";
        }

        return Platform.getOptionalMod(namespace).map(mod -> mod.getName()).orElse(namespace);
    }

    // ---- input ----

    private boolean mouseClicked(double x, double y, int button) {
        ConflictOptionsPayload payload = payload();
        if (payload == null) {
            return false;
        }

        if (button == 0 && payload.pinnable() && overLock(x, y)) {
            boolean pin = !payload.pinned();
            NetworkManager.sendToServer(new SetPinPayload(payload.containerId(), payload.groupKey(), pin));
            ClientConflictState.set(new ConflictOptionsPayload(payload.containerId(), payload.groupKey(), payload.selected(), payload.options(), true, pin));
            return true;
        }

        if (!open) {
            if (button == 0 && overButton(x, y)) {
                toggle(payload);
                return true;
            }

            return false;
        }

        if (button == 0 && overButton(x, y)) {
            open = false;
            return true;
        }

        if (button == 0 && overLeftArrow(payload, x, y)) {
            scroll--;
            clampScroll(payload);
            return true;
        }

        if (button == 0 && overRightArrow(payload, x, y)) {
            scroll++;
            clampScroll(payload);
            return true;
        }

        int index = optionAt(payload, x, y);
        if (button == 0 && index >= 0 && index < payload.options().size()) {
            choose(payload, index);
            return true;
        }

        // Anywhere else closes the list without picking; a click on the list's own background does nothing else.
        if (!overPanel(payload, x, y)) {
            open = false;
        }

        return true;
    }

    private boolean mouseScrolled(double x, double y, double scrollY) {
        ConflictOptionsPayload payload = payload();
        if (payload == null || !open || scrollY == 0) {
            return false;
        }

        scroll += scrollY > 0 ? -1 : 1;
        clampScroll(payload);
        return true;
    }

    private boolean keyPressed(int key) {
        ConflictOptionsPayload payload = payload();
        if (payload == null || !open) {
            return false;
        }

        int last = payload.options().size() - 1;
        switch (key) {
            case KEY_ESCAPE -> open = false;
            case KEY_LEFT -> moveCursor(payload, Math.max(0, cursor - 1));
            case KEY_RIGHT -> moveCursor(payload, Math.min(last, cursor + 1));
            case KEY_ENTER, KEY_KP_ENTER, KEY_SPACE -> choose(payload, Math.max(0, Math.min(last, cursor)));
            default -> {
                return false;
            }
        }

        return true;
    }

    private void moveCursor(ConflictOptionsPayload payload, int index) {
        cursor = index;
        if (cursor < scroll) {
            scroll = cursor;
        } else if (cursor >= scroll + visibleCount(payload)) {
            scroll = cursor - visibleCount(payload) + 1;
        }

        clampScroll(payload);
    }
}
