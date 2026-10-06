package com.chimericdream.opus.core.widget;

import java.util.Map;

/** Parsers and validation for the simple widget types. See {@link RecipeSpec} for recipes. */
public final class WidgetSpecs {
    private WidgetSpecs() {
    }

    /** {@code item} widget: show one item, optionally with a count and a caption. */
    public record ItemSpec(String id, int count, String label) {
        public static ItemSpec parse(Map<String, Object> props) throws SpecException {
            Object id = props.containsKey("id") ? props.get("id") : props.get("value");
            int count = props.get("count") instanceof Number n ? n.intValue() : 1;
            if (count < 1 || count > 99) {
                throw new SpecException("'count' must be between 1 and 99");
            }

            Object label = props.get("label");
            return new ItemSpec(Ids.normalize(id == null ? null : String.valueOf(id)), count, label == null ? null : String.valueOf(label));
        }
    }

    /** {@code entity} widget: a rotating mob preview. */
    public record EntitySpec(String id, float scale, String label) {
        public static EntitySpec parse(Map<String, Object> props) throws SpecException {
            Object id = props.containsKey("id") ? props.get("id") : props.get("value");
            float scale = props.get("scale") instanceof Number n ? n.floatValue() : 1.0f;
            if (scale <= 0 || scale > 8) {
                throw new SpecException("'scale' must be greater than 0 and at most 8");
            }

            Object label = props.get("label");
            return new EntitySpec(Ids.normalize(id == null ? null : String.valueOf(id)), scale, label == null ? null : String.valueOf(label));
        }
    }

    /** Returns an error message for an invalid widget body, or {@code null} when it is valid. */
    public static String validate(String type, Map<String, Object> props) {
        try {
            switch (type) {
                case WidgetTypes.RECIPE -> RecipeSpec.parse(props);
                case WidgetTypes.ITEM -> ItemSpec.parse(props);
                case WidgetTypes.ENTITY -> EntitySpec.parse(props);
                default -> {
                    return "unknown widget type '" + type + "'";
                }
            }

            return null;
        } catch (SpecException e) {
            return e.getMessage();
        }
    }
}
