package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.core.layout.Element;
import com.chimericdream.opus.core.layout.WidgetSizer;
import com.chimericdream.opus.core.widget.RecipePanel;
import com.chimericdream.opus.core.widget.RecipeSpec;
import com.chimericdream.opus.core.widget.SpecException;
import com.chimericdream.opus.core.widget.WidgetSpecs;
import com.chimericdream.opus.core.widget.WidgetTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Locale;

/**
 * Draws recipe, item and entity widgets inside the boxes {@link WidgetSizer#DEFAULT} reserves for them. Recipes
 * are drawn on a grey panel that imitates the vanilla GUI for that recipe type (crafting table, furnace,
 * smithing table, ...); the positions all come from {@link RecipePanel}, which the layout engine also uses to
 * reserve the space.
 *
 * <p>Compiles against 26.2; not yet run in game. The panel is drawn with plain fills in the vanilla colours
 * rather than vanilla textures, so it looks right without depending on texture coordinates.
 */
final class BookWidgets {
    private static final int SLOT = WidgetSizer.SLOT;

    private static final int PANEL_BLACK = 0xFF000000;
    private static final int PANEL_BODY = 0xFFC6C6C6;
    private static final int PANEL_LIGHT = 0xFFFFFFFF;
    private static final int PANEL_SHADOW = 0xFF555555;
    private static final int SLOT_BODY = 0xFF8B8B8B;
    private static final int SLOT_SHADOW = 0xFF373737;
    private static final int GAUGE = 0xFF9C9C9C;
    private static final int TITLE_COLOR = 0xFF404040;
    private static final int FOOTER_COLOR = 0xFF606060;

    private final Font font;
    private final BookTheme theme;

    BookWidgets(Font font, BookTheme theme) {
        this.font = font;
        this.theme = theme;
    }

    void draw(GuiGraphicsExtractor g, Element.WidgetBox box, int x, int y, int mouseX, int mouseY) {
        try {
            switch (box.widget().type()) {
                case WidgetTypes.RECIPE -> recipe(g, RecipeSpec.parse(box.widget().props()), x, y, mouseX, mouseY);
                case WidgetTypes.ITEM -> item(g, WidgetSpecs.ItemSpec.parse(box.widget().props()), x, y, mouseX, mouseY);
                case WidgetTypes.ENTITY -> entity(g, WidgetSpecs.EntitySpec.parse(box.widget().props()), box, x, y);
                default -> {
                }
            }
        } catch (SpecException e) {
            // Unreachable for validated widgets (the layout shows the error instead), but never crash a screen.
            g.text(font, Component.literal(e.getMessage()), x, y, theme.error(), false);
        }
    }

    // ---- recipes ----

    private void recipe(GuiGraphicsExtractor g, RecipeSpec spec, int x, int y, int mouseX, int mouseY) {
        // TODO(tier 2): when spec.recipeId() is set, ask the server for that recipe first and draw what it returns,
        // falling back to this inline definition if the server does not have it. The id is parsed but unused today.
        RecipePanel panel = RecipePanel.of(spec);

        drawPanel(g, x, y, panel.width(), panel.height());
        g.text(font, title(panel), x + RecipePanel.PAD, y + RecipePanel.TITLE_Y, TITLE_COLOR, false);

        for (RecipePanel.Slot slot : panel.slots()) {
            int sx = x + slot.x();
            int sy = y + slot.y();
            drawSlot(g, sx, sy, slot.size());

            ItemStack stack = null;
            if (slot.source() == RecipePanel.RESULT) {
                if (spec.result() != null) {
                    stack = ItemLookup.stack(spec.result(), spec.count());
                }
            } else if (slot.source() >= 0 && slot.source() < spec.grid().size()) {
                RecipeSpec.Ingredient ingredient = spec.grid().get(slot.source());
                if (!ingredient.isEmpty()) {
                    stack = ItemLookup.stack(cycle(ingredient.alternatives()), 1);
                }
            }

            if (stack != null) {
                drawStack(g, stack, sx, sy, slot.size(), mouseX, mouseY);
            }
        }

        drawArrow(g, x + panel.arrow().x(), y + panel.arrow().y());
        if (panel.flame() != null) {
            drawFlame(g, x + panel.flame().x(), y + panel.flame().y());
        }
        if (panel.footerY() >= 0) {
            g.text(font, footer(panel), x + RecipePanel.PAD, y + panel.footerY(), FOOTER_COLOR, false);
        }
    }

    private MutableComponent title(RecipePanel panel) {
        MutableComponent title = panel.literalTitle() != null
            ? Component.literal(panel.literalTitle())
            : Component.translatable(panel.titleKey());

        if (panel.shapeless()) {
            title = title.append(Component.literal(" ")).append(Component.translatable("opus.recipe.shapeless"));
        }

        return title;
    }

