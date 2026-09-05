package com.chimericdream.camelnostrils.block;

import com.chimericdream.camelnostrils.ModInfo;
import com.chimericdream.camelnostrils.menu.UpsideDownCraftingMenu;
import com.chimericdream.camelnostrils.stats.ModStats;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;

public class UpsideDownCraftingTableBlock extends CraftingTableBlock {
    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting");

    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "upside_down_crafting_table");
    public static final ResourceKey<Block> BLOCK_REGISTRY_KEY = ResourceKey.create(Registries.BLOCK, BLOCK_ID);
    public static final ResourceKey<Item> ITEM_REGISTRY_KEY = ResourceKey.create(Registries.ITEM, BLOCK_ID);

    public static final MapCodec<CraftingTableBlock> CODEC = simpleCodec(UpsideDownCraftingTableBlock::new);

    @Override
    public @NonNull MapCodec<CraftingTableBlock> codec() {
        return CODEC;
    }

    public UpsideDownCraftingTableBlock() {
        this(Properties.ofFullCopy(Blocks.CRAFTING_TABLE).setId(BLOCK_REGISTRY_KEY));
    }

    public UpsideDownCraftingTableBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static UpsideDownCraftingTableBlock create() {
        return new UpsideDownCraftingTableBlock();
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(final @NonNull BlockState state, final Level level, final @NonNull BlockPos pos, final @NonNull Player player, final @NonNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            player.openMenu(state.getMenuProvider(level, pos));
            player.awardStat(ModStats.INTERACT_WITH_UD_CRAFTING_TABLE);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NonNull MenuProvider getMenuProvider(final @NonNull BlockState state, final @NonNull Level level, final @NonNull BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new UpsideDownCraftingMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), CONTAINER_TITLE);
    }
}
