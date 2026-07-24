package com.chimericdream.lib.colors;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ColorHelpers {
    public record RGB(int r, int g, int b) {
        public static RGB fromInt(int color) {
            return new RGB(ARGB.red(color), ARGB.green(color), ARGB.blue(color));
        }

        public int getColor() {
            return ARGB.color(0, r, g, b);
        }

        public String toHex() {
            return String.format("#%02x%02x%02x", r, g, b);
        }

        public int toInt() {
            return getColor();
        }
    }

    public static final int[] WHITE = {0xf9fffe, 0xe4e4e4};
    public static final int[] LIGHT_GRAY = {0x9d9d97, 0xa0a7a7};
    public static final int[] GRAY = {0x474f52, 0x414141};
    public static final int[] BLACK = {0x1d1d21, 0x181414};
    public static final int[] BROWN = {0x835432, 0x56331c};
    public static final int[] RED = {0xb02e26, 0x9e2b27};
    public static final int[] ORANGE = {0xf9801d, 0xea7e35};
    public static final int[] YELLOW = {0xfed83d, 0xc2b51c};
    public static final int[] LIME = {0x80c71f, 0x39ba2e};
    public static final int[] GREEN = {0x5e7c16, 0x364b18};
    public static final int[] CYAN = {0x169c9c, 0x267191};
    public static final int[] LIGHT_BLUE = {0x3ab3da, 0x6387d2};
    public static final int[] BLUE = {0x3c44aa, 0x253193};
    public static final int[] PURPLE = {0x8932b8, 0x7e34bf};
    public static final int[] MAGENTA = {0xc74ebd, 0xbe49c9};
    public static final int[] PINK = {0xf38baa, 0xd98199};

    public static String[] getColors() {
        return List.of(
            "white",
            "light_gray",
            "gray",
            "black",
            "brown",
            "red",
            "orange",
            "yellow",
            "lime",
            "green",
            "cyan",
            "light_blue",
            "blue",
            "purple",
            "magenta",
            "pink"
        ).toArray(new String[0]);
    }

    public static int getTint(int tintIndex, int[] variants) {
        if (tintIndex < 0 || tintIndex >= variants.length) {
            return variants[0];
        }

        return variants[tintIndex];
    }

    public static String getName(DyeColor color) {
        return getName(color.getSerializedName());
    }

    public static String getName(String color) {
        return switch (color) {
            case "white" -> "White";
            case "light_gray" -> "Light Gray";
            case "gray" -> "Gray";
            case "black" -> "Black";
            case "brown" -> "Brown";
            case "red" -> "Red";
            case "orange" -> "Orange";
            case "yellow" -> "Yellow";
            case "lime" -> "Lime";
            case "green" -> "Green";
            case "cyan" -> "Cyan";
            case "light_blue" -> "Light Blue";
            case "blue" -> "Blue";
            case "purple" -> "Purple";
            case "magenta" -> "Magenta";
            case "pink" -> "Pink";
            default -> throw new RuntimeException(String.format("Invalid color %s", color));
        };
    }

    public static Item getDye(DyeColor color) {
        return getDye(color.getSerializedName());
    }

    public static Item getDye(String color) {
        return switch (color) {
            case "white" -> Items.DYE.white();
            case "light_gray" -> Items.DYE.lightGray();
            case "gray" -> Items.DYE.gray();
            case "black" -> Items.DYE.black();
            case "brown" -> Items.DYE.brown();
            case "red" -> Items.DYE.red();
            case "orange" -> Items.DYE.orange();
            case "yellow" -> Items.DYE.yellow();
            case "lime" -> Items.DYE.lime();
            case "green" -> Items.DYE.green();
            case "cyan" -> Items.DYE.cyan();
            case "light_blue" -> Items.DYE.lightBlue();
            case "blue" -> Items.DYE.blue();
            case "purple" -> Items.DYE.purple();
            case "magenta" -> Items.DYE.magenta();
            case "pink" -> Items.DYE.pink();
            default -> throw new RuntimeException(String.format("Invalid color %s", color));
        };
    }

    public static Block getWool(String color) {
        return switch (color) {
            case "white" -> Blocks.WOOL.white();
            case "light_gray" -> Blocks.WOOL.lightGray();
            case "gray" -> Blocks.WOOL.gray();
            case "black" -> Blocks.WOOL.black();
            case "brown" -> Blocks.WOOL.brown();
            case "red" -> Blocks.WOOL.red();
            case "orange" -> Blocks.WOOL.orange();
            case "yellow" -> Blocks.WOOL.yellow();
            case "lime" -> Blocks.WOOL.lime();
            case "green" -> Blocks.WOOL.green();
            case "cyan" -> Blocks.WOOL.cyan();
            case "light_blue" -> Blocks.WOOL.lightBlue();
            case "blue" -> Blocks.WOOL.blue();
            case "purple" -> Blocks.WOOL.purple();
            case "magenta" -> Blocks.WOOL.magenta();
            case "pink" -> Blocks.WOOL.pink();
            default -> throw new RuntimeException(String.format("Invalid color %s", color));
        };
    }

    public static ColorHelpers.RGB mixColors(@Nullable ColorHelpers.RGB rgb, List<DyeColor> colors) {
        int l = rgb == null ? 0 : Math.max(rgb.r, Math.max(rgb.g, rgb.b));
        int i = rgb == null ? 0 : rgb.r;
        int j = rgb == null ? 0 : rgb.g;
        int k = rgb == null ? 0 : rgb.b;
        int m = rgb == null ? 0 : 1;

        for (DyeColor color : colors) {
            int p = color.getTextureDiffuseColor();
            int q = ARGB.red(p);
            int r = ARGB.green(p);
            int s = ARGB.blue(p);
            l += Math.max(q, Math.max(r, s));
            i += q;
            j += r;
            k += s;
            ++m;
        }

        if (m == 0) {
            return rgb;
        }

        int n = i / m;
        int o = j / m;
        int p = k / m;
        float f = (float) l / (float) m;
        float g = (float) Math.max(n, Math.max(o, p));
        n = (int) ((float) n * f / g);
        o = (int) ((float) o * f / g);
        p = (int) ((float) p * f / g);

        return new RGB(n, o, p);
    }
}
