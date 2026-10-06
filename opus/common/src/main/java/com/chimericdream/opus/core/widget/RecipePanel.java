package com.chimericdream.opus.core.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Where everything goes inside the vanilla-style GUI panel drawn around a recipe: the title, the input and result
 * slots, the arrow and (for furnaces) the flame. Both the layout engine (to reserve space) and the client
 * renderer (to draw) read these numbers, so the two can never disagree. All coordinates are pixels relative to
 * the panel's top-left corner.
 *
 * <p>The proportions follow the vanilla screens: slots are 18px, the result slot is a larger 26px box in
 * crafting and furnace-style recipes, and the title sits at y=6 with the first row of slots at y=16.
 *
 * @param titleKey     translation key of the vanilla container title ("Crafting", "Furnace", ...)
 * @param literalTitle shown instead of the translated title when not {@code null} (an unresolved recipe id)
 * @param shapeless    the renderer appends a "(Shapeless)" label to the title
 * @param flame        {@code null} when there is no fuel gauge
 * @param footerY      y of the cooking-info line, or {@code -1} when there is none
 */
public record RecipePanel(
    int width,
    int height,
    String titleKey,
    String literalTitle,
    boolean shapeless,
    List<Slot> slots,
    Box arrow,
    Box flame,
    int footerY,
    Integer cookingTime,
    Double experience
) {
    public static final int SLOT = 18;
    public static final int BIG_SLOT = 26;
    public static final int PAD = 8;
    public static final int TITLE_Y = 6;
    public static final int BODY_Y = 16;
    public static final int ARROW_W = 22;
    public static final int ARROW_H = 15;
    public static final int FLAME = 14;
    private static final int GAP = 7;
    private static final int BOTTOM_PAD = 6;
    private static final int FOOTER_HEIGHT = 11;

    /** {@code source}: an index into {@link RecipeSpec#grid()}, or {@link #RESULT} / {@link #EMPTY}. */
    public record Slot(int x, int y, int size, int source) {
    }

    public record Box(int x, int y, int width, int height) {
    }

    /** Slot source: the recipe's result. */
    public static final int RESULT = -1;
    /** Slot source: a slot drawn empty (a furnace's fuel slot). */
    public static final int EMPTY = -2;

    public static RecipePanel of(RecipeSpec spec) {
        return switch (spec.kind()) {
            case CRAFTING_SHAPED, CRAFTING_SHAPELESS, BY_ID -> crafting(spec);
            case SMELTING, BLASTING, SMOKING -> furnace(spec);
            case CAMPFIRE_COOKING -> campfire(spec);
            case STONECUTTING -> stonecutting();
            case SMITHING -> smithing();
        };
    }

    private static RecipePanel crafting(RecipeSpec spec) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            slots.add(new Slot(PAD + (i % 3) * SLOT, BODY_Y + (i / 3) * SLOT, SLOT, i));
        }

        int gridRight = PAD + 3 * SLOT;
        int rowCenter = BODY_Y + (3 * SLOT) / 2;
        int arrowX = gridRight + GAP;
        int resultX = arrowX + ARROW_W + GAP;
        slots.add(new Slot(resultX, rowCenter - BIG_SLOT / 2, BIG_SLOT, RESULT));

        boolean unresolved = spec.kind() == RecipeSpec.Kind.BY_ID;
        return new RecipePanel(
            resultX + BIG_SLOT + PAD, BODY_Y + 3 * SLOT + BOTTOM_PAD,
            "container.crafting", unresolved ? spec.recipeId() : null,
            spec.kind() == RecipeSpec.Kind.CRAFTING_SHAPELESS,
            List.copyOf(slots), new Box(arrowX, rowCenter - ARROW_H / 2, ARROW_W, ARROW_H), null,
            -1, null, null
        );
    }

    private static RecipePanel furnace(RecipeSpec spec) {
        String title = switch (spec.kind()) {
            case BLASTING -> "container.blast_furnace";
            case SMOKING -> "container.smoker";
            default -> "container.furnace";
        };

        // Input on top, fuel below with a flame gauge between them, then the arrow and a big result.
        int inputY = BODY_Y;
        int fuelY = BODY_Y + 2 * SLOT;
        int bodyBottom = fuelY + SLOT;
        int rowCenter = (inputY + bodyBottom) / 2;

        List<Slot> slots = new ArrayList<>();
        slots.add(new Slot(PAD, inputY, SLOT, 0));
        slots.add(new Slot(PAD, fuelY, SLOT, EMPTY));

        Box flame = new Box(PAD + (SLOT - FLAME) / 2, inputY + SLOT + (SLOT - FLAME) / 2, FLAME, FLAME);

        int arrowX = PAD + SLOT + GAP;
        int resultX = arrowX + ARROW_W + GAP;
        slots.add(new Slot(resultX, rowCenter - BIG_SLOT / 2, BIG_SLOT, RESULT));

        return withFooter(spec, resultX + BIG_SLOT + PAD, bodyBottom + BOTTOM_PAD,
            title, slots, new Box(arrowX, rowCenter - ARROW_H / 2, ARROW_W, ARROW_H), flame);
    }

    private static RecipePanel campfire(RecipeSpec spec) {
        int rowCenter = BODY_Y + (3 * SLOT) / 2;
        List<Slot> slots = new ArrayList<>();
        slots.add(new Slot(PAD, rowCenter - SLOT / 2, SLOT, 0));

        int arrowX = PAD + SLOT + GAP;
        int resultX = arrowX + ARROW_W + GAP;
        slots.add(new Slot(resultX, rowCenter - BIG_SLOT / 2, BIG_SLOT, RESULT));

        return withFooter(spec, resultX + BIG_SLOT + PAD, BODY_Y + 3 * SLOT + BOTTOM_PAD,
            "block.minecraft.campfire", slots, new Box(arrowX, rowCenter - ARROW_H / 2, ARROW_W, ARROW_H), null);
    }

    private static RecipePanel stonecutting() {
        int rowCenter = BODY_Y + SLOT / 2;
        int arrowX = PAD + SLOT + GAP;
        int resultX = arrowX + ARROW_W + GAP;
        List<Slot> slots = List.of(
            new Slot(PAD, BODY_Y, SLOT, 0),
            new Slot(resultX, BODY_Y, SLOT, RESULT)
        );

        return new RecipePanel(
            resultX + SLOT + PAD, BODY_Y + SLOT + BOTTOM_PAD,
            "container.stonecutter", null, false,
            slots, new Box(arrowX, rowCenter - ARROW_H / 2, ARROW_W, ARROW_H), null,
            -1, null, null
        );
    }

    private static RecipePanel smithing() {
        // Template, base and addition sit side by side, as in the smithing table.
        int rowCenter = BODY_Y + SLOT / 2;
        int arrowX = PAD + 3 * SLOT + GAP;
        int resultX = arrowX + ARROW_W + GAP;
        List<Slot> slots = List.of(
            new Slot(PAD, BODY_Y, SLOT, 0),
            new Slot(PAD + SLOT, BODY_Y, SLOT, 1),
            new Slot(PAD + 2 * SLOT, BODY_Y, SLOT, 2),
            new Slot(resultX, BODY_Y, SLOT, RESULT)
        );

        return new RecipePanel(
            resultX + SLOT + PAD, BODY_Y + SLOT + BOTTOM_PAD,
            "container.upgrade", null, false,
            slots, new Box(arrowX, rowCenter - ARROW_H / 2, ARROW_W, ARROW_H), null,
            -1, null, null
        );
    }

    /** Adds the cooking-time / experience line under the slots when the recipe has either. */
    private static RecipePanel withFooter(RecipeSpec spec, int width, int bodyHeight, String titleKey, List<Slot> slots, Box arrow, Box flame) {
        boolean info = spec.cookingTime() != null || spec.experience() != null;
        return new RecipePanel(
            width, info ? bodyHeight + FOOTER_HEIGHT : bodyHeight,
            titleKey, null, false,
            List.copyOf(slots), arrow, flame,
            info ? bodyHeight - BOTTOM_PAD + 2 : -1, spec.cookingTime(), spec.experience()
        );
    }
}
