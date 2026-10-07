package com.chimericdream.opus.core.layout;

import com.chimericdream.opus.core.model.Block;
import com.chimericdream.opus.core.widget.MobFit;
import com.chimericdream.opus.core.widget.RecipePanel;
import com.chimericdream.opus.core.widget.RecipeSpec;
import com.chimericdream.opus.core.widget.SpecException;
import com.chimericdream.opus.core.widget.WidgetSpecs;
import com.chimericdream.opus.core.widget.WidgetTypes;

import java.util.function.Function;
import java.util.function.IntSupplier;

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
    WidgetSizer DEFAULT = withMobs(id -> null, () -> MobFit.REFERENCE_GUI_SCALE);

    /**
     * Like {@link #DEFAULT}, but an {@code entity} widget gets a box shaped like its mob. {@code mobs} maps a
     * normalised entity id to the mob's collision box, or {@code null} when the mob can't be previewed live (the
     * widget then keeps the old fixed-size placeholder box). {@code guiScale} is the player's GUI scale setting.
     */
    static WidgetSizer withMobs(Function<String, MobFit.Bounds> mobs, IntSupplier guiScale) {
        return (widget, available) -> {
            try {
                return switch (widget.type()) {
                    case WidgetTypes.RECIPE -> recipe(RecipeSpec.parse(widget.props()));
                    case WidgetTypes.ITEM -> {
                        WidgetSpecs.ItemSpec spec = WidgetSpecs.ItemSpec.parse(widget.props());
                        yield new Size(Math.min(available, SLOT + 4 + (spec.label() == null ? 0 : 6 * spec.label().length())), SLOT);
                    }
                    case WidgetTypes.ENTITY -> {
                        WidgetSpecs.EntitySpec spec = WidgetSpecs.EntitySpec.parse(widget.props());
                        MobFit.Bounds bounds = mobs.apply(spec.id());
                        if (bounds != null) {
                            MobFit.Result fit = MobFit.fit(bounds, spec.scale(), available, guiScale.getAsInt());
                            yield new Size(fit.width(), fit.height());
                        }

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
    }

    /** A recipe fills exactly the vanilla-style GUI panel described by {@link RecipePanel}. */
    private static Size recipe(RecipeSpec spec) {
        RecipePanel panel = RecipePanel.of(spec);
        return new Size(panel.width(), panel.height());
    }
}
