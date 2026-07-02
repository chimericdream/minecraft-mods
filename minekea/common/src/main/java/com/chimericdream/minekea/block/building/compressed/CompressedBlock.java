package com.chimericdream.minekea.block.building.compressed;

import com.chimericdream.lib.blocks.BlockConfig;
import com.chimericdream.minekea.ModInfo;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class CompressedBlock extends Block {
    public static final String TOOLTIP_LEVEL = "block.minekea.building.compressed.tooltip.level";
    public static final String TOOLTIP_COUNT = "block.minekea.building.compressed.tooltip.count";

    public static final EnumProperty<Direction.Axis> AXIS;

    public final ResourceLocation BLOCK_ID;
    public ResourceLocation PARENT_BLOCK_ID;
    public final BlockConfig config;
    public final int compressionLevel;

    @Nullable
    private Item cachedItem;

    static {
        AXIS = BlockStateProperties.AXIS;
    }

    public CompressedBlock(BlockConfig config, int compressionLevel) {
        super(
            BlockBehaviour.Properties
                .ofFullCopy(config.getIngredient())
                .strength(
                    getHardness(compressionLevel, config.getIngredient().defaultDestroyTime()),
                    getResistance(compressionLevel, config.getIngredient().getExplosionResistance())
                )
                .requiresCorrectToolForDrops()
                .setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(config.getMaterial(), compressionLevel)))
                .overrideDescription(makeTranslationKey(config.getMaterial()))
        );

        this.compressionLevel = compressionLevel;
        this.config = config;

        BLOCK_ID = makeId(config.getMaterial(), compressionLevel);

        if (compressionLevel > 1) {
            PARENT_BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/compressed/%s/%dx", config.getMaterial(), compressionLevel - 1));
        } else {
            PARENT_BLOCK_ID = BuiltInRegistries.BLOCK.getKey(config.getIngredient());
        }

        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    public static String makeTranslationKey(String material) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/compressed/%s", material)).toLanguageKey().replace('/', '.');
    }

    public static ResourceLocation makeId(String material, int compressionLevel) {
        return ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/compressed/%s/%dx", material, compressionLevel));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(AXIS, ctx.getClickedFace().getAxis());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public List<Component> getTooltip() {
        DecimalFormat df = new DecimalFormat("###,###,###");

        return List.of(Component.translatable(TOOLTIP_LEVEL, compressionLevel), Component.translatable(TOOLTIP_COUNT, df.format(Math.pow(9, compressionLevel))));
    }

    protected static float getHardness(int level, float baseHardness) {
        return (float) (baseHardness * Math.pow(3, level));
    }

    protected static float getResistance(int level, float baseResistance) {
        return (float) (baseResistance * Math.pow(3, level));
    }
}
