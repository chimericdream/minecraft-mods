package com.chimericdream.minekea.block.building.compressed;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.function.Consumer;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class CompressedBlock extends Block implements TooltipAppender {
    public static final String TOOLTIP_LEVEL = "block.minekea.building.compressed.tooltip.level";
    public static final String TOOLTIP_COUNT = "block.minekea.building.compressed.tooltip.count";

    public static final EnumProperty<Direction.Axis> AXIS;

    public final Identifier BLOCK_ID;
    public Identifier PARENT_BLOCK_ID;
    public final BlockConfig config;
    public final int compressionLevel;

    @Nullable
    private Item cachedItem;

    static {
        AXIS = Properties.AXIS;
    }

    public CompressedBlock(BlockConfig config, int compressionLevel) {
        super(
            AbstractBlock.Settings
                .copy(config.getIngredient())
                .strength(
                    getHardness(compressionLevel, config.getIngredient().getHardness()),
                    getResistance(compressionLevel, config.getIngredient().getBlastResistance())
                )
                .requiresTool()
                .registryKey(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial(), compressionLevel)))
                .overrideTranslationKey(makeTranslationKey(config.getMaterial()))
        );

        this.compressionLevel = compressionLevel;
        this.config = config;

        BLOCK_ID = makeId(config.getMaterial(), compressionLevel);

        if (compressionLevel > 1) {
            PARENT_BLOCK_ID = Identifier.of(ModInfo.MOD_ID, String.format("building/compressed/%s/%dx", config.getMaterial(), compressionLevel - 1));
        } else {
            PARENT_BLOCK_ID = Registries.BLOCK.getId(config.getIngredient());
        }

        this.setDefaultState(this.stateManager.getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    public static String makeTranslationKey(String material) {
        return Identifier.of(ModInfo.MOD_ID, String.format("building/compressed/%s", material)).toTranslationKey().replace('/', '.');
    }

    public static Identifier makeId(String material, int compressionLevel) {
        return Identifier.of(ModInfo.MOD_ID, String.format("building/compressed/%s/%dx", material, compressionLevel));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(AXIS, ctx.getSide().getAxis());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        DecimalFormat df = new DecimalFormat("###,###,###");

        textConsumer.accept(Text.translatable(TOOLTIP_LEVEL, compressionLevel));
        textConsumer.accept(Text.translatable(TOOLTIP_COUNT, df.format(Math.pow(9, compressionLevel))));
    }

    protected static float getHardness(int level, float baseHardness) {
        return (float) (baseHardness * Math.pow(3, level));
    }

    protected static float getResistance(int level, float baseResistance) {
        return (float) (baseResistance * Math.pow(3, level));
    }
}
