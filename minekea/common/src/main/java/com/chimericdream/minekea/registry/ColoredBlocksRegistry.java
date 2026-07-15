package com.chimericdream.minekea.registry;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ColoredBlocksRegistry {
    public static final List<ColoredBlock> REGISTRY = new ArrayList<>();

    public static void init() {
        addBlock(Blocks.CONCRETE.white(), "minecraft:concrete", BlockColor.WHITE);
        addBlock(Blocks.CONCRETE.orange(), "minecraft:concrete", BlockColor.ORANGE);
        addBlock(Blocks.CONCRETE.magenta(), "minecraft:concrete", BlockColor.MAGENTA);
        addBlock(Blocks.CONCRETE.lightBlue(), "minecraft:concrete", BlockColor.LIGHT_BLUE);
        addBlock(Blocks.CONCRETE.yellow(), "minecraft:concrete", BlockColor.YELLOW);
        addBlock(Blocks.CONCRETE.lime(), "minecraft:concrete", BlockColor.LIME);
        addBlock(Blocks.CONCRETE.pink(), "minecraft:concrete", BlockColor.PINK);
        addBlock(Blocks.CONCRETE.gray(), "minecraft:concrete", BlockColor.GRAY);
        addBlock(Blocks.CONCRETE.lightGray(), "minecraft:concrete", BlockColor.LIGHT_GRAY);
        addBlock(Blocks.CONCRETE.cyan(), "minecraft:concrete", BlockColor.CYAN);
        addBlock(Blocks.CONCRETE.purple(), "minecraft:concrete", BlockColor.PURPLE);
        addBlock(Blocks.CONCRETE.blue(), "minecraft:concrete", BlockColor.BLUE);
        addBlock(Blocks.CONCRETE.brown(), "minecraft:concrete", BlockColor.BROWN);
        addBlock(Blocks.CONCRETE.green(), "minecraft:concrete", BlockColor.GREEN);
        addBlock(Blocks.CONCRETE.red(), "minecraft:concrete", BlockColor.RED);
        addBlock(Blocks.CONCRETE.black(), "minecraft:concrete", BlockColor.BLACK);

        addBlock(Blocks.CONCRETE_POWDER.white(), "minecraft:concrete_powder", BlockColor.WHITE);
        addBlock(Blocks.CONCRETE_POWDER.orange(), "minecraft:concrete_powder", BlockColor.ORANGE);
        addBlock(Blocks.CONCRETE_POWDER.magenta(), "minecraft:concrete_powder", BlockColor.MAGENTA);
        addBlock(Blocks.CONCRETE_POWDER.lightBlue(), "minecraft:concrete_powder", BlockColor.LIGHT_BLUE);
        addBlock(Blocks.CONCRETE_POWDER.yellow(), "minecraft:concrete_powder", BlockColor.YELLOW);
        addBlock(Blocks.CONCRETE_POWDER.lime(), "minecraft:concrete_powder", BlockColor.LIME);
        addBlock(Blocks.CONCRETE_POWDER.pink(), "minecraft:concrete_powder", BlockColor.PINK);
        addBlock(Blocks.CONCRETE_POWDER.gray(), "minecraft:concrete_powder", BlockColor.GRAY);
        addBlock(Blocks.CONCRETE_POWDER.lightGray(), "minecraft:concrete_powder", BlockColor.LIGHT_GRAY);
        addBlock(Blocks.CONCRETE_POWDER.cyan(), "minecraft:concrete_powder", BlockColor.CYAN);
        addBlock(Blocks.CONCRETE_POWDER.purple(), "minecraft:concrete_powder", BlockColor.PURPLE);
        addBlock(Blocks.CONCRETE_POWDER.blue(), "minecraft:concrete_powder", BlockColor.BLUE);
        addBlock(Blocks.CONCRETE_POWDER.brown(), "minecraft:concrete_powder", BlockColor.BROWN);
        addBlock(Blocks.CONCRETE_POWDER.green(), "minecraft:concrete_powder", BlockColor.GREEN);
        addBlock(Blocks.CONCRETE_POWDER.red(), "minecraft:concrete_powder", BlockColor.RED);
        addBlock(Blocks.CONCRETE_POWDER.black(), "minecraft:concrete_powder", BlockColor.BLACK);

        addBlock(Blocks.WOOL.white(), "minecraft:wool", BlockColor.WHITE);
        addBlock(Blocks.WOOL.orange(), "minecraft:wool", BlockColor.ORANGE);
        addBlock(Blocks.WOOL.magenta(), "minecraft:wool", BlockColor.MAGENTA);
        addBlock(Blocks.WOOL.lightBlue(), "minecraft:wool", BlockColor.LIGHT_BLUE);
        addBlock(Blocks.WOOL.yellow(), "minecraft:wool", BlockColor.YELLOW);
        addBlock(Blocks.WOOL.lime(), "minecraft:wool", BlockColor.LIME);
        addBlock(Blocks.WOOL.pink(), "minecraft:wool", BlockColor.PINK);
        addBlock(Blocks.WOOL.gray(), "minecraft:wool", BlockColor.GRAY);
        addBlock(Blocks.WOOL.lightGray(), "minecraft:wool", BlockColor.LIGHT_GRAY);
        addBlock(Blocks.WOOL.cyan(), "minecraft:wool", BlockColor.CYAN);
        addBlock(Blocks.WOOL.purple(), "minecraft:wool", BlockColor.PURPLE);
        addBlock(Blocks.WOOL.blue(), "minecraft:wool", BlockColor.BLUE);
        addBlock(Blocks.WOOL.brown(), "minecraft:wool", BlockColor.BROWN);
        addBlock(Blocks.WOOL.green(), "minecraft:wool", BlockColor.GREEN);
        addBlock(Blocks.WOOL.red(), "minecraft:wool", BlockColor.RED);
        addBlock(Blocks.WOOL.black(), "minecraft:wool", BlockColor.BLACK);

        addBlock(Blocks.TERRACOTTA, "minecraft:terracotta", BlockColor.NONE);
        addBlock(Blocks.DYED_TERRACOTTA.white(), "minecraft:terracotta", BlockColor.WHITE);
        addBlock(Blocks.DYED_TERRACOTTA.orange(), "minecraft:terracotta", BlockColor.ORANGE);
        addBlock(Blocks.DYED_TERRACOTTA.magenta(), "minecraft:terracotta", BlockColor.MAGENTA);
        addBlock(Blocks.DYED_TERRACOTTA.lightBlue(), "minecraft:terracotta", BlockColor.LIGHT_BLUE);
        addBlock(Blocks.DYED_TERRACOTTA.yellow(), "minecraft:terracotta", BlockColor.YELLOW);
        addBlock(Blocks.DYED_TERRACOTTA.lime(), "minecraft:terracotta", BlockColor.LIME);
        addBlock(Blocks.DYED_TERRACOTTA.pink(), "minecraft:terracotta", BlockColor.PINK);
        addBlock(Blocks.DYED_TERRACOTTA.gray(), "minecraft:terracotta", BlockColor.GRAY);
        addBlock(Blocks.DYED_TERRACOTTA.lightGray(), "minecraft:terracotta", BlockColor.LIGHT_GRAY);
        addBlock(Blocks.DYED_TERRACOTTA.cyan(), "minecraft:terracotta", BlockColor.CYAN);
        addBlock(Blocks.DYED_TERRACOTTA.purple(), "minecraft:terracotta", BlockColor.PURPLE);
        addBlock(Blocks.DYED_TERRACOTTA.blue(), "minecraft:terracotta", BlockColor.BLUE);
        addBlock(Blocks.DYED_TERRACOTTA.brown(), "minecraft:terracotta", BlockColor.BROWN);
        addBlock(Blocks.DYED_TERRACOTTA.green(), "minecraft:terracotta", BlockColor.GREEN);
        addBlock(Blocks.DYED_TERRACOTTA.red(), "minecraft:terracotta", BlockColor.RED);
        addBlock(Blocks.DYED_TERRACOTTA.black(), "minecraft:terracotta", BlockColor.BLACK);

        addBlock(Blocks.STAINED_GLASS.white(), "minecraft:stained_glass", BlockColor.WHITE);
        addBlock(Blocks.STAINED_GLASS.orange(), "minecraft:stained_glass", BlockColor.ORANGE);
        addBlock(Blocks.STAINED_GLASS.magenta(), "minecraft:stained_glass", BlockColor.MAGENTA);
        addBlock(Blocks.STAINED_GLASS.lightBlue(), "minecraft:stained_glass", BlockColor.LIGHT_BLUE);
        addBlock(Blocks.STAINED_GLASS.yellow(), "minecraft:stained_glass", BlockColor.YELLOW);
        addBlock(Blocks.STAINED_GLASS.lime(), "minecraft:stained_glass", BlockColor.LIME);
        addBlock(Blocks.STAINED_GLASS.pink(), "minecraft:stained_glass", BlockColor.PINK);
        addBlock(Blocks.STAINED_GLASS.gray(), "minecraft:stained_glass", BlockColor.GRAY);
        addBlock(Blocks.STAINED_GLASS.lightGray(), "minecraft:stained_glass", BlockColor.LIGHT_GRAY);
        addBlock(Blocks.STAINED_GLASS.cyan(), "minecraft:stained_glass", BlockColor.CYAN);
        addBlock(Blocks.STAINED_GLASS.purple(), "minecraft:stained_glass", BlockColor.PURPLE);
        addBlock(Blocks.STAINED_GLASS.blue(), "minecraft:stained_glass", BlockColor.BLUE);
        addBlock(Blocks.STAINED_GLASS.brown(), "minecraft:stained_glass", BlockColor.BROWN);
        addBlock(Blocks.STAINED_GLASS.green(), "minecraft:stained_glass", BlockColor.GREEN);
        addBlock(Blocks.STAINED_GLASS.red(), "minecraft:stained_glass", BlockColor.RED);
        addBlock(Blocks.STAINED_GLASS.black(), "minecraft:stained_glass", BlockColor.BLACK);
    }

    public static void addBlock(Block block, String group, BlockColor color) {
        REGISTRY.add(new ColoredBlock(block, group, color));
    }

    public static @Nullable String getBlockGroup(Block block) {
        ColoredBlock match = REGISTRY.stream()
            .filter((ColoredBlock cb) -> cb.block.asItem() == block.asItem())
            .findFirst()
            .orElse(null);

        if (match == null) {
            return null;
        }

        return match.group;
    }

    public static @Nullable Block findBlock(String group, BlockColor color) {
        ColoredBlock match = REGISTRY.stream()
            .filter((ColoredBlock cb) -> cb.group.equals(group) && cb.color == color)
            .findFirst()
            .orElse(null);

        if (match == null) {
            return null;
        }

        return match.block;
    }

    public static class ColoredBlock {
        private final Block block;
        private final String group;
        private final BlockColor color;

        ColoredBlock(Block block, String group, BlockColor color) {
            this.block = block;
            this.group = group;
            this.color = color;
        }
    }

    public enum BlockColor {
        NONE(-1, "-1", null),
        WHITE(0, "2232000", Items.DYE.white()),
        LIGHT_GRAY(1, "2232001", Items.DYE.lightGray()),
        GRAY(2, "2232002", Items.DYE.gray()),
        BLACK(3, "2232003", Items.DYE.black()),
        BROWN(4, "2232004", Items.DYE.brown()),
        RED(5, "2232005", Items.DYE.red()),
        ORANGE(6, "2232006", Items.DYE.orange()),
        YELLOW(7, "2232007", Items.DYE.yellow()),
        LIME(8, "2232008", Items.DYE.lime()),
        GREEN(9, "2232009", Items.DYE.green()),
        CYAN(10, "2232010", Items.DYE.cyan()),
        LIGHT_BLUE(11, "2232011", Items.DYE.lightBlue()),
        BLUE(12, "2232012", Items.DYE.blue()),
        PURPLE(13, "2232013", Items.DYE.purple()),
        MAGENTA(14, "2232014", Items.DYE.magenta()),
        PINK(15, "2232015", Items.DYE.pink());

        private final int idx;
        private final String modelNumber;
        private final Item dye;

        BlockColor(int idx, String modelNumber, Item dye) {
            this.idx = idx;
            this.modelNumber = modelNumber;
            this.dye = dye;
        }

        public static BlockColor get(String value) {
            for (BlockColor color : values()) {
                if (color.toString().equals(value)) {
                    return color;
                }
            }

            return WHITE;
        }

        public int getIndex() {
            return idx;
        }

        public String getModelNumber() {
            return modelNumber;
        }

        public Item getDye() {
            return dye;
        }

        public BlockColor getNext() {
            int nextIdx = (idx + 1) % 16;

            for (BlockColor color : values()) {
                if (color.idx == nextIdx) {
                    return color;
                }
            }

            return WHITE;
        }
    }
}
