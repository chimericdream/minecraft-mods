package com.chimericdream.opus.client.screen;

import com.chimericdream.opus.core.layout.Element;
import com.chimericdream.opus.core.layout.WidgetSizer;
import com.chimericdream.opus.core.widget.RecipeSpec;
import com.chimericdream.opus.core.widget.SpecException;
import com.chimericdream.opus.core.widget.WidgetSpecs;
import com.chimericdream.opus.core.widget.WidgetTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Draws recipe, item and entity widgets inside the boxes {@link WidgetSizer#DEFAULT} reserves for them: 18px
 * slots, a 28px arrow gutter, then the result slot.
 *
 * <p>Compiles against 26.2; not yet run in game. The {@code item}, {@code itemDecorations} and {@code setTooltipForNextFrame}
 * signatures are guesses based on how the other mods call {@code GuiGraphicsExtractor}.
 */
final class BookWidgets {
    private static final int SLOT = WidgetSizer.SLOT;

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

    private void recipe(GuiGraphicsExtractor g, RecipeSpec spec, int x, int y, int mouseX, int mouseY) {
        if (spec.kind() == RecipeSpec.Kind.BY_ID) {
            // TODO(tier 2): resolve by id once recipes are synced from the server.
            g.text(font, Component.literal("recipe: " + spec.recipeId()), x, y, theme.muted(), false);
            return;
        }

        boolean crafting = spec.kind() == RecipeSpec.Kind.CRAFTING_SHAPED || spec.kind() == RecipeSpec.Kind.CRAFTING_SHAPELESS;
        int cols = crafting ? 3 : spec.grid().size();
        int rows = crafting ? 3 : 1;
        int gridWidth = cols * SLOT;
        int gridHeight = rows * SLOT;
        int rowOffset = crafting ? 0 : (SLOT / 2);

        for (int i = 0; i < spec.grid().size(); i++) {
            int sx = x + (i % cols) * SLOT;
            int sy = y + rowOffset + (i / cols) * SLOT;
            slot(g, sx, sy);
            RecipeSpec.Ingredient ingredient = spec.grid().get(i);
            if (!ingredient.isEmpty()) {
                stackSlot(g, cycle(ingredient.alternatives()), 1, sx, sy, mouseX, mouseY);
            }
        }

        int arrowX = x + gridWidth + 6;
        int arrowY = y + rowOffset + (crafting ? gridHeight / 2 : SLOT / 2) - font.lineHeight / 2;
        g.text(font, Component.literal("→"), arrowX, arrowY, theme.ink(), false);

        int resultX = x + gridWidth + 28;
        int resultY = y + rowOffset + (crafting ? SLOT : 0);
        slot(g, resultX, resultY);
        stackSlot(g, spec.result(), spec.count(), resultX, resultY, mouseX, mouseY);
    }

    private void item(GuiGraphicsExtractor g, WidgetSpecs.ItemSpec spec, int x, int y, int mouseX, int mouseY) {
        slot(g, x, y);
        stackSlot(g, spec.id(), spec.count(), x, y, mouseX, mouseY);
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
        g.text(font, Component.literal(spec.id()), x + 4, y + 4, theme.muted(), false);
    }

    private void slot(GuiGraphicsExtractor g, int x, int y) {
        g.fill(x, y, x + SLOT, y + SLOT, 0xFF373737);
        g.fill(x + 1, y + 1, x + SLOT, y + SLOT, 0xFFFFFFFF);
        g.fill(x + 1, y + 1, x + SLOT - 1, y + SLOT - 1, 0xFF8B8B8B);
    }

    private void stackSlot(GuiGraphicsExtractor g, String id, int count, int x, int y, int mouseX, int mouseY) {
        ItemStack stack = ItemLookup.stack(id, count);
        g.item(stack, x + 1, y + 1);
        if (count > 1) {
            g.itemDecorations(font, stack, x + 1, y + 1);
        }
        if (mouseX >= x && mouseX < x + SLOT && mouseY >= y && mouseY < y + SLOT) {
            g.setTooltipForNextFrame(font, stack.getHoverName(), mouseX, mouseY);
        }
    }

    /** Alternatives (e.g. every plank) take turns, one per second, like the recipe book. */
    private static String cycle(List<String> alternatives) {
        return alternatives.get((int) ((Util.getMillis() / 1000) % alternatives.size()));
    }
}
