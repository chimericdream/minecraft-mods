package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.entity.DyedPumpkinBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.chimericdream.allhallowssteve.AllHallowsSteveMod.REGISTRY_HELPER;

/**
 * A pumpkin dyed by the {@link CarvingStationBlock}. A separate block from vanilla's pumpkin rather
 * than a retexture of it, so the stored {@link com.chimericdream.allhallowssteve.component.type.DyedColorComponent}
 * has somewhere of its own to live and render from (see {@link DyedPumpkinBlockEntity}) without
 * touching vanilla pumpkin's stem-growth behavior.
 */
public class DyedPumpkinBlock extends BaseEntityBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "dyed_pumpkin");
    public static final MapCodec<DyedPumpkinBlock> CODEC = simpleCodec(DyedPumpkinBlock::create);

    static DyedPumpkinBlock create(BlockBehaviour.Properties settings) {
        return new DyedPumpkinBlock() {
        };
    }

    public DyedPumpkinBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.PUMPKIN).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));
    }

    public @NotNull MapCodec<DyedPumpkinBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DyedPumpkinBlockEntity(pos, state);
    }
}
