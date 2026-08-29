package com.chimericdream.logallthethings.windowlog.client;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Direction;

/**
 * The glass-only portion of a hand-authored Blockbench block model (see
 * {@code assets/logallthethings/models/block/*.json}) describing exactly how a pane fills a
 * particular stair/slab shape's open notch. Only elements that reference the pane's two texture slots
 * - the flat face and the thin cut edge - are kept; the solid host-shaped elements in the source file
 * are discarded, because the real host block is already rendered separately (see
 * {@code WindowedBlockEntityRenderer}) from its own real, unmodified model.
 *
 * <p>Which texture <em>key</em> is which slot isn't fixed - Blockbench numbers texture slots by
 * creation order, so different files in this set use different key names (stairs' example files use
 * {@code "1"}/{@code "2"}, slabs' use {@code "4"}/{@code "5"}) for the same two roles. This identifies
 * the flat/edge slots by their placeholder texture <em>value</em> - every file in this set uses
 * vanilla's own {@code block/glass}/{@code block/glass_pane_top} as that placeholder (matching
 * {@code template_glass_pane_post.json}'s "pane"/"edge" slots), regardless of which key it's assigned
 * to. Resolution happens per-face (each face's own {@code texture} reference is looked up to its value
 * and compared directly) rather than by first picking one key for each placeholder value up front:
 * Blockbench conventionally also points a model's {@code particle} key at the same texture as the
 * primary face key, so a value can legitimately belong to more than one key. Picking a single "the" key
 * for a value ahead of time is ambiguous in that case, and depending on the backing map's iteration
 * order can silently resolve to the key no face actually uses - dropping every face that referenced the
 * other one instead.
 */
public record WindowFrameGeometry(List<Element> elements) {
    public record Element(float[] from, float[] to, Map<Direction, Face> faces, Rotation rotation) {
    }

    public record Face(float[] uv, int rotation, int paneTextureSlot) {
    }

    /**
     * A Blockbench per-element pivot rotation ({@code x}/{@code y}/{@code z} degrees about
     * {@code origin}, all three axes applicable at once - unlike vanilla's own model format, which
     * only allows a single axis). Used by this set's {@code _top} variants (and the ns/ew orientation
     * swap) to reuse one element's geometry for another orientation instead of hand-rotating its
     * {@code from}/{@code to} coordinates. Applied in the renderer as X, then Y, then Z, matching
     * Blockbench's own composition order.
     */
    public record Rotation(float x, float y, float z, float[] origin) {
    }

    private static final String FLAT_PLACEHOLDER = "block/glass";
    private static final String EDGE_PLACEHOLDER = "block/glass_pane_top";

    public static final class Json {
        Map<String, String> textures;
        List<ElementJson> elements;
    }

    public static final class ElementJson {
        float[] from;
        float[] to;
        Map<String, FaceJson> faces;
        RotationJson rotation;
    }

    public static final class RotationJson {
        float x;
        float y;
        float z;
        float[] origin;
    }

    public static final class FaceJson {
        float[] uv;
        int rotation;
        String texture;
    }

    public static WindowFrameGeometry fromJson(Json json) {
        List<Element> elements = new ArrayList<>();

        if (json.elements != null) {
            for (ElementJson elementJson : json.elements) {
                Map<Direction, Face> faces = new EnumMap<>(Direction.class);

                if (elementJson.faces != null) {
                    for (Map.Entry<String, FaceJson> entry : elementJson.faces.entrySet()) {
                        Direction direction = Direction.byName(entry.getKey());
                        FaceJson faceJson = entry.getValue();
                        int slot = paneTextureSlot(json.textures, faceJson.texture);

                        if (direction != null && slot > 0) {
                            faces.put(direction, new Face(faceJson.uv, faceJson.rotation, slot));
                        }
                    }
                }

                if (!faces.isEmpty()) {
                    Rotation rotation = elementJson.rotation != null
                        ? new Rotation(elementJson.rotation.x, elementJson.rotation.y, elementJson.rotation.z, elementJson.rotation.origin)
                        : null;
                    elements.add(new Element(elementJson.from, elementJson.to, faces, rotation));
                }
            }
        }

        return new WindowFrameGeometry(elements);
    }

    /**
     * Resolves a face's own {@code texture} reference (e.g. {@code "#glass"}) to its value in the
     * model's top-level {@code textures} map and compares that value directly against the flat/edge
     * placeholders - see the class doc for why this must happen per-face rather than by resolving one
     * canonical key per placeholder up front.
     */
    private static int paneTextureSlot(Map<String, String> textures, String textureRef) {
        if (textures == null || textureRef == null || !textureRef.startsWith("#")) {
            return 0;
        }

        String value = textures.get(textureRef.substring(1));
        if (value == null) {
            return 0;
        }
        if (value.equals(FLAT_PLACEHOLDER) || value.equals("minecraft:" + FLAT_PLACEHOLDER)) {
            return 1;
        }
        if (value.equals(EDGE_PLACEHOLDER) || value.equals("minecraft:" + EDGE_PLACEHOLDER)) {
            return 2;
        }
        return 0;
    }
}
