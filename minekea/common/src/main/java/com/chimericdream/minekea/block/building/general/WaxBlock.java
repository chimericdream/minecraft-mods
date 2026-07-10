package com.chimericdream.minekea.block.building.general;

import com.chimericdream.minekea.ModInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import static com.chimericdream.minekea.MinekeaMod.REGISTRY_HELPER;

public class WaxBlock extends Block {
    public final Identifier BLOCK_ID;

    public final String color;

    public WaxBlock(String color) {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.HONEYCOMB_BLOCK).pushReaction(PushReaction.PUSH_ONLY).friction(0.9F).setId(REGISTRY_HELPER.makeBlockRegistryKey(makeId(color))));

        BLOCK_ID = makeId(color);
        this.color = color;
    }

    public static Identifier makeId(String color) {
        return Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, String.format("building/general/wax/%s", color));
    }
}
