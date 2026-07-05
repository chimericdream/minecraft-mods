package com.chimericdream.minekea.block.decorations;

import com.chimericdream.lib.text.TextHelpers;
import com.chimericdream.minekea.ModInfo;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class FakeCakeBlock extends CakeBlock {
    public final static String TOOLTIP_KEY = "block.minekea.decorations.misc.fake_cake.tooltip";
    public final static ResourceLocation BLOCK_ID = ResourceLocation.fromNamespaceAndPath(ModInfo.MOD_ID, "decorations/misc/fake_cake");

    public FakeCakeBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        return InteractionResult.SUCCESS;
    }

    public List<Component> getTooltip() {
        return List.of(TextHelpers.getTooltip(TOOLTIP_KEY));
    }
}
