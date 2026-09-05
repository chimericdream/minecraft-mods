package com.chimericdream.logallthethings.carpetlog.client;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Direction;

/**
 * The carpet-only portion of a hand-authored Blockbench block model (see
 * {@code assets/logallthethings/models/block/*_carpet.json}) describing exactly how a carpet lies
 * across a particular stair/slab shape. Only elements that reference the carpet's own texture slot are
 * kept; the solid host-shaped elements in the source file are discarded, because the real host block is
 * already rendered separately (see {@code CarpetedBlockEntityRenderer}) from its own real, unmodified
 * model. Mirrors {@code windowlog.client.WindowFrameGeometry}, minus the flat/edge texture-slot split -
 * a carpet has only the one texture, applied identically to every face.
 *
 * <p>Which texture <em>key</em> holds that slot isn't fixed - Blockbench numbers texture slots by
 * creation order - so this identifies it by its placeholder texture <em>value</em>: every file in this
 * set uses vanilla's own {@code block/white_wool} as that placeholder, regardless of which key it's
 * assigned to.
 */
public record CarpetFrameGeometry(List<Element> elements) {
    public record Element(float[] from, float[] to, Map<Direction, Face> faces) {
    }

    public record Face(float[] uv, int rotation) {
    }

    private static final String CARPET_PLACEHOLDER = "block/white_wool";

    public static final class Json {
        Map<String, String> textures;
        List<ElementJson> elements;
    }

    public static final class ElementJson {
        float[] from;
        float[] to;
        Map<String, FaceJson> faces;
    }

    public static final class FaceJson {
        float[] uv;
        int rotation;
        String texture;
    }

    public static CarpetFrameGeometry fromJson(Json json) {
        List<Element> elements = new ArrayList<>();

        if (json.elements != null) {
            for (ElementJson elementJson : json.elements) {
                Map<Direction, Face> faces = new EnumMap<>(Direction.class);

                if (elementJson.faces != null) {
                    for (Map.Entry<String, FaceJson> entry : elementJson.faces.entrySet()) {
                        Direction direction = Direction.byName(entry.getKey());
                        FaceJson faceJson = entry.getValue();

                        if (direction != null && isCarpetTexture(json.textures, faceJson.texture)) {
                            faces.put(direction, new Face(faceJson.uv, faceJson.rotation));
                        }
                    }
                }

                if (!faces.isEmpty()) {
                    elements.add(new Element(elementJson.from, elementJson.to, faces));
                }
            }
        }

        return new CarpetFrameGeometry(elements);
    }

    /**
     * Resolves a face's own {@code texture} reference (e.g. {@code "#1"}) to its value in the model's
     * top-level {@code textures} map and compares that value directly against the carpet placeholder.
     */
    private static boolean isCarpetTexture(Map<String, String> textures, String textureRef) {
        if (textures == null || textureRef == null || !textureRef.startsWith("#")) {
            return false;
        }

        String value = textures.get(textureRef.substring(1));
        return value != null && (value.equals(CARPET_PLACEHOLDER) || value.equals("minecraft:" + CARPET_PLACEHOLDER));
    }
}
