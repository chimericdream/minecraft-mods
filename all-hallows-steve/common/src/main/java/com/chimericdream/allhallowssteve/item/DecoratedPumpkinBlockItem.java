package com.chimericdream.allhallowssteve.item;

import com.chimericdream.allhallowssteve.component.type.AllHallowsSteveComponentTypes;
import com.chimericdream.allhallowssteve.component.type.DyedColorComponent;
import com.chimericdream.allhallowssteve.component.type.PumpkinStencilsComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Shared {@code BlockItem} for every decorated-pumpkin variant (lit or not) — appends the stored dye
 * color (advanced tooltips only) and which stencil is carved into each stenciled side, reading the
 * same {@link DyedColorComponent}/{@link PumpkinStencilsComponent} the block entity round-trips
 * through {@code collectImplicitComponents}/{@code applyImplicitComponents}. Unstenciled sides are
 * skipped.
 */
public class DecoratedPumpkinBlockItem extends BlockItem {
    private static final Direction[] STENCIL_SIDES = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public DecoratedPumpkinBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);

        if (flag.isAdvanced()) {
            int color = stack.getOrDefault(AllHallowsSteveComponentTypes.DYED_COLOR_COMPONENT.get(), DyedColorComponent.DEFAULT).color();

            tooltip.accept(Component.translatable(
                "item.allhallowssteve.decorated_pumpkin.tooltip.color",
                String.format("#%06X", color)
            ).withStyle(ChatFormatting.GRAY));
        }

        PumpkinStencilsComponent stencils = stack.getOrDefault(AllHallowsSteveComponentTypes.STENCILS_COMPONENT.get(), PumpkinStencilsComponent.EMPTY);

        for (Direction side : STENCIL_SIDES) {
            Optional<String> stencil = stencils.get(side);
            if (stencil.isEmpty()) {
                continue;
            }

            Item stencilItem = BuiltInRegistries.ITEM.getValue(PumpkinStencilItem.makeId(stencil.get()));

            tooltip.accept(Component.translatable(
                "item.allhallowssteve.decorated_pumpkin.tooltip.stencil",
                Component.translatable(sideTranslationKey(side)),
                Component.translatable(stencilItem.getDescriptionId())
            ).withStyle(ChatFormatting.GRAY));
        }
    }

    private static String sideTranslationKey(Direction side) {
        return "item.allhallowssteve.decorated_pumpkin.tooltip.side." + side.getSerializedName();
    }
}
