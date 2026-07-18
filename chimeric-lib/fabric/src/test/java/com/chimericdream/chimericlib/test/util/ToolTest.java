package com.chimericdream.chimericlib.test.util;

import com.chimericdream.chimericlib.test.BootstrapMinecraft;
import com.chimericdream.lib.tags.CommonBlockTags;
import com.chimericdream.lib.util.Tool;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ToolTest extends BootstrapMinecraft {
    @Test
    void getItemTagMapsDiggingToolsToVanillaTags() {
        assertEquals(ItemTags.AXES, Tool.AXE.getItemTag());
        assertEquals(ItemTags.HOES, Tool.HOE.getItemTag());
        assertEquals(ItemTags.PICKAXES, Tool.PICKAXE.getItemTag());
        assertEquals(ItemTags.SHOVELS, Tool.SHOVEL.getItemTag());
    }

    @Test
    void getItemTagIsNullForShearsAndNone() {
        assertNull(Tool.SHEARS.getItemTag());
        assertNull(Tool.NONE.getItemTag());
    }

    @Test
    void getMineableTagMapsToVanillaMineableTags() {
        assertEquals(BlockTags.MINEABLE_WITH_AXE, Tool.AXE.getMineableTag());
        assertEquals(BlockTags.MINEABLE_WITH_HOE, Tool.HOE.getMineableTag());
        assertEquals(BlockTags.MINEABLE_WITH_PICKAXE, Tool.PICKAXE.getMineableTag());
        assertEquals(BlockTags.MINEABLE_WITH_SHOVEL, Tool.SHOVEL.getMineableTag());
    }

    @Test
    void getMineableTagUsesCommonShearsTag() {
        assertEquals(CommonBlockTags.SHEARS_MINEABLE, Tool.SHEARS.getMineableTag());
    }

    @Test
    void getMineableTagIsNullForNone() {
        assertNull(Tool.NONE.getMineableTag());
    }
}
