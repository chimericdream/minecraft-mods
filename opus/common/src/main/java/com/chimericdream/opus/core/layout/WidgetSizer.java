package com.chimericdream.opus.core.layout;

import com.chimericdream.opus.core.model.Block;
import com.chimericdream.opus.core.widget.RecipeSpec;
import com.chimericdream.opus.core.widget.SpecException;
import com.chimericdream.opus.core.widget.WidgetSpecs;
import com.chimericdream.opus.core.widget.WidgetTypes;

/**
 * Decides how much room a widget needs. The renderer must draw each widget inside the size reported here.
 */
public interface WidgetSizer {
    record Size(int width, int height) {
    }

    Size measure(Block.Widget widget, int availableWidth);

    /** Slot size used by every widget; matches the vanilla inventory slot. */
    int SLOT = 18;

    /** Sizes that match the vanilla-style widget drawing in the client renderer. */
    WidgetSizer DEFAULT = (widget, available) -> {
        try {
            return switch (widget.type()) {
                case WidgetTypes.RECIPE -> recipe(RecipeSpec.parse(widget.props()));
                case WidgetTypes.ITEM -> {
                    WidgetSpecs.ItemSpec spec = WidgetSpecs.ItemSpec.parse(widget.props());
                    yield new Size(Math.min(available, SLOT + 4 + (spec.label() == null ? 0 : 6 * spec.label().length())), SLOT);
                }
                case WidgetTypes.ENTITY -> {
                    WidgetSpecs.EntitySpec spec = WidgetSpecs.EntitySpec.parse(widget.props());
                    int w = Math.min(available, Math.round(64 * spec.scale()));
                    int h = Math.max(40, Math.min(160, Math.round(80 * spec.scale())));
                    yield new Size(Math.max(w, 16), h);
                }
                default -> new Size(0, 0);
            };
        } catch (SpecException e) {
            return new Size(0, 0);
        }
    };

    private static Size recipe(RecipeSpec spec) {
        return switch (spec.kind()) {
            case CRAFTING_SHAPED, CRAFTING_SHAPELESS, BY_ID -> new Size(3 * SLOT + 28 + SLOT, 3 * SLOT);
            case SMITHING -> new Size(3 * SLOT + 28 + SLOT, 2 * SLOT);
            default -> new Size(SLOT + 28 + SLOT, 2 * SLOT);
        };
    }
}