    private MutableComponent footer(RecipePanel panel) {
        MutableComponent line = Component.empty();
        if (panel.cookingTime() != null) {
            int ticks = panel.cookingTime();
            String seconds = ticks % 20 == 0 ? Integer.toString(ticks / 20) : String.format(Locale.ROOT, "%.1f", ticks / 20.0);
            line.append(Component.translatable("opus.recipe.cooking_time", seconds));
        }
        if (panel.experience() != null) {
            if (panel.cookingTime() != null) {
                line.append(Component.literal(" · "));
            }
            line.append(Component.translatable("opus.recipe.experience", String.format(Locale.ROOT, "%s", panel.experience())));
        }

        return line;
    }

    // ---- items and mobs ----

    private void item(GuiGraphicsExtractor g, WidgetSpecs.ItemSpec spec, int x, int y, int mouseX, int mouseY) {
        drawSlot(g, x, y, SLOT);
        drawStack(g, ItemLookup.stack(spec.id(), spec.count()), x, y, SLOT, mouseX, mouseY);
        if (spec.label() != null) {
            g.text(font, Component.literal(spec.label()), x + SLOT + 4, y + (SLOT - font.lineHeight) / 2, theme.ink(), false);
        }
    }

    private void entity(GuiGraphicsExtractor g, WidgetSpecs.EntitySpec spec, Element.WidgetBox box, int x, int y) {
        // TODO(entity): render the live entity. In 26.x entities draw through render states, so this needs the
        // 26.2 equivalent of InventoryScreen.renderEntityInInventory; all-hallows-steve's CarvingStationScreen
        // shows how GuiItemRenderState is queued for items and is the best starting point. Until then, a labelled
        // placeholder keeps the layout honest.
        g.fill(x, y, x + box.width(), y + box.height(), theme.codeBackground());
        String label = spec.id().startsWith("minecraft:") ? spec.id().substring("minecraft:".length()) : spec.id();
        g.text(font, Component.literal(font.plainSubstrByWidth(label, Math.max(1, box.width() - 8))), x + 4, y + 4, theme.muted(), false);
    }

    // ---- vanilla-style pieces ----

    /** The grey container panel: black outline with clipped corners, white top/left and dark bottom/right bevel. */
    private void drawPanel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fill(x + 1, y, x + w - 1, y + h, PANEL_BLACK);
        g.fill(x, y + 1, x + w, y + h - 1, PANEL_BLACK);
        g.fill(x + 3, y + 3, x + w - 3, y + h - 3, PANEL_BODY);
        g.fill(x + 1, y + 1, x + w - 2, y + 3, PANEL_LIGHT);
        g.fill(x + 1, y + 1, x + 3, y + h - 2, PANEL_LIGHT);
        g.fill(x + 2, y + h - 3, x + w - 1, y + h - 1, PANEL_SHADOW);
        g.fill(x + w - 3, y + 2, x + w - 1, y + h - 1, PANEL_SHADOW);
    }

    /** A recessed slot: dark top/left edge, white bottom/right edge, mid-grey inside. */
    private void drawSlot(GuiGraphicsExtractor g, int x, int y, int size) {
        g.fill(x, y, x + size, y + size, SLOT_SHADOW);
        g.fill(x + 1, y + 1, x + size, y + size, PANEL_LIGHT);
        g.fill(x + 1, y + 1, x + size - 1, y + size - 1, SLOT_BODY);
    }

    /** The empty progress arrow: a 14x5 shaft and an 8px triangular head, in the slot grey. */
    private void drawArrow(GuiGraphicsExtractor g, int x, int y) {
        g.fill(x, y + 5, x + 14, y + 10, SLOT_BODY);
        for (int i = 0; i < 8; i++) {
            g.fill(x + 14 + i, y + i, x + 15 + i, y + RecipePanel.ARROW_H - i, SLOT_BODY);
        }
    }

    /** The empty fuel gauge: three continuous wavy lines, like the vanilla furnace's unlit flame. */
    private void drawFlame(GuiGraphicsExtractor g, int x, int y) {
        for (int column = 0; column < 3; column++) {
            int cx = x + 1 + column * 5;
            for (int row = 0; row < 4; row++) {
                int offset = (row + column) % 2;
                g.fill(cx + offset, y + 1 + row * 3, cx + offset + 2, y + 4 + row * 3, GAUGE);
            }
        }
    }

    private void drawStack(GuiGraphicsExtractor g, ItemStack stack, int slotX, int slotY, int slotSize, int mouseX, int mouseY) {
        int inset = (slotSize - 16) / 2;
        g.item(stack, slotX + inset, slotY + inset);
        if (stack.getCount() > 1) {
            g.itemDecorations(font, stack, slotX + inset, slotY + inset);
        }
        if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
            g.setTooltipForNextFrame(font, stack.getHoverName(), mouseX, mouseY);
        }
    }

    /** Alternatives (e.g. every plank) take turns, one per second, like the recipe book. */
    private static String cycle(List<String> alternatives) {
        return alternatives.get((int) ((Util.getMillis() / 1000) % alternatives.size()));
    }
}
