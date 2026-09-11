package com.chimericdream.allhallowssteve.block;

import com.chimericdream.allhallowssteve.ModInfo;
import com.chimericdream.allhallowssteve.block.entity.DecoratedPumpkinBlockEntity;
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
 * has somewhere of its own to live and render from (see {@link DecoratedPumpkinBlockEntity}) without
 * touching vanilla pumpkin's stem-growth behavior.
 */
public class DecoratedPumpkinBlock extends BaseEntityBlock {
    public static final Identifier BLOCK_ID = Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "decorated_pumpkin");
    public static final MapCodec<DecoratedPumpkinBlock> CODEC = simpleCodec(DecoratedPumpkinBlock::create);

    static DecoratedPumpkinBlock create(BlockBehaviour.Properties settings) {
        return new DecoratedPumpkinBlock() {
        };
    }

    public DecoratedPumpkinBlock() {
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.PUMPKIN).setId(REGISTRY_HELPER.makeBlockRegistryKey(BLOCK_ID)));
    }

    public @NotNull MapCodec<DecoratedPumpkinBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DecoratedPumpkinBlockEntity(pos, state);
    }
}
